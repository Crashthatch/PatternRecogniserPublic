package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.getInputFromFile;
import processors.stringLength;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestManyColumnsInOneTable {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void manyColumnsInOneTable() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor lengthProcessor = new Processor( new stringLength(), importer.getOutputAtts(), longStringAtt.getDataTable() );
		lengthProcessor.doWork();
		
		Att finalLengthAtt = lengthProcessor.getOutputAtts().get(0);
		
		try {
			String stringvalue = finalLengthAtt.getData().get(0);
			assertTrue( stringvalue.equals("9") );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
		
	}

}
