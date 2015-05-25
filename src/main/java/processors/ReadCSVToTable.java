package processors;

import com.csvreader.CsvReader;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.io.IOException;

public class ReadCSVToTable extends TransformationService {

	public ReadCSVToTable()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int rowindex = 0;
		
		//CSVReader reader = new CSVReader(new StringReader(stringin));
		
		CsvReader reader = CsvReader.parse(stringin);
	    try {
			while (reader.readRecord()) 
			{
				outTable.put(rowindex, 0, ""+rowindex );
				for( int colindex=0; colindex < reader.getColumnCount(); colindex++ )
				{
					String colValue = reader.get(colindex);
					outTable.put(rowindex, colindex+1, colValue );
				}
				
				
				rowindex++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outTable;
	}
}
