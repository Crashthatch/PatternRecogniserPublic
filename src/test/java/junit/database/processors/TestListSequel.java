package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.ListSequel;

import static org.junit.Assert.assertTrue;

public class TestListSequel {
	private static Table<Integer, Integer, String> in;
	private static TransformationService transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new ListSequel();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testListNeighbours2() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "a");
		in.put(1, 0, "b");
		in.put(2, 0, "c");
		in.put(3, 0, "d");
		in.put(4, 0, "e");
		in.put(5, 0, "f");
		in.put(6, 0, "g");
		out = transformer.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 6);
		assertTrue( out.columnKeySet().size() == 2);
		
		//First row
		assertTrue( out.get(0,0).equals("a") );
		assertTrue( out.get(0,1).equals("b") );
		
		//Second row
		assertTrue( out.get(1,0).equals("b") );
		assertTrue( out.get(1,1).equals("c") );
		
		//Last Row
		assertTrue( out.get(5,0).equals("f") );
		assertTrue( out.get(5,1).equals("g") );
	}
			
}
