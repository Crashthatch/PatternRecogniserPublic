package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class SplitOnRegexesToTable extends TransformationService {
	private String rowDelimiter;
	private String colDelimiter;


	public SplitOnRegexesToTable( String rowDelimiter, String colDelimiter)
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
		this.rowDelimiter = rowDelimiter;
		this.colDelimiter = colDelimiter;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int rownum = 0;
		
		//Split to rows:
		String[] rows = stringin.split(rowDelimiter);

		for (String row : rows)
		{
			int colnum = 0;
			String[] colsOfRow = row.split(colDelimiter);
			for( String col : colsOfRow )
			{
				outTable.put(rownum, colnum, col );
				colnum++;
			}
			
			rownum++;
		}
		
		return outTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((colDelimiter == null) ? 0 : colDelimiter.hashCode());
		result = prime * result
				+ ((rowDelimiter == null) ? 0 : rowDelimiter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SplitOnRegexesToTable other = (SplitOnRegexesToTable) obj;
		if (colDelimiter == null) {
			if (other.colDelimiter != null)
				return false;
		} else if (!colDelimiter.equals(other.colDelimiter))
			return false;
		if (rowDelimiter == null) {
			if (other.rowDelimiter != null)
				return false;
		} else if (!rowDelimiter.equals(other.rowDelimiter))
			return false;
		return true;
	}
}
