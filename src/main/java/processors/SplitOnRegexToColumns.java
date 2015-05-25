package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;

public class SplitOnRegexToColumns extends TransformationService {
	private String split;

    public String getName() {
        return "SplitOnRegex"+ StringEscapeUtils.escapeJava(split)+"ToColumns";
    }


	public SplitOnRegexToColumns( String split)
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = 1;
		this.split = split;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		int index = 0;
		
		String[] strings = stringin.split(split ); 

		for (String string : strings)
		{
			outTable.put(0, index, string );
			
			index++;
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
		SplitOnRegexToColumns other = (SplitOnRegexToColumns) obj;
		if (split == null) {
			if (other.split != null)
				return false;
		} else if (!split.equals(other.split))
			return false;
		return true;
	}

}
