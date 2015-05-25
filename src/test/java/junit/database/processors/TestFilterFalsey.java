package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.FilterFalsey;

import static org.junit.Assert.assertTrue;

public class TestFilterFalsey {
    private static TransformationService transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
        transformer = new FilterFalsey();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testOne() throws Exception
	{
		Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "1");
		out = transformer.doWork(in);

		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		assertTrue( out.get(0,0).equals("1") );
	}

    @Test public void testWord() throws Exception
    {
        Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "apple");
        out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.get(0,0).equals("1") );
    }

    @Test public void testZero() throws Exception
    {
        Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "0");
        out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 0);
        assertTrue( out.columnKeySet().size() == 0);
    }

    @Test public void testPointZero() throws Exception
    {
        Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "0.0");
        out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 0);
        assertTrue( out.columnKeySet().size() == 0);
    }

    @Test public void testEmptyString() throws Exception
    {
        Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "");
        out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 0);
        assertTrue( out.columnKeySet().size() == 0);
    }
}
