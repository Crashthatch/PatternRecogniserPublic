package junit.database.inout;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToColumns;
import processors.SplitOnStringToColumnsAndUnpredictable;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DuplicatesObscureReversible {
	
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
     * Test creates 2 input leaves (number and index), and first creates an output tree with 3 leaves (number, index and unpredictable).
     * At this point the output-tree should be irreversible because the operator that created the 3 leaves can't be reversed because 'unpredictable' can't be predicted.
     * Then create a new processor in the output tree that creates the same number & index atts (but does not produce unpredictable). These 2 atts are duplicates of the 2 created earlier.
     * Test that we now CAN reverse the output-tree (via this new processor), and produce a finalOutputAtt.
     * @throws SQLException
     */
	@Test public void duplicateSameTableObscuresReversible() throws Exception
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
			Processor importer = new Processor( new getInputFromFile("testdata/56897.dat"), inputAtts, constant.getDataTable() );
			importer.doWork();
			
			Att longStringAtt = importer.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
			splitToLines.doWork();
			inNumberAtt= splitToLines.getOutputAtts().get(1);
		}
		



        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(outConstant);
        Processor outImporter = new Processor( new getInputFromFile("testdata/2 columns.dat"), inputAtts, outConstant.getDataTable() );
        outImporter.doWork();

        Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
        Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
        splitToLines.doWork();

        List<Att> splitToColsInput = new ArrayList<>();
        Att tabSeparated = splitToLines.getOutputAtts().get(1);
        splitToColsInput.add(tabSeparated);
        Processor splitToColumnsAndUnpredictable = new Processor( new SplitOnStringToColumnsAndUnpredictable("\t"), splitToColsInput, tabSeparated.getDataTable() );
        splitToColumnsAndUnpredictable.doWork();

        Att outColumn1 = splitToColumnsAndUnpredictable.getOutputAtts().get(0);
        Att outColumn2 = splitToColumnsAndUnpredictable.getOutputAtts().get(1);
        Att outUnpredictableAtt = splitToColumnsAndUnpredictable.getOutputAtts().get(2);

		Collection<Relationship> goodRelationships = RelationshipFinderInputOutput.learnBestRelationships(inputGraph, outputGraph, new ArrayList<Relationship>(), new HashSet<Relationship>(), null, 0);
		//Ensure we can predict the number att (45786) from the input att.
		ArrayList<Relationship> goodRelationshipsThatPredictNumberAtt = new ArrayList<>();
		for( Relationship rel : goodRelationships ){
			if( rel.getLabel() == outColumn1 && rel.getInputAtts().size() == 1 && rel.getInputAtts().contains(inNumberAtt)){
                goodRelationshipsThatPredictNumberAtt.add(rel);
			}
		}
		assertTrue( goodRelationshipsThatPredictNumberAtt.size() == 1);
		Relationship numberPredictor = goodRelationshipsThatPredictNumberAtt.get(0);
		System.out.println(numberPredictor);
        assertTrue( numberPredictor.getCorrectPredictions() == 5);

        //Ensure that we can predict the index att from the input index att.
        ArrayList<Relationship> goodRelationshipsThatPredictIndexAtt = new ArrayList<>();
        for( Relationship rel : goodRelationships ){
            if( rel.getLabel() == outColumn2 && rel.getInputAtts().size() == 1 ){
                goodRelationshipsThatPredictIndexAtt.add(rel);
            }
        }
        assertTrue( goodRelationshipsThatPredictIndexAtt.size() >= 1);
        Relationship indexPredictor = goodRelationshipsThatPredictIndexAtt.get(0);
        System.out.println(indexPredictor);
        assertTrue( indexPredictor.getCorrectPredictions() == 5);

        //Ensure that we can NOT predict the "random" unpredictable att.
        for( Relationship rel : goodRelationships ) {
            if (rel.getLabel() == outUnpredictableAtt ) {
                fail();
            }
        }

        //Ensure it doesn't think we can reverse SplitOnRegexToRowsAndCreateUnpredictable to predict the original input att.
        assertTrue( outputGraph.getUnpredictableSubgraph(goodRelationships, true).getVertices().size() > 1 );



        //Now create the truly reversible processor, but where the columns it outputs are duplicates of Column1 and column2 already produced above.
        Processor splitToColumns = new Processor( new SplitOnStringToColumns("\t"), splitToColsInput, tabSeparated.getDataTable() );
        splitToColumns.doWork(false); //false = Do not save duplicates.

        outputGraph.createDot("output");
        System.out.println(outputGraph);

        //outputGraph.flagDuplicateAtts();
        //outputGraph.removeDuplicateAtts();

        outputGraph.createDot("afterRemoveDupes");

        Att column1dupe = splitToColumns.getOutputAtts().get(0);
        Att column2dupe = splitToColumns.getOutputAtts().get(1);


        Collection<Relationship> goodRelationshipsAfterDupe = RelationshipFinderInputOutput.learnBestRelationships(inputGraph, outputGraph, new ArrayList<Relationship>(), new HashSet<Relationship>(), null, 0);

        AttRelationshipGraph unpredictable = outputGraph.getUnpredictableSubgraph(goodRelationshipsAfterDupe, true);
        unpredictable.createDot("unpredictable");

        assertTrue(unpredictable.getVertices().size() == 1);


        //Additionally test that we can now turn those trees of predictable atts into a single forward tree.
        AttRelationshipGraph singleForwardTree = RelationshipFinderInputOutput.createSingleForwardTree(inputGraph, outputGraph, goodRelationshipsAfterDupe);
        System.out.println("Created single forward tree: "+singleForwardTree);
        singleForwardTree.createDot("singleForwardTree");

        assertTrue(singleForwardTree.getFinalOutputAtts().size() > 0);

	}
}
