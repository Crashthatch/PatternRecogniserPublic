package junit.database.inout;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import models.BestColumnLearner;
import models.WekaLinearRegressionIntegerLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TestCanCreateReverseSingleTree {

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
	@Test public void canGuessFilteredInputTableAsRRT() throws Exception{
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
		Att inFilteredAtt;
        Att inIndexAtt;
		{
			Processor importer = new Processor( new getInputFromFile("testdata/12345678910.dat"), Arrays.asList(constant), constant.getDataTable() );
			importer.doWork();
			
			Att longStringAtt = importer.getOutputAtts().get(0);
			Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
			splitToLines.doWork();
            inIndexAtt = splitToLines.getOutputAtts().get(0);
			Att allNumbersAtt = splitToLines.getOutputAtts().get(1);

            Processor filterEven = new Processor( new filterWhereEven(), Arrays.asList(allNumbersAtt), allNumbersAtt.getDataTable() );
            filterEven.doWork();
            inFilteredAtt = filterEven.getOutputAtts().get(0);
		}
		


        //Populate the output tree
        Processor outImporter = new Processor( new getInputFromFile("testdata/246810quoted.dat"), Arrays.asList(outConstant), outConstant.getDataTable() );
        outImporter.doWork();

        Att longStringOutputAtt = outImporter.getOutputAtts().get(0);
        Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), outImporter.getOutputAtts(), longStringOutputAtt.getDataTable() );
        splitToLines.doWork();

        Att quotedLinesIndex = splitToLines.getOutputAtts().get(0);
        Att quotedLines = splitToLines.getOutputAtts().get(1);
        /*
        We can use the UnwrapQuotes operator here even though it's not fully reversible because we're hand coding and know that the lines really will all be quoted, so UnwrapDoubleQuotes will be reversible in this case - it won't throw away any rows.
        */
        Processor unwrapper = new Processor( new UnwrapDoubleQuotes(), Arrays.asList(quotedLines), quotedLines.getDataTable());
        unwrapper.doWork();
        Att unwrappedOutputLines = unwrapper.getOutputAtts().get(0);


        //Learn the relationship for the leaf of the output tree.
        Relationship rel = new Relationship( new BestColumnLearner(), Arrays.asList(inFilteredAtt), unwrappedOutputLines, inFilteredAtt);
        rel.estimatePerformanceUsingXValidation();
        assertTrue( rel.getAccuracy() == 1);
        rel.learn();

        //Learn the relationship for the index.
        Relationship indexRel = new Relationship( new WekaLinearRegressionIntegerLearner(), Arrays.asList(inIndexAtt), quotedLinesIndex, inFilteredAtt);
        indexRel.estimatePerformanceUsingXValidation();
        indexRel.learn();
        assertTrue( indexRel.getAccuracy() == 1);


        //Now attempt to build a single forward tree. Requires being able to reverse the "unwrapper" for which it must select a RRT with more rows than the original RRT from the output graph.
        AttRelationshipGraph singleForwardTree = RelationshipFinderInputOutput.createSingleForwardTree(inputGraph, outputGraph, Arrays.asList(rel, indexRel));
        boolean foundWrapper = false;
        for( Processor proc : singleForwardTree.getAllProcessors() ){
            if( proc.getTransformer().getClass().equals(WrapDoubleQuotes.class) && proc.getRootRowTable().equals( inFilteredAtt.getDataTable() ) ){
                foundWrapper = true;
            }
        }
        assertTrue( foundWrapper );

        //Ensure that it did not ALSO create the wrapper with the wrong root row att.
        //TODO: Test that it didn't even try to create this.
        boolean foundWrapperWithWrongRRT = false;
        for( Processor proc : singleForwardTree.getAllProcessors() ){
            if( proc.getTransformer().getClass().equals(WrapDoubleQuotes.class) && proc.getRootRowTable().equals( constant.getDataTable() ) ){
                foundWrapperWithWrongRRT = true;
            }
        }
        assertFalse( foundWrapperWithWrongRRT );

        //To get to the final output att, requires that it selected t0 as the output att (which would not have been in the oldToNewMapping).
        boolean foundJoiner = false;
        for( Processor proc : singleForwardTree.getAllProcessors() ){
            if( proc.getTransformer().getClass().equals(ReverseSplitOnStringToRows.class) && proc.getRootRowTable().equals( constant.getDataTable() ) ){
                foundJoiner = true;
            }
        }
        assertTrue( foundJoiner );
        assertTrue( singleForwardTree.getFinalOutputAtts().size() > 0 );
	}
}
