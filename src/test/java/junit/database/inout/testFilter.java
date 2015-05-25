package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
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

public class testFilter {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void filter() throws Exception
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

		Processor filterToEven = new Processor( new filterWhereEven(), inputAtts2, numberAtt.getDataTable() );
		filterToEven.doWork();
		
		Att filteredAtt = filterToEven.getOutputAtts().get(0);
		
		
		//Feed in only the filtered "even" rows into the capitalise operator. 
		ArrayList<Att> a = new ArrayList<>();
		a.add(filteredAtt);
		
		Processor capitalise = new Processor( new capitalise(), a, numberAtt.getDataTable() );
		capitalise.doWork();
		
		Att capitalisedAtt = capitalise.getOutputAtts().get(0);
		
		
		try {
			ArrayList<String> data = capitalisedAtt.getData();
			System.out.println(data.size());
			assertTrue( data.size() == 2 );
			assertTrue( capitalisedAtt.getData().get(0).equals("2") );
			assertTrue( capitalisedAtt.getData().get(1).equals("4") );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
		
	}

}
