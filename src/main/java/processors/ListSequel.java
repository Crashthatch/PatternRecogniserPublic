package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class ListSequel extends TransformationService {

	public ListSequel( )
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		
		int inputRows = input.rowKeySet().size();

		//Copy from input table to output table, 2 columns. 
		for( int rowKey=0; rowKey < inputRows-1; rowKey++ )
		{			
			outTable.put( rowKey, 0, input.get(rowKey, 0) );
			outTable.put( rowKey, 1, input.get(rowKey+1, 0) );
		}
		
		return outTable;
	}
}
