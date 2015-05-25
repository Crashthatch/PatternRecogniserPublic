package junit.database.relationships.specificlabel;

import database.Relationship;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseNot {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredictNotBruteForce() throws Exception
	{
		Relationship bestRelationship = TestHarness.processAndGetBestRelationshipFor("testdata/not.dat", "ReadTSVToTable-4(getInputFromFile())");
		
		assertTrue( bestRelationship.getCorrectPredictions() == 45 );
		assertTrue( bestRelationship.getAccuracy() >= 0.9 );
	}

}
