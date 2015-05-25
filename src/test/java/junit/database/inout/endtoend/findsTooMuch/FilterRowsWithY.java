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

public class FilterRowsWithY {

	/**
	 * Technically works by splitting on ,Y then on \n, and rejecting anything that isn't an exact date in dd/mm/yyyy format (eg. those empty rows or those with trailing commas (those where there was no Y).
     * But this is slow (9m13s). Should be able to filter much faster.
     *
     * With FilterFalsey:
     * Can predict the date att fine, but can't predict "0,1,2,3" - the "index" produced by splitting the output-text.
     * 1) Need to reindex the index column from the input tree so that rows 0,1,3 and 5 get reindexed to 0,1,2 and 3. -Can't easily do this because the rownums need to have a 1:1 map to the date they are indexing, but a many:many "reIndex" processor will create a whole new table and lose the links.
     * 2) Or remove the need for index columns - just rely on the order in the DB.
     * 3) Or have a "annotate existing rows" flag. Ie. takes in n rows, outputs n rows, saved to the same table instead of a new table. Would simplify the solution to the normalisation problem too.
     *
     * With (3), it works, but the filterFalsey(ReadCSVToTable-2(InputAtt())) att is not scored as interesting enough, so numrows doesn't get run on it. When falsely made more interesting, the solution is found in ~3m30s.
     * Could class atts as more interesting if they have NULL rows? Or if one of their sibling atts (in this case the date att) was successfully used as input to a relationship?
     *
     * Works with classing "filterlike" and "sibling used to create good relationship" as more interesting.
     *
     * @throws java.io.IOException
	 */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/school holidays - keep with Y/trainIn.dat", "testdata/school holidays - keep with Y/trainOut.dat");
		assertTrue( tree.getFinalOutputAtts().size() > 0);

        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/school holidays - keep with Y/applyIn.dat", tree);
        assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString( new File("testdata/school holidays - keep with Y/applyOut.dat"));
        boolean foundCorrectPrediction = false;
        boolean foundIncorrectPrediction = false;
        for( String prediction : predictions ){
            if( prediction.equals(expectedValue) ){
                System.out.println("Correct Prediction!");
                foundCorrectPrediction = true;
            }
            else{
                System.out.println("Incorrect Prediction:");
                System.out.println(StringEscapeUtils.escapeJava(prediction));
                System.out.println("Expected: ");
                System.out.println(StringEscapeUtils.escapeJava(expectedValue));
                foundIncorrectPrediction = true;
            }
        }
        assertTrue(foundCorrectPrediction);
        //assertTrue(!foundIncorrectPrediction);
	}
}

