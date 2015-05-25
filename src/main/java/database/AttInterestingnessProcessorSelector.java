package database;

import com.google.common.collect.Sets;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.*;

public class AttInterestingnessProcessorSelector extends ProcessorSelector{
	private Map<Att, Double> interestingnessCache = new HashMap<>();


	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph )
	{
		return getBestProcessors(attGraph, getAllTransformers() );
	}
	
	private double getInterestingness( Att att )
	{
		if( interestingnessCache.get(att) != null )
			return interestingnessCache.get(att);
		
		String firstRow;
		try{
			firstRow = att.getFirstRow();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		
		double interesting = 0;
		//Att is interesting if it has more rows.
		if( att.getDataTable().getNumRows() > 1 )
		{
			interesting += 1;
		}
		if( att.getDataTable().getNumRows() >= 10 )
		{
			interesting += 0.5;
		}
		
		//Not constant
		if( att.getDataTable().getNumRows() > 1 )
		{
			try {
				if( att.getUniqueRowsInTable() > 1 )
				{
					interesting += 2;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//String representation of rows not equal to any of its parents.	
		for( Att parentAtt : att.getParentAtts() )
		{
			try {
				String parentFirstRow = parentAtt.getFirstRow();
				if( firstRow.equals(parentFirstRow) )
				{
					interesting -= 1;
					break;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		//Higher level is more interesting (generally). ie. everything else being equal, favour a breadth-first search of the tree over depth-first.
		interesting -= 0.1*att.getAncestorAtts().size();
		
		
		//Numeric (but not 1 or 0) is interesting.
		boolean isnumeric;
		try{
			Double.parseDouble(firstRow);
			isnumeric = true;
		}
		catch( NumberFormatException E )
		{
			isnumeric = false;
		}
		if( isnumeric && !firstRow.equals("1") && !firstRow.equals("0") )
			interesting += 1;
		
		//Can be used to predict other atts
		
		
		//Not as interesting if it or its parents can all be predicted.
		if( att.getBestPredictor() != null && att.getBestPredictor().getAccuracy() > 0.99 )
		{
			interesting -= 1;
		}
		for( Att parent : att.getParentAtts() )
		{
			if( parent.getBestPredictor() != null && parent.getBestPredictor().getAccuracy() > 0.99 )
			{
				interesting -= 1;
				break;
			}	
		}
		
		//Not very interesting if it's a constant attribute from a constantCreator.
		if( att.getGenerator().getTransformer().getColsIn() > 0 || att.getGenerator().getTransformer().getClass() == getInputFromFile.class )
		{
			interesting += 2;
		}
		
		//TODO: Attributes that are ALWAYS the same value, regardless of the input, are not interesting at all. Entropy?
		// eg. (X AND NOT X) is never going to be useful to predict or help predict another value because it's false for all datasets.
		// Perhaps try testing a subtree on some predefined datasets, including random data, etc. and if the att always appears to be the same, then don't create it.
		// Might be better to do this after the att is generated, and then remove / ignore it rather than trying to figure out if it's a tautology here?
		
		interestingnessCache.put( att, interesting );
		
		return interesting;
	}
	
	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph, Collection<TransformationService> transformers )
	{
		//Get all atts from the graph.
		Collection<Att> atts = attGraph.getNonDuplicateAtts();
		Collection<Processor> previousProcessors = attGraph.getAllProcessors();
		Att rootAtt = attGraph.getRootAtt();
		
		
		//Remove atts where the generator of that att failed, or the att somehow else ended up with no rows.
		List<Att> attsWithAtLeastOneRow = new ArrayList<>();
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
		
		//Set<Att> inputCandidateAtts = new ArrayList<>(attsWithAtLeastOneRow);
		attsWithAtLeastOneRow.remove(rootAtt);
		
		Set<Att> inputCandidateAtts = new HashSet<>(attsWithAtLeastOneRow);
		Set<Att> interestingAtts;
		if( attsWithAtLeastOneRow.size() > 10 )
		{
			class InterestingnessComparator implements Comparator<Att> {
			    @Override
			    public int compare(Att a1, Att a2) {
			        if( getInterestingness(a1) == getInterestingness(a2) )
			        	return 0;
			        else if( getInterestingness(a1) > getInterestingness(a2) )
			        	return -1;
			        else
			        	return 1;
			    }
			}
			
			Collections.sort(attsWithAtLeastOneRow, new InterestingnessComparator());
			for( Att att : attsWithAtLeastOneRow )
			{ 
				System.out.println(att);
				System.out.println(getInterestingness(att));
			}
			
			
			interestingAtts = new HashSet<>(attsWithAtLeastOneRow.subList(0, 3));

		}
		else
		{
			interestingAtts = new HashSet<>(attsWithAtLeastOneRow);
		}
			
		
		//TODO: If one att is identical to another att for every row in the training set, only apply further processors to one of them.
		
		
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
				
				
				//Create the possible input att combinations of the number of cols the transformer takes. Only allow one input to be "non-interesting" (This allows stuff like interesting MOD 2, but disallows stuff like 4 MOD 2, ie. 2 constant atts).
				Set<List<Att>> inputCombinations = new HashSet<>();
				for( int interestingArgument = 1; interestingArgument <= colsIn; interestingArgument++)
				{
					ArrayList<Set<Att>> argumentSets = new ArrayList<>();
					for( int argument=1; argument <= colsIn; argument++ )
					{
						if( argument == interestingArgument )
							argumentSets.add(inputCandidateAtts);
						else
							argumentSets.add(interestingAtts);
					}
					inputCombinations.addAll(Sets.cartesianProduct(argumentSets));
				}
				
				 
				
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
		
		//Collections.shuffle(bestProcessors);
		
		return bestProcessors;
		
	}
	
	

}