package models;

import com.google.common.collect.Table;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.learner.AbstractLearner;
import com.rapidminer.tools.OperatorService;

import java.sql.SQLException;

public class RMKNNLearner extends RMLearner{

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {
		
		ExampleSet exampleSet = createNumericalRMExampleSetWithLabelAtt(inputTable);
		
		//Learn and create the "Model" object from the native model.
		AbstractLearner learner = OperatorService.createOperator( com.rapidminer.operator.learner.lazy.KNNLearner.class );
		com.rapidminer.operator.Model nativeModel = learner.learn(exampleSet);
		
		return new RMKNNModel(nativeModel);		
	}

    public int getComplexityOrdering(){
        return 200;
    }


}