package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.CurrencySymbol;

import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class TestCurrencyName {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void currency() throws Exception
	{
		TransformationService transformer = new CurrencySymbol();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "£");
        Table<Integer, Integer, String> out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.get(0,0).equals("GBP") );
	}

    @Test public void testDollar() throws Exception
    {
        TransformationService transformer = new CurrencySymbol();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "$");
        Table<Integer, Integer, String> out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.get(0,0).equals("USD") );
    }

    @Test public void testEuro() throws Exception
    {
        TransformationService transformer = new CurrencySymbol();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "€");
        Table<Integer, Integer, String> out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.get(0,0).equals("EUR") );
    }

    @Test public void testReverse() throws Exception
    {
        TransformationService transformer = new CurrencySymbol().getReverseTransformer();

        Table<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "EUR");
        Table<Integer, Integer, String> out = transformer.doWork(in);

        assertTrue( out.rowKeySet().size() == 1);
        assertTrue( out.columnKeySet().size() == 1);
        assertTrue( out.get(0,0).equals("€") );
    }

}
