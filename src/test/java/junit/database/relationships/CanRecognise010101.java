package junit.database.relationships;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import models.BestColumnLearner;
import models.RMLinearRegressionLearner;
import models.ZeroLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CanRecognise010101 {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void canRecognise010101HandCoded() throws Exception{
		//Create the root "const" att which contains no data.
        AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb("pattern_activedata", graph);
		
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/01010.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("longStringAtt");
		
		
		ArrayList<TransformationService> transformers = new ArrayList<>();
		//0-column atts:
		transformers.add( new constantCreator("2") );
		transformers.add( new isPrime() );
		transformers.add( new rownum() );
		transformers.add( new ReadCSVToTable() );
		transformers.add( new modulo());

		ProcessorSelector processorSelector = new BruteForceProcessorSelector();
		for( int i=0; i < 2; i++ )
		{
			List<Processor> possibleProcessors = processorSelector.getBestProcessors(graph, transformers );
			
			for( Processor processor : possibleProcessors )
			{
				processor.doWork();
			}
			
		}
		
		//Init Rapidminer.
		LogService logger = LogService.getGlobal();
		RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
		logger.setVerbosityLevel(LogService.ERROR);
		RapidMiner.init();
		
		
		
		//gui.displayGraph(AttRelationshipGraph.getGraph());
		//AttRelationshipGraph.createDot();
		
		//Hand-code what to predict from & to.
		Att toBePredicted = null;
		Att toPredictFrom = null;
		for( GraphVertex vertex : graph.getVertices() )
		{
			if( vertex.getClass() == Att.class )
			{				
				Att att = (Att) vertex;
				if( att.getName().equals("ReadCSVToTable-1(getInputFromFile())") )
				{
					toBePredicted = att;
				}
				
				if( att.getName().equals("modulo(ReadCSVToTable-0(getInputFromFile()),constantCreator-2())") )
				{
					toPredictFrom = att;
				}
			}
		}
		
		System.out.println(toBePredicted);
		System.out.println(toPredictFrom);
		
		//System.out.println(toBePredicted.getBestPredictor().getAccuracy() );
		
		//Find the att to predict from.
		ArrayList<Att> featureAtts = new ArrayList<Att>();
		featureAtts.add(toPredictFrom);
		Relationship rel = new Relationship( new RMLinearRegressionLearner(), featureAtts, toBePredicted );
		rel.estimatePerformanceUsingXValidation(3);
		assertTrue( rel.getCorrectPredictions() == 9 );
		
		System.out.println("");
		
		Relationship stupidRel = new Relationship( new ZeroLearner(), featureAtts, toBePredicted );
		stupidRel.estimatePerformanceUsingXValidation(3);
		
		System.out.println("");
		
		Relationship bestColumnRel = new Relationship( new BestColumnLearner(), featureAtts, toBePredicted );
		bestColumnRel.estimatePerformanceUsingXValidation(3);
		assertTrue( bestColumnRel.getCorrectPredictions() == 9 );
		
		
		
		
		//Non-Hand coded version.
		List<Relationship> possibleRelationships = RelationshipSelector.getBestRelationships(graph);
		
		for(Relationship rel2 : possibleRelationships )
		{
			String debugMsg = "Estimating performance for "+rel2.getName()+", predicting "+rel2.getLabel().getName() + " from ";
			for( Att inputAtt : rel2.getInputAtts() )
			{
				debugMsg += inputAtt.getName() + ", ";
			}
			System.out.println(debugMsg);
			rel2.estimatePerformanceUsingXValidation();
			rel2.learn();
		}
		
		Relationship bestRelationship = toBePredicted.getBestPredictor();
		System.out.println(bestRelationship);
		
		assertTrue( bestRelationship.getCorrectPredictions() == 9 );
		assertTrue( bestRelationship.getAccuracy() >= 0.9 );
	}

}
