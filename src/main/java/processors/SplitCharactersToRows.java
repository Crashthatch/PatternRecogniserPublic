package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class SplitCharactersToRows extends TransformationService {
	
	public SplitCharactersToRows()
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int indexcol = 0;

		for (int i=0; i<stringin.length(); i++)
		{
			outTable.put(indexcol, 0, ""+indexcol );
			outTable.put(indexcol, 1, stringin.substring(i, i+1) );
			
			indexcol++;
		}
		
		return outTable;
	}
}
