package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class FilterFalsey extends TransformationService {

	public FilterFalsey()
 	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}
	
	
	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();

        String stringin = input.get(0, 0);
        if( !stringin.equals("") && !stringin.equals("0") && !stringin.equals("0.0") )
        {
            outTable.put(0, 0, "1");
        }

		return outTable;
	}
}
