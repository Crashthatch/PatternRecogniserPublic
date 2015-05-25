package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class rownumRepeat extends TransformationService {
	private int repeat;
	
	public String getName()
	{
		return this.getClass().getSimpleName()+"-"+repeat;
	}
	
	public rownumRepeat(int repeat)
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		for( int i : input.rowKeySet() )
		{
			outTable.put(i, 0, ""+input.get(i, 0) );
			outTable.put(i, 1, ""+(i%repeat) );
		}
		
		return outTable;
	}
}
