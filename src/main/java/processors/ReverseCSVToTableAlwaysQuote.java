package processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class ReverseCSVToTableAlwaysQuote extends TransformationServiceReversible {

	public ReverseCSVToTableAlwaysQuote()
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input) {
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0, 0);
		int rowindex = 0;

		//Should we sort the table on col1 (the index col) first?

		ArrayList<String> csvRows = new ArrayList<>();
		//Start from col1 to skip the index column.
		for (int rowIdx = 0; rowIdx < input.rowKeySet().size(); rowIdx++) {
			String csvRow = "";
			for (int colIdx = 1; colIdx < input.columnKeySet().size(); colIdx++) {
				String cellValue = input.get(rowIdx, colIdx);
				csvRow += "\""+cellValue+"\",";
			}
			csvRow = csvRow.substring(0, csvRow.length() - 1); //Remove final ,
			csvRows.add(csvRow);
		}

		outTable.put( 0, 0, StringUtils.join(csvRows, "\n") );

		return outTable;
	}

	@Override
	public TransformationService getReverseTransformer() {
		return new ReadCSVToTableAlwaysQuoted();
	}
}
