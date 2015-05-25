package models;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.AtomicLongMap;
import database.UnsuitableModelException;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChopEndsLearner extends ModelLearner{

    public int getComplexityOrdering(){
        return 40;
    }

    //Only uses one feature. Others will be ignored. Perhaps this should be metadata stored on the class so we don't waste time trying to learn from more than one att?
	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, UnsuitableModelException {

        AtomicLongMap<Integer> startIndexCounts = AtomicLongMap.create();
        AtomicLongMap<Integer> endIndexCounts = AtomicLongMap.create();

        if( inputTable.columnKeySet().size() < 2 ){
            throw new UnsuitableModelException("ChopEndsLearner requires a feature to extract from.");
        }

        for( int row=0; row < inputTable.rowKeySet().size(); row++){
            String featureRow = inputTable.get(row, 1);
            String labelRow = inputTable.get(row, 0);

            if( featureRow.contains(labelRow) && labelRow.length() > 0){
                Pattern p = Pattern.compile( Pattern.quote(labelRow) );
                Matcher m = p.matcher(featureRow);
                int readUpTo = 0; //Allows us to find overlapping matches. eg. match "aaa" in "aaaaaa" should return 4 matches, at indexes 0, 1, 2 and 3. Without this, find() finds the first one at index 0, and then starts looking after the end of that, so finds the second match at index 3.
                while( m.find(readUpTo) ) {
                    int startIndex = m.start();
                    int endIndex = -1 * (featureRow.length() - (startIndex + labelRow.length())); //Distance from end of needle to end of featureRow.
                    startIndexCounts.incrementAndGet(startIndex);
                    endIndexCounts.incrementAndGet(endIndex);
                    readUpTo = startIndex + 1;
                }
            }
        }

        if( startIndexCounts.size() == 0 || endIndexCounts.size() == 0){
            throw new UnsuitableModelException("Label never found within feature.");
        }

        //Find the most common startIndex and endIndex.
        Ordering<Integer> valueComparator = Ordering.natural().onResultOf(Functions.forMap(startIndexCounts.asMap()));
        int bestStartIndex = valueComparator.max(startIndexCounts.asMap().keySet());

        Ordering<Integer> endValueComparator = Ordering.natural().onResultOf(Functions.forMap(endIndexCounts.asMap()));
        int bestEndIndex = endValueComparator.max( endIndexCounts.asMap().keySet());

        if( startIndexCounts.get(bestStartIndex) == 1 || endIndexCounts.get(bestEndIndex) == 1){
            throw new UnsuitableModelException("Label never found within feature at the same offset.");
        }

		return new ChopEndsModel(bestStartIndex, bestEndIndex);
	}


}
