package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class normalise extends TransformationService {

	public normalise()
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
		int total = 0;
		for( int i : input.rowKeySet() )
		{
			String stringin = (String) input.get(i,0);
			int j = Integer.parseInt(stringin);
			total += j;
		}
		
		for( int i : input.rowKeySet() )
		{
			String stringin = (String) input.get(i,0);
			int j = Integer.parseInt(stringin);
			float normed = ((float) j )/ total;
			
			outTable.put(i,0,""+normed);
		}
		
		return outTable;
	}
}
