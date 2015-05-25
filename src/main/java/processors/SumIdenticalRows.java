package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.util.ArrayList;
import java.util.HashMap;

public class SumIdenticalRows extends TransformationService {

	public SumIdenticalRows( )
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = Integer.MAX_VALUE;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		HashMap<ArrayList<String>, Float> counts = new HashMap<>();
		
		int inputRows = input.rowKeySet().size();
		int inputColumns = input.columnKeySet().size();
		
		for( int rowKey=0; rowKey < inputRows; rowKey++ )
		{			
			ArrayList<String> countKey = new ArrayList<>();
			for( int colKey = 0; colKey < inputColumns; colKey++ )
			{
				//Skip the first column.
				if( colKey == 0 )
					continue;
				
				countKey.add( input.get(rowKey, colKey) );
			}
			
			float countSoFar;
			if( counts.containsKey(countKey) )
			{
				countSoFar = counts.get(countKey);
			}
			else
			{
				countSoFar = 0;
			}
			
			float rowValue = Float.parseFloat(input.get(rowKey, 0));
			
			counts.put(countKey, countSoFar+rowValue);
		}
		
		int rowKey = 0;
		for( ArrayList<String> countKey : counts.keySet() )
		{
			outTable.put(rowKey, 0, ""+counts.get(countKey));
			int colKey = 1;
			for( String partialKey : countKey )
			{
				outTable.put(rowKey, colKey, partialKey);
				colKey++;
			}
			rowKey++;
		}

		return outTable;
	}
}
