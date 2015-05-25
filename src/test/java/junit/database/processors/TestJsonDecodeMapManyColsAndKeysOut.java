package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.JsonDecodeMapManyColsAndKeysOut;

import static org.junit.Assert.assertTrue;

public class TestJsonDecodeMapManyColsAndKeysOut {
	private static TransformationServiceReversible transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new JsonDecodeMapManyColsAndKeysOut();
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
	
	@Test public void testMultipleRows() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}");
		in.put(1, 0, "{\"name\": \"Phil\", \"age\": 20, \"food\":[\"chips\"]}");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 6);
		
		//Row1
		assertTrue( out.get(0, 0).equals("age") );
		assertTrue( out.get(0, 1).equals("19") );
		assertTrue( out.get(0, 2).equals("food") );
		assertTrue( out.get(0, 3).equals("[\"chips\",\"peas\"]") );
		assertTrue( out.get(0, 4).equals("name") );
		assertTrue( out.get(0, 5).equals("\"Bob\"") );
		
		//Row2
		assertTrue( out.get(1, 0).equals("age") );
		assertTrue( out.get(1, 1).equals("20") );
		assertTrue( out.get(1, 2).equals("food") );
		assertTrue( out.get(1, 3).equals("[\"chips\"]") );
		assertTrue( out.get(1, 4).equals("name") );
		assertTrue( out.get(1, 5).equals("\"Phil\"") );
	}
	
	@Test public void testChangedHashOrder() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}");
		in.put(1, 0, "{\"food\":[\"chips\"], \"name\": \"Phil\", \"age\": 20}");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 6);
		
		//Row1
		assertTrue( out.get(0, 0).equals("age") );
		assertTrue( out.get(0, 1).equals("19") );
		assertTrue( out.get(0, 2).equals("food") );
		assertTrue( out.get(0, 3).equals("[\"chips\",\"peas\"]") );
		assertTrue( out.get(0, 4).equals("name") );
		assertTrue( out.get(0, 5).equals("\"Bob\"") );
		
		//Row2
		assertTrue( out.get(1, 0).equals("age") );
		assertTrue( out.get(1, 1).equals("20") );
		assertTrue( out.get(1, 2).equals("food") );
		assertTrue( out.get(1, 3).equals("[\"chips\"]") );
		assertTrue( out.get(1, 4).equals("name") );
		assertTrue( out.get(1, 5).equals("\"Phil\"") );
	}
	
	@Test public void testMissingFields() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "{\"name\": \"Bob\", \"food\":[\"chips\",\"peas\"]}");
		in.put(1, 0, "{\"name\": \"Phil\", \"age\": 20}");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 6);
		
		//Row1
		assertTrue( out.get(0, 0) == null );
		assertTrue( out.get(0, 1) == null );
		assertTrue( out.get(0, 2).equals("food") );
		assertTrue( out.get(0, 3).equals("[\"chips\",\"peas\"]") );
		assertTrue( out.get(0, 4).equals("name") );
		assertTrue( out.get(0, 5).equals("\"Bob\"") );
		
		//Row2
		assertTrue( out.get(1, 0).equals("age") );
		assertTrue( out.get(1, 1).equals("20") );
		assertTrue( out.get(1, 2) == null );
		assertTrue( out.get(1, 3) == null );
		assertTrue( out.get(1, 4).equals("name") );
		assertTrue( out.get(1, 5).equals("\"Phil\"") );
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
	
	@Test public void testReverseMultipleRows() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> mid;
		String originalStringRow1 = "{\"name\": \"Bob\", \"age\": 25}";
		String originalStringRow2 = "{\"name\": \"Phil\", \"age\": 20}";
		in.put(0,0, originalStringRow1);
		in.put(1,0, originalStringRow2);
		mid = transformer.doWork(in);
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(mid); 
		
		//Check row1
		Object originalParsed = JSONValue.parse(originalStringRow1);
		Object processedParsed = JSONValue.parse(out.get(0, 0));
		assertTrue( originalParsed.equals(processedParsed) );
		
		//row2
		assertTrue( JSONValue.parse(originalStringRow2).equals(JSONValue.parse(out.get(1, 0))) );
	}
	
	@Test public void testReverseMissingFields() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> mid;
		String originalStringRow1 = "{\"name\": \"Bob\", \"food\":[\"chips\",\"peas\"]}";
		String originalStringRow2 = "{\"name\": \"Phil\", \"age\": 20}";
		in.put(0,0, originalStringRow1);
		in.put(1,0, originalStringRow2);
		mid = transformer.doWork(in);
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(mid); 
		
		//Check row1
		Object originalParsed = JSONValue.parse(originalStringRow1);
		Object processedParsed = JSONValue.parse(out.get(0, 0));
		assertTrue( originalParsed.equals(processedParsed) );
		
		//row2
		assertTrue( JSONValue.parse(originalStringRow2).equals(JSONValue.parse(out.get(1, 0))) );
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
