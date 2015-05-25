package junit.database.processors;

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

public class TestMultipleTCandidates {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void connectedTreatsSimilarPathsEqually() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/aWords.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToLines.doWork();
		
		Att wordAtt = splitToLines.getOutputAtts().get(1);
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(wordAtt);

		Processor stringLengthFinder = new Processor( new stringLength(), inputAtts2, wordAtt.getDataTable() );
		stringLengthFinder.doWork();
		
		Att lengthAtt = stringLengthFinder.getOutputAtts().get(0);
		
		Processor capitaliser = new Processor( new capitalise(), inputAtts2, wordAtt.getDataTable() );
		capitaliser.doWork();
		
		Att capitalAtt = capitaliser.getOutputAtts().get(0);
		
		Processor summer = new Processor( new total(), stringLengthFinder.getOutputAtts(), longStringAtt.getDataTable() );
		summer.doWork();
		
		Att sumAtt = summer.getOutputAtts().get(0);
		
		

		try {
			assertTrue(sumAtt.getData().get(0).equals("100"));
			ArrayList<String> words = wordAtt.getData();
			assertTrue(wordAtt.getData().get(0).equals("Aardvark"));
			assertTrue(lengthAtt.getData().get(0).equals("8"));
			assertTrue(capitalAtt.getData().get(0).equals("AARDVARK"));
			assertTrue(capitalAtt.getData().get(1).equals("ABACUS"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		
		
	}

}
