package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitAtCharacterIdxToColumns;

import static org.junit.Assert.assertTrue;

public class TestSplitAtCharacterIdx {
	private static Table<Integer, Integer, String> in;
    private static TransformationServiceReversible transfomer;
    private static TransformationServiceReversible transfomerNegative;
	
	
	@BeforeClass public static void oneTimeSetUp()
	{
        transfomer = new SplitAtCharacterIdxToColumns(5);
        transfomerNegative = new SplitAtCharacterIdxToColumns(-4);
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSplitToRows() throws Exception
	{
		Table<Integer, Integer, String> out;

        in = TreeBasedTable.create();
		in.put(0, 0, "123456789");
		out = transfomer.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 2);
		
		//First row
		assertTrue( out.get(0,0).equals("12345") );
		assertTrue( out.get(0,1).equals("6789") );
	}

    @Test public void testNegativeSplitToRows() throws Exception
    {
        Table<Integer, Integer, String> out;

        in = TreeBasedTable.create();
        in.put(0, 0, "123456789");
        out = transfomerNegative.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 2);

        //First row
        assertTrue( out.get(0,0).equals("12345") );
        assertTrue( out.get(0,1).equals("6789") );
    }

    @Test public void testReverse() throws Exception
    {
        Table<Integer, Integer, String> out;

        TransformationService reverseTransformer = transfomer.getReverseTransformer();

        in = TreeBasedTable.create();
        in.put(0, 0, "12345");
        in.put(0, 1, "6789");
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

        TransformationService reverseTransformer = transfomer.getReverseTransformer();

        in = TreeBasedTable.create();
        in.put(0, 0, "abcdefghijklmnop");
        Table<Integer, Integer, String> intermediate = transfomer.doWork(in);

        assertTrue( intermediate.columnKeySet().size() == 2);
        assertTrue( intermediate.rowKeySet().size() == 1);
        
        out = reverseTransformer.doWork(intermediate);

        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.rowKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("abcdefghijklmnop") );
    }

			
}
