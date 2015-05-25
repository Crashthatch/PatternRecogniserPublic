package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.capitalise;
import processors.filterWhereEven;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestCorrectHashCreatedX {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}

	/**
	 * Tests that the correct columnValuesHash is created for a simple 100 rows index-att.
	 * (Written due to a difficult to detect failure on Linux/Maven only where we used StringUtils.join( HashTable ) which appended the entries in "hashed" order, not "rowKey" order).
	 * @throws SQLException
	 */
	@Test public void testCorrectHashCreated() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/12345...100.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToLines.doWork();
		
		Att indexAtt = splitToLines.getOutputAtts().get(0);

		//Create the correct, target hash.
		String correctString = "";
		for( int i = 0; i < 100; i++){
			correctString += i+"-,-";
		}
		correctString = correctString.substring(0, correctString.length() - 3);
		//System.out.println(correctString);
		String correctHash = DigestUtils.sha1Hex(correctString);
		//System.out.println(correctHash);

		assertTrue(indexAtt.getColumnValuesHash().equals(correctHash));
	}

}
