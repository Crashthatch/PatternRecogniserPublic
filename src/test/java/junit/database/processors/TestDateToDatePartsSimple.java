package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.DateToDatePartsSimple;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class TestDateToDatePartsSimple {
	private static TransformationService transformer;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new DateToDatePartsSimple();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testDateToDateParts() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "1987-11-05");
		Table<Integer, Integer, String> out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("1987") );
		assertTrue( out.get(0,1).equals("11") );
		assertTrue( out.get(0,2).equals("05") );
	}
	
	@Test public void testAmericanDate() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "09-13-2001");
		Table<Integer, Integer, String> out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("2001") );
		assertTrue( out.get(0,1).equals("09") );
		assertTrue( out.get(0,2).equals("13") );
	}

    @Test public void testEnglishDate() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "13/09/2015");
        Table<Integer, Integer, String> out = transformer.doWork(in);
        assertTrue( out.get(0,0).equals("2015") );
        assertTrue( out.get(0,1).equals("09") );
        assertTrue( out.get(0,2).equals("13") );
    }
	
	@Test public void testNotADate() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "asdfghjkl");
		Table<Integer, Integer, String> out = transformer.doWork(in);
		assertTrue( out.rowKeySet().size() == 0 );
		assertTrue( out.columnKeySet().size() == 0 );
	}

}
