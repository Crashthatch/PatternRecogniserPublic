package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;

public class SplitOnRegexToRows extends TransformationService {
	private final String split;

	public String getName() {
		return "SplitOnRegex"+StringEscapeUtils.escapeJava(split)+"ToRows";
	}

	public SplitOnRegexToRows( String split)
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
		this.split = split;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int indexcol = 0;
		
		String[] strings = stringin.split(split, -1); //-1 means don't discard empty strings at the end (eg. if stringin ends with a delimiter)

		for (String string : strings)
		{
			outTable.put(indexcol, 0, ""+indexcol );
			outTable.put(indexcol, 1, string );
			
			indexcol++;
		}
		
		return outTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((split == null) ? 0 : split.hashCode());
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
		SplitOnRegexToRows other = (SplitOnRegexToRows) obj;
		if (split == null) {
			if (other.split != null)
				return false;
		} else if (!split.equals(other.split))
			return false;
		return true;
	}
}
