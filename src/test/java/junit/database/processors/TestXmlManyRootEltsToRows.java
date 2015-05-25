package junit.database.processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.xmlManyRootEltsToRows;

import static org.junit.Assert.assertTrue;

public class TestXmlManyRootEltsToRows {
	private static TransformationService transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new xmlManyRootEltsToRows();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<elt att-name=\"attValue\">A</elt>" +
			"<secondelt att-two=\"Attribute Two\">B</secondelt>"+
			"<elt att-name=\"Attribute Three\">C</elt>");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 3);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 1).equals("<elt att-name=\"attValue\">A</elt>") );
		assertTrue( out.get(1, 1).equals("<secondelt att-two=\"Attribute Two\">B</secondelt>") );
		assertTrue( out.get(2, 1).equals("<elt att-name=\"Attribute Three\">C</elt>") );
	}


	@Test public void testSplitShortClosingTagRootElts() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<elt att-name=\"attValue\">AAA</elt>" +
			"<secondelt att-two=\"Attribute Two\"/>"+
			"<elt att-name=\"Attribute Three\"/>");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 3);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 1).equals("<elt att-name=\"attValue\">AAA</elt>") );
		assertTrue( out.get(1, 1).equals("<secondelt att-two=\"Attribute Two\"/>") );
		assertTrue( out.get(2, 1).equals("<elt att-name=\"Attribute Three\"/>") );
	}

	@Test public void testSplitWithXMLDeclaration() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "" +
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
			"<elt att-name=\"attValue\">AAA</elt>" +
			"<secondelt att-two=\"Attribute Two\"/>");
		out = transformer.doWork(in);

		System.out.println(out);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 1).equals("<elt att-name=\"attValue\">AAA</elt>") );
		assertTrue( out.get(1, 1).equals("<secondelt att-two=\"Attribute Two\"/>") );
	}

	@Test public void testNestedElts() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "<elt att-name=\"attValue\">" +
			"	<child>Text</child>" +
			"	<child abc=\"123\">MoreText</child>" +
			"</elt>" +
			"<secondelt att-two=\"Attribute Two\"/>");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 1).equals("<elt att-name=\"attValue\">" +
			"	<child>Text</child>" +
			"	<child abc=\"123\">MoreText</child>" +
			"</elt>") );
		assertTrue( out.get(1, 1).equals("<secondelt att-two=\"Attribute Two\"/>") );
	}
}
