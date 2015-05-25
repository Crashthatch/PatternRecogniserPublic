package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseMovingA {
	
	@Test public void chooseNextRow() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/moving a.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/moving a.dat", tree);
		
		assertTrue( chanceOfTrain > 0.6);
		
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowOne = RelationshipFinder.applyTreeAndTest("testdata/moving a-aaaahh.dat", tree);
		double chanceOfRowTwo = RelationshipFinder.applyTreeAndTest("testdata/moving a-correct.dat", tree);
		double chanceOfRowThree = RelationshipFinder.applyTreeAndTest("testdata/moving a-wrong.dat", tree);
		double chanceOfRowElephant = RelationshipFinder.applyTreeAndTest("testdata/moving a-elephant.dat", tree);
		double chanceOfRowOneChar = RelationshipFinder.applyTreeAndTest("testdata/moving a-onechar.dat", tree);
		double chanceOfRowGibberish = RelationshipFinder.applyTreeAndTest("testdata/moving a-gibberish.dat", tree);
		double chanceOfRowJsonMap = RelationshipFinder.applyTreeAndTest("testdata/jsonmap.dat", tree);
		
		assertTrue( chanceOfRowTwo > chanceOfRowOne );
		assertTrue( chanceOfRowTwo > chanceOfRowThree );
		assertTrue( chanceOfRowTwo > chanceOfRowElephant );
		assertTrue( chanceOfRowTwo > chanceOfRowOneChar );
		assertTrue( chanceOfRowTwo > chanceOfRowGibberish );
		assertTrue( chanceOfRowTwo > chanceOfRowJsonMap );
		assertTrue( chanceOfRowTwo > 0.5 );
	}

}
