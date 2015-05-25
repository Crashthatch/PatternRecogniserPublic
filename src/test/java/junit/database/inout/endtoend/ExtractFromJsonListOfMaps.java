package junit.database.inout.endtoend;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class ExtractFromJsonListOfMaps {
	
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/jsonlistofmaps.dat", "testdata/jsonlistofmapsnames.dat");
		
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/jsonlistofmaps2.dat", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = "Matthew\nNick\nOscar\nPeter\nQuintin\nRomeo\nSteve";
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

