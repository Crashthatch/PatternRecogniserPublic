package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.ListNeighbours2;

import static org.junit.Assert.assertTrue;

public class TestListNeighbours {
	private static Table<Integer, Integer, String> in;
	private static TransformationService transformer;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new ListNeighbours2();
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
		assertTrue( out.rowKeySet().size() == 3);
		assertTrue( out.columnKeySet().size() == 5);
		
		//First row
		assertTrue( out.get(0,0).equals("a") );
		assertTrue( out.get(0,1).equals("b") );
		assertTrue( out.get(0,2).equals("c") );
		assertTrue( out.get(0,3).equals("d") );
		assertTrue( out.get(0,4).equals("e") );
		
		//Last Row
		assertTrue( out.get(2,0).equals("c") );
		assertTrue( out.get(2,1).equals("d") );
		assertTrue( out.get(2,2).equals("e") );
		assertTrue( out.get(2,3).equals("f") );
		assertTrue( out.get(2,4).equals("g") );
	}
			
}
