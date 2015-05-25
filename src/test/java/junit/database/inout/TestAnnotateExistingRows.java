package junit.database.inout;

import database.Att;
import database.AttRelationshipGraph;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestAnnotateExistingRows {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testAnnotatesExistingRows() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/4,5,7,8,6.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor separator = new Processor( new SplitOnStringToRows(","), importer.getOutputAtts(), longStringAtt.getDataTable() );
		separator.doWork();
		
		Att numbersColumn = separator.getOutputAtts().get(1);

        Processor rowNumberer = new Processor( new rownum(), Arrays.asList(numbersColumn), longStringAtt.getDataTable());
        rowNumberer.doWork();

        assertEquals(5, rowNumberer.getOutputAtts().get(0).getNotNullRowsInTable() );
        assertEquals("0", rowNumberer.getOutputAtts().get(0).getFirstRow());
        assertEquals("4", rowNumberer.getOutputAtts().get(0).getData().get(4));
	}

    /*
     * Tries to renumber the rows with indexes that are the same as the indexes produced by splitting.
     * So att is identified as a duplicate and should not be saved to the DB.
     */
    @Test public void testAnnotatesExistingRowsDontSaveDuplicates() throws Exception
    {
        //Create the root "const" att which contains no data.
        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/4,5,7,8,6.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor separator = new Processor( new SplitOnStringToRows(","), importer.getOutputAtts(), longStringAtt.getDataTable() );
        separator.doWork();

        Att idxColumn = separator.getOutputAtts().get(0);
        Att numbersColumn = separator.getOutputAtts().get(1);

        Processor rowNumberer = new Processor( new rownum(), Arrays.asList(numbersColumn), longStringAtt.getDataTable());
        rowNumberer.doWork(false);

        Att reindexedColumn = rowNumberer.getOutputAtts().get(0);


        assertTrue( reindexedColumn.isDuplicate() );
        assertEquals( idxColumn.getDbColumnName(), reindexedColumn.getDbColumnName());
    }


    //Similar to the TestFilterOneOrNull test, but without creating the output tree or doing the learn-relationship part at the end.
    @Test public void testAnnotatesExistingFilteredRows() throws Exception{
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor(new getInputFromFile("testdata/school holidays - keep with Y/trainIn.dat"), inputAtts, constant.getDataTable());
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor splitCSV = new Processor(new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable());
        splitCSV.doWork();

        Att inIdx = splitCSV.getOutputAtts().get(0);
        Att dateAtt = splitCSV.getOutputAtts().get(1);
        Att yAtt = splitCSV.getOutputAtts().get(2);


        Processor filterOutNonYs = new Processor(new FilterFalsey(), Arrays.asList(yAtt), yAtt.getDataTable());
        filterOutNonYs.doWork();

        Att filteredAtt = filterOutNonYs.getOutputAtts().get(0);

        Processor indexFiltered = new Processor(new rownum(), Arrays.asList(filteredAtt), longStringAtt.getDataTable());
        indexFiltered.doWork(false); //Do not create duplicates.

        Att filteredIdx = indexFiltered.getOutputAtts().get(0);

        assertFalse(filteredIdx.isDuplicate());
        assertTrue( filteredIdx.getNotNullRowsInTable() == 4 );
        assertTrue( filteredIdx.getData().get(3).equals("3") );
    }

}
