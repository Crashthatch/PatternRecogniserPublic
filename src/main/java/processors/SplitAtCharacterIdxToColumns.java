package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;

import java.util.ArrayList;

public class SplitAtCharacterIdxToColumns extends TransformationServiceReversible {
	private int splitIdx;


	public SplitAtCharacterIdxToColumns(int splitIdx)
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = 1;
		this.splitIdx = splitIdx;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = input.get(0,0);

        //If they passed in -ve indexes, count from the end of the string.
        int useSplitIdx = splitIdx;
        if( splitIdx < 0 ){
            useSplitIdx = stringin.length()+splitIdx;
        }
		
		ArrayList<String> strings = new ArrayList<>();
        strings.add(stringin.substring(0, useSplitIdx));
        strings.add(stringin.substring(useSplitIdx));

        int index = 0;
        for (String string : strings)
		{
			outTable.put(0, index, string );
			index++;
		}
		
		return outTable;
	}
	
	@Override
	public TransformationService getReverseTransformer() {
		return new concatenate();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + splitIdx;
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
		SplitAtCharacterIdxToColumns other = (SplitAtCharacterIdxToColumns) obj;
		if (splitIdx != other.splitIdx)
            return false;
		return true;
	}

}
