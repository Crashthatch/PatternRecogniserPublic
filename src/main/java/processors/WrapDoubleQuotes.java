package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;

public class WrapDoubleQuotes extends TransformationServiceReversible {

	
	public WrapDoubleQuotes()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		outTable.put(0, 0, "\""+input.get(0,0)+"\"");
		
		return outTable;
	}

	@Override
	public TransformationService getReverseTransformer() {
		return new UnwrapDoubleQuotes();
	}
}
