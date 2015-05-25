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

public class Select2ColumnsFromCSVNoHeader {
	
	/**
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/flightquest-testflights-first10-noheader.csv", "testdata/flightquest-testflights-first10-runwayDepartureAndArrival.csv");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		//Try to predict for 20 single-digit numbers.
		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/flightquest-testflights-second10-noheader.csv", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/flightquest-testflights-second10-runwayDepartureAndArrival.csv"));
			boolean foundCorrectPrediction = false;
			boolean foundIncorrectPrediction = false;
			for( String prediction : predictions ){
				if( prediction.equals(expectedValue) ){
					System.out.println("Correct Prediction!");
					foundCorrectPrediction = true;
				}
				else{
					System.out.println("Incorrect Prediction:");
					System.out.println(StringEscapeUtils.escapeJava(prediction));
					foundIncorrectPrediction = true;
				}
			}
			assertTrue( foundCorrectPrediction );
			assertTrue(!foundIncorrectPrediction);
		}
	}
}

