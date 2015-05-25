package junit.database.inout.endtoend.findsTooMuch;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class SecretarysTSVAddCommasNoTrailingTabs {

	/**
     * Takes a TSV (Tab separated) and converts it to a CSV (ie. replaces "\t" with ",\t" and \n with ",\n").
     * Easier than "SecretarysTSVAddCommas" because none of the lines end with \t.
     *
     * Finds too much due to learning that the names always begin with an "A".
     * Would be solved by choosing training data from the start AND the end of the file.
     *
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/secretarys tsv add commas - no trailing tabs/trainIn.dat", "testdata/secretarys tsv add commas - no trailing tabs/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/secretarys tsv add commas - no trailing tabs/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/secretarys tsv add commas - no trailing tabs/applyOut.dat"));
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

