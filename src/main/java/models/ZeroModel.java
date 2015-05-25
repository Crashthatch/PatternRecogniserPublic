package models;

import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.List;

public class ZeroModel extends Model{

	public ZeroModel() {
		
	}

	@Override
	public String predictForRow(List<String> inputRow) {
		return "0";
	}

	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		
		ArrayList<String> predictions = new ArrayList<>();
		for( int row=0; row < testTable.rowKeySet().size(); row++ )
		{
			predictions.add( "0" );
		}
		
		return predictions;
	}
	



}
