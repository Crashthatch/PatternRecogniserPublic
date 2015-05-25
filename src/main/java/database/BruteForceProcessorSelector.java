package database;

import com.google.common.collect.Sets;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.*;

public class BruteForceProcessorSelector extends ProcessorSelector{


	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph )
	{
		return getBestProcessors(attGraph, getAllTransformers() );
	}

	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph, Collection<TransformationService> transformers )
	{
		//Get all atts from the graph.
		Collection<Att> atts = attGraph.getNonDuplicateAtts();
		Collection<Processor> previousProcessors = attGraph.getAllFinishedProcessors();
		Att rootAtt = attGraph.getRootAtt();
		
		
		//Remove atts where the generator of that att failed, or the att somehow else ended up with no rows.
		Collection<Att> attsWithAtLeastOneRow = new ArrayList<>();
		for( Att att : atts )
		{
			try {
				if( att.getNotNullRowsInTable() > 0 )
				{
					attsWithAtLeastOneRow.add(att);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
		Set<Att> inputCandidateAtts = new HashSet<>(attsWithAtLeastOneRow);
		inputCandidateAtts.remove(rootAtt);
		
		

		
		
		ArrayList<Processor> bestProcessors = new ArrayList<>();
		
		System.out.println("Found "+inputCandidateAtts.size()+" input att candidates.");
		System.out.println("Found "+transformers.size()+" transformers.");
		
		
		for( TransformationService transformer : transformers )
		{			
			//Transformer does not look at the input, (eg. constant outputters) just feed in the root attribute.
			if(transformer.getColsIn() == 0)
			{
				ArrayList<Att> inputAtts = new ArrayList<>();
				inputAtts.add(rootAtt);
				Processor newProcessor = new Processor( transformer, inputAtts, rootAtt.getDataTable() );
				
				if( !previousProcessors.contains(newProcessor) )
				{
					bestProcessors.add( newProcessor );
				}
				
			}
			
			
			if( transformer.getColsIn() >= 1 )
			{
				
				//For transformers that take any number of columns as inputs, give them 2 for now. Really we want the powerset of the inputCandidateAtts, but that's HUGE so we'll stick with 2 for now. 
				//TODO: Increase this / do something smarter.
				int colsIn = transformer.getColsIn();
				if( transformer.getColsIn() == Integer.MAX_VALUE )
					colsIn = 2;
				
				
				//Create the possible input att combinations of the number of cols the transformer takes.
				ArrayList<Set<Att>> argumentSets = new ArrayList<>();  
				for( int argument=1; argument <= colsIn; argument++ )
				{
					argumentSets.add(inputCandidateAtts);
				}
				
				Set<List<Att>> inputCombinations = Sets.cartesianProduct(argumentSets); 
				
				//Do not apply transformers to constant attributes by themselves: Only combine a constant with a non-constant attribute. We don't need to do modulus(2,1) on every data set ever. //TODO: Reconsider this? Might need some way to come up with a constant that isn't in the original constants. eg. there is no constantCreator-1500 but we still want to be able to recognise patterns that involve 1500 in some way.
				//Filter out inputCombinations that contain all constant-atts.
				Set<List<Att>> nonConstantInputCombinations = new HashSet<>();
				for( List<Att> inputAtts : inputCombinations )
				{
					boolean foundANonConstant = false;
					for( Att inputAtt : inputAtts )
					{
						if( inputAtt.getGenerator().getTransformer().getColsIn() > 0 || inputAtt.getGenerator().getTransformer().getClass() == getInputFromFile.class )
						{
							foundANonConstant = true;
							break;
						}
					}
					if( foundANonConstant )
					{
						nonConstantInputCombinations.add( inputAtts );
					}
				}
				
				//OPTIMIZATION: Could we filter out inputAtts where 2 of the atts are lists, but one of them is not an ancestor of the other in the DataTableGraph?
				//eg. t0 has 1 row (root table). t0 is a parent of t1 and t2, which both contain different lists.
				//    is it ever valuable to create an inputAttCombination which contains atts from both "t1" and "t2", with a rootRowTable of t0?
				//    Will essentially do a X-product on t1 and t2, so if both lists contain 100 rows, the resulting inputTable will contain 10,000 rows, the majority of which is redundant.
				// It IS useful to be able to have t1 and t2, where either t1 or t2 is the rootRow att (for processors like "is this row from t1 in the list from t2?"), but that only does 1x100 = 100 rows per transformers (and there will be a transformer for each of the 100 rows in t1).
				// It IS useful to have t2 and t3 with a rootRowTable of t2 or t3, where t3 is a DataTable descendant of t2: This is just lists of lists. Does rootRowAtt of t0 make sense in this situation?
				// Processor that maybe needs this: Filter to elements from t1 that also appear in t2? Could be solved using rootRowAtt t1, and having a different transformer to return a 1 or null for each t1 row? 
				// Are there other processors that NEED a cross-product to work? ie. can't be made to work with a list and a single item from list2 as inputs to each transformer.
				
				
				for( List<Att> inputAtts : nonConstantInputCombinations )
				{					
					HashSet<DataTable> possibleRootRowTables = new HashSet<>();
					//If the transformer only takes 1 row, then the only valid RootRowTable is the table containing the att. Choosing a rootRowTable from "higher up" groups many rows to a single transformer and means that the number of rows per transformer will be > 1, and hence not "fit" into the transformer's input (which only takes 1 row).
					//TODO: Can # of rows in a table for a given rootRowTable be calculated without actually doing the join? Would mean we can give transformers that ask for specific numbers of rows the correct inputs & rootRows, instead of brute-force trying them all.
					if( transformer.getRowsIn() == 1 )
					{
						for( Att inputAtt : inputAtts )
						{
							possibleRootRowTables.add( inputAtt.getDataTable() );
						}
					}
					else
					{						
						//TODO: Only consider using a table as the rootRowTable if that table has the same number of rows as transformer.rowsIn
						//ie. generalise the == 1 case above to other numbers.
						for( Att inputAtt : inputAtts )
						{
							possibleRootRowTables.addAll( inputAtt.getDataTable().getAncestorTables() );
						}
					}
					
					for( DataTable rootRowTable : possibleRootRowTables )
					{
						Processor newProcessor = new Processor( transformer, inputAtts, rootRowTable );
						if( !previousProcessors.contains(newProcessor) )
						{
							bestProcessors.add( newProcessor );
						}
					}
				}
			}
		}
		
		System.out.println("Found a total of "+bestProcessors.size()+" possible processors that could be applied.");
		
		return bestProcessors;
		
	}
	
	

}