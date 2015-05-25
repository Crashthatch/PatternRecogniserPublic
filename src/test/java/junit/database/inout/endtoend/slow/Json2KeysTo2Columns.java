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

public class Json2KeysTo2Columns {
	
	/**
	 * Should split json list, then map, and choose 2 of the columns and recombine them into a CSV with 2 columns.
     * Actually, rather than stripping the ""s (after decoding json map), it splits on : and parses part of the date out, then recombines.
     * Doesn't generalise correctly because all the training data is from the same day (but test passes because test data is also from that same day).
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/flightquest-testflights-first10.json", "testdata/flightquest-testflights-first10-runwayDepartureAndArrival.csv");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/flightquest-testflights-second10.json", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/flightquest-testflights-second10-runwayDepartureAndArrival.csv"));
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
}

