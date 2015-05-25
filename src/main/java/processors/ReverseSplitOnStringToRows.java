package processors;

import com.google.common.base.Functions;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.primitives.Doubles;
import database.TransformationServiceReversible;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;

public class ReverseSplitOnStringToRows extends TransformationServiceReversible {
	private final String split;

	public String getName() {
		return "ReverseSplitOnString"+StringEscapeUtils.escapeJava(split)+"ToRows";
	}

	public ReverseSplitOnStringToRows(String split)
	{
		colsIn = 2;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
		this.split = split;
	}
	
	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String out = "";
		
		//Table has n rows. Create a permutation of 1..n by sorting by the "index-column" (cast to doubles).
		Map<Integer, String> indexCol = input.column(0);
		Ordering<Integer> valueComparator = Ordering.natural().onResultOf(Functions.compose(Doubles.stringConverter(), Functions.forMap(indexCol))); //Create comparator that will apply map to any inputs, then naturally sort the results. 
		
		for( int rowId : valueComparator.immutableSortedCopy(indexCol.keySet()) ){
			out += input.get(rowId, 1) + split;
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
		ReverseSplitOnStringToRows other = (ReverseSplitOnStringToRows) obj;
		if (split == null) {
			if (other.split != null)
				return false;
		} else if (!split.equals(other.split))
			return false;
		return true;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new SplitOnStringToRows(split);
	}
}
