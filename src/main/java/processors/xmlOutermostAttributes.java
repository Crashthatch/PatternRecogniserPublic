package processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

public class xmlOutermostAttributes extends TransformationService {

	DocumentBuilder dBuilder;
	InputSource is;

	public xmlOutermostAttributes()
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			//Disable various validation as it's really slow.
			dbFactory.setNamespaceAware(false);
			dbFactory.setValidating(false);
			dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
			dbFactory.setFeature("http://xml.org/sax/features/validation", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dBuilder = dbFactory.newDocumentBuilder();
			is = new InputSource();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
	
		try {
			is.setCharacterStream(new StringReader(stringin));
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NamedNodeMap attributes = doc.getDocumentElement().getAttributes();

			int rowNum = 0;
			for( int i=0; i < attributes.getLength(); i++ ){
				Node att = attributes.item(i);
				String value = att.getNodeValue();
				String name = att.getNodeName();

				outTable.put(rowNum, 0, name);
				outTable.put(rowNum, 1, value);

				rowNum++;
			}
			
		
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return outTable;
	}
}
