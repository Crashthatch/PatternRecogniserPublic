package junit.database.inout;

import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSomeOutputColumnsDuplicates {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	/**
	 * The input is 2 columns of similar times. In particular the year, month and day are identical for each pair of times.
	 * We parse column 1 to 7 new columns in the same table: Year, Month, Day, Hour, Minute, Second, Millisecond.
	 * We then parse column 2 to 7 new columns.
	 * In the error that prompted writing this test, the Year, Month and Day columns produced by the second date parser were being identified as "already existing"
	 *  and so not inserted, and the second processor only produced 4 outputAtts. 
	 * This confused the applyTree processor later because during application it created 7 columns, which was different to the number of columns it had produced in training,
	 *  so the atts from the test-run couldn't be mapped to those from the training run.
	 * @throws SQLException
	 */
	@Test public void testSomeOutputColumnDuplicatesNoNewTable() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/flightquest-testflights-first10-runwayDepartureAndArrival.csv"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToTable = new Processor( new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToTable.doWork();
		
		Att date1StringAtt = splitToTable.getOutputAtts().get(1);
		ArrayList<Att> date1StringAttAsList = new ArrayList<>();
		date1StringAttAsList.add(date1StringAtt);

		Processor dateCreator1 = new Processor( new DateToDateParts(), date1StringAttAsList, date1StringAtt.getDataTable() );
		dateCreator1.doWork();
		
		Att date2StringAtt = splitToTable.getOutputAtts().get(2);
		ArrayList<Att> date2StringAttAsList = new ArrayList<>();
		date2StringAttAsList.add(date2StringAtt);
		
		Processor dateCreator2 = new Processor( new DateToDateParts(), date2StringAttAsList, date2StringAtt.getDataTable() );
		dateCreator2.doWork(false); //Do not save duplicates.
		
		graph.createDot();
		
		assertTrue( dateCreator2.getOutputAtts().size() == 9);
		Att firstDateYearAtt = dateCreator1.getOutputAtts().get(0);
		Att firstDateHourAtt = dateCreator1.getOutputAtts().get(3);
		ArrayList<String> firstDateYearContents = firstDateYearAtt.getData();
		ArrayList<String> firstDateHourContents = firstDateHourAtt.getData();
		assertTrue( firstDateYearContents.size() == 9 );
		assertTrue( firstDateYearContents.get(0).equals( "2012" ) );
		assertTrue( firstDateHourContents.size() == 9 );
		assertTrue( firstDateHourContents.get(0).equals( "17" ) );
		
		Att secondDateYearAtt = dateCreator2.getOutputAtts().get(0);
		Att secondDateHourAtt = dateCreator2.getOutputAtts().get(3);
		ArrayList<String> secondDateYearContents = secondDateYearAtt.getData();
		ArrayList<String> secondDateHourContents = secondDateHourAtt.getData();
		assertTrue( secondDateYearContents.size() == 9 );
		assertTrue( secondDateYearContents.get(0).equals( "2012" ) );
		assertTrue( secondDateHourContents.size() == 9 );
		assertTrue( secondDateHourContents.get(0).equals( "18" ) );


        //Check the years for departure and arrival point to the same column in the database (ie. we didn't save it twice, and the duplicate removal is working).
        assertTrue( secondDateYearAtt.getDbColumnName().equals( firstDateYearAtt.getDbColumnName() ) );
	}

    /**
     * Tests applying 2 copies of the same processor (so one table is a duplicate of an entire table).
     * @throws SQLException
     */
    @Test public void testSomeOutputColumnDuplicatesNewTable() throws Exception
    {
        //Create the root "const" att which contains no data.
        AttRelationshipGraph graph = new AttRelationshipGraph();
        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/2 columns.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor splitToTable = new Processor( new ReadTSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
        splitToTable.doWork(false);

        Processor splitToTableAgain = new Processor( new ReadTSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
        splitToTableAgain.doWork(false);

        List<Att> outAtts = splitToTable.getOutputAtts();
        List<Att> outAttsAgain = splitToTableAgain.getOutputAtts();


        //Ensure it produced 2 output atts + an index col (there were 2 columns in the original data).
        assertEquals(outAtts.size(), 3);
        assertEquals(outAtts.size(), outAttsAgain.size());

        //Ensure the duplicate atts refer to the original atts columns in the DB (those produced by the initial splitToTable processor).
        assertEquals( outAtts.get(0).getDbColumnName(), outAttsAgain.get(0).getDbColumnName());
        assertEquals( outAtts.get(1).getDbColumnName(), outAttsAgain.get(1).getDbColumnName());
        assertEquals( outAtts.get(2).getDbColumnName(), outAttsAgain.get(2).getDbColumnName());
        

        //Ensure there are only 2 tables in the DB (the "rootRowAtt" table with 1 row, and the "expanded to rows" table with 5 rows).
        String SQL = "SHOW TABLES IN pattern_activedata";
        ResultSet result = Database.doQuery(SQL);
        //Test the values in the first row.
        result.next();
        String rootTable = result.getString(1);
        result.next();
        String tbl1 = result.getString(1);

        assertFalse( result.next() ); //No more tables exist.
    }


    /**
     * Tests that a processor that returns 2 identical columns only saves one of them.
     * Single-row-per-transformer, so inserts into the original table.
     * @throws SQLException
     */
    @Test public void testDuplicatesInOutputTableNoNewTable() throws Exception{

        //Create the root "const" att which contains no data.
        AttRelationshipGraph graph = new AttRelationshipGraph();
        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/identical columns.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor splitToRows = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
        splitToRows.doWork();

        Processor splitToCols = new Processor( new SplitOnStringToColumns("\t"), Arrays.asList(splitToRows.getOutputAtts().get(1)), splitToRows.getOutputAtts().get(1).getDataTable() );
        splitToCols.doWork(false);

        List<Att> outAtts = splitToCols.getOutputAtts();

        //Ensure it produced 2 output atts (there were 2 columns in the original data).
        assertEquals(outAtts.size(), 2);

        //Ensure the 2nd att shares a column with the first att since they are identical columns.
        assertEquals(outAtts.get(0).getDbColumnName(), outAtts.get(1).getDbColumnName());
        
        //Ensure there really is only one column in the DB.
        String SQL = "SELECT * FROM "+outAtts.get(0).getDataTable().getSubtableJoinClause();
        ResultSet result = Database.doQuery(SQL);
        //Test the values in the first row.
        result.next();
        String colId = result.getString(1);
        String colRef = result.getString(2);
        String colIdx = result.getString(3);
        String colBothCols = result.getString(4);
        String colCol1 = result.getString(5);
        String colCol2 = result.getString(6);

        assertTrue( colCol1.equals("5") );
        assertTrue( colCol2 == null);
    }

    /**
     * Tests a processor that returns multiple rows per root-row, so creates a new table, where two columns are identical.
     * @throws SQLException
     */
    @Test public void testDuplicatesInOutputTableNewTable() throws Exception
    {
        //Create the root "const" att which contains no data.
        AttRelationshipGraph graph = new AttRelationshipGraph();
        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/identical columns.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        Processor splitToTable = new Processor( new ReadTSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
        splitToTable.doWork(false);

        List<Att> outAtts = splitToTable.getOutputAtts();

        //Ensure it produced 2 output atts + an index col (there were 2 columns in the original data).
        assertEquals(outAtts.size(), 3);

        //Ensure the 2nd att shares a column with the first att since they are identical columns.
        assertEquals(outAtts.get(1).getDbColumnName(), outAtts.get(2).getDbColumnName());

        //Ensure there really is only one column in the DB.
        String SQL = "SELECT * FROM "+outAtts.get(0).getDataTable().getSubtableJoinClause();
        ResultSet result = Database.doQuery(SQL);
        //Test the values in the first row.
        result.next();
        String colId = result.getString(1);
        String colRef = result.getString(2);
        String colIdx = result.getString(3);
        String colCol1 = result.getString(4);
        String colCol2 = result.getString(5);

        assertTrue( colCol1.equals("5") );
        assertTrue( colCol2 == null);
    }


}
