package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.ReverseJsonDecodeList;
import processors.jsonDecodeList;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TestJsonDecodeList {
	private static Table<Integer, Integer, String> in;
	private static TransformationService transformer;
	private static TransformationService reverseTransformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new jsonDecodeList();
		reverseTransformer = new ReverseJsonDecodeList();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{
	}
	
	@Test public void test() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "[3,5,7,9,4,4]");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 6);
		assertTrue( out.columnKeySet().size() == 2);
		
		//Index column
		assertTrue( out.get(0, 0).equals("0"));
		assertTrue( out.get(3, 0).equals("3"));
		assertTrue( out.get(5, 0).equals("5"));
		
		//Value column
		assertTrue( out.get(0, 1).equals("3"));
		assertTrue( out.get(3, 1).equals("9"));
		assertTrue( out.get(5, 1).equals("4"));
	}
	
	@Test public void testStrings() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "[\"banana\",\"aardvark\"]");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);
		
		//Index column
		assertTrue( out.get(0, 0).equals("0"));
		assertTrue( out.get(1, 0).equals("1"));
		
		//Value column
		assertTrue( out.get(0, 1).equals("\"banana\""));
		assertTrue( out.get(1, 1).equals("\"aardvark\""));
	}
	
	@Test public void testReverse() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "0");
		in.put(0, 1, "3");
		
		in.put(1, 0, "1");
		in.put(1, 1, "2");
		
		in.put(2, 0, "2");
		in.put(2, 1, "9");
		
		in.put(3, 0, "3");
		in.put(3, 1, "7");
		
		in.put(4, 0, "4");
		in.put(4, 1, "9");
		out = reverseTransformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		
		assertTrue( out.get(0,0).equals("[3,2,9,7,9]"));
	}
	
	@Test public void testReverseOutOfOrder() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "0");
		in.put(0, 1, "3");
		
		in.put(1, 0, "2");
		in.put(1, 1, "2");
		
		in.put(2, 0, "4");
		in.put(2, 1, "9");
		
		in.put(3, 0, "1");
		in.put(3, 1, "7");
		
		in.put(4, 0, "3");
		in.put(4, 1, "9");
		out = reverseTransformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		
		assertTrue( out.get(0,0).equals("[3,7,2,9,9]"));
	}
	
	@Test public void testForwardThenReverse() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> midWay;
		String originalString = "[\"a\",\"c\",\"d\",\"c\"]";
		in.put(0, 0, originalString);
		midWay = transformer.doWork(in);
		out = reverseTransformer.doWork(midWay);
		
		assertTrue( out.get(0,0).equals(originalString));
	}
	
	@Test public void testForwardThenReverseManyNested() throws Exception
	{
		Table<Integer, Integer, String> out;
		Table<Integer, Integer, String> midWay;
		String originalString = FileUtils.readFileToString(new File("testdata/jsonlistofmaps.dat"));
		in.put(0, 0, originalString);
		midWay = transformer.doWork(in);
		out = reverseTransformer.doWork(midWay);
		
		assertTrue( JSONValue.parse(out.get(0,0)).equals(JSONValue.parse(originalString)));
	}

	@Test public void testLongDoubles() throws Exception
	{
		in.put(0, 0, "[51.384751000000001, -2.3831899999999999]");
		Table<Integer, Integer, String> out = transformer.doWork(in);

		assertTrue( out.get(0,1).equals("51.384751000000001"));
		assertTrue( out.get(1,1).equals("-2.3831899999999999"));
	}

	@Test public void testReverseLongDoubles() throws Exception
	{
		String originalString = "[51.384751000000001,-2.3831899999999999]";
		in.put(0, 0, originalString );
		Table<Integer, Integer, String> intermediate = transformer.doWork(in);

		Table<Integer, Integer, String> out = reverseTransformer.doWork(intermediate);

		System.out.println(out.get(0,0));
		assertTrue( out.get(0,0).equals( originalString ));
	}
}
