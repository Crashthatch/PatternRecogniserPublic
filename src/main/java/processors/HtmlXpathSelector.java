package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlXpathSelector extends TransformationService {

	//TODO: Rename xPath to cssSelector because it's misnamed due to me not knowing the difference when I first wrote it.
	String xPath;
	
	public String getName() {
		return "HtmlXPathSelector"+StringEscapeUtils.escapeJava(xPath);
	}
	
	public HtmlXpathSelector(String clas)
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
		this.xPath = clas;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
	
		Document doc = Jsoup.parse(stringin);
		Elements els = doc.select(this.xPath);
		
		int idx = 0;
		for( Element element : els ){
			outTable.put(idx, 0, ""+idx);
			outTable.put(idx, 1, element.toString());
			idx++;
		}
	
		return outTable;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((xPath == null) ? 0 : xPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HtmlXpathSelector other = (HtmlXpathSelector) obj;
		if (xPath == null) {
			if (other.xPath != null)
				return false;
		} else if (!xPath.equals(other.xPath))
			return false;
		return true;
	}
}
