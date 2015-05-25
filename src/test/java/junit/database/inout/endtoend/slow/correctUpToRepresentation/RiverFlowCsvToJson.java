package junit.database.inout.endtoend.slow.correctUpToRepresentation;

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

public class RiverFlowCsvToJson {

	/**
     * Finds the "correct" prediction, and also others that are found to be "incorrect".
     * Some are incorrect because the number format does not match. eg. predicted "1.42" instead of "1.420"
     *
     * Was also failing when UnwrapDoubleQuotes was set to be fully reversible. Was predicting meanDailyFlow: "1961-05-01" (or other date).
     * WrapDoubleQuotes was returning NULL for those entries which were not wrapped in double quotes (eg. the value of "meanDailyFlow")
     * So a predictor was learned that could correctly predict those for which it returned correctly (ie. the 6 rows that were the values of "date").
     * But then the reversed WrapDoubleQuotes operator chooses a RRT which causes 12 outputs, to match what happened in the forward output-tree, and the predicted 6 rows get duplicated (because it just chose a RRT that gave the correct # of inputs (6) and outputs (12) instead of considering where the extra values should come from).
     * These extra values then get incorporated as values for the "date" field.
     *
     * The blind-multiplication of the "date" could be removed by an "Equal Up To X" operator which would identify that the 12-valued-duplicated-dates att was not equal to the equivalent att from the output tree.
     * I have also stopped UnwrapDoubleQuotes being classed as a TransformationServiceReversible, because it throws away some data (rows without quotes) so can't full recreate its original input.
     * How do we tackle recombining partialRowsReversible back together? eg. If transformer transforms rows 1,3,5,7,9,11 and leaves the others as NULL, how do we prevent rows that get created incorrectly (ie. the duplicate rows) from being counted as "correct"?
     *
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/river flow CSV to JSON/trainIn.dat", "testdata/river flow CSV to JSON/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/river flow CSV to JSON/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/river flow CSV to JSON/applyOut.dat"));
        boolean foundCorrectPrediction = false;
        boolean foundIncorrectPrediction = false;
        for( String prediction : predictions ){
            if( JSONValue.parse(expectedValue).equals(JSONValue.parse(prediction)) ){
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

