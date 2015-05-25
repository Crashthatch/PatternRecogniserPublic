package junit.database.relationships.specificlabel;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TestHarness {

	/**
	 * This class takes an input file, imports it as a string, and does several rounds of processing and relationship-finding.
	 * This is essentially what the final whole model should do. However, this class stops at the point of finding a relationship that can predict the (intermediate, internal) attribute specified. We are only testing up to that point in this class.
	 * The reason this class exists is so that we can play around with the order of steps, use different Processor-Selectors at different stages etc, and still easily check that all of the CanRecognise* Tests pass.
	 * Eventually, we will probably copy+paste the code from here and to form the first half of a top-level controller (we will also need code to use the relationships found to create and verify predictions).
	 * The final API should be black-box and not expose its inner workings, ie. shouldn't require the intermediate-name, and there should never be a need to get at an individual relationship(?)
	 *  
	 * @param inputfile
	 * @param intermediateLabelName
	 * @param expectedCorrectPredictions
	 * @return
	 * @throws SQLException
	 */
	public static Relationship processAndGetBestRelationshipFor(String inputfile, String intermediateLabelName ) throws SQLException
	{
		AttRelationshipGraph inputGraph = new AttRelationshipGraph();
		Att constant = Main.initDb("pattern_activedata", inputGraph);
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile(inputfile), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("longStringAtt");
		
		//Init Rapidminer.
		LogService logger = LogService.getGlobal();
		RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
		logger.setVerbosityLevel(LogService.ERROR);
		RapidMiner.init();
		
		//First round of processing.
		//ProcessorSelector processorSelector = new BruteForceProcessorSelector();
		ProcessorSelector processorSelector = new AttInterestingnessProcessorSelector();
		List<Processor> possibleProcessors = processorSelector.getBestProcessors(inputGraph );
		for( Processor processor : possibleProcessors )
		{
			processor.doWork();
		}
		
		//Can any of the first-round attributes be predicted by a relationship?
		List<Relationship> firstRoundPossibleRelationships = RelationshipSelector.getBestRelationships(inputGraph);
		for(Relationship rel2 : firstRoundPossibleRelationships )
		{
			String debugMsg = "Estimating performance for "+rel2.getName()+", predicting "+rel2.getLabel().getName() + " from ";
			for( Att inputAtt : rel2.getInputAtts() )
			{
				debugMsg += inputAtt.getName() + ", ";
			}
			System.out.println(debugMsg);
			try {
				rel2.estimatePerformanceUsingXValidation();
			} catch (InsufficientRowsException e) {
				fail("Not enough training records to estimate performance");
			} catch (IncorrectInputRowsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rel2.learn();
		}
		
		inputGraph.getAttsAndProcessorsOnly().createDot();
		
		
		//Second round of processing.
		//processorSelector = new BruteForceProcessorSelector();
		processorSelector = new AttInterestingnessProcessorSelector();
		possibleProcessors = processorSelector.getBestProcessors(inputGraph );
		for( Processor processor : possibleProcessors )
		{
			processor.doWork();
		}
		
		//inputGraph.getAttsAndProcessorsOnly().createDot();
			
			
		
		//It's infeasible to apply 2-column-in operators in a brute-force manner, since there are over 2000 columns as inputs, so 2000^2 = 4000000 ways of applying each 2-column-in processor.
		/*ProcessorSelector lastRoundProcessorSelector = new BruteForceSingleAttInProcessorSelector();
		List<Processor> possibleProcessors = lastRoundProcessorSelector.getBestProcessors(inputGraph );
		for( Processor processor : possibleProcessors )
		{
			processor.doWork();
		}*/

		//List<Relationship> possibleRelationships = RelationshipSelector.getBestRelationships(inputGraph);
		
		Att toBePredicted = null;
		for( GraphVertex vertex : inputGraph.getVertices() )
		{
			if( vertex.getClass() == Att.class )
			{				
				Att att = (Att) vertex;
				if( att.getName().equals(intermediateLabelName) ) 
				{
					toBePredicted = att;
				}
			}
		}
		
		List<Relationship> possibleRelationships = RelationshipSelector.getAllRelationshipsForLabel(toBePredicted);
		
		
		for(Relationship rel2 : possibleRelationships )
		{
			if( rel2.getLabel().equals(toBePredicted))
			{
				String debugMsg = "Estimating performance for "+rel2.getName()+", predicting "+rel2.getLabel().getName() + " from ";
				for( Att inputAtt : rel2.getInputAtts() )
				{
					debugMsg += inputAtt.getName() + ", ";
				}
				System.out.println(debugMsg);
				try {
					rel2.estimatePerformanceUsingXValidation();
				} catch (InsufficientRowsException e) {
					fail("Not enough training records to estimate performance");
				} catch (IncorrectInputRowsException e) {

				}
				rel2.learn();
			}
		}
		
		Relationship bestRelationship = toBePredicted.getBestPredictor();
		System.out.println(bestRelationship);
		
		//AttRelationshipGraph g = inputGraph.getSubGraph( bestRelationship );
		//g.createDot();
		
		return bestRelationship;
	}

}
