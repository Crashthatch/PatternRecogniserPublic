package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.escapeSpaces;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class TestEscapeSpaces {
	private static TransformationService transformer;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new escapeSpaces();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSingleLine() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "this is a test");
		Table<Integer, Integer, String> out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("this\\ is\\ a\\ test") );
	}
	
	@Test public void testMultipleLines() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "this is a \nmultiline test");
		Table<Integer, Integer, String> out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("this\\ is\\ a\\ \nmultiline\\ test") );
	}


    @Test public void testMultipleRNLines() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "this is a \r\nmultiline test");
        Table<Integer, Integer, String> out = transformer.doWork(in);
        assertTrue( out.get(0,0).equals("this\\ is\\ a\\ \r\nmultiline\\ test") );
    }
}
