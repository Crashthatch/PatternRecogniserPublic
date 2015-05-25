package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;

import static org.junit.Assert.assertTrue;

public class TestSplitOnStringReverse {
	private static TransformationServiceReversible transformer;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new SplitOnStringToRows(",");
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSplit() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "apple,banana");
		out = transformer.doWork(in);
		assertTrue( out.get(0,1).equals("apple") );
		assertTrue( out.get(1,1).equals("banana") );
	}
	
	@Test public void testReverse() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "0");
		in.put(1, 0, "1");
		in.put(0, 1, "apple");
		in.put(1, 1, "banana");
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("apple,banana") );
	}
	
	@Test public void testReverseSplitRegexRequireIndexes() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "1");
		in.put(1, 0, "0");
		in.put(0, 1, "banana");
		in.put(1, 1, "apple");
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("apple,banana") );
	}
	
	@Test public void testReverseSplitRegexFloatIndexes() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "0.0");
		in.put(1, 0, "1.0");
		in.put(0, 1, "apple");
		in.put(1, 1, "banana");
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("apple,banana") );
	}
	
	@Test public void testReverseSplitRegexFloatIndexesNotIntegers() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "0.00000001");
		in.put(1, 0, "0.99999999");
		in.put(2, 0, "3.00000012");
		in.put(3, 0, "1.99999999");
		in.put(0, 1, "apple");
		in.put(1, 1, "banana");
		in.put(2, 1, "orange");
		in.put(3, 1, "grape");
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("apple,banana,grape,orange") );
	}
	
	@Test public void testReverseLongerThan10() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "0");
		in.put(1, 0, "1");
		in.put(2, 0, "2");
		in.put(3, 0, "12");
		in.put(4, 0, "4");
		in.put(5, 0, "5");
		in.put(6, 0, "8");
		in.put(7, 0, "7");
		in.put(8, 0, "6");
		in.put(9, 0, "9");
		in.put(10, 0, "10");
		in.put(11, 0, "11");
		in.put(12, 0, "3");
		in.put(13, 0, "13");
		
		in.put(0, 1, "apple");
		in.put(1, 1, "banana");
		in.put(2, 1, "pineapple");
		in.put(3, 1, "pear");
		in.put(4, 1, "peach");
		in.put(5, 1, "orange");
		in.put(6, 1, "clementine");
		in.put(7, 1, "lemon");
		in.put(8, 1, "lime");
		in.put(9, 1, "kiwi");
		in.put(10, 1, "grape");
		in.put(11, 1, "grapefruit");
		in.put(12, 1, "pomegranete");
		in.put(13, 1, "tomato");
		
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("apple,banana,pineapple,pomegranete,peach,orange,lime,lemon,clementine,kiwi,grape,grapefruit,pear,tomato") );
	}
	
	@Test public void testReverseSplitRegexSomeIndicesMissing() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "4");
		in.put(1, 0, "3");
		in.put(0, 1, "banana");
		in.put(1, 1, "apple");
		TransformationService reverseTransformer = transformer.getReverseTransformer();
		out = reverseTransformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("apple,banana") );
	}
	
}
