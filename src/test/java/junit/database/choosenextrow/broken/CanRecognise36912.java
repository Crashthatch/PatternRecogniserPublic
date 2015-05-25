package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecognise36912 {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredict36912() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/36912.dat");
		
		tree.createDot();
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/36912.dat", tree);
		
		assertTrue( chanceOfTrain > 0.6);
				
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowMultiplesOfTwo = RelationshipFinder.applyTreeAndTest("testdata/246810.dat", tree);
		double chanceOfRowRepeatLast = RelationshipFinder.applyTreeAndTest("testdata/3691212.dat", tree);
		double chanceOfRowRepeatFirst = RelationshipFinder.applyTreeAndTest("testdata/369123.dat", tree);
		double chanceOfRowCorrect = RelationshipFinder.applyTreeAndTest("testdata/3691215.dat", tree);
		
		assertTrue( chanceOfRowCorrect > 0.5 );
		assertTrue( chanceOfRowMultiplesOfTwo < chanceOfRowCorrect );
		assertTrue( chanceOfRowRepeatLast < chanceOfRowCorrect );
		assertTrue( chanceOfRowRepeatFirst < chanceOfRowCorrect );
		
	}

}
