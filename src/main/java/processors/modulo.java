package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class modulo extends TransformationService {
	
	public modulo()
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
		float i = Integer.parseInt(stringin);
		float j = Integer.parseInt(stringin2);
		
		outTable.put(0, 0, ""+(i%j) );
		
		return outTable;
	}

}
