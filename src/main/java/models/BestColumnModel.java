package models;

import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.List;

public class BestColumnModel extends Model{
	private int col;

	public BestColumnModel(int col) {
		this.col = col;
	}

	@Override
	public String predictForRow(List<String> inputRow) {
		return inputRow.get(col);
	}

	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		
		ArrayList<String> predictions = new ArrayList<>();
		for( int row=0; row < testTable.rowKeySet().size(); row++ )
		{
			predictions.add( testTable.get(row, col) );
		}
		
		return predictions;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName()+"-col"+col;
	}



}
