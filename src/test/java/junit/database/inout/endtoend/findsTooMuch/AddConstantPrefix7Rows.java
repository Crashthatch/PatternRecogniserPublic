package junit.database.inout.endtoend.findsTooMuch;

import database.AttRelationshipGraph;
import database.RelationshipFinderInputOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class AddConstantPrefix7Rows {

    /**
     * Finds the correct solution, but also finds a non-correct solution which produces the correct number of rows (although with incorrect predictions) for the training data,
     * and produces the wrong number of rows for the test data.
     *
     * Uses primeFactors on the index of the 7 rows 0-6, producing 7 output rows (2,3,2,2,5,2,3) which are used to produce the correct number of "Bob"s, but when combined
     * with the numbers from the input data, duplicates some of the inputRows instead of doing a 1:1 matching.
     *
     * Could be fixed by implementing the "equal up to X" test, which would find that the att "forward" and "reversed" versions of the output att are not really very equal at all.
     * We can't compare the output tree and the corresponding single-forward-tree directly because our reversible transformers are not always 100% reversible- they throw away some data.
     * eg. JSON prediction "[1,2]" should not be discarded for being 'unequal' to "[1 , 2]".
     *
     * --This might work in this case, because the training data does get mangled when the rows get joined, but in some cases it would fail when it produces a correct output for the training data, but not the general case.
     * eg. Consider SplitCharactersToRows, which would work for the training data provided all of the training data values were < 10, but fail for test data which has values > 10.
     * The above test of "equal up to X" would not catch this.
     * Maybe this is just lack of training data, but in this scenario we'd always need > 10 test rows or else this could happen to the index. Another processor might have a similar problem at 20 or 100 instead of 10...
     *
     * Perhaps some kind of check with the user when 2 predictions disagree on a piece of testdata? Prompt them to choose the correct prediction and to add the row that they disagree on to the training set?
     *
     * @throws SQLException
     * @throws IOException
     */
	@Test public void test() throws Exception{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/excel7MultiDigitNumbersAsRowWith0s.txt", "testdata/excel7MultiDigitNumbersAsColumnWith0sPrefixBob.txt");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/excel20NumbersAsRow.txt", tree);
		assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString(new File("testdata/excel20NumbersAsColumnPrefixBob.txt"));
		boolean foundCorrectPrediction = false;
		boolean foundIncorrectPrediction = false;
		for( String prediction : predictions ){
			if( prediction.equals(expectedValue) ){
				foundCorrectPrediction = true;
				System.out.println("Correct Prediction!");
			}
			else{
				System.out.println("Incorrect Prediction:");
				System.out.println(StringEscapeUtils.escapeJava(prediction));
				foundIncorrectPrediction = true;
			}
		}
		assertTrue(foundCorrectPrediction);
		//assertTrue(!foundIncorrectPrediction);
	}

}

