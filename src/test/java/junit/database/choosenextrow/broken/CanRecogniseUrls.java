package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecogniseUrls {
	
	@Test public void canPredictUrls() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/urls.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/urls.dat", tree);
		
		assertTrue( chanceOfTrain > 0.6);
				
		//Give 3 possible next rows, and pick the best one.
		double chanceOfRow12345 = RelationshipFinder.applyTreeAndTest("testdata/12345.dat", tree);
		double chanceOfRowBWords = RelationshipFinder.applyTreeAndTest("testdata/bWords.dat", tree);
		double chanceOfRowUrls2 = RelationshipFinder.applyTreeAndTest("testdata/urls2.dat", tree);
		double chanceOfRowInvalidUrls = RelationshipFinder.applyTreeAndTest("testdata/urls-invalid.dat", tree);
		
		assertTrue( chanceOfRow12345 < 0.5 );
		assertTrue( chanceOfRowBWords < 0.5 );
		assertTrue( chanceOfRowUrls2 > 0.5 );
		assertTrue( chanceOfRowInvalidUrls < 0.5 );
	}

}

