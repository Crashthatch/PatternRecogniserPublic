package junit.database.choosenextrow.broken;

import database.AttRelationshipGraph;
import database.RelationshipFinder;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class CanRecognise010101 {

    /**
     * Fails because learns a relationship that predicts the normalised column of values (ie. 0, 0.06666667, 0, 0.06666667, 0, 0.06666667, ...)
     * Then in the test set there are more 1s and 0s, so that normalises to 0, 0.05, 0, 0.05, 0, 0.05, ...
     * So the predicted values are wrong (0.06666667 != 0.05).
     * Only just started happening after adding annotateExistingRows because we only do 2 rounds of processing when there's no output tree, and previously we needed 3 rounds for normalisation (one to split to rows, one to create a sum total, one to do the divide).
     *
     * Not sure if this is fixable. It's essentially happening because there's only a "single example" (the normalise processor only runs once).
     * Possibly would need the user to provide 3+ example sets (eg. 3 text boxes) for training data, which could then be processed independently and only keep the relationships that succeed for all examples. (eg. example1: 010101, example2: 010101010101, example3: 0101).
     * But then the user needs to split their data and provide multiple example-sets (less simple) and probably needs to split their test-data in the same way (which could be a lot of examples).
     * Maybe could add this into a different (related) product, not part of the "input/output" flow webpage.
     *
     * @throws SQLException
     */
	@Test public void canPredict010101BruteForce() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinder.processAndGetBestTree("testdata/01010x30.dat");
		
		//Test that the tree found accepts the training data.
		double chanceOfTrain = RelationshipFinder.applyTreeAndTest("testdata/01010x30.dat", tree);
		
		assertTrue( chanceOfTrain == 1.0);
				
		//Give possible next rows, and pick the best one.
		double chanceOfRowSeven = RelationshipFinder.applyTreeAndTest("testdata/7.dat", tree);
		double chanceOfRow12345 = RelationshipFinder.applyTreeAndTest("testdata/12345.dat", tree);
		double chanceOfRowABABAB = RelationshipFinder.applyTreeAndTest("testdata/ababab.dat", tree);
		double chanceOfRow010101more = RelationshipFinder.applyTreeAndTest("testdata/01010x40.dat", tree);
		
		assertTrue( chanceOfRowSeven < 0.5 );
		assertTrue( chanceOfRow12345 < 0.5 );
		assertTrue( chanceOfRowABABAB < 0.5 );
		assertTrue( chanceOfRow010101more > 0.5 );
	}

}

