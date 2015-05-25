package junit.database.inout.endtoend.broken;

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

public class NumberOfChildrenInJson {
    /**
     * Manages to extract the children correctly to their own column, but that att is then not interesting enough to run numRows() on.
     * 369th most interesting: `t127_0`.`2` jsonDecodeList-1(JsonDecodeMapManyColsOut-1(jsonDecodeList-1(getInputFromFile()))) FeatureSet: [ 8 8 8 0 4 9 Unknown false true 0 0.0 0 0 0.0 0 8 6.875 1 false 0 0.0 com.mysql.jdbc.JDBC4PreparedStatement@e096a0c: INSERT INTO `attribute` (`runId`, `processingRound`, `attName`, `attId`, `notNullRowsInTable`,`uniqueRowsInTable`,`numRowsInTable`,`nullRowsInTable`,`numAncestorAtts`,`depth`,`guessedType`,`isDuplicate`,`isAlmostDuplicate`,`bestTargetAttRowsCovered`,`bestPercentageOfTargetAttCovered`,`bestTargetAttTotalLength`,`bestTargetAttRows`,`bestAimForScore`,`totalLength`,`firstRowLength`,`averageRowLength`,`numParents`,`generatedByConstantCreator`,`bestCoveringRows`,`bestCoveringPercentageAvg`) VALUES (  'testdata/fictionalChildren.jsontestdata/fictionalChildrenCounts.json2014-05-22 17:17:50', 5, 'stringLength(SplitOnRegex ToRows-1(SplitOnRegex\\r\\nToRows-1(getInputFromFile())))', 321, '25', '9', '25', '0', '4', '9', 'integer', '0', '0', '3', '0.75', '4', '4', '0.1875', '0', '1', '1.4', '1', '0', '12', '0.5833333333333334') [doNotSaveProperties, att, insertPreparedStatement] ]
     * 370th most interesting: `t127_0`.`1` jsonDecodeList-0(JsonDecodeMapManyColsOut-1(jsonDecodeList-1(getInputFromFile()))) FeatureSet: [ 8 3 8 0 4 9 boolean false true 0 0.0 0 0 0.0 0 1 1.0 1 false 0 0.0 com.mysql.jdbc.JDBC4PreparedStatement@e096a0c: INSERT INTO `attribute` (`runId`, `processingRound`, `attName`, `attId`, `notNullRowsInTable`,`uniqueRowsInTable`,`numRowsInTable`,`nullRowsInTable`,`numAncestorAtts`,`depth`,`guessedType`,`isDuplicate`,`isAlmostDuplicate`,`bestTargetAttRowsCovered`,`bestPercentageOfTargetAttCovered`,`bestTargetAttTotalLength`,`bestTargetAttRows`,`bestAimForScore`,`totalLength`,`firstRowLength`,`averageRowLength`,`numParents`,`generatedByConstantCreator`,`bestCoveringRows`,`bestCoveringPercentageAvg`) VALUES (  'testdata/fictionalChildren.jsontestdata/fictionalChildrenCounts.json2014-05-22 17:17:50', 5, 'stringLength(SplitOnRegex ToRows-1(SplitOnRegex\\r\\nToRows-1(getInputFromFile())))', 321, '25', '9', '25', '0', '4', '9', 'integer', '0', '0', '3', '0.75', '4', '4', '0.1875', '0', '1', '1.4', '1', '0', '12', '0.5833333333333334') [doNotSaveProperties, att, insertPreparedStatement] ]
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
	@Test public void test() throws Exception
	{
		AttRelationshipGraph tree = RelationshipFinderInputOutput.processAndGetBestTree("testdata/fictionalChildren.json", "testdata/fictionalChildrenCounts.json");
		
		assertTrue( tree.getFinalOutputAtts().size() > 0 );
		
		Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/fictionalChildren2.json", tree);
		assertTrue( predictions.size() > 0);
        String expectedValue = FileUtils.readFileToString(new File("testdata/fictionalChildren2Counts.dat"));
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
		assertTrue(!foundIncorrectPrediction);
	}

}

