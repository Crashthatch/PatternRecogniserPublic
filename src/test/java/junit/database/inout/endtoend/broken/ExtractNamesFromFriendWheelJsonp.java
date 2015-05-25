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

public class ExtractNamesFromFriendWheelJsonp {

	/**
     * Must unwrap the jsonp wrapper, then decode a json map of which "nodes" is a json-list of maps which must be decoded to get the "name" property of each "node".
     * * ie. jsonpwrapper({..., nodes: [{name: Bob, ...}, {name: Bill}, ...] }) -> Bob\nBill\n...
     * Fails - Unwraps the json and does the first jsonDecodeMap, but then the column is not interesting enough to run jsonDecodeList on:
     * 142th most interesting 1.5977611940298506: `t0_2`.`131` JsonDecodeMapManyColsOut-1(UnwrapOuterBrackets(getInputFromFile())) AttFeatureSet: [ 1 1 1 0 3 7 JSON false false 8 1.0 106 8 0.19776119402985076 0 1607 1607.0 1 false 1 0.006222775357809583 0 0 0 0 ]
     *
     * Succeeds if we add *100 to "interesting += 100*features.getBestAimForScore();"
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/names from friendwheel jsonp/trainIn.dat", "testdata/names from friendwheel jsonp/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/names from friendwheel jsonp/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/names from friendwheel jsonp/applyOut.dat"));
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

