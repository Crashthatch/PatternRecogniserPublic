package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class AlphabetLetters extends TransformationService {

	
	public AlphabetLetters()
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
		String alphasOnly = stringin.replaceAll("[^A-Za-z]","");
		outTable.put(0, 0, alphasOnly);
		
		return outTable;
	}
}
