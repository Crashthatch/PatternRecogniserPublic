package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnRegexToRows;

import static org.junit.Assert.assertTrue;

public class TestSplitOnRegex {
	private static Table<Integer, Integer, String> in;
    private static TransformationService transformerEscapedSlash;
	private static TransformationService transformerNewline;
	private static TransformationService transformerComma;
	private static TransformationService transformerTab;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
        transformerEscapedSlash = new SplitOnRegexToRows("\\\\");
		transformerNewline = new SplitOnRegexToRows("\n");
		transformerComma = new SplitOnRegexToRows(",");
		transformerTab = new SplitOnRegexToRows("\t");
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSplitOnCommaToRows() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "a,b,c,");
		out = transformerComma.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 4);
		assertTrue( out.columnKeySet().size() == 2);
		
		//First row
		assertTrue( out.get(0,0).equals("0") );
		assertTrue( out.get(0,1).equals("a") );
		
		//Second row
		assertTrue( out.get(1,1).equals("b") );
		
		//Last Row
		assertTrue( out.get(3,0).equals("3") );
		assertTrue( out.get(3,1).equals("") );
	}
	
	@Test public void testSplitOnTabToRows() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "a	b	c	");
		out = transformerTab.doWork(in);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 4);
		assertTrue( out.columnKeySet().size() == 2);
		
		//First row
		assertTrue( out.get(0,0).equals("0") );
		assertTrue( out.get(0,1).equals("a") );
		
		//Second row
		assertTrue( out.get(1,1).equals("b") );
		
		//Last Row
		assertTrue( out.get(3,0).equals("3") );
		assertTrue( out.get(3,1).equals("") );
	}
	
	
	@Test public void testSplitOnWindowsNewlineToRows() throws Exception
	{
		Table<Integer, Integer, String> out;
		
		in.put(0, 0, "a\nb\nc\n");
		out = transformerNewline.doWork(in);
		
		System.out.println(out);
		
		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 4);
		assertTrue( out.columnKeySet().size() == 2);
		
		//First row
		assertTrue( out.get(0,0).equals("0") );
		assertTrue( out.get(0,1).equals("a") );
		
		//Second row
		assertTrue( out.get(1,1).equals("b") );
		
		//Last Row
		assertTrue( out.get(3,0).equals("3") );
		assertTrue( out.get(3,1).equals("") );
	}

    @Test public void testSplitOnEscapedBackslash() throws Exception
    {
        Table<Integer, Integer, String> out;

        in.put(0, 0, "a\\b\\c\\");
        out = transformerEscapedSlash.doWork(in);

        System.out.println(out);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 4);
        assertTrue( out.columnKeySet().size() == 2);

        //First row
        assertTrue( out.get(0,0).equals("0") );
        assertTrue( out.get(0,1).equals("a") );

        //Second row
        assertTrue( out.get(1,1).equals("b") );

        //Last Row
        assertTrue( out.get(3,0).equals("3") );
        assertTrue( out.get(3,1).equals("") );
    }
			
}
