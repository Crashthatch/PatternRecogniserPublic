package models;

import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.List;

public class MostCommonValueModel extends Model{
	private String value;

	public MostCommonValueModel(String value) {
		this.value = value;
	}

	@Override
	public String predictForRow(List<String> inputRow) {
		return value;
	}

	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		
		ArrayList<String> predictions = new ArrayList<>();
		for( int row=0; row < testTable.rowKeySet().size(); row++ )
		{
			predictions.add( value );
		}
		
		return predictions;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName()+"-"+value;
	}



}
