package junit.database.inout.endtoend.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class FilterDatesAndPutIntoSQL {

	/**
     * Filter to all rows that contain a Y, and then insert the date from those rows into INSERT INTO 'holidays' VALUES('<date>',1); SQL statements.
     *
     * Does actually succeed, but finds too much, and also finds more than 1000 possible routes through the graph, so makes that many predictions.
     * Can the number of different predictions be brought down somehow? Perhaps by keeping it as 2 trees or keeping the "4 different methods of predicting X" (X is some internal att) so that if all 4 methods predict the same value given some test-data, they can be collapsed and processors that take X as an input don't need to be replicated 4 times, once for each prediction.
     * See "Many Single Trees" optimization in green book.
     *
     * Fixed by
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/filter dates and put into SQL/trainIn.dat", "testdata/filter dates and put into SQL/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/filter dates and put into SQL/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/filter dates and put into SQL/applyOut.dat"));
        boolean foundIncorrectPrediction = false;
        boolean foundCorrectPrediction = false;
        for( String prediction : predictions ){
            if( prediction.equals(expectedValue) ){
                System.out.println("Correct Prediction!");
                foundCorrectPrediction = true;
            }
            else{
                System.out.println("Incorrect Prediction:");
                System.out.println(StringEscapeUtils.escapeJava(prediction));
                System.out.println("Expected: ");
                System.out.println(StringEscapeUtils.escapeJava(expectedValue));
                foundIncorrectPrediction = true;
            }
        }
        assertTrue(foundCorrectPrediction);
        assertTrue(!foundIncorrectPrediction);
	}
}

