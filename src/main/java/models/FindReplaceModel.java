package models;

import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindReplaceModel extends Model{
	private LinkedHashMap<String,String> replacements;

	public FindReplaceModel(LinkedHashMap<String,String> replacements) {
        this.replacements = replacements;
	}

	@Override
	public String predictForRow(List<String> inputRow) {
        //Take the feature-1 column (the 0th column is the label) and perform the find/replaces in order.
        String str = "$ROWDELIMITER^"+inputRow.get(1)+"$ROWDELIMITER^";
        for( String needle : replacements.keySet()){
            String newNeedle = replacements.get(needle);
            try {
                if (needle.length() <= str.length()) {
                    str = str.replaceAll(Pattern.quote(needle), Matcher.quoteReplacement(newNeedle));
                }
            }
            catch( StringIndexOutOfBoundsException e ){
                System.out.println(e);
            }
            str = str.replaceAll(Pattern.quote("$ROWDELIMITER^"), "");
        }
        return str;
	}

	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		
		ArrayList<String> predictions = new ArrayList<>();
		for( int row=0; row < testTable.rowKeySet().size(); row++ )
		{
			predictions.add( predictForRow(Arrays.asList("LABEL",testTable.get(row,1))) );
		}
		
		return predictions;
	}
	
	public String toString()
	{
		return this.getClass().getSimpleName();
	}



}
