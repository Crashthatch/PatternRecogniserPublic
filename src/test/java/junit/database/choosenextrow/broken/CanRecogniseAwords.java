package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseAwords {
	
	@Test public void canPredictAWords() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/aWords.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/aWords.dat", tree);
		
		assertTrue( chanceOfTrain > 0.6);
				
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
		assertTrue( chanceOfRowAWords2 > 0.3 );
	}

}

