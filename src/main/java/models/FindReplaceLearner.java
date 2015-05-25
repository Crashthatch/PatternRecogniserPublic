package models;

import com.google.common.collect.Table;
import com.google.common.primitives.Chars;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class FindReplaceLearner extends ModelLearner{

    public int getComplexityOrdering(){
        return 50;
    }

    //Only uses one feature. Others will be ignored. Perhaps this should be metadata stored on the class so we don't waste time trying to learn from more than one att?
	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, OperatorException {
        String allFeatureRows = StringUtils.join(inputTable.column(1).values(), "ROWDELIMITER");
        String allLabelRows = StringUtils.join(inputTable.column(0).values(), "ROWDELIMITER");

        LinkedHashMap<String, String> replacements = new LinkedHashMap<String, String>();

        String allFeatureRowsReplaced = allFeatureRows; //Start with what was passed in.
        while( !allFeatureRowsReplaced.equals(allLabelRows) && replacements.size() <= 20) {
            //Find diffs between the current working copy and the target.
            List<Character> labelCharList = Chars.asList(allLabelRows.toCharArray());
            List<Character> featureCharList = Chars.asList(allFeatureRowsReplaced.toCharArray());
            Patch diff = DiffUtils.diff(featureCharList, labelCharList);

            //Find out which delta (substitution) is the best to apply next (which delta reduces the number of diffs between the current string and the target?)
            //Optimization: Instead of finding the "best" delta to apply out of the ones found, just apply the first one and backtrack if it makes it worse.
            Set<String> triedReplacements = new HashSet<>();
            int bestFitness = StringUtils.getLevenshteinDistance(allFeatureRowsReplaced, allLabelRows);
            String bestFrom = null;
            String bestTo = null;
            for (Delta delta : diff.getDeltas()) {
                String original = StringUtils.join(delta.getOriginal().getLines(), "");

                int originalStartIdx = delta.getOriginal().getPosition();
                int originalEndIdx = originalStartIdx + delta.getOriginal().getLines().size();
                String revised = StringUtils.join(delta.getRevised().getLines(), "");

                if( triedReplacements.contains(original+revised)){ //Strictly speaking, just because we already tried "x" => "y" it might be different this time because x is surrounded by different letters this time (in this position). Can't easily think of an example of when this would happen though.
                    continue;
                }
                triedReplacements.add(original+revised);

                for( int charsBefore = 0; charsBefore < 4; charsBefore++ ) {
                    int startPrefixIdx = Math.max(0, originalStartIdx - charsBefore);
                    String prefix = allFeatureRowsReplaced.substring(startPrefixIdx, originalStartIdx);
                    for( int charsAfter = 0; charsAfter < 4-charsBefore; charsAfter++ ) {

                        int endSuffixIdx = Math.min(allFeatureRowsReplaced.length(), originalEndIdx + charsAfter );
                        String suffix = allFeatureRowsReplaced.substring(originalEndIdx, endSuffixIdx);

                        String from = new StringBuilder(prefix).append(original).append(suffix).toString();
                        if( from.equals("")){
                            continue;
                        }
                        String to = new StringBuilder(prefix).append(revised).append(suffix).toString();

                        String allFeatureRowsReplacedThisDelta = allFeatureRowsReplaced.replaceAll(Pattern.quote(from), to);
                        int fitness = StringUtils.getLevenshteinDistance(allFeatureRowsReplacedThisDelta, allLabelRows);
                        if( fitness < bestFitness ){
                            bestFitness = fitness;
                            bestFrom = from;
                            bestTo = to;
                        }
                    }
                }
            }

            //Apply the best substitution we found.
            if( bestFrom == null ){
                break; //Cannot improve from the current position.
            }
            else{
                replacements.put( bestFrom, bestTo);
                allFeatureRowsReplaced = allFeatureRowsReplaced.replaceAll(bestFrom, bestTo);
            }
        }


		
		return new FindReplaceModel(replacements);
	}


}
