package junit.database.inout.endtoend.findsTooMuch;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class JsonToCSVBathLocationsNoHeaderRow {

	/**
     * Finds the correct predictor, but also finds ones that are specific to things in the training data.
     * eg. always predicts "-2.3" as the start of the latitude (because all the ones in the training data were).
     *
     * Could be addressed by identifying that the test-data contains values outside those it was trained on (ie. not between -2.3 and -2.39999 )
     * and prompting the user at runtime.
     *
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/json to csv - bath locations/trainIn.dat", "testdata/json to csv - bath locations/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/json to csv - bath locations/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/json to csv - bath locations/applyOut.dat"));
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
        //assertTrue(!foundIncorrectPrediction);
	}
}

