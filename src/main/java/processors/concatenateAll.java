package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class concatenateAll extends TransformationService {

	public concatenateAll()
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
        StringBuilder strBuilder = new StringBuilder();
        for( int i=0; i < input.columnKeySet().size(); i++ ){
            strBuilder.append( input.get(0,i));
        }
		
		outTable.put(0, 0, strBuilder.toString() );
		
		return outTable;
	}
}
