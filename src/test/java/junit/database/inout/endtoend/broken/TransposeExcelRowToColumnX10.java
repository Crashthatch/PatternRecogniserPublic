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

public class TransposeExcelRowToColumnX10 {
	
	/**
	 * Learns based on a trainingset 1 -> 10, 2 -> 20, 5 -> 50, etc.
	 * Learns that target = input * 10.
	 * But rule is actually "add a zero to end of number (eg. 1 -> 10, 2-> 20 etc)". Test set contains a 0, so learned relationships predict "0 -> 0*10 = 0". But should be "0 -> 00".
	 * So test fails.
	 * 
	 * There is nothing we can do about this except warn the user that the test data is substantially different to the training data (ie. testdata includes 0s). And make it clear exactly what relationship was learned.
	 * Maybe we learn both relationships during training (both "addAZero" and "*10") and then highlight that 2 conflicting predictions were made for the test set and get them to choose a prediction or modify their training data to include an example that differentiates the two cases (in this case, adding "0 -> 00" to training set).
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/excel7NumbersAsRow.txt", "testdata/excel7NumbersX10AsColumn.txt");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/excel7MultiDigitNumbersAsRowWith0s.txt", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/excel7MultiDigitNumbersWith0sAddAZero.txt"));
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

