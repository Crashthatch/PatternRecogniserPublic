package models;

import com.google.common.collect.Table;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;

import java.util.ArrayList;
import java.util.List;

public abstract class RMModel extends Model{
	protected com.rapidminer.operator.Model nativeModel;

	public RMModel(com.rapidminer.operator.Model nativeModel) {
		this.nativeModel = nativeModel;
	}
	
	@Override
	public String predictForRow(List<String> inputRow) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		//Create the RapidMiner ExampleTable.
		ExampleSet exampleSet = createExampleSet(testTable);
		
		ArrayList<String> predictions = new ArrayList<>();
		try{
			ExampleSet predictionsExampleSet = nativeModel.apply(exampleSet);
			Attribute predictedAttribute = predictionsExampleSet.getAttributes().getPredictedLabel();
			for( int row=0; row < predictionsExampleSet.size(); row++ )
			{
				predictions.add( predictionsExampleSet.getExample(row).getValueAsString(predictedAttribute) );
			}
		}
		catch( OperatorException e )
		{
			e.printStackTrace();
		}
		
		return predictions;
		
	}
	
	
	protected abstract ExampleSet createExampleSet(Table<Integer, Integer, String> testTable);

	@Override
	public String toString()
	{
		return nativeModel.toString();
	}

	



}
