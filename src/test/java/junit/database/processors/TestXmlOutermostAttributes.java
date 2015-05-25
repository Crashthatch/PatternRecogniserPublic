package junit.database.processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.HtmlXpathSelector;
import processors.xmlOutermostAttributes;

import static org.junit.Assert.assertTrue;

public class TestXmlOutermostAttributes {
	private static TransformationService transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new xmlOutermostAttributes();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<elt att-name=\"attValue\"></elt>");
		out = transformer.doWork(in);
		transformer.checkOutputDimensions(in, out);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 0).equals("att-name") );
		assertTrue( out.get(0, 1).equals("attValue") );
	}

	@Test public void testMultipleAtts() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<elt abc=\"123\" def=\"456\"></elt>");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 0).equals("abc") );
		assertTrue( out.get(0, 1).equals("123") );
		assertTrue( out.get(1, 0).equals("def") );
		assertTrue( out.get(1, 1).equals("456") );
	}

	@Test public void testAlphabeticalOrder() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<parameter key=\"return_preprocessing_model\" value=\"false\" confusingatt=\"ignoreme\" />");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 3);
		assertTrue( out.columnKeySet().size() == 2);

		System.out.println(out);

		assertTrue( out.get(0, 0).equals("confusingatt") );
		assertTrue( out.get(0, 1).equals("ignoreme") );
		assertTrue( out.get(1, 0).equals("key") );
		assertTrue( out.get(1, 1).equals("return_preprocessing_model") );
		assertTrue( out.get(2, 0).equals("value") );
		assertTrue( out.get(2, 1).equals("false") );
	}

	@Test public void testExtractAttsFromEltWithChildren() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<elt abc=\"123\" def=\"456\">" +
			"<child xyz=\"999\">Text!</child>"+
			"<child xyz=\"888\">More Text!</child>"+
			"</elt>");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 0).equals("abc") );
		assertTrue( out.get(0, 1).equals("123") );
		assertTrue( out.get(1, 0).equals("def") );
		assertTrue( out.get(1, 1).equals("456") );
	}

}
