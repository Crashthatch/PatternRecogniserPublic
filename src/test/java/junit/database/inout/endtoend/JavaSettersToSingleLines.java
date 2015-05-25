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

public class JavaSettersToSingleLines {

	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/setterMethods2.java", "testdata/setterMethods2OneLinePerMethod.java");
		assertTrue(tree.getFinalOutputAtts().size() > 0);
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/setterMethods1.java", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue;
		expectedValue = FileUtils.readFileToString(new File("testdata/setterMethods1OneLinePerMethod.java"));
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