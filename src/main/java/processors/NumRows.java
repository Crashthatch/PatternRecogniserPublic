package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class NumRows extends TransformationService {

	public NumRows()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		outTable.put(0, 0, ""+input.rowKeySet().size() );
		
		return outTable;
	}
}
