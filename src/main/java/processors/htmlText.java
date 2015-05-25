package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class htmlText extends TransformationService {
	

	public htmlText()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
	
		Document doc = Jsoup.parse(stringin);
		
		outTable.put(0, 0, doc.text());
	
		return outTable;
	}
}
