package junit.database.inout;

import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;
import processors.jsonDecodeList;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class TestUnpredictableGraph {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void unpredictableGraph() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb(graph);
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToLines.doWork();
		Att numberAtt = splitToLines.getOutputAtts().get(0);
		

		//Reversible Processor that will return nothing.
		Processor returnEmptyProcessor = new Processor( new jsonDecodeList(), importer.getOutputAtts(), longStringAtt.getDataTable() );
		returnEmptyProcessor.doWork();
		
		graph.createDot();
		AttRelationshipGraph unpredictable = graph.getUnpredictableSubgraph(new ArrayList<Relationship>(), true);
		
		assertTrue( unpredictable.containsVertex(numberAtt));
	}

}
