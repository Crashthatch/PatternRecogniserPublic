package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.ReadCSVToTable;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestUnknownNumberOfOutputAttsInsert {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/soml-alphabetagamma-questions.csv"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToQuestions = new Processor( new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToQuestions.doWork();
		
		Att dateAtt =  splitToQuestions.getOutputAtts().get(2);
		try{
			assertTrue( dateAtt.getData().get(0).equals("05/18/2011 14:14:05") );
			assertTrue( dateAtt.getData().get(1).equals("02/02/2011 11:30:10") );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		
	}

}
