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

public class ExtractFromFatsecretTable {
    /**
     * Fails because without splitOnTabsToColumns in the outputTable, all the values end up in a single column (2011-12-04, 63, 2011-11-04, 69.5 etc.)
     * Also probably fails to split on "-" and predict months with the leading 0, etc?
     * @throws SQLException
     * @throws IOException
     */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/fatsecretfirst5.dat", "testdata/fatsecretfirst5-out.dat");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/fatsecret.dat", tree);
		assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString(new File("testdata/fatsecret-out.dat"));
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
				foundIncorrectPrediction = true;
			}
		}
		assertTrue(foundCorrectPrediction);
		assertTrue(!foundIncorrectPrediction);
	}

}

