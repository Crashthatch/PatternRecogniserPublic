package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class SpecialCharacters extends TransformationService {


	public SpecialCharacters()
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
		String specialsOnly = stringin.replaceAll("[0-9A-Za-z \n\r\t]","");
		outTable.put(0, 0, specialsOnly);
		
		return outTable;
	}
}
