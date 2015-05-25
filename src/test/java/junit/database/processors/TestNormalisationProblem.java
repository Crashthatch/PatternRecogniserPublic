package junit.database.processors;

import database.Att;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.divide;
import processors.getInputFromFile;
import processors.total;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestNormalisationProblem {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void normalisationProblem() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToLines.doWork();
		
		Att numberAtt = splitToLines.getOutputAtts().get(1);
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(numberAtt);

		Processor summer = new Processor( new total(), inputAtts2, longStringAtt.getDataTable() );
		summer.doWork();
		
		Att totalAtt = summer.getOutputAtts().get(0);
		
		ArrayList<Att> a = new ArrayList<>();
		a.add(numberAtt);
		a.add(totalAtt);
		
		Processor divider = new Processor( new divide(), a, numberAtt.getDataTable() );
		divider.doWork();
		
		Att normalisedAtt = divider.getOutputAtts().get(0);
		
		
		try {
			String stringvalue = normalisedAtt.getData().get(2);
			assertTrue( stringvalue.equals("0.2") );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
		
	}

}
