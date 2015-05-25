package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.md5Values;

import static org.junit.Assert.assertTrue;

public class TestMD5 {
	private static TransformationService transformer;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new md5Values();
		
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSimpleMd5() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "a");
		out = transformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("0cc175b9c0f1b6a831c399e269772661"));
	}
	
	@Test public void testEmptyStringMd5() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "");
		out = transformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("d41d8cd98f00b204e9800998ecf8427e"));
	}
	
	@Test public void testEmptyTableMd5() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		out = transformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("d41d8cd98f00b204e9800998ecf8427e"));
	}
	
	@Test public void testMultiRow() throws Exception
	{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "a");
		in.put(1, 0, "b");
		in.put(2, 0, "c");
		out = transformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("900150983cd24fb0d6963f7d28e17f72"));
	}

}
