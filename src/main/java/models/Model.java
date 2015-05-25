package models;

import com.google.common.collect.Table;

import java.util.List;

public abstract class Model {

	public abstract String predictForRow( List<String> inputRow );

	public abstract List<String> predictForRows(Table<Integer, Integer, String> testTable);
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}
	
}
