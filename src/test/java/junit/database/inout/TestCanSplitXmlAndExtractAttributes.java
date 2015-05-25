package junit.database.inout;

import database.Att;
import database.AttRelationshipGraph;
import database.Main;
import database.Processor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestCanSplitXmlAndExtractAttributes {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}

	/**
	 * Originally used to debug XMLOutermostAttributes failing to parse.
	 * Kept as unit test because why not?
	 * @throws Exception
	 */
	@Test public void TestCanSplitXmlAndExtractAttributes() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/xml to csv - rapidminer processor/trainIn.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor xpathSplitter = new Processor( new HtmlXpathSelector("parameter"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		xpathSplitter.doWork();

		Att xmlLines = xpathSplitter.getOutputAtts().get(1);

		Processor attributeExtractor = new Processor( new xmlOutermostAttributes(), Arrays.asList(xmlLines), xmlLines.getDataTable() );
		attributeExtractor.doWork();
		
		Att keys = attributeExtractor.getOutputAtts().get(0);
		Att values = attributeExtractor.getOutputAtts().get(1);

		
		try{
			assertTrue( keys.getData().get(0).equals("key") );
			assertTrue( values.getData().get(0).equals("return_preprocessing_model") );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
	}

}
