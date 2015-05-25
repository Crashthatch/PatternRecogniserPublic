package junit.database.inout;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import models.BestColumnLearner;
import models.MostCommonValueLearner;
import models.WekaLinearRegressionIntegerLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CopeWithJoinedAllNulls {
	
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
	 * We create a table which has 2 columns, one where all the even numbered rows are NULL, the other where all the odd numbered rows are NULL.
	 * We then try to create a Processor with RRT = root and the 2 columns as inputs, this results in an empty table being given to the processor as InputTable.
	 * However, the processor should still be able to succeed and result in an empty output.
	 */
	@Test public void copeWithJoinedAllNulls() throws Exception{
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
		Att filteredEven;
		Att filteredOdd;
		{
			ArrayList<Att> inputAtts = new ArrayList<Att>();
			inputAtts.add(constant);
			Processor importer = new Processor( new getInputFromFile("testdata/12345678910.dat"), inputAtts, constant.getDataTable() );
			importer.doWork();
			
			Att longStringAtt = importer.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
			splitToLines.doWork();
			
			inLineIdx = splitToLines.getOutputAtts().get(0);

			Processor evenFilter = new Processor( new filterWhereEven(), Arrays.asList(splitToLines.getOutputAtts().get(1)), splitToLines.getOutputAtts().get(1).getDataTable());
			evenFilter.doWork();

			Processor oddFilter = new Processor( new filterWhereOdd(), Arrays.asList(splitToLines.getOutputAtts().get(1)), splitToLines.getOutputAtts().get(1).getDataTable());
			oddFilter.doWork();
			
			filteredEven = evenFilter.getOutputAtts().get(0);
			filteredOdd = oddFilter.getOutputAtts().get(0);
		}
		
		
		//Populate the outputTree.
		Att outIndex;
		Att outEven;
		Att outOdd;
		Att outComma;
		{
			ArrayList<Att> inputAtts = new ArrayList<Att>();
			inputAtts.add(outConstant);
			Processor outImporter = new Processor( new getInputFromFile("testdata/12 34 56 78 910.dat"), inputAtts, outConstant.getDataTable() );
			outImporter.doWork();
			
			Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
			splitToLines.doWork();
			outIndex = splitToLines.getOutputAtts().get(0);
			
			Processor splitToCols = new Processor( new SplitEndsToColumns(1,2), Arrays.asList(splitToLines.getOutputAtts().get(1)), splitToLines.getOutputAtts().get(1).getDataTable());
			splitToCols.doWork();
			outOdd = splitToCols.getOutputAtts().get(0);
			outComma = splitToCols.getOutputAtts().get(1);
			outEven = splitToCols.getOutputAtts().get(2);
		}

		Relationship evenPredictor = new Relationship(new BestColumnLearner(), Arrays.asList(filteredEven), outEven, filteredEven );
		Relationship oddPredictor = new Relationship(new BestColumnLearner(), Arrays.asList(filteredOdd), outOdd, filteredOdd );
		Relationship commaPredictor = new Relationship(new MostCommonValueLearner(), Arrays.asList(filteredEven), outComma, filteredEven );
		//We also need a predictor for the "index" of 'split on \n'.
		Relationship indexPredictor = new Relationship(new WekaLinearRegressionIntegerLearner(), Arrays.asList(filteredEven), outIndex, filteredEven);

		evenPredictor.estimatePerformanceUsingXValidation();
		oddPredictor.estimatePerformanceUsingXValidation();
		commaPredictor.estimatePerformanceUsingXValidation();
		indexPredictor.estimatePerformanceUsingXValidation();

		
		assertTrue(evenPredictor.getAccuracy() == 1);
		assertTrue( oddPredictor.getAccuracy() == 1);
		assertTrue( commaPredictor.getAccuracy() == 1);
		assertTrue( indexPredictor.getAccuracy() == 1);

		evenPredictor.learn();
		oddPredictor.learn();
		commaPredictor.learn();
		indexPredictor.learn();


		//Check we can reverse the "splitToRows" output processor. (Requires creating modelAppliers and then doing a join on the two nulled columns).
		//What was failing before was that the concatenate() processor didn't get its firstInputSize set, so we got a nullpointerexception thrown when we later tried to read it.
		AttRelationshipGraph singleForwardTree = RelationshipFinderInputOutput.createSingleForwardTree(inputGraph, outputGraph, Arrays.asList(evenPredictor, oddPredictor, commaPredictor, indexPredictor));
		assertTrue( singleForwardTree.getFinalOutputAtts().size() >= 1 );
	}

}
