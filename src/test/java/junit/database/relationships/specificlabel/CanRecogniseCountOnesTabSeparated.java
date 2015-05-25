package junit.database.relationships.specificlabel;

import database.Relationship;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseCountOnesTabSeparated {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredictCountOnesTabSeparatedBruteForce() throws Exception
	{
		Relationship bestRelationship = TestHarness.processAndGetBestRelationshipFor("testdata/countonestabseparated.dat", "ReadTSVToTable-1(getInputFromFile())");
		
		assertTrue( bestRelationship.getCorrectPredictions() == 15 );
		assertTrue( bestRelationship.getAccuracy() >= 0.9 );
	}

}
