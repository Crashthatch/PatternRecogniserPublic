package junit.database.inout.endtoend.slow;

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

public class JsonToSQLFBAccessTokensWrapped {

	/**
	 * Like JsonToSQLFBAccessTokens, but input data includes the entire FB response (eg {data: [XXX], paging: ...}) rather than just the list [XXX].
     * Must learn to crop that out and ignore it / only focus on the important part.
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/facebook pages access_tokens to SQL wrapped/trainIn.dat", "testdata/facebook pages access_tokens to SQL wrapped/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/facebook pages access_tokens to SQL wrapped/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/facebook pages access_tokens to SQL wrapped/applyOut.dat"));
        boolean foundIncorrectPrediction = false;
        for( String prediction : predictions ){
            if( prediction.equals(expectedValue) ){
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
        assertTrue(!foundIncorrectPrediction);
	}
}

