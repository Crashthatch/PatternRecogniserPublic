package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpGet extends TransformationService {

	
	public HttpGet()
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );
		
        URL url;
        BufferedReader in = null;
        
        outTable.put(0, 0, "" ); //Gets overwritten
        outTable.put(0, 1, "" ); //Gets overwritten if there is better info, but need to return *something* (empty string) even if the site returned 404 or other error.
        
		try {
			url = new URL(stringin);
			System.out.println("Making GET Request to "+url);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	        System.out.println(conn.getRequestProperties() );
	        System.out.println(conn.getHeaderFields() );
	        outTable.put(0, 0, ""+conn.getResponseCode() );
	        
	        in = new BufferedReader( new InputStreamReader(conn.getInputStream()));
	        String inputLine;

	        StringBuilder ret = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) 
	            ret.append(inputLine);
        
	        outTable.put(0, 1, ret.toString() );
	        
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally{
			if( in != null ){
				try {
					in.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		
		
		return outTable;
	}
}
