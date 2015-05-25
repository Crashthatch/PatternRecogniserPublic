package junit.database.inout;

import database.Att;
import database.AttRelationshipGraph;
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

public class TestExternalLevel2SingleValueANDAfterNormalisationProblem2 {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void externalSingleValueProblem() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb(graph);
		
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
		
		//We want a level-2 external att for this test, so split the constant attribute to create one.
		Processor primeFactorFinder = new Processor( new primeFactors(), externalCreator.getOutputAtts(), constant.getDataTable());
		primeFactorFinder.doWork();
		
		Att externalLevel2Att = primeFactorFinder.getOutputAtts().get(0);
		
		ArrayList<Att> a = new ArrayList<>();
		a.add(numberAtt);
		a.add(externalLevel2Att);
		
		graph.createDot();
		
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
		c.add(numberAtt);
		c.add(totalAtt);
		
		graph.createDot();
		
		Processor divider = new Processor( new divide(), c, numberAtt.getDataTable() );
		divider.doWork();
		
		Att normalisedAtt = divider.getOutputAtts().get(0);
		
		graph.createDot();
		
		try
		{
			String stringvalue = normalisedAtt.getData().get(2);
			assertTrue( stringvalue.equals("0.12") );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		
	}

}
