package models;

import com.google.common.collect.Table;
import com.google.common.primitives.Chars;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.rapidminer.operator.OperatorCreationException;
import database.DiffCallable;
import database.UnsuitableModelException;
import difflib.Delta;
import difflib.Patch;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindReplaceLearnerSinglePass extends ModelLearner{
    private static final TimeLimiter limiter = new SimpleTimeLimiter();

    public int getComplexityOrdering(){
        return 50;
    }

    //Only uses one feature. Others will be ignored. Perhaps this should be metadata stored on the class so we don't waste time trying to learn from more than one att?
	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws SQLException, OperatorCreationException, UnsuitableModelException {
        String allFeatureRows = "$ROWDELIMITER^"+StringUtils.join(inputTable.column(1).values(), "$ROWDELIMITER^")+"$ROWDELIMITER^";
        String allLabelRows = "$ROWDELIMITER^"+StringUtils.join(inputTable.column(0).values(), "$ROWDELIMITER^")+"$ROWDELIMITER^";
        List<Character> labelCharList = Chars.asList(allLabelRows.toCharArray());
        List<Character> featureCharList = Chars.asList(allFeatureRows.toCharArray());

        if( allFeatureRows.length() > 5*(allLabelRows.length()+10) || allLabelRows.length() > 5*(allFeatureRows.length()+10)){
            throw new UnsuitableModelException("input & output rows are significantly different lengths. FindReplaceModel is unlikely to produce a good model.");
        }

        final long timeit = System.nanoTime();
        Patch diff = null;
        try {
            Callable<Patch> diffCallable = new DiffCallable(featureCharList, labelCharList);
            diff = limiter.callWithTimeout(diffCallable, 1, TimeUnit.SECONDS, false);
        }
        catch( UncheckedTimeoutException E ){
            throw new UnsuitableModelException("Computing diff took a long time. Probably would not produce a good model.");
        }
        catch( Exception E){
            E.printStackTrace();
        }
        LinkedHashMap<String, String> replacements = new LinkedHashMap<>();

        //Convert the diffs to a lookup table so we can easily find all the places that a certain "x -> y" replacement was made.
        LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> originalAndReplacementStringPos = new LinkedHashMap<>();
        LinkedHashMap<String, List<Integer>> originalStringPos = new LinkedHashMap<>();

        for( Delta delta : diff.getDeltas()) {
            String original = StringUtils.join(delta.getOriginal().getLines(), "");
            String revised = StringUtils.join(delta.getRevised().getLines(), "");

            if( original.length() > 50 || revised.length() > 50) {
                throw new UnsuitableModelException("original or revised are too long.");
            }


            List<Integer> allReplacedOriginalStringPos = originalStringPos.get(original);
            if( allReplacedOriginalStringPos == null ){
                allReplacedOriginalStringPos = new ArrayList<>();
                originalStringPos.put(original, allReplacedOriginalStringPos);
            }
            allReplacedOriginalStringPos.add(delta.getOriginal().getPosition());


            LinkedHashMap<String, List<Integer>> replacementLocations = originalAndReplacementStringPos.get(original);
            if( replacementLocations == null ) {
                replacementLocations = new LinkedHashMap<>();
                originalAndReplacementStringPos.put(original, replacementLocations);
            }
            List<Integer> locations = replacementLocations.get(revised);
            if( locations == null ){
                locations = new ArrayList<>();
                replacementLocations.put( revised, locations );
            }
            locations.add(delta.getOriginal().getPosition());
        }

        //System.out.println("Found "+originalStringPos.keySet().size()+" original strings that need to be replaced by something.");
        if( originalStringPos.keySet().size() > 10 ){
            throw new UnsuitableModelException("Too many replacements to use a FindReplace Model (probably won't have much success).");
        }

        //If we are replacing versions of original that should not be replaced (eg. replacing 'and' as part of 'hand' with an '&'), add that as a "replacement" that replaces with itself (the clash 'replacing x with both x and y' will be resolved below and then we'll strip out useless replacements).
        for( String original : originalStringPos.keySet() ){
            List<Integer> replacementLocations = originalStringPos.get(original);
            int index = allFeatureRows.indexOf(original);
            while( index > -1 ){
                if( !replacementLocations.contains(index) ){
                    //Found a version of "original" that is not part of any diff.
                    LinkedHashMap<String, List<Integer>> replacementsMap = originalAndReplacementStringPos.get(original);
                    if( replacementsMap == null ) {
                        replacementsMap = new LinkedHashMap<>();
                        originalAndReplacementStringPos.put(original, replacementsMap);
                    }
                    List<Integer> locations = replacementsMap.get(original);
                    if( locations == null ){
                        locations = new ArrayList<>();
                        replacementsMap.put( original, locations );
                    }
                    locations.add(index);
                }
                //Find the next one for the next loop.
                int oldIndex = index;
                index = allFeatureRows.indexOf(original, index + 1);
                if( oldIndex == index ){
                    break;
                }
            }
        }

        //If an "original" string is mapped to more than one "replacement" (ie. the inner hashmap has more than one key) then need to extend both of the "original" strings (extending only one will not do because the shorter one will still match both).
        for( String original : originalStringPos.keySet() ){
            if( originalAndReplacementStringPos.get(original).size() == 1) {
                String replacement = originalAndReplacementStringPos.get(original).keySet().iterator().next();
                if( !replacement.equals(original) ){
                    replacements.put(original, replacement);
                }
            }
            else if (!original.equals("") && originalAndReplacementStringPos.get(original).size() > 5){
                //System.out.println("Skipping replacements of "+original+" because it is replaced by too many things: "+originalAndReplacementStringPos.get(original));
            }
            else{
                //System.out.println("Extending "+original+" because it is replaced by many things: "+originalAndReplacementStringPos.get(original));
                for( String replacement : originalAndReplacementStringPos.get(original).keySet() ) {
                    List<Integer> thisReplacementIndexes = originalAndReplacementStringPos.get(original).get(replacement); //The indexes of the original that are to be replaced by this replacement (and not other indexes of the original).
                    int firstOccurranceOriginalThisReplacement = thisReplacementIndexes.get(0);
                    boolean extensionFound = false;
                    prefixLoop:
                    for (int prefixLength = 0; prefixLength < 4 && prefixLength <= firstOccurranceOriginalThisReplacement; prefixLength++) {
                        for (int suffixLength = 0; suffixLength < 4 && suffixLength <= allFeatureRows.length() - firstOccurranceOriginalThisReplacement - original.length(); suffixLength++) {
                            //Extend the first instance of the original that is being placed by replacement to create a new replacement.
                            String extendedOriginal = allFeatureRows.substring(firstOccurranceOriginalThisReplacement-prefixLength, firstOccurranceOriginalThisReplacement+original.length()+suffixLength);
                            //System.out.println("Extending "+original+" (> "+replacement+") to "+extendedOriginal);

                            //Find all instances of extendedOriginal.
                            List<Integer> locationsOfOriginalInExtendedOriginal = new ArrayList<>();
                            Pattern p = Pattern.compile(Pattern.quote(extendedOriginal));
                            Matcher m = p.matcher(allFeatureRows);
                            while (m.find()) {
                                //Adjust the index to ignore the prefix, so we specify the index of "Original" inside "extendedOriginal" in the long allFeatureRows string.
                                locationsOfOriginalInExtendedOriginal.add( m.start()+prefixLength );
                            }
                            //Check that the extended version matches all those that should get this replacement.
                            //Make sure that extendedOriginal ONLY matches those that should get this replacement, and there are no other instances of extendedOriginal that should not be replaced.
                            if( locationsOfOriginalInExtendedOriginal.equals(thisReplacementIndexes)){
                                //We've successfully extended original and made it match exactly those it should. Add this as a replacement.
                                String prefix = extendedOriginal.substring(0,prefixLength);
                                String suffix = extendedOriginal.substring(extendedOriginal.length()-suffixLength, extendedOriginal.length());
                                String extendedReplacement = prefix+replacement+suffix;
                                if( !extendedReplacement.equals( extendedOriginal )) {
                                    replacements.put(extendedOriginal, extendedReplacement);
                                    //System.out.println("Successfully extended " + original + " -> " + replacement + " to " + extendedOriginal + " -> " + extendedReplacement + "");
                                    extensionFound = true;
                                    break prefixLoop;
                                }
                            }
                        }
                    }
                    if( !extensionFound ){
                        //System.out.println("Could not extend "+original+" -> "+replacement);
                    }
                }
            }
        }



        //TODO: Or support nested replacements. eg. "<foo>" -> "<words", then "<" -> "two", resulting in "<foo>" -> "two words".
        //Probably much slower because we need to make a replacement, then re-run on the replaced string, or adjust string positions we already calculated.

        //TODO: Combine replacements? If one replacement output contains another replacement's input, substitute it in.
        // eg. (1) "<foo>" -> "<words" and (2) "<" -> "two ": (1)'s replacement ("<words") contains (2)'s original ("<"), so can be substituted in to give: "<foo>" -> "two words"

        //System.out.println("All Replacements: "+replacements);
        return new FindReplaceModel(replacements);

    }


}
