package processors;

import com.csvreader.CsvReader;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import database.TransformationServiceReversible;
import database.UnsuitableTransformerException;

import java.io.IOException;

public class ReadCSVToTableQuoteOnlyIfNeeded extends TransformationServiceReversible {

	public ReadCSVToTableQuoteOnlyIfNeeded()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
		throws UnsuitableTransformerException
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int rowindex = 0;
		
		//CSVReader reader = new CSVReader(new StringReader(stringin));
		CsvReader reader = CsvReader.parse(stringin);
		reader.setTrimWhitespace(false);
	    try {
			while (reader.readRecord()) 
			{
				outTable.put(rowindex, 0, ""+rowindex );
				for( int colindex=0; colindex < reader.getColumnCount(); colindex++ )
				{
					String colValue = reader.get(colindex);
					if( reader.isQualified(colindex) ){
						if( !colValue.contains(",") && !colValue.contains("\n") && !colValue.contains("\"")){
							//Quoting was unnecessary.
							//Alternative to throwing this is to make this not reversible and have a separate reversible transformer.
							throw new UnsuitableTransformerException("Unnecessary Quoting detected. Rely on ReadCSVToTableAlwaysQuoted instead to make reversible?");
						}
					}
					else{
						//If it is NOT qualified but should be, then we also won't be able to reverse.
						if( colValue.contains(",") || colValue.contains("\n") || colValue.contains("\"")){
							throw new UnsuitableTransformerException("Found a field that was not quoted but should have been.");
						}
					}
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

	@Override
	public TransformationService getReverseTransformer() {
		return new ReverseCSVToTableQuoteIfNeeded();
	}
}
