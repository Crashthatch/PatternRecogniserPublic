package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestExternalLevel1SingleValueANDAfterNormalisationProblem {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void externalSingleValueProblem() throws Exception
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

		Processor externalCreator = new Processor( new constantCreator("2"), inputAtts, constant.getDataTable() );
		externalCreator.doWork();
		
		Att externalAtt = externalCreator.getOutputAtts().get(0);
		
		ArrayList<Att> a = new ArrayList<>();
		a.add(numberAtt);
		a.add(externalAtt);
		
		Processor adder = new Processor( new plus(), a, numberAtt.getDataTable() );
		adder.doWork();
		
		Att plussedAtt = adder.getOutputAtts().get(0);
		
		//Now normalise the plussedAtt:
		ArrayList<Att> b = new ArrayList<>();
		b.add(plussedAtt);
		Processor summer = new Processor( new total(), b, longStringAtt.getDataTable() );
		summer.doWork();
		
		Att totalAtt = summer.getOutputAtts().get(0);
		
		ArrayList<Att> c = new ArrayList<>();
		c.add(plussedAtt);
		c.add(totalAtt);
		
		
		Processor divider = new Processor( new divide(), c, plussedAtt.getDataTable() );
		divider.doWork();
		
		Att normalisedAtt = divider.getOutputAtts().get(0);
		
		
		try{
			String stringvalue = normalisedAtt.getData().get(2);
			assertTrue( stringvalue.equals("0.2") );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		
	}

}
