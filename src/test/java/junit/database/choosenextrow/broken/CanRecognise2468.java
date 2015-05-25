package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecognise2468 {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredict2468() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/2468.dat");
		
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowOne = RelationshipFinder.applyTreeAndTest("testdata/24688.dat", tree);
		double chanceOfRowTwo = RelationshipFinder.applyTreeAndTest("testdata/246810.dat", tree);
		double chanceOfRowThree = RelationshipFinder.applyTreeAndTest("testdata/24682.dat", tree);
		
		assertTrue( chanceOfRowTwo > chanceOfRowOne );
		assertTrue( chanceOfRowTwo > chanceOfRowThree );
		assertTrue( chanceOfRowTwo > 0.5 );
	}

}
