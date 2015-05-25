package junit.database.relationships.specificlabel;

import database.Relationship;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecognise010101 {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredict010101BruteForce() throws Exception
	{
		Relationship bestRelationship = TestHarness.processAndGetBestRelationshipFor("testdata/01010.dat", "ReadCSVToTable-1(getInputFromFile())");
		
		assertTrue( bestRelationship.getCorrectPredictions() == 9 );
		assertTrue( bestRelationship.getAccuracy() >= 0.9 );
	}

}
