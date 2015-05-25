package models;

import com.google.common.collect.Table;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;

import java.sql.SQLException;
import java.util.HashMap;

public class MostCommonValueLearner extends ModelLearner{

    public int getComplexityOrdering(){
        return 10;
    }

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {

		//Choose the column that is most often equal to the label column.
		int columns = inputTable.columnKeySet().size();
		int rows = inputTable.rowKeySet().size();

		//Count how many times each value appears.
		HashMap<String, Integer> valueCounts = new HashMap<>(); 
		for( int row=0; row < rows; row++ )
		{
			String stringin = inputTable.get(row,0);
			
			Integer count = valueCounts.get( stringin );
			if( count == null )
				valueCounts.put(stringin, 1);
			else
				valueCounts.put( stringin, count + 1);
		}
		
		// Find the key with the highest count.
		String highestValue = "";
		int highestCount = 0;
		for( String value : valueCounts.keySet() )
		{
			int count = valueCounts.get(value);
			if( count > highestCount )
			{
				highestCount = count;
				highestValue = value;
			}
		}
		
		return new MostCommonValueModel(highestValue);
	}


}
