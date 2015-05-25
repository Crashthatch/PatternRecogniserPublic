package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SpecialCharacters;

import static org.junit.Assert.assertTrue;

public class TestSpecialCharacters {
	private static Table<Integer, Integer, String> in;
	private static TransformationService specialchars;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		specialchars = new SpecialCharacters();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testStripNonLetters() throws Exception
	{
		in.put(0, 0, "$hello_this HAS \t 9998 characters in £ it! \r\n Can you see them all???");
		out = specialchars.doWork(in);
		assertTrue( out.get(0,0).equals("$_£!???"));

	}
}
