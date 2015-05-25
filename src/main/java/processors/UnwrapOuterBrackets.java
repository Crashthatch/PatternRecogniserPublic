package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks for the outermost ()s, []s, or {}s, and discards them and everything outside them.
 * Irreversible since the outer stuff gets discarded. Could potentially make reversible by outputting 3 columns: "before", "inside", "after".
 * eg. strips jsonwrapper( {a: "bcd"} ) to {a: "bcd"}
 * eg. strips fdsjo  fkodsaj fijf djsiao  (4,6,7,9,2) gkodr ji fdoks  -> 4,6,7,9,2
 */
public class UnwrapOuterBrackets extends TransformationService {
    Pattern startBracketPattern = Pattern.compile("[\\[\\(\\{]");
    Pattern endBracketPattern = Pattern.compile("[\\]\\)\\}]");

	public UnwrapOuterBrackets()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

    private String getOpenType(String closeBracket){
        if( closeBracket.equals(")") ){
            return "(";
        }
        else if( closeBracket.equals("]") ){
            return "[";
        }
        else if( closeBracket.equals("}") ){
            return "{";
        }
        else{
            throw new RuntimeException("Unknown close bracket type");
        }
    }

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
        String stringin = (String) input.get(0,0);
        String stringrev = StringUtils.reverse(stringin);
        Matcher startBracketMatcher = startBracketPattern.matcher(stringin);
        //Find the first open-bracket.
        int startPos = -1;
        int endPos = -1;
        startBracketLoop:
        while( startBracketMatcher.find() ){
            String startBracketType = startBracketMatcher.group();
            //Start at the END of the string and move backwards until we find a close-bracket of the same type as the open-bracket we found.
            Matcher endBracketMatcher = endBracketPattern.matcher(stringrev);
            while( endBracketMatcher.find() ){
                String endBracketType = getOpenType(endBracketMatcher.group());
                if( endBracketType.equals( startBracketType )){
                    startPos = startBracketMatcher.start();
                    endPos = stringin.length()-endBracketMatcher.start();
                    if( startPos < endPos ) {
                        break startBracketLoop;
                    }
                    else{
                        //The closing-position (searching backwards) has gone past the start pos. There is no matching closing bracket.
                        // Skip to the next opening bracket.
                        startPos = -1;
                        endPos = -1;
                        continue startBracketLoop;
                    }
                }
            }
        }

		if( startPos != -1 && endPos != -1){
			outTable.put(0, 0, stringin.substring(startPos+1, endPos-1));
		}
		
		return outTable;
	}
}
