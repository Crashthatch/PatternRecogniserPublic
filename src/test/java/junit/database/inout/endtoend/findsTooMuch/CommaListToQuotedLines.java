package junit.database.inout.endtoend.findsTooMuch;

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

public class CommaListToQuotedLines {
	
	@Test public void test() throws Exception{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/Comma Separated To Quoted Lines/trainIn.dat", "testdata/Comma Separated To Quoted Lines/trainOut.dat");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/Comma Separated To Quoted Lines/applyIn.dat", tree);
		assertTrue( predictions.size() > 0);

        String expectedValue = FileUtils.readFileToString(new File("testdata/Comma Separated To Quoted Lines/applyOut.dat"));
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
				System.out.println("Expected :");
				System.out.println(expectedValue);
				foundIncorrectPrediction = true;
			}
		}
		assertTrue(foundCorrectPrediction);
		//assertTrue(!foundIncorrectPrediction);
	}

}

