package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.htmlText;

import static org.junit.Assert.assertTrue;

public class TestHtmlText {
	private static Table<Integer, Integer, String> in;
	private static TransformationService htmlText;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		htmlText = new htmlText();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testHtmlText() throws Exception
	{
		in.put(0, 0, "<html><head><title>Test HTML</title></head> <body><ul><li>One</li><li>Two</li><li>Three</li></ul></body></html>");
		out = htmlText.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("Test HTML One Two Three"));

	}

}
