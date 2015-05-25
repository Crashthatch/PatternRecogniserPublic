package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class andProcessor extends TransformationService {
	
	public andProcessor()
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
		boolean i, j;
		if( stringin.equals( "" ) || stringin.equals("0") || stringin.equals("0.0") )
		{
			i = false;
		}
		else
		{
			i = true;
		}
		if( stringin2.equals("") || stringin2.equals("0") || stringin2.equals("0.0") )
		{
			j = false;
		}
		else
		{
			j = true;
		}
		
		if( i && j )
			outTable.put(0, 0, "1" );
		else
			outTable.put(0, 0, "0" );
		
		return outTable;
	}

}
