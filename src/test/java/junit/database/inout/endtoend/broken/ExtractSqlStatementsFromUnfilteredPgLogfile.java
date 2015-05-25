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

public class ExtractSqlStatementsFromUnfilteredPgLogfile {

    /**
     * Fails because there's no way to easily filter to the rows containing SQL statements.
     * Need a processor that's "contains 'Statement'" or "begins with 'Statement'" or similar to allow the process to filter to the required rows (ie. those rows in testdata/postgres logfile-filtered extract SQL statements where the filtering was done by hand)
     */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/postgres logfile-unfiltered extract SQL statements/trainIn.dat", "testdata/postgres logfile-unfiltered extract SQL statements/trainOut.dat");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/postgres logfile-unfiltered extract SQL statements/applyIn.dat", tree);
		assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString(new File("testdata/postgres logfile-unfiltered extract SQL statements/applyOut.dat"));
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

