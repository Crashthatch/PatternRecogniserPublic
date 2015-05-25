package junit.database.inout;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import database.features.AttFeatureSet;
import database.features.AttFeatureSetFactory;
import database.features.RelationshipFeatureSet;
import database.features.UnknownPropertyTypeException;
import models.WekaLinearRegressionIntegerLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.fail;

public class TestSaveFeaturesToDatabase {
	
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

    @Test public void testSaveRelationshipFeaturesToDatabase() throws Exception{
        //Create the attribute table if it doesn't already exist.
        try {
            String SQL = RelationshipFeatureSet.getCreateTableSql();
            MetaModelDatabase.reconnect("pattern_metamodel");
            System.out.println(SQL);
            MetaModelDatabase.doWriteQuery(SQL);
        }
        catch( SQLException e){
            e.printStackTrace();
        }


        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        AttRelationshipGraph outputGraph = new AttRelationshipGraph();
        DataTable rootTable = new DataTable();
        Att outConstant = new Att(rootTable, outputGraph);

        HashMap<Att, String> rootRowValues = new HashMap<>();
        rootRowValues.put(outConstant, "root");
        rootTable.insert(0, rootRowValues);
        rootTable.save();


        //Populate the inputTree. Create the att with 5 rows: 1,2,3,4,5.
        Att inNumberAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(constant);
            Processor importer = new Processor(new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable());
            importer.doWork();

            Att longStringAtt = importer.getOutputAtts().get(0);
            Processor splitToLines = new Processor(new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable());
            splitToLines.doWork();
            inNumberAtt = splitToLines.getOutputAtts().get(1);
        }

        Att outNumberAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(outConstant);
            Processor outImporter = new Processor(new getInputFromFile("testdata/23456.dat"), inputAtts, outConstant.getDataTable());
            outImporter.doWork();

            Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
            Processor splitToLines = new Processor(new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable());
            splitToLines.doWork();
            outNumberAtt = splitToLines.getOutputAtts().get(1);
        }

        //Create a relationship that attempts to learn an att in the output table from an att in the input table.
        Relationship rel = new Relationship(new WekaLinearRegressionIntegerLearner(), Arrays.asList(inNumberAtt), outNumberAtt, inNumberAtt);

        //Try creating some features and saving them.
        MetaModelDatabase.reconnect("pattern_metamodel");
        RelationshipFeatureSet features = new RelationshipFeatureSet(rel, inputGraph, outputGraph, new ArrayList<Relationship>() );
        features.addToBatchInsert("featuresSaveTest", 1);
        RelationshipFeatureSet.saveBatch();
    }

    @Test public void testRelationshipNoInputs() throws Exception{
        //Create the attribute table if it doesn't already exist.
        try {
            String SQL = RelationshipFeatureSet.getCreateTableSql();
            MetaModelDatabase.reconnect("pattern_metamodel");
            System.out.println(SQL);
            MetaModelDatabase.doWriteQuery(SQL);
        }
        catch( SQLException e){
            e.printStackTrace();
        }


        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        AttRelationshipGraph outputGraph = new AttRelationshipGraph();
        DataTable rootTable = new DataTable();
        Att outConstant = new Att(rootTable, outputGraph);

        HashMap<Att, String> rootRowValues = new HashMap<>();
        rootRowValues.put(outConstant, "root");
        rootTable.insert(0, rootRowValues);
        rootTable.save();


        //Populate the inputTree. Create the att with 5 rows: 1,2,3,4,5.
        Att inNumberAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(constant);
            Processor importer = new Processor(new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable());
            importer.doWork();

            Att longStringAtt = importer.getOutputAtts().get(0);
            Processor splitToLines = new Processor(new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable());
            splitToLines.doWork();
            inNumberAtt = splitToLines.getOutputAtts().get(1);
        }

        Att outNumberAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(outConstant);
            Processor outImporter = new Processor(new getInputFromFile("testdata/23456.dat"), inputAtts, outConstant.getDataTable());
            outImporter.doWork();

            Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
            Processor splitToLines = new Processor(new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable());
            splitToLines.doWork();
            outNumberAtt = splitToLines.getOutputAtts().get(1);
        }

        //Create a relationship that attempts to learn an att in the output table from an att in the input table.
        Relationship rel = new Relationship(new WekaLinearRegressionIntegerLearner(), new ArrayList<Att>(), outNumberAtt, inNumberAtt);

        //Try creating some features and saving them.
        MetaModelDatabase.reconnect("pattern_metamodel");
        RelationshipFeatureSet features = new RelationshipFeatureSet(rel, inputGraph, outputGraph, new ArrayList<Relationship>() );
        features.addToBatchInsert("featuresSaveTest", 1);
        RelationshipFeatureSet.saveBatch();
    }

	@Test public void testSaveAttFeaturesToDatabase() throws Exception{

        //Create the attribute table if it doesn't already exist.
        try {
            String SQL = AttFeatureSet.getCreateAttributeTableSql();
            MetaModelDatabase.reconnect("pattern_metamodel");
            System.out.println(SQL);
            MetaModelDatabase.doWriteQuery(SQL);
        }
        catch( SQLException e){
            e.printStackTrace();
        }

        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        AttRelationshipGraph outputGraph = new AttRelationshipGraph();
        //Att outConstant = Main.initDb("pattern_outputdata", outputGraph);
        //Create the root "const" att which contains no data.
        DataTable rootTable = new DataTable();
        Att outConstant = new Att(rootTable, outputGraph);
        HashMap<Att, String> rootRowValues = new HashMap<>();
        rootRowValues.put(outConstant, "root");
        rootTable.insert(0, rootRowValues);
        rootTable.save();


        //Populate the inputTree. Create the att with 5 rows: 1,2,3,4,5.
        Att inNumberAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(constant);
            Processor importer = new Processor(new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable());
            importer.doWork();

            Att longStringAtt = importer.getOutputAtts().get(0);
            Processor splitToLines = new Processor(new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable());
            splitToLines.doWork();
            inNumberAtt = splitToLines.getOutputAtts().get(1);
        }

        Att outNumberAtt;
        {
            ArrayList<Att> inputAtts = new ArrayList<Att>();
            inputAtts.add(outConstant);
            Processor outImporter = new Processor(new getInputFromFile("testdata/23456.dat"), inputAtts, outConstant.getDataTable());
            outImporter.doWork();

            Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
            Processor splitToLines = new Processor(new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable());
            splitToLines.doWork();
            outNumberAtt = splitToLines.getOutputAtts().get(1);
        }

        //Try creating some features and saving them.
        MetaModelDatabase.reconnect("pattern_metamodel");
        AttFeatureSetFactory featureGenerator = new AttFeatureSetFactory(inputGraph, outputGraph, new ArrayList<Relationship>() );
        for (Att att : inputGraph.getAllAtts()) {
            AttFeatureSet features = featureGenerator.getFeaturesForAtt(att);
            //Save the features to the DB.
            try {
                features.addToBatchInsert("featuresSaveTest", 1);
            } catch (SQLException e) {
                e.printStackTrace();
                fail();
            }
        }
        AttFeatureSet.saveBatch();

        //TODO: Assert that the database contains the features we just generated.
	}

}
