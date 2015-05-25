package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServiceReversible;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Random;

public class SplitOnStringToColumnsAndUnpredictable extends TransformationServiceReversible {
	private String split;

    public String getName() {
        return "SplitOnString"+ StringEscapeUtils.escapeJava(split)+"ToColumnsAndUnpredictable";
    }


	public SplitOnStringToColumnsAndUnpredictable(String split)
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

        String[] strings = StringUtils.splitByWholeSeparatorPreserveAllTokens(stringin, split);

		for (String string : strings)
		{
			outTable.put(0, index, string );
			
			index++;
		}
        Random random = new Random();
        outTable.put(0, index, ""+random.nextInt());
		
		return outTable;
	}
	
	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new ReverseSplitOnStringToColumns(split);
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
		SplitOnStringToColumnsAndUnpredictable other = (SplitOnStringToColumnsAndUnpredictable) obj;
		if (split == null) {
			if (other.split != null)
				return false;
		} else if (!split.equals(other.split))
			return false;
		return true;
	}

}
