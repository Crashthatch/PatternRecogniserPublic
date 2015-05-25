package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class ListNeighbours2 extends TransformationService {

	public ListNeighbours2( )
	{
		colsIn = 1;
		colsOut = 5;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		
		int inputRows = input.rowKeySet().size();

		//Copy from input table to output table at offset -2, -1, 0 (direct copy), +1 and +2.
		for( int rowKey=2; rowKey < inputRows-2; rowKey++ )
		{			
			for( int colKey = -2; colKey <= 2; colKey++ )
			{				
				outTable.put( rowKey-2, colKey+2, input.get(rowKey + colKey, 0) );
			}
		}
		
		return outTable;
	}
}
