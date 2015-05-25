package junit.database.processors.broken;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.isANoun;

import static org.junit.Assert.assertTrue;

public class TestIsANoun {
	private static Table<Integer, Integer, String> in;
	private static TransformationService transformer;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new isANoun();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testLowercaseNoun() throws Exception
	{
        assertTrue(false);
		in.put(0, 0, "apple");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0) == "1" );
	}
	
	@Test public void testUppercaseNoun() throws Exception{
		in.put(0, 0, "Apple");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0) == "1" );
	}
	
	@Test public void testVerb() throws Exception{
		in.put(0, 0, "devour");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0) == "0" );
	}
	
	@Test public void testNotAWord() throws Exception{
		in.put(0, 0, "afjidjasifjdisao");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0) == "0" );
	}
	@Test public void testNotAlpha() throws Exception{
		in.put(0, 0, "fdjsai fjdia @ []");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0) == "0" );
	}
		
}
