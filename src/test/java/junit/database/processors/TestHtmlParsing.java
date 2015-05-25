package junit.database.processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.IncorrectOutputDimensionsException;
import database.TransformationService;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.HtmlXpathSelector;
import processors.htmlToXhtml;
import processors.xmlUnwrap;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TestHtmlParsing {
	private static Table<Integer, Integer, String> in;
	private static TransformationService unwrapper;
	private static TransformationService htmlToXhtml;
	private static Table<Integer, Integer, String> out;
	
	@BeforeClass public static void oneTimeSetUp()
	{
		htmlToXhtml = new htmlToXhtml();
		unwrapper = new xmlUnwrap();
		in = TreeBasedTable.create();
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testHtmlToXhtml() throws Exception
	{
		in.put(0, 0, "<html><head><title>Test HTML</title></head> <body><ul><li>One</li><li>Two</li><li>Three</li></ul></body></html>");
		out = htmlToXhtml.doWork(in);
        assertTrue( out.get(0,0).replace("\r\n", "\n").equals("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n<title>Test HTML</title>\n</head>\n<body>\n<ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>\n</body>\n</html>\n"));

	}
	
	@Test public void testUnwrapping() throws Exception
	{
		in.put(0, 0, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n<title>Test HTML</title>\n</head>\n<body>\n<ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>\n</body>\n</html>\n");
		out = unwrapper.doWork(in);
		assertTrue( out.rowKeySet().size() == 2);
		assertTrue( out.get(0,1).replace("\r\n", "\n").equals( "<head>\n<title>Test HTML</title>\n</head>") );
		assertTrue( out.get(1,1).replace("\r\n", "\n").equals( "<body>\n<ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>\n</body>") );
	}
	
	@Test public void testUnwrappingTwoRoots() throws Exception
	{
		in.put(0, 0, "<head><title>Test HTML</title></head> <body><ul><li>One</li><li>Two</li><li>Three</li></ul></body>");
		out = unwrapper.doWork(in);
		assertTrue( out.get(0,1) == null );
	}
	
	@Test public void testUnwrappingHead() throws Exception
	{
		in.put(0, 0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><head>\n<title>Test HTML</title>\n</head>");
		out = unwrapper.doWork(in);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.get(0,1).equals("<title>Test HTML</title>") );
	}
	
	@Test public void testUnwrappingHeadNoXmlDeclaration() throws Exception
	{
		in.put(0, 0, "<head>\n<title>Test HTML</title>\n</head>");
		out = unwrapper.doWork(in);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.get(0,1).equals("<title>Test HTML</title>") );
	}
	
	@Test public void testUnwrappingBody() throws Exception
	{
		in.put(0, 0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><body>\n<ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>\n</body>");
		out = unwrapper.doWork(in);
		assertTrue( out.rowKeySet().size() == 1);
		assertTrue( out.get(0,1).replace("\r\n", "\n").equals("<ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>") );
	}
	
	@Test public void testUnwrappingFragment() throws Exception
	{
		in.put(0, 0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>");
		out = unwrapper.doWork(in);
		assertTrue( out.rowKeySet().size() == 3);
		assertTrue( out.get(0,0).equals( "0") );
		assertTrue( out.get(0,1).equals( "<li>One</li>") );
		assertTrue( out.get(1,0).equals( "1") );
		assertTrue( out.get(1,1).equals( "<li>Two</li>") );
		assertTrue( out.get(2,0).equals( "2") );
		assertTrue( out.get(2,1).equals( "<li>Three</li>") );
	}
	
	@Test public void testCheckOutputDimensions() throws Exception
	{
		in.put(0, 0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ul>\n<li>One</li>\n<li>Two</li>\n<li>Three</li>\n</ul>");
		out = unwrapper.doWork(in);
		assertTrue( out.rowKeySet().size() == 3);
		try{
			unwrapper.checkOutputDimensions(in, out);
		}
		catch( IncorrectOutputDimensionsException e ){
			assertTrue( false );
		}
	}
	
	@Test public void testUnwrappingTbody() throws Exception
	{
		in.put(0, 0, "<tbody><tr><td>Cell1</td><td>cell2</td></tr><tr><td>Cell3</td><td>Cell4</td></tr></tbody>");
		out = unwrapper.doWork(in);
		assertTrue( out.get(0,1).equals("<tr><td>Cell1</td><td>cell2</td></tr>") );
		assertTrue( out.get(1,1).equals("<tr><td>Cell3</td><td>Cell4</td></tr>") );
	}
	
	@Test public void testUnwrappingFragmentsTwoRoots() throws Exception
	{
		in.put(0, 0, "<ul><li>One</li><li>Two</li><li>Three</li></ul><ul><li>Alpha</li><li>Beta</li></ul>");
		out = unwrapper.doWork(in);
		assertTrue( out.get(0,1) == null );
	}
	
	
	@Test public void testSplittingWikipediaTable() throws Exception{
		in.put(0,0, FileUtils.readFileToString(new File("testdata/listOfGoTEpsTable-series1.htm")));
		Table<Integer, Integer, String> xhtml = htmlToXhtml.doWork(in);
		Table<Integer, Integer, String> body = unwrapper.doWork(xhtml);
		in.clear();
		in.put(0, 0, body.get(1, 1)); //row 0 is the empty <head> tag.
		Table<Integer, Integer, String> table = unwrapper.doWork( in );
		
		in.clear();
		in.put(0, 0, table.get(0,1));
		Table<Integer, Integer, String> tbody = unwrapper.doWork( in );
		
		in.clear();
		in.put(0, 0, tbody.get(0,1));
		Table<Integer, Integer, String> rows = unwrapper.doWork( in );
		assertTrue( rows.rowKeySet().size() == 11 );
		
		in.clear();
		in.put(0, 0, rows.get(3,1));
		Table<Integer, Integer, String> rowThree = unwrapper.doWork( in );
		assertTrue( rowThree.rowKeySet().size() == 7);
		
		in.clear();
		in.put(0, 0, rowThree.get(5,1));
		Table<Integer, Integer, String> cellContents = unwrapper.doWork( in );
		
		assertTrue( cellContents.get(0, 1).equals("May 1, 2011") ); //First part of the cell-contents (before the <span>).
	}
	
	@Test public void testSplittingSimpleHtml() throws Exception{
		in.put(0,0, FileUtils.readFileToString(new File("testdata/simplehtml.htm")));
		
		Table<Integer, Integer, String> body = unwrapper.doWork(in);
		
		in.clear();
		in.put(0, 0, body.get(0,1));
		Table<Integer, Integer, String> spans = unwrapper.doWork( in );
		
		assertTrue( spans.get(0, 1).equals( "<span>Alan</span>" ) );
		
	}
	
	@Test public void testClassSelector() throws Exception{
		in.put(0, 0, "<html><body><div class=\"title\">Stuff</div><div class=\"item\">One</div><div class=\"item\">Two</div></body></html>");
		TransformationService classSelector = new HtmlXpathSelector(".title");
		Table<Integer, Integer, String> output = classSelector.doWork(in);
		
		assertTrue(output.get(0, 1).replace("\r", "").replace("\n", "").equals("<div class=\"title\"> Stuff</div>"));
	}
	
	@Test public void testClassSelectorMultipleReturns() throws Exception{
		in.put(0, 0, "<html><body><div class=\"title\">Stuff</div><div class=\"item\">One</div><div class=\"item\">Two</div></body></html>");
		TransformationService classSelector = new HtmlXpathSelector(".item");
		Table<Integer, Integer, String> output = classSelector.doWork(in);
		
		assertTrue(output.rowKeySet().size() == 2);
		assertTrue(output.get(0, 1).replace("\r", "").replace("\n", "").equals("<div class=\"item\"> One</div>"));
		assertTrue(output.get(1, 1).replace("\r", "").replace("\n", "").equals("<div class=\"item\"> Two</div>"));
	}

}
