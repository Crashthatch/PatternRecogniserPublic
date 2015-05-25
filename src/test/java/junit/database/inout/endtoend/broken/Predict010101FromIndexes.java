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

public class Predict010101FromIndexes {

    /**
     * Fails due to there being no "mod2" processor.
     * Instead, it finds another way (using the normalised stringlength and date-parts) that works for the training values, but produces the wrong predictions when we extrapolate to more training records.
     * To fix, need to find the simpler rule "mod2" rule first. Maybe start adding back the 2-att-in processors? Or add specific 1-in processors as use-cases are found (So here, add %2)?
     * @throws SQLException
     * @throws IOException
     */
	@Test public void test() throws Exception{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/123456.dat", "testdata/01010x6.dat");
        assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/12345678910.dat", tree);
		assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString(new File("testdata/01010.dat"));
		boolean foundIncorrectPrediction = false;
		for( String prediction : predictions ){
			if( prediction.equals(expectedValue) ){
				System.out.println("Correct Prediction!");
			}
			else{
				System.out.println("Incorrect Prediction:");
				System.out.println(StringEscapeUtils.escapeJava(prediction));
				foundIncorrectPrediction = true;
			}
		}
		assertTrue(!foundIncorrectPrediction);
	}

}

