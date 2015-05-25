package models;

import com.google.common.collect.Table;
import com.rapidminer.example.ExampleSet;

public class RMLinearRegressionModel extends RMModel{

	public RMLinearRegressionModel(com.rapidminer.operator.Model nativeModel) {
		super(nativeModel);
	}

	protected ExampleSet createExampleSet( Table<Integer, Integer, String> table )
	{
		return RMLearner.createNumericalRMExampleSetWithLabelAtt(table);
	}

}
