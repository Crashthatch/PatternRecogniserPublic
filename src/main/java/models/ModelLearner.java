package models;

import com.google.common.collect.Table;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import database.UnsuitableModelException;

import java.sql.SQLException;
import java.text.ParseException;

public abstract class ModelLearner {

	public abstract Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws Exception, SQLException, OperatorCreationException, OperatorException, ParseException, UnsuitableModelException;

    public abstract int getComplexityOrdering();

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return true;
	}
	
	public int hashCode() {
		final int prime = 47;
		int result = 1;
		result = prime * result
				+ ((getClass() == null) ? 0 : getClass().hashCode());
		return result;
	}
}