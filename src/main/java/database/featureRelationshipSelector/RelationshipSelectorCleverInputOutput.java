package database.featureRelationshipSelector;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import database.*;
import database.featureRelationshipSelector.RMRelationshipRanker.RMRelationshipRanker;
import database.features.AttFeatureSet;
import database.features.RelationshipFeatureSet;
import models.*;

import java.sql.SQLException;
import java.util.*;

public class RelationshipSelectorCleverInputOutput extends InputOutputRelationshipSelector {

	public List<Relationship> getBestRelationships( AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph, Collection<Relationship> existingGoodRelationships, HashSet<Relationship> attemptedToLearnRelationships, String runId, int processingRound )
	{
		//Get all atts from the graph.
		Collection<Att> featureAtts = inputGraph.getNonDuplicateAtts();
		Collection<Att> labelAtts = outputGraph.getNonDuplicateAtts();

        //Order atts and labelAtts by attOrder to make order consistent across runs.
        Function attOrderGetter = new Function<Att, Integer>(){
            public Integer apply(Att att){
                return att.getAttOrder();
            }
        };
        Ordering<Att> valueComparator = Ordering.natural().onResultOf( attOrderGetter );
        featureAtts = valueComparator.sortedCopy(featureAtts);
        labelAtts = valueComparator.sortedCopy(labelAtts);
		
		ArrayList<ModelLearner> learners = new ArrayList<>();
		learners.add( new ZeroLearner() );;
		learners.add( new BestColumnLearner() );
		learners.add( new RMLinearRegressionLearner() );
		learners.add( new WekaLinearRegressionLearner() );
		learners.add( new WekaLinearRegressionIntegerLearner() );
		learners.add( new RMKNNLearner() ); 
		//learners.add( new RMKNNNominalLearner() ); //Removed as RM nominal mappings (String -> integer) may not always be the same between training and testing.
        learners.add( new MostCommonValueLearner() );
        learners.add( new ChopEndsLearner());
        learners.add( new FindReplaceLearnerSinglePass() );

        //Create an arraylist of those learners that actually take an input and produce a different output depending on it.
        //eg. There is no point trying lots of different zeroLearners with different inputs- they are always going to predict "0".
        ArrayList<ModelLearner> nonConstantLearners = new ArrayList<>();
        nonConstantLearners.add( new BestColumnLearner() );
        nonConstantLearners.add( new RMLinearRegressionLearner() );
        nonConstantLearners.add( new WekaLinearRegressionLearner() );
        nonConstantLearners.add( new WekaLinearRegressionIntegerLearner() );
        nonConstantLearners.add( new RMKNNLearner() );
        nonConstantLearners.add( new ChopEndsLearner() );
        nonConstantLearners.add( new FindReplaceLearnerSinglePass() );


        ArrayList<Relationship> bestRelationships = new ArrayList<>();
		
		Set<DataTable> dataTables = new LinkedHashSet<>();
		for( Att att : featureAtts ){
			dataTables.add(att.getDataTable());
		}

        //Filter out constant feature atts- a constant att will not help to predict a value (although may be used as a root-row-att).
        Collection<Att> filteredAtts = new ArrayList<>();
        for( Att att : featureAtts){
            try {
                if (att.getUniqueRowsInTable() > 1) {
                    filteredAtts.add(att);
                }
            }
            catch( SQLException e ){
                e.printStackTrace();
            }
        }
        featureAtts = filteredAtts;
        filteredAtts = null;

        labelLoop:
		for( Att labelAtt : labelAtts )
		{
			try{
				if( labelAtt.getNotNullRowsInTable() < 3)
					continue;
			}
			catch(SQLException e){
				e.printStackTrace();
				continue;
			}

            //Filter out labelAtts that we already have a good relationship for.
            //While it would be nice to find ALL possible ways of predicting that att, we'll take a shortcut and stop if previous rounds found ways of predicting it already.
            /*for( Relationship existingGoodRel : existingGoodRelationships){
                if( existingGoodRel.getLabel().equals(labelAtt) ){
                    continue labelLoop;
                }
            }*/


            //Find rootRowAtts that fit with the label (correct # of rows) and, when the row is hydrated with inputAtts, could be used to predict it.
			for( DataTable rootDataTable : dataTables ){
                HashSet<List<Integer>> addedAttsWithRows = new LinkedHashSet<>(); //We only want one att which exists for each possible subset of the rows in this table - there's no point using t1.1, t1.2 and t1.3 as rras in different relationships because they will result in the same tables being returned.

				for( Att rootRowAtt : rootDataTable.getAllAttsInTable() ){
					if( !inputGraph.containsVertex(rootRowAtt)){
						continue;
					}
					if( rootRowAtt.isDuplicate() ){
						continue;
					}
					try {
						if( rootRowAtt.getNotNullRowsInTable() != labelAtt.getNotNullRowsInTable() ){
							continue;
						}

                        if( addedAttsWithRows.contains(rootRowAtt.getNotNullRowIds())){
                            //Already added an att like this as a rootRowAtt.
                            continue;
                        }

					} catch (SQLException e1) {
						e1.printStackTrace();
						continue;
					}
					
					
					//If we get here, att is going to be used as rootRowAtt. If it's a "complete" column, record it so we don't add more rootRowAtts that filter to the same rows.
					try {
                        addedAttsWithRows.add( rootRowAtt.getNotNullRowIds() );
					} catch (SQLException e) {
						e.printStackTrace();
						continue;
					}

                    //Use no atts to predict the label (eg. for constant labels).
                    for( ModelLearner learner : learners )
                    {
                        bestRelationships.add( new Relationship( learner, new ArrayList<Att>(), labelAtt, rootRowAtt ) );
                    }

                    //Use each feature att to predict the label (don't bother if the label is constant - the relationship with no inputs above will be sufficient).
                    try {
                        if( labelAtt.getUniqueRowsInTable() > 1) {
                            for (Att featureAtt : featureAtts) {
                                //TODO: Ignore / demote featureAtts with identical values (that are not strict dupes) to another already-used att.
                                // see AttInterestingnessProcessorSelector where we do a similar thing. Need to consider how rootRowAtt complicates this- 2 atts with identical values may join to a rra in different ways, so need both.
                                if (rootRowAtt.equals(featureAtt)
                                        || (rootRowAtt.getDataTable().equals(featureAtt.getDataTable())) //If in the same datatable, then must order the rows or filter differently to rootRowAtts already added.
                                        || (rootRowAtt.getDataTable().getAncestorTables().contains(featureAtt.getDataTable()) && rootRowAtt.getNotNullRowsInTable() != featureAtt.getNotNullRowsInTable())) { //The only reason to choose a RRA from a lower table is to change the number of rows that get predicted (ie. to filter or duplicate rows). TODO: Test here is wrong in this example- feature Att has rows 1,2. if 2 root-rows corresp. to the same feature-row, then our test will throw out the root row, but actually it should be kept because it gives a different result (1,1) to using the feature-att as the root-row (1,2).
                                    //No point considering featureAtts from lower than the RRT because either 1) they will result in >1 row per rootRow (and so can't be used for prediction) or 2) we might as well choose a root-row-att from the feature att's table.

                                    //If the rootRowAtt does not add anything at all (ie. it has exactly the same rows as the feature att) then just use the feature att as the root row att).
                                    //Simplifies the tree diagram a little bit by eliminating some useless arrows.
                                    /* While this would be good, it conflicts with our "only use one RRA from each table with a given set of row ids" above, which means that relationships with 1 input att can't later be discarded in favour of a simpler relationship with 0 input atts (with the same label and RRA) because we didn't learn the simple relationship with this RRA and no input-atts.
                                    Att rootRowAttToUse;
                                    if( false && !rootRowAtt.equals(featureAtt) && rootRowAtt.getDataTable().equals(featureAtt.getDataTable()) && rootRowAtt.getNotNullRowIds().equals( featureAtt.getNotNullRowIds() ) ){
                                        rootRowAttToUse = featureAtt;
                                    }
                                    else{
                                        rootRowAttToUse = rootRowAtt;
                                    }*/

                                    if( featureAtt.getUniqueRowsInTable() < labelAtt.getUniqueRowsInTable() ){
                                        //Since a learner will give the same output every time it sees the same input, it can't possibly predict "2,4,6" from "1,1,1".
                                        //Must be at least as many different inputs as we need outputs.
                                        continue;
                                    }

                                    ArrayList<Att> featureAttsToUse = new ArrayList<>();
                                    featureAttsToUse.add(featureAtt);
                                    int estimatedJoinedSize = DataTable.estimateJoinedSize(Arrays.asList(featureAtt, rootRowAtt), rootRowAtt.getDataTable());
                                    if (estimatedJoinedSize != labelAtt.getNotNullRowsInTable()) {
                                        //System.out.println("Skipping creating Relationship with featureAtt "+featureAtt.getDbColumnNameNoQuotes()+" and RRA "+rootRowAtt.getDbColumnNameNoQuotes()+" because it results in "+estimatedJoinedSize+" rows and the label has "+labelAtt.getNotNullRowsInTable()+" rows.");
                                    } else {
                                        //Shortcut if we know the columns are equal. Don't bother with more advanced models than a "BestColumnLearner".
                                        if( labelAtt.getColumnValuesHash().equals(featureAtt.getColumnValuesHash()) ){
                                            bestRelationships.add( new Relationship(new BestColumnLearner(), featureAttsToUse, labelAtt, rootRowAtt) );
                                        }

                                        for (ModelLearner learner : nonConstantLearners) {
                                            bestRelationships.add(new Relationship(learner, featureAttsToUse, labelAtt, rootRowAtt));
                                        }
                                    }

                                    //TODO: Use more than one feature att to predict the label.
                                }
                            }
                        }
                    }
                    catch (SQLException E) {
                        E.printStackTrace();
                    }
				}
			}
		}

        System.out.println( "Total of "+bestRelationships.size()+" relationships that we could try to learn. ");

        //Filter out those relationships, that even if they behaved perfectly, wouldn't be as good as those in the already learned goodRelationships.
        //TODO: unify this logic with the very similar "keepRelationship" filtering in filterToMinimalGoodRelationships
        ArrayList<Relationship> filteredRelationships = new ArrayList<>();
        for( Relationship possibleRelationship : bestRelationships ){
            boolean keepRelationship = true;
            for( Relationship goodRelationship : existingGoodRelationships ){
                if( goodRelationship.isLessComplex(possibleRelationship) || goodRelationship.requiresLess(possibleRelationship) ){
                    keepRelationship = false;
                    break;
                }
            }
            if( keepRelationship ){
                filteredRelationships.add(possibleRelationship);
            }
        }

        System.out.println( "Removed "+(bestRelationships.size() - filteredRelationships.size())+" by filtering out relationships that wouldn't be used even if they were perfect. ");


        System.out.println( "Creating features for remaining "+filteredRelationships.size()+" atts.");
        //Create features for the remaining relationships which will be used to predict how likely they are to find a good model.
        RMRelationshipRanker ranker = new RMRelationshipRanker();
        List<RelationshipFeatureSet> relationshipsFeatures = new ArrayList<>();
        for (Relationship rel : filteredRelationships) {
            RelationshipFeatureSet features = new RelationshipFeatureSet(rel, inputGraph, outputGraph, existingGoodRelationships);
            relationshipsFeatures.add( features );
        }

        //Make predictions based on those relationships.
        ArrayList<Relationship> predictedGoodRelationships = new ArrayList<>();
        ranker.predictWillMakeAllCorrectPredictions(relationshipsFeatures);
        for( RelationshipFeatureSet features : relationshipsFeatures ){
            if( features.getScoreDuringRun() > 0.001 || Double.isNaN(features.getScoreDuringRun()) ){
                predictedGoodRelationships.add( features.getRelationship() );
            }

            //Save features for later analysis.
            if (runId != null) {
                try {
                    features.addToBatchInsert(runId, processingRound);
                }
                catch( SQLException E ){
                    E.printStackTrace();
                }
            }
        }

        try {
            if (runId != null) {
                RelationshipFeatureSet.saveBatch();
            }
        } catch (SQLException E) {
            E.printStackTrace();
        }

        System.out.println((filteredRelationships.size() - predictedGoodRelationships.size()) + " relationships removed because the feature-model believes they will fail.");
        filteredRelationships = predictedGoodRelationships;

        //TODO: Order them so that the simplest (least complex models, & least input atts) relationships are learned first so we may not need to learn the harder ones.
        //Maybe combine complexity / likely processing time with the probability that this relationship will yield a good result?

        return filteredRelationships;
	}
}
