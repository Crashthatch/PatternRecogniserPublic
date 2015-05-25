package junit.database.inout.endtoend.slow;

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

public class ExtractNamesNumbersFromWikipediaTableHtml {
	
	/**
	 * Test creating a list of json objects with multiple keys. Requires being able to create a column of all values with the same key (eg. a column of "names") so they can be predicted, 
	 * and requires keys to be extracted (or stored somehow) to make it reversible.
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/listOfGoTEpsTable-series1.htm", "testdata/listOfGoTEpNamesNumbers-json-series1.json");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/listOfGoTEpsTable-series2.htm", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = FileUtils.readFileToString( new File("testdata/listOfGoTEpNamesNumbers-json-series2.json"));
		boolean foundIncorrectPrediction = false;
		for( String prediction : predictions ){
			if( JSONValue.parse(expectedValue).equals(JSONValue.parse(prediction)) ){
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

