package junit.database.inout.endtoend;

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

public class RemoveEmptyLinesFromJavaSetters {

    /**
     * Fails because Estimating the joined size doesn't estimate correctly for attributes with null rows.
     * Correctly splits on \n to rows, then does "firstCharacter" and uses "bestColumnLearner" to return the value of the rows output by SplitOnRegex filtered by (RRA) firstCharacter(splitOnRegex)) (filters from 19 rows down to 15 rows).
     * But then skips creating the forward processor because the estimate takes into account only 1 table (t2), and no filtering on RRA or any input is applied.
     * TO FIX: Improve the Joined Row Estimator.
     *
     * Relevant logfile output happens after 2 rounds of processing:
     * modelApplier-BestColumnLearner(firstCharacter(SplitOnRegex\nToRows-1(getInputFromFile())),SplitOnRegex\nToRows-1(getInputFromFile()))
     * 1 possibleRRTs for SplitOnRegex\nToRows with input Atts t2_1.19, t2_1.18
     * Trying RRT t0...Estimated size of joined inputs (19) did not match old output table size (15).
     * No versions of the output-tree's root were created. Creating partial tree dot. Output-tree fileProcessor output att:
     * @throws SQLException
     * @throws IOException
     */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/setterMethods2.java", "testdata/setterMethods2EmptyLinesRemoved.java");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/setterMethods1.java", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue;
		expectedValue = FileUtils.readFileToString(new File("testdata/setterMethods1EmptyLinesRemoved.java"));
		boolean foundIncorrectPrediction = false;
		boolean foundCorrectPrediction = false;
		for( String prediction : predictions ){
			if( prediction.equals(expectedValue) ){
				System.out.println("Correct Prediction!");
				foundCorrectPrediction = true;
			}
			else{
				System.out.println("Incorrect Prediction:");
				System.out.println(prediction);
				System.out.println("Expected:");
				System.out.println(expectedValue);
				foundIncorrectPrediction = true;
			}
		}
		assertTrue( foundCorrectPrediction );
		assertTrue(!foundIncorrectPrediction);
	}
}