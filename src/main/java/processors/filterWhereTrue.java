package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class filterWhereTrue extends TransformationService {

	public filterWhereTrue( )
 	{
		colsIn = Integer.MAX_VALUE;
		colsOut = Integer.MAX_VALUE;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	//See Also: Processor filterFalsey which takes 1x1 cell, and either returns "1" or nothing (throws an exception?), so that when that output column is used as root-row-att
	// then only the rows that are "1" are returned. Saves copying the other input atts like this processor does. 
	//	--It's not this simple: The useful part of filters is to filter one column based on another. 
	//    I think there are 3 ways to do filtering:
	//	  1) Specify both the filter column AND the input column as inputs, then make the transformer ignore the "filter column". Requires all transformers to take an extra column.
	//	  2) As this transformer works: Accept many-rows, many-columns, and copy all but the filter att into an output table. Problem is that we could have one filterWhereTrue processor for every combination of inputs & filterAtt. At least as bad as option (3) in terms of number of times we have to filter, but at least it takes an extra round of processing (one for the filter, one for the transformers using the filtered table).
	//    3) Add a "filter att" property to all relationships, which gets included in the SQL query as a "WHERE filterAtt IS NOT NULL AND filterAtt IS NOT 0" or similar, but doesn't actually get selected and passed into the transformer. Blows up the number of of possible relationships by the number of atts (or at least the number of atts that have the same non-null values, could approach the powerset of rows)

    //Think we now do (1)?
	
	
	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		int outRowKey = 0;
		
		for( int rowKey : input.rowKeySet() )
		{		
			String stringin = input.get(rowKey, 0);
			//If the first column is true, add the other columns to the output.
			if( !stringin.equals("") && !stringin.equals("0") && !stringin.equals("0.0") ) 
			{
				for( int colKey=1; colKey < input.columnKeySet().size(); colKey++)
				{
					outTable.put(outRowKey, colKey-1, input.get(rowKey, colKey));
				}
				outRowKey++;
			}
		}

		return outTable;
	}
}
