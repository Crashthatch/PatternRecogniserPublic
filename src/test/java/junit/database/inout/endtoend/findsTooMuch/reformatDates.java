package junit.database.inout.endtoend.findsTooMuch;

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

public class reformatDates {

	/**
	 * Need to be able to split on dashes to columns to make 2014-09-01 split into 3 columns for year, month and day, which can then be predicted.
     *
     * Fails due to primeFactors correctly creating the leaf of the output tree, but joining incorrectly to other leaves, causing some rows to be removed, and some rows to be duplicated.
     * Same problem as AddConstantPrefix7Rows test.
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/datereformat/trainIn.dat", "testdata/datereformat/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/datereformat/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/datereformat/applyOut.dat"));
        boolean foundIncorrectPrediction = false;
        boolean foundCorrectPrediction = false;
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
        assertTrue( foundCorrectPrediction );
        //assertTrue(!foundIncorrectPrediction);
	}
}

