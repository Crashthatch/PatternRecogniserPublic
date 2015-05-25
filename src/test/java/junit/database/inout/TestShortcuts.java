package junit.database.inout;

import database.Att;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.concatenate;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestShortcuts {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void connectedAvoidsRoot() throws Exception
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
		ArrayList<Att> toBeConcatenated = new ArrayList<>();
		toBeConcatenated.add(wordAtt);
		toBeConcatenated.add(longStringAtt);
		Processor concatenator = new Processor( new concatenate(), toBeConcatenated, wordAtt.getDataTable());
		concatenator.doWork();
		
		Att concatenatedAtt = concatenator.getOutputAtts().get(0);
		
		try{
			assertTrue( concatenatedAtt.getData().get(0).replaceAll("\n", "").equals( "AardvarkAardvarkAbacusActorAddressAdultAfricaAirAirplaneAlabamaAlaskaAlbatrossAllAmbulanceAngryAnkleAntApple" ) );
			assertTrue( concatenatedAtt.getData().get(1).replaceAll("\n", "").equals( "AbacusAardvarkAbacusActorAddressAdultAfricaAirAirplaneAlabamaAlaskaAlbatrossAllAmbulanceAngryAnkleAntApple" ) );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		
		
	}

}
