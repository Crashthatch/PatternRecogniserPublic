package processors;

import com.csvreader.CsvReader;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import database.TransformationServiceReversible;
import database.UnsuitableTransformerException;

import java.io.IOException;

public class ReadCSVToTableAlwaysQuoted extends TransformationServiceReversible {

	public ReadCSVToTableAlwaysQuoted()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input) throws UnsuitableTransformerException {
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int rowindex = 0;
		
		//CSVReader reader = new CSVReader(new StringReader(stringin));
		//CsvReader r = new CsvReader(stringin);

		CsvReader reader = CsvReader.parse(stringin);
		reader.setTextQualifier('"');
	    try {
			while (reader.readRecord()) 
			{
				outTable.put(rowindex, 0, ""+rowindex );
				for( int colindex=0; colindex < reader.getColumnCount(); colindex++ )
				{
					String colValue = reader.get(colindex);
					if( reader.isQualified(colindex) ) {
						outTable.put(rowindex, colindex + 1, colValue);
					}
					else{
						throw new UnsuitableTransformerException("All cells must be quoted in ");
					}
				}
				
				
				rowindex++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outTable;
	}

	@Override
	public TransformationService getReverseTransformer() {
		return new ReverseCSVToTableAlwaysQuote();
	}
}
