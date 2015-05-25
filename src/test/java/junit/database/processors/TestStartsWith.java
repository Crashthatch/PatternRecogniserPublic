package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.Test;
import processors.StartsWithFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestStartsWith {
	

	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testStartsWith() throws Exception
	{
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService startsWithA = new StartsWithFilter("a");
        in.put(0, 0, "apple");
        Table<Integer,Integer,String> out = startsWithA.doWork(in);
        assertTrue( out.get(0,0).equals("1") );
	}
	
	@Test public void testDoesntStartWith() throws Exception
	{
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService startsWithA = new StartsWithFilter("a");
        in.put(0, 0, "banana");
        Table<Integer,Integer,String> out = startsWithA.doWork(in);
        assertTrue( out.rowKeySet().size() == 0 );
	}

    @Test public void testStartsWithMultipleCharacters() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService startsWithA = new StartsWithFilter("aar");
        in.put(0, 0, "aardvark");
        Table<Integer,Integer,String> out = startsWithA.doWork(in);
        assertTrue( out.get(0,0).equals("1") );
    }

    @Test public void testDoesntStartWithMultipleCharacters() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService startsWithA = new StartsWithFilter("aar");
        in.put(0, 0, "piano");
        Table<Integer,Integer,String> out = startsWithA.doWork(in);
        assertTrue( out.rowKeySet().size() == 0 );
    }

    @Test public void testEmptyString() throws Exception
    {
        Table<Integer, Integer, String> in = TreeBasedTable.create();
        TransformationService startsWithA = new StartsWithFilter("aar");
        in.put(0, 0, "");
        Table<Integer,Integer,String> out = startsWithA.doWork(in);
        assertTrue( out.rowKeySet().size() == 0 );
    }

    @Test public void testEquals() throws Exception
    {
        TransformationService startsWithA = new StartsWithFilter("a");
        TransformationService startsWithA2 = new StartsWithFilter("a");
        TransformationService startsWithB = new StartsWithFilter("b");

        assertTrue( startsWithA.equals( startsWithA2 ));
        assertFalse( startsWithA.equals(startsWithB));
    }

}
