package junit.database.inout;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.Att;
import database.AttRelationshipGraph;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class DuplicateGetDatatable {

	@BeforeClass public static void oneTimeSetUp()
	{
		// Init Rapidminer.
		LogService logger = LogService.getGlobal();
		RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
		logger.setVerbosityLevel(LogService.ERROR);
		RapidMiner.init();
	}

	@AfterClass public static void oneTimeTearDown()
	{

	}

    /**
     *
     * @throws java.sql.SQLException
     */
	@Test public void duplicateSameTableObscuresReversible() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph inputGraph = new AttRelationshipGraph();
		Att constant = Main.initDb(inputGraph);

		
		//Populate the inputTree. Create the att with 5 rows: 1,2,3,4,5.
		Att inNumberAtt, inNumberAttAgain, childOfInNumberAtt;
		{
			ArrayList<Att> inputAtts = new ArrayList<Att>();
			inputAtts.add(constant);
			Processor importer = new Processor( new getInputFromFile("testdata/56897.dat"), inputAtts, constant.getDataTable() );
			importer.doWork();
			
			Att longStringAtt = importer.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
			splitToLines.doWork();
			inNumberAtt= splitToLines.getOutputAtts().get(1);

            //Do the same transformation again. The outputs from this duplicate transformation will be identified as duplicates as those already created, so not saved to the db.
            Processor splitToLinesAgain = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
            splitToLinesAgain.doWork(false);
            inNumberAttAgain = splitToLinesAgain.getOutputAtts().get(1);
		}

        assertTrue( inNumberAtt != inNumberAttAgain );
        assertTrue( inNumberAttAgain.isDuplicate() );
        assertTrue( inNumberAttAgain.getDuplicateOf() == inNumberAtt );

        assertTrue( inNumberAttAgain.getDataTable().equals( inNumberAtt.getDataTable() ));
	}
}
