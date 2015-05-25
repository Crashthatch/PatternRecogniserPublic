package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class JoinWhereEqual extends TransformationService {

	public JoinWhereEqual( )
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = Integer.MAX_VALUE;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		int inputColumns = input.columnKeySet().size();
		int inputRows = input.rowKeySet().size();
		
		for( int rowKey = 0; rowKey < inputRows; rowKey++ )
		{			
			if( input.get(rowKey, 0).equals( input.get(rowKey, 1) ) ) //If the first 2 columns are equal, return the remaining columns.
			{
				for( int colKey=2; colKey < inputColumns; colKey++)
				{
					outTable.put(0, colKey-2, input.get(rowKey, colKey));
				}
				return outTable;
			}
		}

		return outTable;
	}
}
