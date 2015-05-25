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

public class LsFilenamesToMvIrregularInTrain {

	/**
     * Based on User Data from extractbyexample.com.
     *
     * Extract from the output of "ls -l", and put the filenames into "mv FILENAME.mp4 TRANSFORMED_FILENAME.mp4".
     * The original user data had one line with an additonal .[VTV] at the end before the mp4, but no equivalent .[VTV] example in the training data (so it's ambiguous what should be done with it), so I added an example to the training data in this test.
     * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/filenames from ls to mv - irregular in train/trainIn.dat", "testdata/filenames from ls to mv - irregular in train/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/filenames from ls to mv - irregular in train/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/filenames from ls to mv - irregular in train/applyOut.dat"));
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

