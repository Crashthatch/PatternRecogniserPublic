package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.Test;
import processors.ContainsFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestContains {
	

	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testContains() throws Exception
	{
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService containsA = new ContainsFilter("a");
        in.put(0, 0, "apple");
        Table<Integer,Integer,String> out = containsA.doWork(in);
        assertTrue( out.get(0,0).equals("1") );
	}


    @Test public void testContains2() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService containsA = new ContainsFilter("l");
        in.put(0, 0, "apple");
        Table<Integer,Integer,String> out = containsA.doWork(in);
        assertTrue( out.get(0,0).equals("1") );
    }
	
	@Test public void testDoesntContain() throws Exception
	{
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService containsA = new ContainsFilter("z");
        in.put(0, 0, "banana");
        Table<Integer,Integer,String> out = containsA.doWork(in);
        assertTrue( out.rowKeySet().size() == 0 );
	}

    @Test public void testContainsMultipleCharacters() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService containsA = new ContainsFilter("aar");
        in.put(0, 0, "aardvark");
        Table<Integer,Integer,String> out = containsA.doWork(in);
        assertTrue( out.get(0,0).equals("1") );
    }

    @Test public void testDoesntStartWithMultipleCharacters() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService containsA = new ContainsFilter("aar");
        in.put(0, 0, "piano");
        Table<Integer,Integer,String> out = containsA.doWork(in);
        assertTrue( out.rowKeySet().size() == 0 );
    }

    @Test public void testEmptyString() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService containsA = new ContainsFilter("aar");
        in.put(0, 0, "");
        Table<Integer,Integer,String> out = containsA.doWork(in);
        assertTrue( out.rowKeySet().size() == 0 );
    }

    @Test public void testEquals() throws Exception
    {
        TransformationService containsA = new ContainsFilter("a");
        TransformationService containsA2 = new ContainsFilter("a");
        TransformationService containsB = new ContainsFilter("b");

        assertTrue( containsA.equals( containsA2 ));
        assertFalse( containsA.equals(containsB));
    }

}
