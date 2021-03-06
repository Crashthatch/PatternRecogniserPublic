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

public class ExtractHourFromCSV {
	
	/**
	 * Currently fails because DateToDateParts never gets applied after ReadCSVToTable. Need better processor selector logic.
	 * BigML Model 30:1ObjectiveWeights attributeMetaModelTraining2: ReadCSVToTable-9 (which contains the dates) never gets tagged as an attribute on which further processing should be performed.
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/flightquest-testflights-first10-noheader.csv", "testdata/flightquest-testflights-first10-scheduled_runway_arrival_hourtrunc.csv");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/flightquest-testflights-second10-noheader.csv", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = FileUtils.readFileToString( new File("testdata/flightquest-testflights-second10-scheduled_runway_arrival_hourtrunc.csv"));
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

