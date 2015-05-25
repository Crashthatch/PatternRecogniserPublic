package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.util.Map;

public class appearsInList extends TransformationService {


	public appearsInList( )
	{
		colsIn = 2;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String needle = input.get(0, 0);
		
		Map<Integer, String> haystack = input.column(1);
		
		if( haystack.containsValue(needle))
		{
			outTable.put(0, 0, "1");
			return outTable;
		}

		outTable.put(0, 0, "0");
		return outTable;
	}
}
