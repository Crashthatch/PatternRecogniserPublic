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

public class ExtractNamesNumbersDirectorsFromWikipediaTableHtml {
	
	/**
	 * Requires cells from the same row of the HTML table to be grouped together into JSON objects.
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		//Fails because can't extract "director". Does HtmlXPathSelector.vevent, but then there's no way to create a column of "the 3rd td in the row". Need to check that it is trying to create XPathSelectortd( XPathSelector.vevent).
		// Would something that "groups by key" / pivots be useful here? Use index as the "key", so we get back columns of "first things", "second things" etc? Also a possible alternative for the reversible-json-map.
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/listOfGoTEpsTable-series1.htm", "testdata/listOfGoTEpNamesNumbersDirectors-json-series1.json");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/listOfGoTEpsTable-series2.htm", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = FileUtils.readFileToString( new File("testdata/listOfGoTEpNamesNumbersDirectors-json-series2.json"));
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

