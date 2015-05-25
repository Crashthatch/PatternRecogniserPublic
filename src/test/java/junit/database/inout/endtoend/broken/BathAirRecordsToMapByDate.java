package junit.database.inout.endtoend.broken;

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

public class BathAirRecordsToMapByDate {

	/**
     * Real-life situation discovered while making AirLapse. Have to convert from a list of records, one per sensor-station,
     * to a single row per keyframe (time) that contains all readings for all sensor stations.
     *
     * Quite complex json that requires merging 2 records with the same value (the same time) into a single record, discarding some data, and
     *
     * Javascript function that performs this conversion is available here: https://github.com/Crashthatch/bathair/blob/b608e0df801569bdf5fd926bc7ab1da4c3b3434c/js/scripts.js#L157
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/bathair json to map by datetime/trainIn.dat", "testdata/bathair json to map by datetime/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/bathair json to map by datetime/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/bathair json to map by datetime/applyOut.dat"));
        boolean foundCorrectPrediction = false;
        boolean foundIncorrectPrediction = false;
        for( String prediction : predictions ){
            if( JSONValue.parse(prediction).equals(JSONValue.parse(expectedValue)) ){
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

