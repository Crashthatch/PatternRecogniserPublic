package junit.database.inout.endtoend.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class ExtractAnswerDetailsFromStackOverflowQuestionHtml {
	
	/**
	 * There are 188 different classes in this stack overflow page. We can't brute force them all.
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/stackoverflowQuestion.html", "testdata/stackoverflowQuestionAnswerDetails.json");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/stackoverflowQuestion2.html", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = FileUtils.readFileToString( new File("testdata/stackoverflowQuestion2AnswerDetails.json"));
		boolean foundIncorrectPrediction = false;
		for( String prediction : predictions ){
			if( JSONValue.parse(prediction).equals(JSONValue.parse(expectedValue)) ){
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

