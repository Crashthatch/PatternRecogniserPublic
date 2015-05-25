package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class concatenate extends TransformationService {
	
	public concatenate()
	{
		colsIn = 2;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		String stringin2 = (String) input.get(0,1);
		
		outTable.put(0, 0, stringin+stringin2 );
		
		return outTable;
	}
}
