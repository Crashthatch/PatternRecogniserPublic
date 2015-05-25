package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanCountOnes {
	
	@Test public void canPredict() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/countones.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/countones.dat", tree);
		
		assertTrue( chanceOfTrain == 1.0);
				
		//Give possible next rows, and pick the best one.
		double chanceOfRowCorrect = RelationshipFinder.applyTreeAndTest("testdata/countones-correct.dat", tree);
		double chanceOfRowIncorrect = RelationshipFinder.applyTreeAndTest("testdata/countones-incorrect.dat", tree);
		double chanceOfRowSeven = RelationshipFinder.applyTreeAndTest("testdata/7.dat", tree);
		
		assertTrue( chanceOfRowSeven < 0.5 );
		assertTrue( chanceOfRowIncorrect < 0.5 );
		assertTrue( chanceOfRowCorrect > 0.5 );
	}

}

