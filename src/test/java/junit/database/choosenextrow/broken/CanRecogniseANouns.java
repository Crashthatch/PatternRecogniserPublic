package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseANouns {
	
	@Test public void canPredictANouns() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/aNouns.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/aNouns.dat", tree);
		
		assertTrue( chanceOfTrain > 0.6);
				
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowOne = RelationshipFinder.applyTreeAndTest("testdata/1.dat", tree);
		double chanceOfRowBWords = RelationshipFinder.applyTreeAndTest("testdata/bWords.dat", tree);
		double chanceOfRowANotWords = RelationshipFinder.applyTreeAndTest("testdata/aNotWords.dat", tree);
		double chanceOfRowAWords2 = RelationshipFinder.applyTreeAndTest("testdata/aWords2.dat", tree);
		double chanceOfRowANouns2 = RelationshipFinder.applyTreeAndTest("testdata/aNouns2.dat", tree);
		
		assertTrue( chanceOfRowOne < 0.5 );
		assertTrue( chanceOfRowBWords < 0.5 );
		assertTrue( chanceOfRowANotWords < 0.5 );
		assertTrue( chanceOfRowAWords2 < 0.9 );
		assertTrue( chanceOfRowANouns2 > 0.3 );
	}

}

