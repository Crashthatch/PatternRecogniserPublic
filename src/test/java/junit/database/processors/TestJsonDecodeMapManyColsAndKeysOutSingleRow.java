package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.JsonDecodeMapManyColsAndKeysOutSingleRow;

import static org.junit.Assert.assertTrue;

public class TestJsonDecodeMapManyColsAndKeysOutSingleRow {
	private static TransformationServiceReversible transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new JsonDecodeMapManyColsAndKeysOutSingleRow();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		
		in.put(0, 0, "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 6);
		
		//Order of columns is alphabetical by key.
		assertTrue( out.get(0, 0).equals("age") );
		assertTrue( out.get(0, 1).equals("19") );
		assertTrue( out.get(0, 2).equals("food") );
		assertTrue( out.get(0, 3).equals("[\"chips\",\"peas\"]") );
		assertTrue( out.get(0, 4).equals("name") );
		assertTrue( out.get(0, 5).equals("\"Bob\"") );
	}
	
	@Test public void testChangedHashOrder() throws Exception
	{
		//Hash order should be the same for each row, provided the names of the keys did not change.
		Table<Integer, Integer, String> in1 = TreeBasedTable.create();
		Table<Integer, Integer, String> in2 = TreeBasedTable.create();
		Table<Integer, Integer, String> out1;
		Table<Integer, Integer, String> out2;
		
		in1.put(0, 0, "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}");
		out1 = transformer.doWork(in1);
		in2.put(0, 0, "{\"food\":[\"chips\"], \"name\": \"Phil\", \"age\": 20}");
		out2 = transformer.doWork(in2);
		
		//Check dimensions.
		assertTrue( out1.rowKeySet().size() == 1);
		assertTrue( out1.columnKeySet().size() == 6);
		assertTrue( out2.rowKeySet().size() == 1);
		assertTrue( out2.columnKeySet().size() == 6);
		
		//Row1
		assertTrue( out1.get(0, 0).equals("age") );
		assertTrue( out1.get(0, 1).equals("19") );
		assertTrue( out1.get(0, 2).equals("food") );
		assertTrue( out1.get(0, 3).equals("[\"chips\",\"peas\"]") );
		assertTrue( out1.get(0, 4).equals("name") );
		assertTrue( out1.get(0, 5).equals("\"Bob\"") );
		
		//Row2
		assertTrue( out2.get(0, 0).equals("age") );
		assertTrue( out2.get(0, 1).equals("20") );
		assertTrue( out2.get(0, 2).equals("food") );
		assertTrue( out2.get(0, 3).equals("[\"chips\"]") );
		assertTrue( out2.get(0, 4).equals("name") );
		assertTrue( out2.get(0, 5).equals("\"Phil\"") );
	}
	
	
	@Test public void testReverse() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> mid;
		String originalString = "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}";
		in.put(0, 0, originalString);
		mid = transformer.doWork(in);
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(mid); 
		
		Object originalParsed = JSONValue.parse(originalString);
		Object processedParsed = JSONValue.parse(out.get(0, 0));
		
		assertTrue( originalParsed.equals(processedParsed) );
	}

	@Test public void testLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		//Too long to fit in a Double.
		in.put(0, 0, "{\"lat\": 51.384751000000001, \"long\": -2.3831899999999999}");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue(out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 4);

		assertTrue( out.get(0, 0).equals("lat") );
		assertTrue( out.get(0, 1).equals("51.384751000000001") );
		assertTrue( out.get(0, 2).equals("long") );
		assertTrue( out.get(0, 3).equals("-2.3831899999999999") );
	}

	@Test public void testNestedLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "{\"location\":{\"lat\":51.384751000000001,\"long\":-2.3831899999999999}}");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 2);

		//Technically this test is a bit strong. Whitespace doesn't matter.
		assertTrue( out.get(0, 0).equals("location") );
		assertTrue( out.get(0, 1).equals("{\"lat\":51.384751000000001,\"long\":-2.3831899999999999}") );
	}

	@Test public void testReverseLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		//Too long to fit in a Double.
		String originalString = "{\"lat\":51.384751000000001,\"long\":-2.3831899999999999}";
		in.put(0, 0, originalString);
		Table<Integer, Integer, String> mid = transformer.doWork(in);

		out = transformer.getReverseTransformer().doWork(mid);

		assertTrue(out.get(0, 0).equals(originalString));
	}

	@Test public void testReverseNestedLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		//Too long to fit in a Double.
		String originalString = "{\"location\":{\"lat\":51.384751000000001,\"long\":-2.3831899999999999}}";
		in.put(0, 0, originalString);
		Table<Integer, Integer, String> mid = transformer.doWork(in);

		out = transformer.getReverseTransformer().doWork(mid);

		assertTrue(out.get(0, 0).equals(originalString));
	}
}
