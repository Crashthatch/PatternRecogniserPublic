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

public class TransposeExcelRowToColumnX10 {
	
	/**
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/excel7NumbersAsRow.txt", "testdata/excel7NumbersX10AsColumn.txt");
		assertTrue( tree.getFinalOutputAtts().size() > 0);
		
		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/excel20NumbersAsRow.txt", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/excel20NumbersX10AsColumn.txt"));
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
		
		{
			Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/excel7MultiDigitNumbersAsRowWith0s.txt", tree);
			assertTrue( predictions.size() > 0);
			String expectedValue = FileUtils.readFileToString( new File("testdata/excel7MultiDigitNumbersWith0sX10AsColumn.txt"));
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

