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

public class FilterToErrorsFromApacheErrorLog {

	/**
     * Finds BestColumnLearner Label:t10_0.2 SplitOnString\nToRows-1(getInputFromFile()) RRA:t3_1.22 contains] [error] [client 127.0.0.1] (SplitOnString\nToRows-1(getInputFromFile())) FeatureAtts:`t3_0`.`2` SplitOnString\nToRows-1(getInputFromFile())
     * Fails to find the INDEX att. Would need to do rownum(t3_1.22) ? Or have a relationship that can predict rownums?
     * Can we just drop index atts all together and rely on order in DB / id order like we do for finding relationships between input & output trees?
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/apache log filter to errors/trainIn.dat", "testdata/apache log filter to errors/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/apache log filter to errors/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/apache log filter to errors/applyOut.dat"));
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
                System.out.println("Expected: ");
                System.out.println(StringEscapeUtils.escapeJava(expectedValue));
                foundIncorrectPrediction = true;
            }
        }
        assertTrue(foundCorrectPrediction);
        assertTrue(!foundIncorrectPrediction);
	}
}

