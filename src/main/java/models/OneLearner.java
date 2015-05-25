package models;

import com.google.common.collect.Table;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;

import java.sql.SQLException;

public class OneLearner extends ModelLearner{

    public int getComplexityOrdering(){
        return 2;
    }

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {

		return new OneModel();	
	}


}
