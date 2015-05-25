package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.AlphabetLetters;
import processors.stripDoubleQuotes;
import processors.stripPunctuation;

import static org.junit.Assert.assertTrue;

public class testAlphas {
	private static Table<Integer, Integer, String> in;
	private static TransformationService alphas;
	private static TransformationService stripPunc;
	private static TransformationService stripDoubleQuotes;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		alphas = new AlphabetLetters();
		stripPunc = new stripPunctuation();
		stripDoubleQuotes = new stripDoubleQuotes();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testStripNonLetters() throws Exception
	{
		in.put(0, 0, "<html><head><title>Test HTML 123</title></head> <body><ul><li>One</li><li>Two</li><li>Three</li></ul></body></html>");
		out = alphas.doWork(in);
		assertTrue( out.get(0,0).equals("htmlheadtitleTestHTMLtitleheadbodyulliOneliliTwoliliThreeliulbodyhtml"));

	}
	
	@Test public void testStripPunc() throws Exception
	{
		in.put(0, 0, "String? /with\\ <12.3> *funny* \"character's\" in!__^$%");
		out = stripPunc.doWork(in);
		assertTrue( out.get(0,0).equals("String with 123 funny characters in"));

	}
	
	@Test public void testStripPuncKeepQuotes() throws Exception
	{
		in.put(0, 0, "String containing \"Double quotes\".");
		out = stripDoubleQuotes.doWork(in);
		assertTrue( out.get(0,0).equals("String containing Double quotes."));

	}	

}
