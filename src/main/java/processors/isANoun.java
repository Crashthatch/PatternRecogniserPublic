package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class isANoun extends TransformationService {

	HashSet<String> wordMap;
	
	public isANoun()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );
		boolean isANoun = false;
		
		if( stringin.length() < 40 && StringUtils.isAlpha(stringin)){
			Runtime rt = Runtime.getRuntime() ;
			Process p;
			try {
				p = rt.exec("lib/WordNet/bin/wn.exe "+stringin+" -over");
				BufferedReader result = new BufferedReader(new InputStreamReader( p.getInputStream() ) );
				String line;
				while( ( line = result.readLine() ) != null ){
					
					if( line.contains("Overview of noun")){
						isANoun = true;
						break;
					}
					else if( line.contains("No information available for noun")){
						isANoun = false;
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		if( isANoun ){
			outTable.put(0, 0, "1");
		}
		else{
			outTable.put(0, 0, "0");
		}
		
		return outTable;
	}
}
