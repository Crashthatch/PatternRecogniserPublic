package database.features;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import database.Att;
import database.AttRelationshipGraph;
import database.Relationship;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import processors.modelApplier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class AttFeatureSetFactory {
	private AttRelationshipGraph inputGraph;
	private AttRelationshipGraph outputGraph;
    private Collection<Relationship> relationships;
    private boolean lookAtData = true;

	public AttFeatureSetFactory(AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph, Collection<Relationship> relationships){
		this.inputGraph = inputGraph;
		this.outputGraph = outputGraph;
        this.relationships = relationships;
	}

    public void setLookAtData(boolean look){
        lookAtData = look;
    }
	
	public AttFeatureSet getFeaturesForAtt(Att att){
        AttFeatureSet features = new AttFeatureSet(att);

        AttRelationshipGraph otherGraph = outputGraph;
        AttRelationshipGraph containingGraph = inputGraph;
        if( inputGraph.containsVertex(att) ){
            containingGraph = inputGraph;
            otherGraph = outputGraph;
        }
        else if( outputGraph.containsVertex(att) ){
            containingGraph = outputGraph;
            otherGraph = inputGraph;
        }
        else{
            //Att not in either graph!
            assert(false);
        }


        features.setNumAncestorAtts( att.getAncestorAtts().size() );
        features.setDepth(containingGraph.getDepth(att) );
        if( lookAtData) {
            features.setGuessedType(TypeGuesser.guessType(att));
        }
        else{
            features.setGuessedType("");
        }
        features.setDuplicate( att.isDuplicate() ); //TODO: This should be relative to the inputGraph, not the "isDuplicate" stored on the att.
        features.setNumParents( att.getParentAtts().size() );
        if( att.getGenerator() == null ){
            features.setGeneratedByConstantCreator(false);
        }
        else {
            features.setGeneratedByConstantCreator(att.getGenerator().getTransformer().getColsIn() == 0);
        }
		
		try {
            features.setNotNullRowsInTable( att.getNotNullRowsInTable() );
            features.setUniqueRowsInTable( att.getUniqueRowsInTable() );
            features.setNumRowsInTable( att.getDataTable().getNumRows() );
            features.setNullRowsInTable( att.getDataTable().getNumRows() - att.getNotNullRowsInTable() );
		} catch (SQLException e) {
			
		}

        int inputToRelationships = 0; //Find out if this att is used as an input to a known good relationship.
        int RRAToRelationships = 0; //Is this att used as a RRA in an existing relationship?
        int datatableAttsUsedInRelationships = 0; //Is an att in this datatable used in a known good relationship?
        int datatableAttsUsedAsRRAsInRelationships = 0; //Is an att in this datatable used as a RRA in a known good relationship?
        HashSet<Att> attsUsedInAnyRel = new LinkedHashSet<>();
        Collection<Att> attsInTable = att.getDataTable().getAllAttsInTable();
        for( Relationship rel : this.relationships ){
            if( rel.getInputAtts().contains(att) ){
                inputToRelationships++;
            }
            if( rel.getRootRowAtt().equals(att)){
                RRAToRelationships++;
            }
            if( attsInTable.contains(rel.getRootRowAtt())){
                RRAToRelationships++;
            }
            attsUsedInAnyRel.addAll( rel.getInputAtts() );
        }
        datatableAttsUsedInRelationships += Sets.intersection(Sets.newHashSet(attsInTable), attsUsedInAnyRel).size();

        features.setInputToRelationships(inputToRelationships);
        features.setRRAToRelationships(RRAToRelationships);
        features.setDatatableAttsUsedInRelationships(datatableAttsUsedInRelationships);
        features.setDatatableAttsUsedAsRRAsInRelationships(datatableAttsUsedAsRRAsInRelationships);

        if( att.getGenerator() != null && att.getGenerator().getTransformer().getClass().equals( modelApplier.class ) ){
            features.setCreatedByModelApplier(true);
        }
        else{
            features.setCreatedByModelApplier(false);
        }
		
		//"Almost Duplicate" if another, earlier att exists in the inputGraph with the same values.
		try{
			String attValuesHash = att.getColumnValuesHash();
			for( Att dupeAtt : containingGraph.getAllAtts() ){
				try{
					if( dupeAtt.getColumnValuesHash().equals( attValuesHash ) && dupeAtt.getAttOrder() < att.getAttOrder() ){
                        features.setAlmostDuplicate( true );
						break;
					}
				}
				catch( SQLException E ){
					E.printStackTrace();
				}
			}
		}
		catch( SQLException E ){
			E.printStackTrace();
		}
		
		//Find out if this att contains all the values of any of the atts from the targetGraph.
		//Eg. "<div>1<span>Bob</span>2<span>Alan</span>3<span>Joe</span></div>" covers a target att with 3 rows: "Bob", "Alan", "Joe".
        if( lookAtData ) {
            try {
                ArrayList<String> allData = att.getData();
                int totalLength = 0;

                if (allData.size() > 0) {
                    features.setFirstRowLength(allData.get(0).length());
                    for (String attRow : allData) {
                        totalLength += attRow.length();
                    }
                }

                if (allData.size() > 0 && !allData.get(0).equals("0")) {
                    Att bestTargetAtt = null;
                    //Optimization: At FeatureSetFactory instantiation time, create a set of lists of the targetAtts' in memory to avoid making lots of calls to the DB here.
                    for (Att targetAtt : otherGraph.getNonDuplicateAtts()) {
                        try {
                            if (!targetAtt.getFirstRow().equals("0")) { //Try to filter out "index" type atts without filtering out all numeric type columns. Could do better identification of index type atts, or generalise to all "not worth digging for" columns somehow?
                                int targetAttTotalLength = 0;
                                int foundTargetRowsInAtt = 0; //The number of targetRows covered by some row in the original att.
                                int coveringRows = 0; //The reverse of foundTargetRowsInAtt- counts the number of rows in the original att that cover rows in the target att.
                                double bestCoveringPercentageAvg = 0.0;
                                HashSet<String> targetDataSet = new HashSet<>(targetAtt.getData());
                                if (targetDataSet.size() >= 3) {


                                    targetRowLoop:
                                    for (String targetRow : targetDataSet) {
                                        targetAttTotalLength += targetRow.length();
                                        for (String attRow : allData) {
                                            if (attRow.contains(targetRow)) {
                                                foundTargetRowsInAtt++;
                                                continue targetRowLoop;
                                            }
                                        }
                                    }
                                    DescriptiveStatistics attRowsCoverPercentages = new DescriptiveStatistics();
                                    attRowLoop:
                                    for (String attRow : allData) {
                                        for (String targetRow : targetDataSet) {
                                            if (attRow.contains(targetRow)) {
                                                coveringRows++;
                                                attRowsCoverPercentages.addValue((double) targetRow.length() / attRow.length());
                                                continue attRowLoop;
                                            }
                                        }

                                    }

                                    double percentageOfTargetAttCovered = (double) foundTargetRowsInAtt / (double) targetDataSet.size();
                                    //TODO: Add percentageOfTargetAttCoveredByLength (rather than by rows)
                                    //TODO: Bonus if there's a 1:1 covering from att's rows to targetAtt's rows (ie. each row covers and is covered by exactly 1 row)
                                    //Make 100%-covering atts much better than covering most atts.
                                    //Multiple atts.
                                    if (percentageOfTargetAttCovered > 0.5) {
                                        double aimForScore = 3.0 * Math.pow(percentageOfTargetAttCovered, 2) * Math.min(1.0, (double) ((double) targetAttTotalLength / (totalLength + 1.0)));
                                        if (totalLength >= targetAttTotalLength && aimForScore > features.getBestAimForScore()) {
                                            features.setBestCoveringRows(coveringRows);
                                            features.setBestAimForScore(aimForScore);
                                            features.setBestTargetAttRows(targetDataSet.size());
                                            features.setBestCoveringPercentageAvg(attRowsCoverPercentages.getMean());
                                            features.setBestTargetAttRowsCovered(foundTargetRowsInAtt);
                                            features.setBestPercentageOfTargetAttCovered(percentageOfTargetAttCovered);
                                            features.setBestTargetAttTotalLength(targetAttTotalLength);
                                            bestTargetAtt = targetAtt;
                                        }
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                features.setAverageRowLength((double) totalLength / att.getNotNullRowsInTable());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            //TODO: Convert so NULLs get saved in the DB?
            features.setBestCoveringRows(-1);
            features.setBestAimForScore(-1);
            features.setBestTargetAttRows(-1);
            features.setBestCoveringPercentageAvg(-1);
            features.setBestTargetAttRowsCovered(-1);
            features.setBestPercentageOfTargetAttCovered(-1);
            features.setBestTargetAttTotalLength(-1);
            features.setAverageRowLength(-1);
            features.setFirstRowLength(-1);
        }
		

		
		return features;
	}
}
