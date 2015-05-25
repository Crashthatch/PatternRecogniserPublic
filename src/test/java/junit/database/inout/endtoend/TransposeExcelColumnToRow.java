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

public class TransposeExcelColumnToRow {
	
	/**
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/excel7NumbersAsColumn.txt", "testdata/excel7NumbersAsRow.txt");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		//Try to predict for 20 single-digit numbers.
		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/excel20NumbersAsColumn.txt", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/excel20NumbersAsRow.txt"));
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
		
		//Use the same tree to predict for 7 multidigit numbers, including a 0.
		//Test data is "different in some respect" (numbers have >1 digit), but splitOnTabs -> recombineWith\n should still work.
		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/excel7MultiDigitNumbersAsColumnWith0s.txt", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/excel7MultiDigitNumbersAsRowWith0s.txt"));
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

