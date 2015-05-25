package database.features;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import database.Att;
import org.apache.commons.lang.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class TypeGuesser {
	public static String guessType(Att att){
		ArrayList<String> data;
		try {
			data = att.getData();
		} catch (SQLException e) {
			return "Unknown";
		}
		
		if( data.size() == 1 && data.get(0) == null ){
			return "Unknown";
		}
		
		if( data.size() == 1){
			return guessType( data.get(0));
		}
		else{
			LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();

			//Find the types of the first 3 rows.
			for( int rowNum=0; rowNum < Math.min(3, data.size()); rowNum++ ){
				String guessedRowType = guessType(data.get(rowNum));
				if( guessedRowType.equals("Unknown"))
					continue;
				if( !scores.containsKey(guessedRowType)){
					scores.put(guessedRowType, 1);
				}
				else{ //increment score by 1.
					int typeScore = scores.get(guessedRowType);
					scores.put(guessedRowType, typeScore+1);
				}
			}
			
			//If the first 3 rows agree, then att is probably that thing. If they don't agree, try testing some more rows.
			if( scores.size() == 1 && scores.values().iterator().next() == Math.min(3, data.size())){
				Iterator<String> iter = scores.keySet().iterator();
				return iter.next();
			}
			else{
				for( int rowNum=3; rowNum < Math.min(9, data.size()); rowNum++ ){
					String guessedRowType = guessType(data.get(rowNum));
					if( guessedRowType.equals("Unknown"))
						continue;
					if( !scores.containsKey(guessedRowType)){
						scores.put(guessedRowType, 1);
					}
					else{ //increment score by 1.
						int typeScore = scores.get(guessedRowType);
						scores.put(guessedRowType, typeScore+1);
					}
				}				
			}
			
			//Return guess with the most votes.
			int bestCount = 0;
			String bestString = "Unknown";
			for( String key : scores.keySet() ){
				if( !key.equals("Unknown") && scores.get(key) > bestCount){
					bestString = key;
					bestCount = scores.get(key);
				}
			}

            try {
                //If it's still unknown, and the att takes 2 possible values, then call it "binominal" (We can do filtering on it etc.)
                if (bestString.equals("Unknown") && data.size() > 2)
                if( att.getUniqueRowsInTable() == 2) {
                    bestString = "binominal";
                }
            } catch( SQLException e ){

            }

			return bestString;
		}


	}
	private static String guessType(String row){
		//TODO: Better guessing. Classifier?

        if( row.length() == 0){
            return "empty";
        }
        if( StringUtils.trim(row).length() == 0){
            return "whitespace";
        }

		if( StringUtils.startsWith(row, "<") || (StringUtils.countMatches(row, "<") == StringUtils.countMatches(row, ">") ) && StringUtils.countMatches(row, "<") >= 2 ){  
			return "HTML";
		}
		if( (row.startsWith("[") && row.endsWith("]")) || (row.startsWith("{") && row.endsWith("}") ) ){
			return "JSON";
		}
		
		if( row.length() > 1 && row.length() < 28 && StringUtils.isAlpha(row)){
			return "word";
		}

//        if( row.length() < 200 && row.length() > 5 && StringUtils.isAlpha(row.replace(".,-'\"", ""))){
//            return "sentence";
//        }


		try{
			if( (row.startsWith("http") || row.startsWith("//")) && row.contains(".") && !row.contains("\n") ){
				URL rowAsUrl = new URL( row );
				return "URL";
			}
		}
		catch( MalformedURLException e){
		}

        //It's a file if it ends .xxx or contains a /
        if( row.length() < 200 && row.length() > 5 && !row.contains("\n") && (row.contains("/") || row.substring(row.length()-4, row.length()-3).equals(".") )){
            return "file";
        }

		try{
			int rowAsInt = Integer.parseInt(row);
			
			if( rowAsInt == 0 || rowAsInt == 1){
				return "boolean";
			}
			else{
				return "integer";
			}
		}
		catch( NumberFormatException e){
			//not an integer.
		}


		try{
			Float rowAsFloat = Float.parseFloat(row);

			return "float";
		}
		catch( NumberFormatException e){
			//not an integer.
		}
		
		if( ( row.length() < 30 && row.length() >= 6 ) && row.matches(".*(19|20)[0-9]{2}.*") ){
			Parser dateParser = new Parser();
			List<DateGroup> stringParts = dateParser.parse(row);
			if( stringParts.size() > 0){
				return "date";
			}
		}
		
		if( StringUtils.countMatches(row, ",") > row.length() / 100){
			return "CSV";
		}

        if( row.length() == 1 ){
            return "character";
        }
		
		//Add date, time, image, audio, video, Hex digits, names, places,

		return "Unknown";
		
	}
}
