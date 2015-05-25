package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class escapeSpacesAndSingleQuotes extends TransformationService {

	public escapeSpacesAndSingleQuotes()
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

        String escaped = stringin.replace(" ", "\\ " );
        escaped = escaped.replace("'", "\\'" );
		outTable.put(0, 0, escaped);
		
		return outTable;
	}

}
