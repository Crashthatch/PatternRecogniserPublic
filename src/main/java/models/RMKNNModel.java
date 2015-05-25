package models;

import com.google.common.collect.Table;
import com.rapidminer.example.ExampleSet;

import java.util.List;

public class RMKNNModel extends RMModel{

	public RMKNNModel(com.rapidminer.operator.Model nativeModel) {
		super(nativeModel);
	}

	@Override
	protected ExampleSet createExampleSet(Table<Integer, Integer, String> table)
	{
		return RMLearner.createNumericalRMExampleSetWithLabelAtt(table);
	}
	
	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		return super.predictForRows(testTable);
	}
	
}
