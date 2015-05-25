package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.inWordList8;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class TestInWordlist {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testInWordlist() throws Exception
	{
		inWordList8 wl = new inWordList8();
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "apple");
		Table<Integer, Integer, String> out = wl.doWork(in);
		assertTrue( out.get(0,0) == "1" );
		
		in.put(0, 0, "Apple");
		out = wl.doWork(in);
		assertTrue( out.get(0,0) == "1" );
		
		in.put(0, 0, "afjidjasifjdisao");
		out = wl.doWork(in);
		assertTrue( out.get(0,0) == "0" );
		
	}

}
