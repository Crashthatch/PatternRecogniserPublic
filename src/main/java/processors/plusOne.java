package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class plusOne extends TransformationService {

	public plusOne()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int i = Integer.parseInt(stringin);
		
		outTable.put(0, 0, ""+(i+1) );
		
		return outTable;
	}

}
