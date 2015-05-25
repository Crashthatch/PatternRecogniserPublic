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
import processors.xmlUnwrap;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class CanFilterRows {
	
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
	 * 
	 */
	@Test public void canFilterRowsUsingRelationshipsRRA() throws Exception
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
		
		
		//Populate the inputTree.
		Att inLineIdx;
		Att inUnwrapped;
		Att inUnwrapIndex;
		{
			ArrayList<Att> inputAtts = new ArrayList<Att>();
			inputAtts.add(constant);
			Processor importer = new Processor( new getInputFromFile("testdata/listOfGoTEpNamesCellContentsWithHeaderRow-series1.txt"), inputAtts, constant.getDataTable() );
			importer.doWork();
			
			Att longStringAtt = importer.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
			splitToLines.doWork();
			
			inLineIdx = splitToLines.getOutputAtts().get(0);
			assertTrue( inLineIdx.getNotNullRowsInTable() == 11);
			
			ArrayList<Att> xPathSelectorInputs = new ArrayList<>();
			xPathSelectorInputs.add(splitToLines.getOutputAtts().get(1));
			DataTable xPathSelectorRRA = splitToLines.getOutputAtts().get(0).getDataTable();
			//Processor getAElements = new Processor( new HtmlXpathSelector("a"), xPathSelectorInputs, xPathSelectorRRA );
			Processor getAElements = new Processor( new xmlUnwrap(), xPathSelectorInputs, xPathSelectorRRA);
			getAElements.doWork();
			
			inUnwrapIndex = getAElements.getOutputAtts().get(0);
			inUnwrapped = getAElements.getOutputAtts().get(1);
			assertTrue( inUnwrapped.getNotNullRowsInTable() == 10);
		}
		
		
		//Populate the outputTree.
		Att outIdx;
		{
			ArrayList<Att> inputAtts = new ArrayList<Att>();
			inputAtts.add(outConstant);
			Processor outImporter = new Processor( new getInputFromFile("testdata/listOfGoTEpNames-series1.txt"), inputAtts, outConstant.getDataTable() );
			outImporter.doWork();
			
			Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
			splitToLines.doWork();
			
			outIdx = splitToLines.getOutputAtts().get(0);
		}

		//Discard the index of the "a" element, (always 0, output from getAElements).
		inputGraph.removeVertex(inUnwrapIndex);
		
		Collection<Relationship> goodRelationships = RelationshipFinderInputOutput.learnBestRelationships(inputGraph, outputGraph, new ArrayList<Relationship>(), new HashSet<Relationship>(), null, 0);
		//Make sure the atts in the output graph can be predicted.
		Relationship outIdxPredictor = null;
		for( Relationship rel : goodRelationships ){
			if( rel.getLabel() == outIdx && rel.getInputAtts().size() == 1 && rel.getInputAtts().contains( inLineIdx )){
				outIdxPredictor = rel;
			}
		}
		
		//Can we predict the outIdx from the input line number (requires filtering using the RRA from the child table)?
		assertTrue( outIdxPredictor != null);
		assertTrue( outIdxPredictor.getRootRowAtt() == inUnwrapped );
		
		//Verify that the relationship does make correct predictions.
		Table<Integer, Integer, String> testTable = TreeBasedTable.create();
		testTable.put(0, 0, "LABEL");
		testTable.put(0, 1, "12");
		List<String> predictions = outIdxPredictor.makePredictions(testTable);
		assertTrue( Float.parseFloat(predictions.get(0)) == Float.parseFloat("11"));
		
		//Check we can reverse the "splitToRows" output processor.
		AttRelationshipGraph singleForwardTree = RelationshipFinderInputOutput.createSingleForwardTree(inputGraph, outputGraph, goodRelationships);
		assertTrue( singleForwardTree.getFinalOutputAtts().size() >= 1 );
	}

}
