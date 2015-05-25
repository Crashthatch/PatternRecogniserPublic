package junit.database.inout;

import database.Att;
import database.AttRelationshipGraph;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.constantCreator;
import processors.divide;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestExternalSingleValueProblem {
	
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
		
		Processor divider = new Processor( new divide(), a, numberAtt.getDataTable() );
		divider.doWork();
		
		Att dividedAtt = divider.getOutputAtts().get(0);
		
		graph.createDot();
		
		try{
			String stringvalue = dividedAtt.getData().get(2);
			assertTrue( stringvalue.equals("1.5") );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
	}

}
