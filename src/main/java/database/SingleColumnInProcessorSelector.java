package database;

import com.google.common.collect.Sets;

import java.sql.SQLException;
import java.util.*;

public class SingleColumnInProcessorSelector extends ProcessorSelector{


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
			
			
			if( transformer.getColsIn() == 1 )
			{
				int colsIn = 1;
				
				
				//Create the possible input att combinations of the number of cols the transformer takes.
				ArrayList<Set<Att>> argumentSets = new ArrayList<>();  
				for( int argument=1; argument <= colsIn; argument++ )
				{
					argumentSets.add(inputCandidateAtts);
				}
				
				Set<List<Att>> inputCombinations = Sets.cartesianProduct(argumentSets); 
				
				for( List<Att> inputAtts : inputCombinations )
				{					
					HashSet<DataTable> possibleRootRowTables = new HashSet<>();
					//If the transformer only takes 1 row, then the only valid RootRowTable is the table containing the att. Choosing a rootRowTable from "higher up" groups many rows to a single transformer and means that the number of rows per transformer will be > 1.
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