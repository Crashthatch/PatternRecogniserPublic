package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.util.HashSet;
import java.util.Map;

public class removeDuplicateRows extends TransformationService {

	public removeDuplicateRows( )
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = Integer.MAX_VALUE;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		HashSet<Map<Integer, String>> alreadySeen = new HashSet<>();
		int outRowKey = 0;
		
		for( int rowKey : input.rowKeySet() )
		{		
			Map<Integer, String> row = input.row(rowKey);
			if( !alreadySeen.contains(row) )
			{
				alreadySeen.add(row);
				for( int colKey=0; colKey < input.columnKeySet().size(); colKey++)
				{
					outTable.put(outRowKey, colKey, input.get(rowKey, colKey));
				}
				outRowKey++;
			}
		}

		return outTable;
	}
}
