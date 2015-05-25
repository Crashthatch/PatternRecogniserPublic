package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class notProcessor extends TransformationService {
	
	public notProcessor()
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
		boolean i;
		if( stringin.equals( "" ) || stringin.equals("0") || stringin.equals("0.0") )
		{
			i = false;
		}
		else
		{
			i = true;
		}
		
		if( i )
			outTable.put(0, 0, "0" );
		else
			outTable.put(0, 0, "1" );
		
		return outTable;
	}

}
