package models;

import com.google.common.collect.Table;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.NominalMapping;
import com.rapidminer.example.table.PolynominalMapping;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.learner.AbstractLearner;
import com.rapidminer.tools.OperatorService;

import java.sql.SQLException;
import java.util.ArrayList;

public class RMKNNNominalLearner extends RMLearner{

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {
		
		//Create empty Mappings. The mappings will be filled during ExampleSet creation.
		int columns = inputTable.columnKeySet().size();
		ArrayList<NominalMapping> mappings = new ArrayList<>();
		for( int col = 0; col < columns; col++ )
		{
			mappings.add( new PolynominalMapping() );
		}
		
		ExampleSet exampleSet = createNominalRMExampleSetWithLabelAtt(inputTable, mappings);
		
		//Learn and create the "Model" object from the native model.
		AbstractLearner learner = OperatorService.createOperator( com.rapidminer.operator.learner.lazy.KNNLearner.class );
		com.rapidminer.operator.Model nativeModel = learner.learn(exampleSet);
		
		return new RMKNNNominalModel(nativeModel, mappings);		
	}

    public int getComplexityOrdering(){
        return 210;
    }

}