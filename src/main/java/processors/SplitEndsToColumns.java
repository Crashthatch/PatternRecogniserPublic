package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;

import java.util.ArrayList;

public class SplitEndsToColumns extends TransformationServiceReversible {
	private int splitIdx;
    private int endSplitIdx;


	public SplitEndsToColumns(int splitIdx, int endSplitIdx)
	{
		colsIn = 1;
		colsOut = 3;
		rowsIn = 1;
		rowsOut = 1;
		this.splitIdx = splitIdx;
        this.endSplitIdx = endSplitIdx;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = input.get(0,0);
		int index = 0;

        //If they passed in -ve indexes, count from the end of the string.
        int useSplitIdx = splitIdx;
        int useEndSplitIdx = endSplitIdx;
        if( splitIdx < 0 ){
            useSplitIdx = stringin.length()+splitIdx;
        }
        if( endSplitIdx < 0 ){
            useEndSplitIdx = stringin.length()+endSplitIdx;
        }
		
		ArrayList<String> strings = new ArrayList<>();
        strings.add(stringin.substring(0, useSplitIdx));
        strings.add(stringin.substring(useSplitIdx, useEndSplitIdx));
        strings.add(stringin.substring(useEndSplitIdx));

        for (String string : strings)
		{
			outTable.put(0, index, string );
			index++;
		}
		
		return outTable;
	}
	
	@Override
	public TransformationService getReverseTransformer() {
		return new concatenateAll();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + splitIdx;
        result = prime * result + endSplitIdx;
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
		SplitEndsToColumns other = (SplitEndsToColumns) obj;
		if (splitIdx != other.splitIdx || endSplitIdx != other.endSplitIdx)
            return false;
		return true;
	}

}
