package junit.database.inout.endtoend;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class CommaListToNewlinesPlusOne {
	
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/4,5,7,8,6.dat", "testdata/56897.dat");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/1,2,3.dat", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = "2\n3\n4";
		boolean foundCorrectPrediction = false;
		boolean foundIncorrectPrediction = false;
		for( String prediction : predictions ){
			if( prediction.equals(expectedValue) ){
				foundCorrectPrediction = true;
				System.out.println("Correct Prediction!");
			}
			else{
				System.out.println("Incorrect Prediction:");
				System.out.println(StringEscapeUtils.escapeJava(prediction));
				foundIncorrectPrediction = true;
			}
		}
		assertTrue(foundCorrectPrediction);
		assertTrue(!foundIncorrectPrediction);
	}

}

