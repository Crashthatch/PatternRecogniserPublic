package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServiceReversible;
import org.apache.commons.lang.StringEscapeUtils;

public class ReverseSplitOnStringToColumns extends TransformationServiceReversible {
	private final String split;

	public String getName() {
		return "ReverseSplitOnString"+StringEscapeUtils.escapeJava(split)+"ToColumns";
	}

	public ReverseSplitOnStringToColumns(String split)
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		this.split = split;
	}
	
	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String out = "";
		
		for( int col = 0; col < input.columnKeySet().size(); col++){
			out += input.get(0, col) + split;
		}
		
		out = out.substring(0, out.length()-split.length());
		outTable.put(0,0,out);
		
		return outTable;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 37;
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
		ReverseSplitOnStringToColumns other = (ReverseSplitOnStringToColumns) obj;
		if (split == null) {
			if (other.split != null)
				return false;
		} else if (!split.equals(other.split))
			return false;
		return true;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new SplitOnStringToColumns(split);
	}
}
