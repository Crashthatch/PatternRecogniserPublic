package junit.database.inout.endtoend;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class SimpleXMLAttributesToCSVWithConfusingAtt {

	/**
     * The basic "SimpleXMLAttributesToCSV" test can be accomplished with clever string splitting,
     * so this test adds some confusingatts into the XML to try to force the XML attribute parser / extractor transformationServices to be used.
     *
     * Unfortunately the confusingatts also then need filtering / ignoring, so probably need a XMLOutermostAttributesToColumns operator to succeed.
     *
	 * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/xml to csv - rapidminer processor/trainIn.dat", "testdata/xml to csv - rapidminer processor/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/xml to csv - rapidminer processor/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/xml to csv - rapidminer processor/applyOut.dat"));
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

