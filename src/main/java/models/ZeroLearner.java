package models;

import com.google.common.collect.Table;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;

import java.sql.SQLException;

public class ZeroLearner extends ModelLearner{

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {

		return new ZeroModel();	
	}

    public int getComplexityOrdering(){
        return 1;
    }

}
