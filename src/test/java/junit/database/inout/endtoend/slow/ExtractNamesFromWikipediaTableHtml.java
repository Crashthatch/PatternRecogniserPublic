package junit.database.inout.endtoend.slow;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class ExtractNamesFromWikipediaTableHtml {
	
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/listOfGoTEpsTable-series1.htm", "testdata/listOfGoTEpNames-series1.txt");
		
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/listOfGoTEpsTable-series2.htm", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = "The North Remembers\nThe Night Lands\nWhat Is Dead May Never Die\nGarden of Bones\nThe Ghost of Harrenhal\nThe Old Gods and the New\nA Man Without Honor\nThe Prince of Winterfell\nBlackwater\nValar Morghulis";
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

