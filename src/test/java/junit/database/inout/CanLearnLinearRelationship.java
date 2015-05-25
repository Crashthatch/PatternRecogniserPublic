package junit.database.inout;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class CanLearnLinearRelationship {
	
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
	
	/*
	 * This test tests that we can predict "2,3,4,5,6" from "1,2,3,4,5" without any additional processors. (eg. via linear regression).
	 * This is important for situations where we need to predict an index column from a filtered version (say, without the header) of the input index column.
	 * Not being able to do this causes the GoTFromWikipediaPage to fail because an internal column in the output tree can't be predicted and so the reverse processor can't be applied. Was difficult to track down.
	 * Happened when we required exact-string-matching to be counted as "successful", and WekaLinearRegressionLearner produces float outputs like 1.0 (!= 1).
	 */
	@Test public void canLearnLinearRelationship() throws Exception
	{
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
			Processor importer = new Processor( new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable() );
			importer.doWork();
			
			Att longStringAtt = importer.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
			splitToLines.doWork();
			inNumberAtt= splitToLines.getOutputAtts().get(1);
		}
		



		Att outNumberAtt;
		{
			ArrayList<Att> inputAtts = new ArrayList<Att>();
			inputAtts.add(outConstant);
			Processor outImporter = new Processor( new getInputFromFile("testdata/23456.dat"), inputAtts, outConstant.getDataTable() );
			outImporter.doWork();
			
			Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
			splitToLines.doWork();
			outNumberAtt = splitToLines.getOutputAtts().get(1);
		}

		//Only keep the "number" atts, not the "index" atts.
		inputGraph = inputGraph.getSubGraph(inNumberAtt);
		outputGraph = outputGraph.getSubGraph(outNumberAtt);
		Collection<Relationship> goodRelationships = RelationshipFinderInputOutput.learnBestRelationships(inputGraph, outputGraph, new ArrayList<Relationship>(), new HashSet<Relationship>(), null, 0);
		//Find the relationship that predicts outNumberAtt from inNumberAtt.
		ArrayList<Relationship> goodRelationshipsThatPredictWhatWeAreTesting = new ArrayList<>();
		for( Relationship rel : goodRelationships ){
			if( rel.getLabel() == outNumberAtt && rel.getInputAtts().size() == 1 && rel.getInputAtts().contains(inNumberAtt)){
				goodRelationshipsThatPredictWhatWeAreTesting.add(rel);
			}
		}
		
		assertTrue( goodRelationshipsThatPredictWhatWeAreTesting.size() == 1);
		Relationship rel = goodRelationshipsThatPredictWhatWeAreTesting.get(0);
		System.out.println(rel);
		//The real test is that its predictions are counted as "correct":
		assertTrue( rel.getCorrectPredictions() == 5);
		
		//Verify that the relationship does make correct predictions.
		Table<Integer, Integer, String> testTable = TreeBasedTable.create();
		testTable.put(0, 0, "LABEL");
		testTable.put(0, 1, "8");
		List<String> predictions = rel.makePredictions(testTable);
		assertTrue( Float.parseFloat(predictions.get(0)) == Float.parseFloat("9"));
	}

}
