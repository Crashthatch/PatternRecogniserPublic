package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.Att;
import database.Main;
import database.Processor;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.HttpGet;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class TestHttpGet {
	private static Table<Integer, Integer, String> in;
	private static TransformationService transformer;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		transformer = new HttpGet();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testQC() throws Exception
	{
		in.put(0, 0, "http://questionablecontent.net/view.php?comic=101");
		out = transformer.doWork(in);
		System.out.println(out);
		assertTrue( out.get(0,0).equals("200") );
		assertTrue( out.get(0,1).toLowerCase().contains("doctype html") );
	}
	
	@Test public void testValid() throws Exception
	{
		in.put(0, 0, "http://wikipedia.org");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("200") );
		assertTrue( out.get(0,1).toLowerCase().contains("doctype html") );
	}
	
	@Test public void testGetParam() throws Exception{
		in.put(0, 0, "http://search.yahoo.com/search?p=Wiki");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("200") );
		assertTrue( out.get(0,1).contains("Wikipedia") );
	}
	
	@Test public void test404() throws Exception
	{
		in.put(0, 0, "http://google.com/thispagedoesnotexistandreturnsafourohfour");
		out = transformer.doWork(in);
		assertTrue( out.get(0,0).equals("404") );
	}
	
	@Test public void testFailure() throws Exception{
		in.put(0, 0, "http://notarealwebsitefjidsjaiofjdiosajriofewjaiofejasoi.com");
		out = transformer.doWork(in);
		assertTrue( out.get(0,1).equals("") );
	}
	
	@Test public void testGetForMany() throws Exception{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/urls.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitter = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable());
		splitter.doWork();
		
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(splitter.getOutputAtts().get(1));
		
		Processor httpGetter = new Processor( new HttpGet(), inputAtts2, splitter.getOutputAtts().get(0).getDataTable() );
		httpGetter.doWork();
		
		Att finalLengthAtt = httpGetter.getOutputAtts().get(0);
		for( String statusCode : finalLengthAtt.getData()){
			System.out.println(statusCode);
			assertTrue( statusCode.equals("200") );
		}
	}
		
}
