package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class htmlToXhtml extends TransformationService {
	
	Tidy tidy;

	public htmlToXhtml()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		
		tidy = new Tidy();
		tidy.setShowWarnings(false);
        tidy.setXmlTags(false);
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setXHTML(true);// 
        tidy.setMakeClean(true);
        tidy.setQuiet(true);
        tidy.setMakeBare(true);
        tidy.setTidyMark(false);
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		
		ByteArrayInputStream is = new ByteArrayInputStream(stringin.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        tidy.parseDOM(is, os);
		outTable.put(0, 0, os.toString() );
		
		return outTable;
	}
}
