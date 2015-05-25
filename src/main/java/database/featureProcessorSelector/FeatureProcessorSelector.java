package database.featureProcessorSelector;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import database.*;
import database.features.AttFeatureSet;
import database.features.AttFeatureSetFactory;
import org.apache.commons.lang.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * This ProcessorSelector uses given attsToProcessRanker and transformerSelector to rank atts, then choose the best transformers for the top X atts.
 * It creates FeatureSets for every att in the table which can be saved to the DB for metamodel training, then uses the given attsToProcessRanker
 *  metamodel to rank each of those featureSets.
 * @author Tom
 *
 */
public class FeatureProcessorSelector extends InputOutputProcessorSelector{
	private HashSet<String> stringConstants = new HashSet<>();
	private AttsToProcessRanker attsToProcessRanker;
	private TransformerSelector transformerSelector;
	private String runId;
	private int processingRound;
	
	public FeatureProcessorSelector(AttsToProcessRanker attsToProcessRanker, TransformerSelector transformerSelector, String runId, int processingRound){
		this.attsToProcessRanker = attsToProcessRanker;
		this.transformerSelector = transformerSelector;
		this.runId = runId;
		this.processingRound = processingRound;
	}
	public FeatureProcessorSelector(AttsToProcessRanker attsToProcessRanker, TransformerSelector transformerSelector){
		this.attsToProcessRanker = attsToProcessRanker;
		this.transformerSelector = transformerSelector;
	}
	
	
	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph){
		return getBestProcessors( attGraph, null, null);
	}
	
	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph, final AttRelationshipGraph outputGraph, final Collection<Relationship> relationships)
	{
		
		//Get all atts from the graph.
		Collection<Att> atts = attGraph.getNonDuplicateAtts();
		Collection<Processor> previousProcessors = attGraph.getAllProcessors();
		Att rootAtt = attGraph.getRootAtt();
		
		
		//Remove atts where the generator of that att failed, or the att somehow else ended up with no rows, or its a useless "duplicate" att.
		List<Att> attsWithAtLeastOneRow = new ArrayList<>();
		for( Att att : atts )
		{
			try {
				if( att.getNotNullRowsInTable() > 0 && !att.isDuplicate())
				{
					attsWithAtLeastOneRow.add(att);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//TODO: Do further processing on atts that are used by non-perfect relationships. If A can be used to predict X in 70% of situations, maybe f(A) will be successful in 100% of situations (or f(A) will be a filtered version of A to those that we can successfully predict for).
		
		
		//Set<Att> inputCandidateAtts = new ArrayList<>(attsWithAtLeastOneRow);
		attsWithAtLeastOneRow.remove(rootAtt);
		atts = null;
		Set<Att> inputCandidateAtts = new HashSet<>(attsWithAtLeastOneRow);
		
		//Create feature rows for each candidate att, and apply the att-ranking metamodel to each set of features to choose which atts to do further processing on.
		List<Att> interestingAtts = new ArrayList<>();
		{
			AttFeatureSetFactory featureGenerator = new AttFeatureSetFactory( attGraph.getSubGraph(inputCandidateAtts), outputGraph, relationships);
			HashMap<AttFeatureSet, Double> attSelectorFeatures = new HashMap<>();
			for( Att att : attsWithAtLeastOneRow ){
				AttFeatureSet features = featureGenerator.getFeaturesForAtt(att);
				//Save the features to the DB.
				if( runId != null){
					try {
						features.addToBatchInsert(runId, processingRound);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				//Rank the Att.
				double score = attsToProcessRanker.scoreAtt(features);
				attSelectorFeatures.put( features, score );
			}
            if( runId != null) {
                try{
                    AttFeatureSet.saveBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
			//Order bigger scores first. Use attOrder to break ties deterministically.
			Function attOrderGetter = new Function<AttFeatureSet, Integer>(){
				public Integer apply(AttFeatureSet attFeatures){
					return attFeatures.getAtt().getAttOrder();
				}
			};
			Ordering<AttFeatureSet> valueComparator = Ordering.natural().onResultOf(Functions.forMap(attSelectorFeatures)).reverse().compound(Ordering.natural().onResultOf( attOrderGetter ) );
					
			int rank = 1;
			List<AttFeatureSet> attsSortedByInterestingness = valueComparator.sortedCopy(attSelectorFeatures.keySet());
            if (runId != null) {
                try {
                    String SQL = "UPDATE attribute SET `scoreDuringRun`=?, `rankingDuringRun`=? WHERE `runId`='" + runId + "' AND `processingRound`='" + processingRound + "' AND `attId`=?";
                    PreparedStatement preparedStatement = MetaModelDatabase.getConnection().prepareStatement(SQL);
                    for (AttFeatureSet features : attsSortedByInterestingness) {
                        System.out.println(rank + "th most interesting " + attSelectorFeatures.get(features) + " (AttOrder " + features.getAtt().getAttOrder()+"): "+features);
                        //Add the score to the features table, not to be used for training/predictions, just for investigation & debugging.
                        try {
                            //String SQL = "UPDATE attribute SET `scoreDuringRun`='"+attSelectorFeatures.get(features)+"', `rankingDuringRun` = '"+rank+"' WHERE `runId`='"+runId+"' AND `processingRound`='"+processingRound+"' AND `attId`="+features.getAtt().getAttOrder();
                            preparedStatement.setDouble(1, attSelectorFeatures.get(features));
                            preparedStatement.setInt(2, rank);
                            preparedStatement.setInt(3, features.getAtt().getAttOrder());
                            preparedStatement.addBatch();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        rank++;
                    }
                    preparedStatement.executeBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
			
			int attsToProcessFurther = Math.min(100, attsSortedByInterestingness.size());
			for(int attIdx=0; attIdx < attsToProcessFurther; attIdx++ ){
				interestingAtts.add( attsSortedByInterestingness.get(attIdx).getAtt() );
			}
		}
		
		
		ArrayList<Processor> bestProcessors = new ArrayList<>();
		System.out.println("Found "+inputCandidateAtts.size()+" input att candidates.");
		//Create a list of constants to "aim for". InputAtts containing one of these constants will be weighted more highly.
		if( outputGraph != null ){
			stringConstants = new HashSet<>();
			for( Att att : outputGraph.getNonDuplicateAtts() ){
				try{ 
					for( String row : att.getData() ){
						//The longer the row is, the less likely finding it in an input att is a coincidence.
						//Don't allow 1, 2, 3 etc if there are < 10 rows in this att. Don't allow 20, 30, 55 etc id there are less than 100 rows in this att. etc.
						if( row.length() > 8 || (row.length() >= 3 && !StringUtils.isNumeric(row)) || row.length() > 1.0+Math.log10(att.getNotNullRowsInTable()) ){
							stringConstants.add(row);
						}
					}
				}catch( SQLException e ){
					e.printStackTrace();
				}
			}
		}
		
		int attIdx = 0;
		for( Att att : interestingAtts ){
			attIdx++;
			int transformersForThisAtt = (int) (10 - Math.floor(attIdx/3)); //10 for the first few atts, 9 for the next few, etc.
			if( interestingAtts.size() == 1){ //If it's the first round, brute-force do lots of processing on the longStringAtt.
				transformersForThisAtt = 100;
			}
			if( transformersForThisAtt == 0){
				break;
			}
			
			List<TransformationService> bestTransformersForAtt = transformerSelector.getBestProcessorsForAtt(att);
			
			//TODO:Sort by combination of att-ranking, att-type-ranking (what type and how sure we are of what it is) and how long the transformers are likely to take to run.
			//TODO: Make it so "likely" processors on a medium-interesting att come before "unlikely" processors for an interesting att (so the most interesting atts don't get ALL processors run on them before the less interesting ones get a chance). 
			

			int transformersAddedForAttCount = 0;
			for( TransformationService transformer : bestTransformersForAtt ){
				if( transformersAddedForAttCount >= transformersForThisAtt ){
					break;
				}
				
				HashSet<DataTable> possibleRootRowTables = new HashSet<>();
				//If the transformer only takes 1 row, then the only valid RootRowTable is the table containing the att. Choosing a rootRowTable from "higher up" groups many rows to a single transformer and means that the number of rows per transformer will be > 1.
				//TODO: Can # of rows in a table for a given rootRowTable be calculated without actually doing the join? Would mean we can give transformers that ask for specific numbers of rows the correct inputs & rootRows, instead of brute-force trying them all.
				if( transformer.getRowsIn() == 1 )
				{
					possibleRootRowTables.add( att.getDataTable() );
				}
				else
				{
					//TODO: Only consider using a table as the rootRowTable if that table has the same number of rows as transformer.rowsIn
					//ie. generalise the == 1 case above to other numbers.
					possibleRootRowTables.addAll( att.getDataTable().getAncestorTables() );
				}
				
				for( DataTable rootRowTable : possibleRootRowTables )
				{
					ArrayList<Att> inputAtts = new ArrayList<>();
					inputAtts.add(att);
					Processor newProcessor = new Processor( transformer, inputAtts, rootRowTable );
					if( !previousProcessors.contains(newProcessor) )
					{
						bestProcessors.add( newProcessor );
						transformersAddedForAttCount++;
					}
				}
			}
			
			//If we already added all the processors for this att, do an extra att instead.
			if( transformersAddedForAttCount == 0 && transformersForThisAtt != 0){
				attIdx--;
			}
			
			System.out.println(transformersAddedForAttCount+" new transformers added for att: "+att.getDbColumnNameNoQuotes());
		}
		
		System.out.println("Found a total of "+bestProcessors.size()+" possible processors that could be applied.");
		
		return bestProcessors;
	}
}