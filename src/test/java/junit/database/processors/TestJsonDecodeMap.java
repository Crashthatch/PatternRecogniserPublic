package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.jsonDecodeMap;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestJsonDecodeMap {
	private static Table<Integer, Integer, String> in;
	private static TransformationServiceReversible transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new jsonDecodeMap();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "{\"name\": \"Bob\", \"age\": 19, \"food\":[\"chips\",\"peas\"]}");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 3);
		assertTrue( out.columnKeySet().size() == 2);
		
		//First row
		for(int i=0; i<3; i++){
			Map<Integer, String> row = out.row(i);
			switch( row.get(0) ){
			case "name":
				assertTrue( row.get(1).equals("\"Bob\"") );
				break;
			case "age":
				assertTrue( row.get(1).equals("19") );
				break;
			case "food":
				assertTrue( row.get(1).equals("[\"chips\",\"peas\"]") );
				break;
			}
		}
	}
	
	@Test public void testReverse() throws Exception
	{
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
		assertTrue(out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);

		assertTrue( out.get(0, 0).equals("lat") );
		assertTrue( out.get(0, 1).equals("51.384751000000001") );
		assertTrue( out.get(1, 0).equals("long") );
		assertTrue( out.get(1, 1).equals("-2.3831899999999999") );
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
