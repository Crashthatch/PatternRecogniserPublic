package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class isConstant extends TransformationService {
	
	public isConstant()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String firstStringin = (String) input.get(0,0);
		boolean constant = true;
		for( int i : input.rowKeySet() )
		{
			String stringin = (String) input.get(i,0);
			if( ! firstStringin.equals( stringin) )
			{
				constant = false;
				break;
			}
		}
		if( constant )
			outTable.put(0,0,"1");
		else
			outTable.put(0,0,"0");
		
		return outTable;
	}
}
