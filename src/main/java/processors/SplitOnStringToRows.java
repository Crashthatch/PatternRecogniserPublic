package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServiceReversible;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class SplitOnStringToRows extends TransformationServiceReversible {
	private final String split;

	public String getName() {
		return "SplitOnString"+StringEscapeUtils.escapeJava(split)+"ToRows";
	}

	public SplitOnStringToRows(String split)
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

        String[] strings = StringUtils.splitByWholeSeparatorPreserveAllTokens(stringin, split);
        //String[] strings = stringin.split(StringEscapeUtils.escapeJava(split), -1); //-1 means don't discard empty strings at the end (eg. if stringin ends with a delimiter)

		for (String string : strings)
		{
			outTable.put(indexcol, 0, ""+indexcol );
			outTable.put(indexcol, 1, string );
			
			indexcol++;
		}
		
		return outTable;
	}
	
	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new ReverseSplitOnStringToRows(split);
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
		SplitOnStringToRows other = (SplitOnStringToRows) obj;
		if (split == null) {
			if (other.split != null)
				return false;
		} else if (!split.equals(other.split))
			return false;
		return true;
	}
}
