package junit.database.relationships;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import models.WekaLinearRegressionLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.ListSequel;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class CanRecognise36912 {
	
	@BeforeClass public static void oneTimeSetUp()
	{}
	
	@AfterClass public static void oneTimeTearDown()
	{}
	
	@Test public void canRecognise36912HandCoded() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		//Init Rapidminer.
		LogService logger = LogService.getGlobal();
		RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
		logger.setVerbosityLevel(LogService.ERROR);
		RapidMiner.init();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/36912.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitter = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable());
		splitter.doWork();
		
		ArrayList<Att> inputsToSequel = new ArrayList<>();
		inputsToSequel.add(splitter.getOutputAtts().get(1));
		
		Processor sequel = new Processor( new ListSequel(), inputsToSequel, longStringAtt.getDataTable() );
		sequel.doWork();
		
		ArrayList<String> multsOfThree = sequel.getOutputAtts().get(0).getData();
		ArrayList<String> multsOfThreeSequels = sequel.getOutputAtts().get(1).getData();
		
		assertTrue( multsOfThree.get(0).equals("3"));
		assertTrue( multsOfThree.get(1).equals("6"));
		assertTrue( multsOfThree.get(2).equals("9"));
		
		assertTrue( multsOfThreeSequels.get(0).equals("6"));
		assertTrue( multsOfThreeSequels.get(1).equals("9"));
		assertTrue( multsOfThreeSequels.get(2).equals("12"));
		
		ArrayList<Att> featureAtts = new ArrayList<>(); 
		featureAtts.add(sequel.getOutputAtts().get(0));
		Relationship rel = new Relationship( new WekaLinearRegressionLearner(), featureAtts, sequel.getOutputAtts().get(1));
		
		rel.learn();
        try {
            rel.estimatePerformanceUsingXValidation();
        } catch (IncorrectInputRowsException e) {
            e.printStackTrace();
        }

        assertTrue( rel.getAccuracy() == 1.0);
		
	}
}
