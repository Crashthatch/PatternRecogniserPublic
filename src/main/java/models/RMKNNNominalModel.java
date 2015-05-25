package models;

import com.google.common.collect.Table;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.NominalMapping;

import java.util.ArrayList;
import java.util.List;

public class RMKNNNominalModel extends RMModel{
	private ArrayList<NominalMapping> mappings;

	public RMKNNNominalModel(com.rapidminer.operator.Model nativeModel, ArrayList<NominalMapping> mappings) {
		super(nativeModel);
		this.mappings = mappings;
	}

	@Override
	protected ExampleSet createExampleSet(Table<Integer, Integer, String> table)
	{
		//Use the mapping created during training to convert this test-table to an ExampleSet.
		return RMLearner.createNominalRMExampleSetWithLabelAtt(table, mappings);
	}
	
	
	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		return super.predictForRows(testTable);
	}


}
