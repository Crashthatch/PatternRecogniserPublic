package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.UnwrapOuterBrackets;

import static org.junit.Assert.assertTrue;

public class TestUnwrapOuterBrackets {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSimpleBrackets() throws Exception
	{
		Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "123(456)789");
		out = unwrapper.doWork(in);

		//Check dimensions.
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		
		//First row
		assertTrue( out.get(0,0).equals("456") );
	}

    @Test public void testNestedBrackets() throws Exception
    {
        Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "123(4()5(6))789");
        out = unwrapper.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("4()5(6)") );
    }

    @Test public void testNoClosingBracket() throws Exception
    {
        Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "123(456789");
        out = unwrapper.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 0);
        assertTrue( out.columnKeySet().size() == 0);
    }

    @Test public void testSecondBracketType() throws Exception
    {
        Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "123(45[678]9");
        out = unwrapper.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("678") );
    }

    @Test public void testCloseBeforeOpen() throws Exception
    {
        Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "123456)78(9");
        out = unwrapper.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 0);
        assertTrue( out.columnKeySet().size() == 0);
    }

    @Test public void testCloseBeforeOpenClose() throws Exception
    {
        Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "123)45(678)9");
        out = unwrapper.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("678") );
    }

    /**
     * Finds the first open brace { that has a matching close, and returns everything in between.
     */
    @Test public void testConfusing() throws Exception
    {
        Table<Integer, Integer, String> out;
        TransformationService unwrapper = new UnwrapOuterBrackets();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "}{([)]{(]}})){[[]])((()]{{([{");
        out = unwrapper.doWork(in);

        //Check dimensions.
        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);

        //First row
        assertTrue( out.get(0,0).equals("([)]{(]}") );
    }
}
