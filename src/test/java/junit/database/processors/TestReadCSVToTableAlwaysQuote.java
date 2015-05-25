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
import processors.ReadCSVToTableAlwaysQuoted;
import processors.ReverseCSVToTableAlwaysQuote;
import processors.ReverseCSVToTableQuoteIfNeeded;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestReadCSVToTableAlwaysQuote {

	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {

	}

	@Test	public void testParseTable() throws Exception {
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "\"col1\",\"col2\",\"col3\"\n\"7\",\"8\",\"9\"\n\"17\",\"18\",\"19\"\n\"27\",\"28\",\"29\"\n\"37\",\"38\",\"39\"\n\"47\",\"48\",\"49\"");
		TransformationService csvReader = new ReadCSVToTableAlwaysQuoted();
		Table<Integer, Integer, String> out = csvReader.doWork(in);
		assertTrue( out.rowKeySet().size() == 6);
		assertTrue( out.columnKeySet().size() == 4); //Index col + 3 cols in table.
		assertTrue( out.get(5, 3).equals("49") );
	}

	@Test public void testParseWithCommas() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "\"eats, shoots and leaves\"\n\"Lists, Arrays and Series\"");
		TransformationService csvReader = new ReadCSVToTableAlwaysQuoted();
		Table<Integer, Integer, String> out = csvReader.doWork(in);
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.columnKeySet().size() == 2);
		assertTrue( out.get(1, 1).equals("Lists, Arrays and Series") );
	}

	@Test public void testKeepWhitespace() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "\"     some text     \"");
		TransformationService csvReader = new ReadCSVToTableAlwaysQuoted();
		Table<Integer, Integer, String> out = csvReader.doWork(in);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 2);
		assertTrue( out.get(0, 1).equals("     some text     ") );
	}

	@Test	public void testReverseCSV() throws Exception {
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "0");
		in.put(0, 1, "header0");
		in.put(0, 2, "header1");
		in.put(1, 0, "1");
		in.put(1, 1, "aa");
		in.put(1, 2, "ab");
		in.put(2, 0, "2");
		in.put(2, 1, "ba");
		in.put(2, 2, "bb");
		TransformationServiceReversible csvCreator = new ReverseCSVToTableAlwaysQuote();
		Table<Integer, Integer, String> out = csvCreator.doWork(in);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		assertTrue( out.get(0,0).equals("\"header0\",\"header1\"\n\"aa\",\"ab\"\n\"ba\",\"bb\"") );
	}

	@Test	public void testForwardsAndReverse() throws Exception {
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		String originalString = "\"col1\",\"col2\",\"col3\"\n\"7\",\"8\",\"9\"\n\"17\",\"18\",\"19\"\n\"27\",\"28\",\"29\"\n\"37\",\"38\",\"39\"\n\"47\",\"48\",\"49\"";
		in.put(0, 0, originalString);
		TransformationServiceReversible csvReader = new ReadCSVToTableAlwaysQuoted();
		Table<Integer, Integer, String> intermediateTbl = csvReader.doWork(in);

		TransformationService csvCreator = csvReader.getReverseTransformer();
		Table<Integer, Integer, String> out = csvCreator.doWork(intermediateTbl);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		assertTrue( out.get(0,0).equals(originalString) );
	}

	@Test	public void testForwardsAndReverseWithWhitespace() throws Exception {
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		String originalString = "\"   whitespace!   \",\"col2\"\n\"  7  \",\"  8  \"";
		in.put(0, 0, originalString);
		TransformationServiceReversible csvReader = new ReadCSVToTableAlwaysQuoted();
		Table<Integer, Integer, String> intermediateTbl = csvReader.doWork(in);

		TransformationService csvCreator = csvReader.getReverseTransformer();
		Table<Integer, Integer, String> out = csvCreator.doWork(intermediateTbl);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.columnKeySet().size() == 1);
		assertTrue( out.get(0,0).equals(originalString) );
	}

	/**
	 * The AlwaysQuote processor must fail in the case that there are unquoted entries.
	 * Otherwise when we try to reverse it, it will add extra quotes back in.
	 * eg. in this example, the left column would become quoted.
	 * @throws Exception
	 */
	@Test public void testNoQuotesFails() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		String originalString = "quotes not needed,\"contains , so needs quotes\"\n" +
			"cow,\"cow,beef,steak\"\n" +
			"sheep,\"lamb,mutton\"";
		in.put(0, 0, originalString);
		TransformationServiceReversible csvReader = new ReadCSVToTableAlwaysQuoted();
		try {
			Table<Integer, Integer, String> outTable = csvReader.doWork(in);
			fail();
		}
		catch(UnsuitableTransformerException e){
			assertTrue( true );
		}
	}

	@Test public void testNoQuotesFailsSimple() throws Exception{
		Table<Integer, Integer, String> in = TreeBasedTable.create();
		String originalString = "quotes not needed";
		in.put(0, 0, originalString);
		TransformationServiceReversible csvReader = new ReadCSVToTableAlwaysQuoted();
		try {
			Table<Integer, Integer, String> outTable = csvReader.doWork(in);
			fail();
		}
		catch(UnsuitableTransformerException e){
			assertTrue( true );
		}
	}
}