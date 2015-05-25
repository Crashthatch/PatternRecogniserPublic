package junit.database.inout.endtoend;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class LengthOfFirstWordWithTrailingNewlines {
	
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/aWords-trailingNewlines.dat", "testdata/aWordsLength.dat");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/aWords2-7trailingNewlines.dat", tree);
		assertTrue( predictions.size() > 0);
		String expectedValue = "7\n5\n5\n5\n3\n4\n4\n5\n5\n5\n9\n6\n6\n9";
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
		
		/*assertTrue( chanceOfTrain > 0.6);
				
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowOne = RelationshipFinder.applyTreeAndTest("testdata/1.dat", tree);
		double chanceOfRowSeven = RelationshipFinder.applyTreeAndTest("testdata/7.dat", tree);
		double chanceOfRow12345 = RelationshipFinder.applyTreeAndTest("testdata/12345.dat", tree);
		double chanceOfRowBWords = RelationshipFinder.applyTreeAndTest("testdata/bWords.dat", tree);
		double chanceOfRowANotWords = RelationshipFinder.applyTreeAndTest("testdata/aNotWords.dat", tree);
		double chanceOfRowAWords2 = RelationshipFinder.applyTreeAndTest("testdata/aWords2.dat", tree);
		
		assertTrue( chanceOfRowOne < 0.5 );
		assertTrue( chanceOfRowSeven < 0.5 );
		assertTrue( chanceOfRow12345 < 0.5 );
		assertTrue( chanceOfRowBWords < 0.5 );
		assertTrue( chanceOfRowANotWords < 0.5 );
		assertTrue( chanceOfRowAWords2 > 0.3 );*/
	}

}

