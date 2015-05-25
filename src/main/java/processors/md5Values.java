package processors;

import com.google.common.base.Charsets;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import database.TransformationService;

public class md5Values extends TransformationService {
	
	public md5Values()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String allRowsAsString = "";
		for( int i=0; i<input.rowKeySet().size(); i++ )
		{
			String stringin = (String) input.get(i,0);
			allRowsAsString += stringin;
		}
		
		//System.out.println(allRowsAsString);
		Hasher hasher = Hashing.md5().newHasher();
		hasher.putString(allRowsAsString, Charsets.UTF_8);
		String md5 = hasher.hash().toString();
		
		outTable.put(0,0,""+md5);
		
		return outTable;
	}
}
