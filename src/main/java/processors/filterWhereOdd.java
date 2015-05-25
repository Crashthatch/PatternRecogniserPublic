package processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;

public class filterWhereOdd extends TransformationService {

	public filterWhereOdd()
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
		int intin = Integer.parseInt(stringin);
		if( intin % 2 == 1 )
		{
			outTable.put(0, 0, stringin);
		}
		else{
			//Return nothing. So whenever this attribute is used as part of inputAttributes, the "AND tx_0.a IS NOT NULL" will filter out rows that don't match this filter.
			//outTable.put(0, 0, "");
		}

		return outTable;
	}
}
