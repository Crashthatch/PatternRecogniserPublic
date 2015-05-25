package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class total extends TransformationService {
	
	public total()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		int total = 0;
		for( int i : input.rowKeySet() )
		{
			String stringin = (String) input.get(i,0);
			int j = Integer.parseInt(stringin);
			total += j;
		}
		
		outTable.put(0,0,""+total);
		
		return outTable;
	}
}
