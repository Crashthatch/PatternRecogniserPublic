package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecognise12345 {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredict12345() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/12345windowsLineEndings.dat");
		
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRowOne = RelationshipFinder.applyTreeAndTest("testdata/123455windowsLineEndings.dat", tree);
		double chanceOfRowTwo = RelationshipFinder.applyTreeAndTest("testdata/123456windowsLineEndings.dat", tree);
		double chanceOfRowThree = RelationshipFinder.applyTreeAndTest("testdata/123457windowsLineEndings.dat", tree);
		
		assertTrue( chanceOfRowTwo > chanceOfRowOne );
		assertTrue( chanceOfRowTwo > chanceOfRowThree );
		assertTrue( chanceOfRowTwo > 0.5 );
	}

}
