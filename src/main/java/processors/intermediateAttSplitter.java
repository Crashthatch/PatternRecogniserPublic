package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class intermediateAttSplitter extends TransformationService {	
	
	public intermediateAttSplitter()
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
		
		String[] colValues = stringin.split(":DELIMITER:", -1);
		
		int colIdx = 0;
		for( String colValue : colValues )
		{
			outTable.put(0, colIdx, colValue );
			colIdx++;
		}
		
		return outTable;
	}
}
