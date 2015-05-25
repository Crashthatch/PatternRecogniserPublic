package junit.database.inout;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;
import processors.mod2;
import processors.plusOne;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttEquals {

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
	@Test public void equality() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph inputGraph = new AttRelationshipGraph();
		Att constant = Main.initDb(inputGraph);

        //Populate the inputTree. Create the att with 5 rows.
        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/56897.dat"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);
        Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
        splitToLines.doWork();
        Att inIndexAtt= splitToLines.getOutputAtts().get(0);
        Att inNumberAtt= splitToLines.getOutputAtts().get(1);

        assertFalse( inIndexAtt.equals( inNumberAtt ) );
        assertFalse( inIndexAtt.hashCode() == inNumberAtt.hashCode() );

        assertFalse( longStringAtt.equals( inNumberAtt ));
        assertFalse( longStringAtt.hashCode() == inNumberAtt.hashCode() );

        assertTrue( inputGraph.containsVertex(inIndexAtt));
        assertTrue( inputGraph.containsVertex(inNumberAtt));

	}


    @Test public void equalProcessorsCreateDifferentAtts() throws Exception
    {
        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        //Populate the inputTree. Create the att with 5 rows.
        Att inNumberAtt, inIndexAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(constant);
            Processor importer = new Processor( new getInputFromFile("testdata/56897.dat"), inputAtts, constant.getDataTable() );
            importer.doWork();

            Att longStringAtt = importer.getOutputAtts().get(0);
            Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
            splitToLines.doWork();
            inIndexAtt= splitToLines.getOutputAtts().get(0);
            inNumberAtt= splitToLines.getOutputAtts().get(1);
        }

        //Do another operation on the "inNumberAtt".
        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(inNumberAtt);
        Processor incrementor = new Processor( new plusOne(), inputAtts, inNumberAtt.getDataTable());
        incrementor.doWork();
        Att incremented = incrementor.getOutputAtts().get(0);

        Processor identicalIncrementor = new Processor( new plusOne(), inputAtts, inNumberAtt.getDataTable());
        identicalIncrementor.doWork(true);  //Make sure to create the att in the DB so we have 2 identical atts, both stored in the DB.
        Att identicallyIncremented = identicalIncrementor.getOutputAtts().get(0);

        assertTrue( incrementor.equals(identicalIncrementor) ); //Both processors do the same thing to the same inputs, so are equal.
        assertFalse(incremented.isDuplicate()); //Neither are identified as dupes.
        assertFalse(identicallyIncremented.isDuplicate());

        assertFalse(incremented == identicallyIncremented); //However the processors produce different (equivalent) atts.
        assertTrue( incremented.equals(identicallyIncremented));
        assertTrue( incremented.hashCode() == identicallyIncremented.hashCode());

        //Do another round of processing on only ONE of copies (the second one).
        Processor incrementedModder = new Processor( new mod2(), Arrays.asList(identicallyIncremented), inNumberAtt.getDataTable());
        incrementedModder.doWork(true);
        Att modAtt = incrementedModder.getOutputAtts().get(0);
        modAtt.setFinalOutputAtt(true);

        inputGraph.createDot("equalProcessors");


        //Now try to apply this tree to some new data.
        Collection<String> predictions = RelationshipFinderInputOutput.applyTree("testdata/12345.dat", inputGraph);
        assertTrue( predictions.size() > 0);

    }
}
