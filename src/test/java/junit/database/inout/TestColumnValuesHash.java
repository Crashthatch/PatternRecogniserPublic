package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

public class TestColumnValuesHash {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testManyRowsNotEqualToOneRow() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/4,5,7,8,6.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor separator = new Processor( new SplitOnStringToRows(","), importer.getOutputAtts(), longStringAtt.getDataTable() );
		separator.doWork();
		
		Att numbersColumn = separator.getOutputAtts().get(1);

        assertTrue(!longStringAtt.getColumnValuesHash().equals(numbersColumn.getColumnValuesHash()));
	}

}
