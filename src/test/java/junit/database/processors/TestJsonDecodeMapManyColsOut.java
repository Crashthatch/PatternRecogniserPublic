package junit.database.processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.JsonDecodeMapManyColsAndKeysOut;
import processors.JsonDecodeMapManyColsOut;

import static org.junit.Assert.assertTrue;

public class TestJsonDecodeMapManyColsOut {
	private static TransformationService transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new JsonDecodeMapManyColsOut();
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
		assertTrue( out.columnKeySet().size() == 3);

		//Order of columns is alphabetical by key. ie. age, food, name.
		assertTrue( out.get(0, 0).equals("19") );
		assertTrue( out.get(0, 1).equals("[\"chips\",\"peas\"]") );
		assertTrue( out.get(0, 2).equals("\"Bob\"") );
	}

	@Test public void testChangedHashOrder() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}");
		Table<Integer, Integer, String> out1 = transformer.doWork(in);

		in.put(0, 0, "{\"food\":[\"chips\"], \"name\": \"Phil\", \"age\": 20}");
		Table<Integer, Integer, String> out2 = transformer.doWork(in);

		
		//Check dimensions.
		assertTrue( out1.rowKeySet().size() == 1);
		assertTrue( out1.columnKeySet().size() == 3);
		
		//Row1
		assertTrue( out1.get(0, 0).equals("19") );
		assertTrue( out1.get(0, 1).equals("[\"chips\",\"peas\"]") );
		assertTrue( out1.get(0, 2).equals("\"Bob\"") );

		//Row2
		assertTrue( out2.get(0, 0).equals("20") );
		assertTrue( out2.get(0, 1).equals("[\"chips\"]") );
		assertTrue( out2.get(0, 2).equals("\"Phil\"") );
	}


	@Test public void testLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		//Too long to fit in a Double.
		in.put(0, 0, "{\"lat\": 51.384751000000001, \"long\": -2.3831899999999999}");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 0).equals("51.384751000000001") );
		assertTrue( out.get(0, 1).equals("-2.3831899999999999") );
	}

	@Test public void testNestedLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "{\"location\":{\"lat\":51.384751000000001,\"long\":-2.3831899999999999}}");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);

		//Technically this test is a bit strong. Whitespace doesn't matter.
		assertTrue( out.get(0, 0).equals("{\"lat\":51.384751000000001,\"long\":-2.3831899999999999}") );
	}

	@Test public void testVeryLongDecimal() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "{\"lat\": 51.3847510000000000000000000000000000000000000000000000000001}");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);

		assertTrue( out.get(0, 0).equals("51.3847510000000000000000000000000000000000000000000000000001") );
	}

	@Test public void testTrailingZeros() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "{\"lat\": 51.300}");
		out = transformer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);

		assertTrue( out.get(0, 0).equals("51.300") );
	}

	@Test public void testNotAMap() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> in = TreeBasedTable.create();

		in.put(0, 0, "[5,10,15,20]");
		out = transformer.doWork(in);

		//Returns an empty table.
		assertTrue( out.columnKeySet().size() == 0);
		assertTrue( out.rowKeySet().size() == 0);
	}
}
