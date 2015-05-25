package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class SplitCharactersToColumns extends TransformationService {
	
	public SplitCharactersToColumns()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int indexcol = 0;

		for (int i=0; i<stringin.length(); i++)
		{
			outTable.put(0, indexcol, stringin.substring(i, i+1) );
			
			indexcol++;
		}
		
		return outTable;
	}
}
