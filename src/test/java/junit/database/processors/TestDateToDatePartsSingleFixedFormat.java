package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServiceReversible;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.DateToDatePartsSingleFixedFormat;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class TestDateToDatePartsSingleFixedFormat {
	private static TransformationServiceReversible transformerYMD;
    private static TransformationServiceReversible transformerAmerican;
    private static TransformationServiceReversible transformerUK;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformerYMD = new DateToDatePartsSingleFixedFormat("yyyy-MM-dd");
        transformerAmerican = new DateToDatePartsSingleFixedFormat("MM-dd-yyyy");
        transformerUK = new DateToDatePartsSingleFixedFormat("dd-MM-yyyy");
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testDateToDateParts() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "1987-11-05");
		Table<Integer, Integer, String> out = transformerYMD.doWork(in);
		assertTrue( out.get(0,0).equals("1987") );
		assertTrue( out.get(0,1).equals("11") );
		assertTrue( out.get(0,2).equals("05") );
	}
	
	@Test public void testAmericanDate() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "09-13-2001");
		Table<Integer, Integer, String> out = transformerAmerican.doWork(in);
		assertTrue( out.get(0,0).equals("2001") );
		assertTrue( out.get(0,1).equals("09") );
		assertTrue( out.get(0,2).equals("13") );
	}

    @Test public void testEnglishDate() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "13-09-2015");
        Table<Integer, Integer, String> out = transformerUK.doWork(in);
        assertTrue( out.get(0,0).equals("2015") );
        assertTrue( out.get(0,1).equals("09") );
        assertTrue( out.get(0,2).equals("13") );
    }

    @Test public void testUkParserFailsForUSDate() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "02-14-2015");
        Table<Integer, Integer, String> out = transformerUK.doWork(in);
        assertTrue( out.rowKeySet().size() == 0);
    }
	
	@Test public void testNotADate() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "asdfghjkl");
		Table<Integer, Integer, String> out = transformerYMD.doWork(in);
		assertTrue( out.rowKeySet().size() == 0 );
		assertTrue( out.columnKeySet().size() == 0 );
	}

    @Test public void testUkReverse() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "13-04-2015");
        Table<Integer, Integer, String> inter = transformerUK.doWork(in);

        Table<Integer, Integer, String> out = transformerUK.getReverseTransformer().doWork(inter);
        assertTrue( out.get(0,0).equals("13-04-2015"));
    }

    @Test public void testUkReverseToUS() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "13-04-2015");
        Table<Integer, Integer, String> inter = transformerUK.doWork(in);

        Table<Integer, Integer, String> out = transformerAmerican.getReverseTransformer().doWork(inter);
        assertTrue( out.get(0,0).equals("04-13-2015"));
    }

    @Test public void testStrictWorks() throws Exception{
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "1-4-2015");
        Table<Integer, Integer, String> out = transformerUK.doWork(in);

        //date format of "dd-MM-yyyy" should FAIL on 1-4-2015 because that is in format d-M-yyyy. Necessary for reversibleness.
        assertTrue( out.columnKeySet().size() == 0);
    }

}
