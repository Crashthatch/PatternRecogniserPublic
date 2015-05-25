package junit.database.inout.endtoend.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class AptGetProgramNamesToList {

	/**
     * Takes several "sudo apt-get install X1 X2", "sudo apt-get install Y1 Y2" commands and should produce a list of all the programs installed.
     * ie. "X1 X2 Y1 Y2".
     *
     * Manages to do the "Split on \n" and then "Split At Character Idx" to get to the lists of programs,
     * but then doesn't try "SplitOn ToRows" (although it does try other splits).
     * Maybe re-order the processors that get tried, and make sure SplitOnSpace is in there?
     *
     *
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/apt-get program names/trainIn.dat", "testdata/apt-get program names/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/apt-get program names/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/apt-get program names/applyOut.dat"));
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

