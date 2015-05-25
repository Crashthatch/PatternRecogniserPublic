package junit.database.inout.endtoend.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class WordLengthWithOutputTrailingNewlines {

    /**
     * Training output contains trailing newlines.
     * The test output should create the output (integer lengths of words), with or without newlines at the end.
     *
     * Requires some kind of "equal up to whitespace" operator so that during training when it can predict "7\n5\n5\n...\n6\n6\n9" it knows that is the "same" (up to whitespace) as what we're aiming for.
     * Or perhaps it's confused by the NULLs in the leaf that the relationship tries to predict? So the relationship believes it cannot predict the value because it can only predict some rows? And it needs to add nulls at the end?
     * @throws SQLException
     */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/aWords.dat", "testdata/aWordsLength-trailingNewlines.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/aWords2.dat", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = "7\n5\n5\n5\n3\n4\n4\n5\n5\n5\n9\n6\n6\n9";
		boolean foundCorrectPrediction = false;
		boolean foundIncorrectPrediction = false;
		for( String prediction : predictions ){
			if( prediction.startsWith(expectedValue) ){
				foundCorrectPrediction = true;
				System.out.println("Correct Prediction!");
			}
			else{
				System.out.println("Incorrect Prediction:");
				System.out.println(StringEscapeUtils.escapeJava(prediction));
				foundIncorrectPrediction = true;
			}
		}
		assertTrue(foundCorrectPrediction);
		assertTrue(!foundIncorrectPrediction);
	}

}

