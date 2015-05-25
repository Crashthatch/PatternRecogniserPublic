package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class inWordList8 extends TransformationService {

	HashSet<String> wordMap;
	
	public inWordList8()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		
		wordMap = new HashSet<>();
		
		BufferedReader in = null;
		try{
			in = new BufferedReader(new FileReader("lib/wlist_match8.txt") );
		
			String word;
			while( (word = in.readLine() ) != null ){
				wordMap.add(word);
			}
		}
		catch( IOException e ){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );
		if( wordMap.contains(stringin.toLowerCase().trim()) ){
			outTable.put(0, 0, "1");
		}
		else{
			outTable.put(0, 0, "0");
		}
		
		return outTable;
	}
}
