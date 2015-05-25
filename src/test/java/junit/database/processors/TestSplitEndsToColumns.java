package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitEndsToColumns;

import static org.junit.Assert.assertTrue;

public class TestSplitEndsToColumns {
    private static TransformationServiceReversible transformer;
    private static TransformationServiceReversible transformerNegativeEnd;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
        transformer = new SplitEndsToColumns(5,7);
        transformerNegativeEnd = new SplitEndsToColumns(5, -1);
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testNegativeSplitToRows() throws Exception
	{
		Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "123456789");
		out = transformerNegativeEnd.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 3);
		
		//First row
		assertTrue( out.get(0,0).equals("12345") );
		assertTrue( out.get(0,1).equals("678") );
        assertTrue( out.get(0,2).equals("9") );
	}

    @Test public void testNegativeReverse() throws Exception
    {
        Table<Integer, Integer, String> out;

        TransformationService reverseTransformer = transformerNegativeEnd.getReverseTransformer();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "12345");
        in.put(0, 1, "678");
        in.put(0, 2, "9");
        out = reverseTransformer.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("123456789") );
    }

    @Test public void testNegativeSplitAndReverse() throws Exception
    {
        Table<Integer, Integer, String> out;

        TransformationService reverseTransformer = transformerNegativeEnd.getReverseTransformer();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        String stringin = "abcdefghijklmnop";
        in.put(0, 0, stringin);
        Table<Integer, Integer, String> intermediate = transformerNegativeEnd.doWork(in);

        assertTrue( intermediate.columnKeySet().size() == 3);
        assertTrue( intermediate.rowKeySet().size() == 1);
        
        out = reverseTransformer.doWork(intermediate);

        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.rowKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals(stringin) );
    }


    @Test public void testSplitToRows() throws Exception
    {
        Table<Integer, Integer, String> out;

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "123456789");
        out = transformer.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 3);

        //First row
        assertTrue( out.get(0,0).equals("12345") );
        assertTrue( out.get(0,1).equals("67") );
        assertTrue( out.get(0,2).equals("89") );
    }

    @Test public void testReverse() throws Exception
    {
        Table<Integer, Integer, String> out;

        TransformationService reverseTransformer = transformer.getReverseTransformer();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "12345");
        in.put(0, 1, "67");
        in.put(0, 2, "89");
        out = reverseTransformer.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("123456789") );
    }

    @Test public void testSplitAndReverse() throws Exception
    {
        Table<Integer, Integer, String> out;

        TransformationService reverseTransformer = transformer.getReverseTransformer();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        String stringin = "abcdefghijklmnop";
        in.put(0, 0, stringin);
        Table<Integer, Integer, String> intermediate = transformer.doWork(in);

        assertTrue( intermediate.columnKeySet().size() == 3);
        assertTrue( intermediate.rowKeySet().size() == 1);

        out = reverseTransformer.doWork(intermediate);

        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.rowKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals(stringin) );
    }
			
}
