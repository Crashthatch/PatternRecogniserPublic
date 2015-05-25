package models;

import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChopEndsModel extends Model{
	private int startIdx;
    private int endIdx;

	public ChopEndsModel(int startIdx, int endIdx) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
	}

	@Override
	public String predictForRow(List<String> inputRow) {
        //Take the feature-1 column (the 0th column is the label) and chop the ends off.
        String str = inputRow.get(1);
        int end = endIdx;
        if( end <= 0 ){
            end = str.length()+endIdx;
        }
        return str.substring(Math.min(startIdx, str.length()), Math.min(Math.max(startIdx, end), str.length()));
	}

	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		
		ArrayList<String> predictions = new ArrayList<>();
		for( int row=0; row < testTable.rowKeySet().size(); row++ )
		{
			predictions.add( predictForRow(Arrays.asList("LABEL",testTable.get(row,1))) );
		}
		
		return predictions;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName()+startIdx+","+endIdx;
	}



}
