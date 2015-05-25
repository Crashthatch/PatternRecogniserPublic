package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class rownum extends TransformationService {
	
	public rownum()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
        annotateExistingRows = true;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		for( int i=0; i< input.rowKeySet().size(); i++ )
		{
			outTable.put(i, 0, ""+i );
		}
		
		return outTable;
	}
}
