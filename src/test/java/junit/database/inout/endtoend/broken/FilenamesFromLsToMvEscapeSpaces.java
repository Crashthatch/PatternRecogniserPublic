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

public class FilenamesFromLsToMvEscapeSpaces {

	/**
     * Based on User Data from extractbyexample.com.
     *
     * Had to add additional training data to stop the pattern "1,2,3" being learnt from the row numbers.
     * Haven't looked into why it's still failing.
     * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/filenames from ls to mv escape spaces/trainIn.dat", "testdata/filenames from ls to mv escape spaces/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/filenames from ls to mv escape spaces/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/filenames from ls to mv escape spaces/applyOut.dat"));
        boolean foundCorrectPrediction = false;
        boolean foundIncorrectPrediction = false;
        for( String prediction : predictions ){
            if( prediction.equals(expectedValue) ){
                foundCorrectPrediction = true;
                System.out.println("Correct Prediction!");
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