package junit.database.processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import database.TransformationServiceReversible;
import database.UnsuitableTransformerException;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.ReadCSVToTable;
import processors.ReverseCSVToTableQuoteIfNeeded;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestReadCSVToTable {

	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {

	}

	@Test	public void testParseTable() throws Exception {
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "col1,col2,col3\n7,8,9\n17,18,19\n27,28,29\n37,38,39\n47,48,49");
		TransformationService csvReader = new ReadCSVToTable();
		Table<Integer, Integer, String> out = csvReader.doWork(in);
		assertTrue( out.rowKeySet().size() == 6);
		assertTrue( out.columnKeySet().size() == 4); //Index col + 3 cols in table.
		assertTrue( out.get(5, 3).equals("49") );
	}

	@Test public void testParseWithCommas() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "\"eats, shoots and leaves\"\n\"Lists, Arrays and Series\"");
		TransformationService csvReader = new ReadCSVToTable();
		Table<Integer, Integer, String> out = csvReader.doWork(in);
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);
		assertTrue( out.get(1, 1).equals("Lists, Arrays and Series") );
	}

	/**
	 * Do not succeed if table contains unnecessary quoting as the result will be irreversible: the reverse processor would not know where to add quotes.
	 */
	@Test public void testUnnecessaryQuotesSucceeds() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		String originalString = "\"quotes not needed\",\"contains , so needs quotes\"\n" +
			"\"cow\",\"cow,beef,steak\"\n" +
			"\"chicken\",\"chicken\"";
		in.put(0, 0, originalString);
		TransformationService csvReader = new ReadCSVToTable();
		Table<Integer, Integer, String> outTable = csvReader.doWork(in);

		assertTrue( outTable.get(1, 1).equals("cow") );
		assertTrue( outTable.get(1, 2).equals( "cow,beef,steak" ) );
	}
}