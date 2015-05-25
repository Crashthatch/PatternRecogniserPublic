package junit.database.inout;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.*;
import database.featureRelationshipSelector.RelationshipSelectorCleverInputOutput;
import models.BestColumnLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestFilterOneOrNull {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void filterCreatesCorrectInputColumns() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/school holidays - keep with Y/applyIn.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToLines.doWork();
		
		Att lineAtt = splitToLines.getOutputAtts().get(1);
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(lineAtt);

        Processor splitToColumns = new Processor( new SplitOnRegexToColumns(","), inputAtts2, lineAtt.getDataTable());
        splitToColumns.doWork();

        Att dateAtt = splitToColumns.getOutputAtts().get(0);
        Att yAtt = splitToColumns.getOutputAtts().get(1);


		Processor filterOutNonYs = new Processor( new FilterFalsey(), Arrays.asList(yAtt), lineAtt.getDataTable() );
		filterOutNonYs.doWork();
		
		Att filteredAtt = filterOutNonYs.getOutputAtts().get(0);

        //Test that only the correct dates get returned with filteredAtt as the root-row-att and dateAtt.
        //This is the query an Input/Output relationship performs (See Relationship.executeQueryJoiningTestInputsAllRows, line 407)
        String inSql = DataTable.getSqlStringJoiningInputs(filteredAtt, Arrays.asList(dateAtt));

        PreparedStatement ps = Database.getConnection().prepareStatement(inSql);
        ResultSet inputData = ps.executeQuery();

        inputData.next();
        assertEquals(inputData.getString(dateAtt.getDbColumnNameNoQuotes()), "01/09/2014");
        inputData.next();
        assertEquals( inputData.getString(dateAtt.getDbColumnNameNoQuotes()), "02/09/2014");
        inputData.next();
        inputData.next();
        inputData.next();
        inputData.next();
        assertEquals( inputData.getString(dateAtt.getDbColumnNameNoQuotes()), "08/09/2014");

        inputData.last();
        assertEquals(inputData.getRow(), 195);

        inputData.close();
        ps.close();
	}


    @Test public void filterCanLearnCorrectRelationship() throws Exception{

        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/school holidays - keep with Y/applyIn.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor splitCSV = new Processor( new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable());
        splitCSV.doWork();

        Att dateAtt = splitCSV.getOutputAtts().get(1);
        Att yAtt = splitCSV.getOutputAtts().get(2);


        Processor filterOutNonYs = new Processor( new FilterFalsey(), Arrays.asList(yAtt), yAtt.getDataTable() );
        filterOutNonYs.doWork();

        Att filteredAtt = filterOutNonYs.getOutputAtts().get(0);



        //Create the output tree.
        AttRelationshipGraph outputGraph = new AttRelationshipGraph();
        DataTable rootTable = new DataTable();
        Att outConstant = new Att(rootTable, outputGraph);
        HashMap<Att, String> rootRowValues = new HashMap<>();
        rootRowValues.put(outConstant, "root");
        rootTable.insert(0, rootRowValues);
        rootTable.save();

        ArrayList<Att> inputAtts3 = new ArrayList<Att>();
        inputAtts3.add(outConstant);
        Processor outImporter = new Processor( new getInputFromFile("testdata/school holidays - keep with Y/applyOut.dat"), inputAtts3, outConstant.getDataTable() );
        outImporter.doWork();

        Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
        Processor splitToLinesOutput = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
        splitToLinesOutput.doWork();

        Att outIdx = splitToLinesOutput.getOutputAtts().get(0);
        Att outDate = splitToLinesOutput.getOutputAtts().get(1);


        //Learn a relationship between the input Atts and the output Att.
        Relationship rel = new Relationship(new BestColumnLearner(), Arrays.asList(dateAtt), outDate, filteredAtt);
        try {
            rel.estimatePerformanceUsingXValidation();
            rel.learn();
        }
        catch( IncorrectInputRowsException | InsufficientRowsException e){
            e.printStackTrace();
        }

        assertEquals(195, rel.getCorrectPredictions());
        System.out.println(rel.getIncorrectPredictions());


        //Ensure that the RelationshipFinder would have attempted this relationship.
        InputOutputRelationshipSelector relationshipSelector = new RelationshipSelectorCleverInputOutput();
        List<Relationship> potentialRelationships = relationshipSelector.getBestRelationships(inputGraph, outputGraph, new ArrayList<Relationship>(), new HashSet<Relationship>(), null, 0);
        assertTrue( potentialRelationships.contains(rel));



        //Make sure the relationship correctly predicts for new values.
        Table<Integer, Integer, String> testTable = TreeBasedTable.create();
        testTable.put(0, 0, "LABEL");
        testTable.put(0, 1, "05/11/1987");
        List<String> predictions = rel.makePredictions(testTable);
        assertEquals("05/11/1987", predictions.get(0));


    }

    /**
     * FAILS BECAUSE:
     * Splitting the output tree to rows results in 4 rows, with an index att going 0,1,2,3.
     * The input tree is split to 7 rows (with an index), but then filtered down to 4 rows. So the index of filtered rows now has gaps: 0,1,2,4.
     * Need to reindex the index column from the input tree so that rows 0,1,2 and 4 get reindexed to 0,1,2 and 3.
     * Or remove the need for index columns - just rely on the order in the DB.
     * Or have a "annotate existing rows" flag. Ie. takes in n rows, outputs n rows, saved to the same table instead of a new table. Would simplify the solution to the normalisation problem too.
     * @throws SQLException
     */
    @Test public void filterCanPredictFilteredIndex() throws Exception{

        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/school holidays - keep with Y/trainIn.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor splitCSV = new Processor( new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable());
        splitCSV.doWork();

        Att inIdx = splitCSV.getOutputAtts().get(0);
        Att dateAtt = splitCSV.getOutputAtts().get(1);
        Att yAtt = splitCSV.getOutputAtts().get(2);


        Processor filterOutNonYs = new Processor( new FilterFalsey(), Arrays.asList(yAtt), yAtt.getDataTable() );
        filterOutNonYs.doWork();

        Att filteredAtt = filterOutNonYs.getOutputAtts().get(0);

        Processor indexFiltered = new Processor( new rownum(), Arrays.asList(filteredAtt), longStringAtt.getDataTable());
        indexFiltered.doWork();

        Att filteredIdx = indexFiltered.getOutputAtts().get(0);



        //Create the output tree.
        AttRelationshipGraph outputGraph = new AttRelationshipGraph();
        DataTable rootTable = new DataTable();
        Att outConstant = new Att(rootTable, outputGraph);
        HashMap<Att, String> rootRowValues = new HashMap<>();
        rootRowValues.put(outConstant, "root");
        rootTable.insert(0, rootRowValues);
        rootTable.save();

        ArrayList<Att> inputAtts3 = new ArrayList<Att>();
        inputAtts3.add(outConstant);
        Processor outImporter = new Processor( new getInputFromFile("testdata/school holidays - keep with Y/trainOut.dat"), inputAtts3, outConstant.getDataTable() );
        outImporter.doWork();

        Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
        Processor splitToLinesOutput = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
        splitToLinesOutput.doWork();

        Att outIdx = splitToLinesOutput.getOutputAtts().get(0);
        Att outDate = splitToLinesOutput.getOutputAtts().get(1);


        //Make sure it is possible to learn a relationship that predicts the output tree's INDEX variable.
        Relationship rel = new Relationship(new BestColumnLearner(), Arrays.asList(filteredIdx), outIdx, filteredIdx);
        try {
            rel.estimatePerformanceUsingXValidation();
            rel.learn();
        }
        catch( IncorrectInputRowsException | InsufficientRowsException e){
            e.printStackTrace();
        }

        assertEquals(4, rel.getCorrectPredictions());
        assertEquals(0, rel.getIncorrectPredictions());


        //Ensure that the RelationshipFinder would have attempted this relationship or equivalent.
        InputOutputRelationshipSelector relationshipSelector = new RelationshipSelectorCleverInputOutput();
        List<Relationship> potentialRelationships = relationshipSelector.getBestRelationships(inputGraph, outputGraph, new ArrayList<Relationship>(), new HashSet<Relationship>(), null, 0);
        assertTrue( potentialRelationships.contains(rel) || potentialRelationships.contains(new Relationship(new BestColumnLearner(), Arrays.asList(filteredIdx), outIdx, filteredAtt)));


    }
}
