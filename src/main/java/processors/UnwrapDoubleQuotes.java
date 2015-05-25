package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServicePartialRowsReversible;
import database.TransformationServiceReversible;

public class UnwrapDoubleQuotes extends TransformationServicePartialRowsReversible {

	
	public UnwrapDoubleQuotes()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		if( stringin.startsWith("\"") && stringin.endsWith("\"")){
			outTable.put(0, 0, stringin.substring(1, stringin.length()-1));
		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new WrapDoubleQuotes();
	}
}
