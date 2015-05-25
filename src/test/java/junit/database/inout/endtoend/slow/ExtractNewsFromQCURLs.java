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

public class ExtractNewsFromQCURLs {
	
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/qc-urls-101+.txt", "testdata/qc-news-singleline-101+.txt");
        assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictionsForTraining = RelationshipFinderInputOutput.applyTree("testdata/qc-urls-101+.txt", tree);
        assertTrue( predictionsForTraining.size() > 0);

		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/qc-urls-201+.txt", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue;
		expectedValue = FileUtils.readFileToString(new File("testdata/qc-news-singleline-201+.txt"));
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

