package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServicePartialRowsReversible;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.UnwrapDoubleQuotes;

import static org.junit.Assert.assertTrue;

public class TestUnwrapDoubleQuotes {
	private static TransformationServicePartialRowsReversible transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new UnwrapDoubleQuotes();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		
		in.put(0, 0, "\"aaa\"");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		
		assertTrue( out.get(0, 0).equals("aaa") );
	}
	
	@Test public void testNoQuotes() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "aaa");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 0);
		assertTrue( out.columnKeySet().size() == 0);
	}

	@Test public void testWrap() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "aaa");
		out = transformer.getReverseTransformer().doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		
		assertTrue( out.get(0, 0).equals("\"aaa\"") );
	}
	
	
	
	@Test public void testForwardsBackwards() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> mid;
		String originalString = "\"string\"";
		in.put(0, 0, originalString);
		mid = transformer.doWork(in);
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(mid); 
		
		assertTrue( originalString.equals(out.get(0,0)) );
	}

    /* not reversible because it returns NULL for any inputs that don't have " at the start and end.
	@Test public void testForwardsBackwardsThisIsWhyItsNotFullyReversible() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> mid;
		String originalString = "string";
		in.put(0, 0, originalString);
		mid = transformer.doWork(in);
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(mid);

		assertTrue( originalString.equals(out.get(0,0)) );
	}*/
			
}
