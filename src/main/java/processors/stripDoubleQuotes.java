package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class stripDoubleQuotes extends TransformationService {

	
	public stripDoubleQuotes()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );
		String outString = stringin.replaceAll("\"","");
		outTable.put(0, 0, outString);
		
		return outTable;
	}
}