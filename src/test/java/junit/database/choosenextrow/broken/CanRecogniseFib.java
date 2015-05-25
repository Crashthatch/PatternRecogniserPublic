package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseFib {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{
	}
	
	@Test public void canPredictFibonacci() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/fibonacci30.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/fibonacci30.dat", tree);
		
		assertTrue( chanceOfTrain > 0.6);
		
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowOne = RelationshipFinder.applyTreeAndTest("testdata/12345.dat", tree);
		double chanceOfRowTwo = RelationshipFinder.applyTreeAndTest("testdata/fibonacci40.dat", tree);
		double chanceOfRowThree = RelationshipFinder.applyTreeAndTest("testdata/fibonacci40-incorrect.dat", tree);
		
		assertTrue( chanceOfRowTwo > chanceOfRowOne );
		assertTrue( chanceOfRowTwo > chanceOfRowThree );
		assertTrue( chanceOfRowTwo > 0.5 );
	}

}
