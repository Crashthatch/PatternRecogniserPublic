package models;

import com.google.common.collect.Table;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;

import java.sql.SQLException;

public class BestColumnLearner extends ModelLearner{

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {

		//Choose the column that is most often equal to the label column.
		int columns = inputTable.columnKeySet().size();
		int rows = inputTable.rowKeySet().size();
		int bestColumn = 1;
		int bestScore = 0;
		for( int col=1; col < columns; col++ )
		{
			int colScore = 0;
			for( int row=0; row < rows; row++ )
			{
				if( inputTable.get(row, col).equals( inputTable.get(row,0) ) )
				{
					colScore++;
				}
			}
			
			if( colScore >= bestScore )
			{
				bestScore = colScore;
				bestColumn = col;
			}
			
		}
		
		return new BestColumnModel(bestColumn);
	}

    public int getComplexityOrdering(){
        return 20;
    }


}
