package junit.database.inout;

import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestProcessorSelector {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testProcessorSelector() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb(graph);
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/soml-alphabetagamma-questions.csv"), inputAtts, constant.getDataTable() );		
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("longStringAtt");
		
		
		ArrayList<TransformationService> transformers = new ArrayList<>();
		//0-column atts:
		transformers.add( new constantCreator("0") );
		transformers.add( new isPrime() );
		transformers.add( new stringLength() );
		transformers.add( new capitalise() );
		transformers.add( new rownum() );
		
		//transformers.add( new SplitOnRegexesToTable("\n",",") );
		//transformers.add( new SplitOnStringToColumns(",") );
		//transformers.add( new SplitOnStringToRows("\n") );
		transformers.add( new ReadCSVToTable() );
		
		ProcessorSelector processorSelector = new BruteForceProcessorSelector();
		List<Processor> possibleProcessors = processorSelector.getBestProcessors(graph, transformers);
		
		for( Processor processor : possibleProcessors )
		{
			processor.doWork();
		}
		
		
		
		
		//2nd Processors
		ArrayList<TransformationService> transformers2 = new ArrayList<>();
		transformers2.add( new constantCreator("0") );
		transformers2.add( new isPrime() );
		transformers2.add( new stringLength() );
		transformers2.add( new capitalise() );
		transformers2.add( new rownum() );
		
		ProcessorSelector processorSelector2 = new BruteForceProcessorSelector();
		List<Processor>possibleProcessors2 = processorSelector2.getBestProcessors(graph, transformers2);
		for( Processor processor : possibleProcessors2 )
		{
			processor.doWork();
		}
		
		
		
		
		//Tests.
		boolean foundLongStringLengthAtt = false;
		boolean foundTitleField = false;
		boolean foundLengthOfTitle = false;
		boolean foundFailedTransformer = false;
		Att titleAtt = null;
		Collection<Att> atts = graph.getAllAtts();
		
		for( Att att : atts )
		{
			if( att.getName().equals("stringLength(getInputFromFile())") )
			{
				foundLongStringLengthAtt = true;
                assertTrue( att.getFirstRow().equals("555") );
			}
			
			if( att.getName().equals("ReadCSVToTable-7(getInputFromFile())") )
			{
				foundTitleField = true;
				titleAtt = att;
				assertTrue( att.getData().size() == 3 );
			}
			
			if( att.getName().equals("stringLength(ReadCSVToTable-7(getInputFromFile()))") )
			{
				foundLengthOfTitle = true;
				assertTrue( att.getData().get(2).equals("22") );
			}
		}
		
		assertTrue( foundLongStringLengthAtt );
		assertTrue( foundTitleField );
		assertTrue( foundLengthOfTitle );
		
		for( Processor proc : titleAtt.getProcessors())
		{
			if( proc.getName().equals( "isPrime" ) )
			{
				foundFailedTransformer = true;
				assertTrue( proc.getSuccessfulTransformers() == 0 );
				assertTrue( proc.getNumberOfTransformers() == 3 );
			}
		}
		
		assertTrue( foundFailedTransformer );
		
		graph.createDot();
		
		
		
		
	}

}
