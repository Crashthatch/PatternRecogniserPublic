package junit.database.relationships.specificlabel;

import database.Relationship;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseBracketedCount {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canPredictBracketedCount() throws Exception
	{
		Relationship bestRelationship = TestHarness.processAndGetBestRelationshipFor("testdata/bracketedCount.dat", "Digits(ReadCSVToTable-1(getInputFromFile()))");
		
		assertTrue( bestRelationship.getCorrectPredictions() == 18 );
		assertTrue( bestRelationship.getAccuracy() >= 0.9 );
	}

}
