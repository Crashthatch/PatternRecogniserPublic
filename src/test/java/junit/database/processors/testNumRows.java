package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.NumRows;

import static org.junit.Assert.assertTrue;

public class testNumRows {
	private static Table<Integer, Integer, String> in;
	private static TransformationService numRows;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		numRows = new NumRows();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testOne() throws Exception
	{
        in = TreeBasedTable.create();
		in.put(0, 0, "<html><head><title>Test HTML 123</title></head> <body><ul><li>One</li><li>Two</li><li>Three</li></ul></body></html>");
		out = numRows.doWork(in);
		assertTrue( out.get(0,0).equals("1"));

	}

    @Test public void testZero() throws Exception
    {
        in = TreeBasedTable.create();
        out = numRows.doWork(in);
        assertTrue( out.get(0,0).equals("0"));

    }
	
	@Test public void testMany() throws Exception
	{
        in = TreeBasedTable.create();
		in.put(0, 0, "143");
        in.put(1, 0, "144");
        in.put(2, 0, "182");
        in.put(3, 0, "156");

		out = numRows.doWork(in);
		assertTrue( out.get(0,0).equals("4"));

	}
	
	@Test public void testManyWithDuplicates() throws Exception
	{
        in = TreeBasedTable.create();
        in.put(0, 0, "143");
        in.put(1, 0, "143");
        in.put(2, 0, "144");
        in.put(3, 0, "182");
        in.put(4, 0, "143");
        in.put(5, 0, "156");

		out = numRows.doWork(in);
		assertTrue( out.get(0,0).equals("6") );
	}	

}
