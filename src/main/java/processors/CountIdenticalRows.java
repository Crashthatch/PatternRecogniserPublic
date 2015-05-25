package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.util.ArrayList;
import java.util.HashMap;

public class CountIdenticalRows extends TransformationService {

	public CountIdenticalRows( )
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = Integer.MAX_VALUE;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		HashMap<ArrayList<String>, Integer> counts = new HashMap<>();
		
		for( int rowKey : input.rowKeySet() )
		{			
			ArrayList<String> countKey = new ArrayList<>();
			for( int colKey : input.columnKeySet() )
			{			
				countKey.add( input.get(rowKey, colKey) );
			}
			
			int countSoFar;
			if( counts.containsKey(countKey) )
			{
				countSoFar = counts.get(countKey);
			}
			else
			{
				countSoFar = 0;
			}
			
			counts.put(countKey, countSoFar+1);
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
