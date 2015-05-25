package processors;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Extracts attributes to columns.
 * Only takes 1 row in at a time, so there's no guarantee that different transformers will output the same attributes in the same columns, but if they all have the same attributes (which is often the case) we will end up with columns of att values.
 *
 * Attribute names (keys) are discarded.
 */
public class xmlOutermostAttributeValuesToCols extends TransformationService {

	DocumentBuilder dBuilder;
	InputSource is;

	public xmlOutermostAttributeValuesToCols()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = 1;
		
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

			for( int i=0; i < attributes.getLength(); i++ ){
				Node att = attributes.item(i);
				String value = att.getNodeValue();
				String name = att.getNodeName(); //Discard the attribute name. Like we do in JsonDecodeMapManyColsOut.

				outTable.put(0, i, value);
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
