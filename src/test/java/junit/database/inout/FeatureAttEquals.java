package junit.database.inout;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import database.features.AttFeatureSet;
import database.features.AttFeatureSetFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeatureAttEquals {

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
     * Create two atts (the index and the values att) that have identical features (same length, type, number of nulls etc), but are different atts with different values so should not be "equal".
     * @throws java.sql.SQLException
     */
	@Test public void similarAreNotEqual() throws Exception
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

        AttFeatureSetFactory featureSetFactory = new AttFeatureSetFactory(inputGraph, new AttRelationshipGraph(), new ArrayList<Relationship>() );
        AttFeatureSet inIndexFeatures = featureSetFactory.getFeaturesForAtt(inIndexAtt);
        AttFeatureSet inNumberFeatures = featureSetFactory.getFeaturesForAtt(inNumberAtt);

        assertFalse( inIndexFeatures.equals(inNumberFeatures));
        assertFalse( inIndexFeatures.hashCode() == inNumberFeatures.hashCode() );
	}

    /**
     * Test that the features for the same att generated twice are equal.
     * @throws SQLException
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

        AttFeatureSetFactory featureSetFactory = new AttFeatureSetFactory(inputGraph, new AttRelationshipGraph(), new ArrayList<Relationship>() );
        AttFeatureSet inNumberFeatures = featureSetFactory.getFeaturesForAtt(inNumberAtt);

        //Ensure equal to self.
        assertTrue(inNumberFeatures.equals(inNumberFeatures));

        AttFeatureSetFactory featureSetFactory2 = new AttFeatureSetFactory(inputGraph, new AttRelationshipGraph(), new ArrayList<Relationship>() );
        AttFeatureSet inNumberFeatures2 = featureSetFactory2.getFeaturesForAtt(inNumberAtt);

        //Ensure equal to the same features generated in the same way from the same att.
        assertTrue(inNumberFeatures.equals(inNumberFeatures2));
        assertTrue(inNumberFeatures.hashCode() == inNumberFeatures2.hashCode());
    }
}
