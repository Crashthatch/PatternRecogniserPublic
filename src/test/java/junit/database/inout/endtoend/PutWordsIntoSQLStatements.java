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

public class PutWordsIntoSQLStatements {
	
	/**
	 * Requires being able to extract a column of quoted values from each SQL statement into a column so it can be predicted. eg. Split on ' to columns, or remove identical start / end of string.
	 * When we disable splitOn'ToColumns (because it can slow down other use cases a lot by exploding the number of columns) this test fails.
	 * Need either a way to reduce the number of columns returned from splitOn'ToColumns (add an arbitrary max outputColumns of 10?) or a better processor that can produce the required column of values.
     *
     * NOW SUCCEEDS! Uses splitAtCharacterIdxToRows and splitEndsTocolumns to identify the part of the word that changes and save that.
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/aWords.dat", "testdata/aWordsSQL.dat");
		
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/aWords2.dat", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue;
		expectedValue = FileUtils.readFileToString(new File("testdata/aWords2SQL.dat"));
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

