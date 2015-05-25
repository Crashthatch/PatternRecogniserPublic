package database.featureProcessorSelector.transformerSelector;

import com.google.common.base.Functions;
import com.google.common.collect.*;
import com.google.common.math.IntMath;
import com.google.common.util.concurrent.AtomicLongMap;
import database.Att;
import database.TransformationService;
import database.featureProcessorSelector.TransformerSelector;
import database.features.TypeGuesser;
import org.apache.commons.collections4.map.DefaultedMap;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import processors.*;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuessedTypeTransformerSelector extends TransformerSelector {
	
	public GuessedTypeTransformerSelector(){ //Constructors are not inherited by default because then everything would inherit the empty constructor from Object. See http://stackoverflow.com/questions/1644317/java-constructor-inheritance
		super();
	}
	public GuessedTypeTransformerSelector( Collection<TransformationService> transformers ){
		super(transformers);
	}

	public List<TransformationService> getBestProcessorsForAtt(Att att) {
		LinkedHashSet<TransformationService> bestTransformersForAtt = new LinkedHashSet<>();
		String guessedType = TypeGuesser.guessType(att);
        try {
            if (att.getNotNullRowsInTable() < att.getDataTable().getNumRows() && att.getNotNullRowsInTable() > 1) {
                bestTransformersForAtt.add(new rownum());
            }
        }
        catch( SQLException e ){
            e.printStackTrace();
        }


		if( guessedType.equals("HTML") ){
			//Try the HTML-type processors.
			bestTransformersForAtt.add( new xmlUnwrap()); 
			bestTransformersForAtt.add( new htmlText() );
			bestTransformersForAtt.add( new htmlToXhtml() );
			//bestTransformersForAtt.addAll( getSelectorsForTagsContainingConstant(att, stringConstants) );
            bestTransformersForAtt.add( new xmlManyRootEltsToRows() );
            bestTransformersForAtt.add( new xmlOutermostAttributes() );
            bestTransformersForAtt.add( new xmlOutermostAttributeValuesToCols() );
            bestTransformersForAtt.add( new HtmlXpathSelector("h1") );
            bestTransformersForAtt.addAll( getHtmlSelectorsForAllClasses(att));
			bestTransformersForAtt.addAll( getHtmlSelectorsForAllIds(att));
            bestTransformersForAtt.addAll( getHtmlSelectorsForAllTags(att));
		}
		else if( guessedType.equals("JSON") ){
			bestTransformersForAtt.add( new jsonDecodeList() );
			bestTransformersForAtt.add( new jsonDecodeMap() );
			bestTransformersForAtt.add( new JsonDecodeMapManyColsOut() );
		}
		else if( guessedType.equals("URL") ){
			bestTransformersForAtt.add( new HttpGet() );
			//Extract Domain? TLD? QueryParams? Directory? Protocol?
		}
		else if( guessedType.equals("word") ){
			//bestTransformersForAtt.add( new isANoun() );  //Removed because I haven't written a unix version of the wordnet processors yet.
			//bestTransformersForAtt.add( new isAVerb() );
			bestTransformersForAtt.add( new inWordList8() );
			bestTransformersForAtt.add( new firstCharacter() );
			bestTransformersForAtt.add( new stringLength() );
		}
		else if( guessedType.equals("boolean") ){
			bestTransformersForAtt.add( new notProcessor() );
            bestTransformersForAtt.add( new FilterFalsey() );
		}
        else if( guessedType.equals("binominal")){
            bestTransformersForAtt.add( new FilterFalsey() );
            //TODO: Add filter that returns 2 columns, one col is "isValA", the other is "isValB", which contain either 1 or NULL.
        }
        else if( guessedType.equals("empty")){
            bestTransformersForAtt.add( new FilterFalsey() );
        }
        else if( guessedType.equals("character")){
            bestTransformersForAtt.add( new CurrencySymbol() );
        }
        else if( guessedType.equals("file")){
            bestTransformersForAtt.add( new escapeSpaces() );
            bestTransformersForAtt.add( new escapeSpacesAndSingleQuotes() );
            bestTransformersForAtt.add( new SplitOnStringToColumns("/"));

            try {
                bestTransformersForAtt.addAll(getSplitOnGuessedDelimitersToRows(att));
            }
            catch( SQLException e ){
                System.err.println(e);
            }
        }
		else if( guessedType.equals("integer") ){
			bestTransformersForAtt.add( new normalise() );
			bestTransformersForAtt.add( new isPrime() );
			bestTransformersForAtt.add( new primeFactors() );
			bestTransformersForAtt.add( new total() );
			bestTransformersForAtt.add( new jsonDecodeMap() );
		}
		else if( guessedType.equals("date")){
			bestTransformersForAtt.add( new DateToDateParts() ); //Parses a lot, but irreversible.
            bestTransformersForAtt.add( new DateToDatePartsSingleFixedFormat("dd/MM/yyyy")); //DateToDateParts fails to parse dd/mm/yy dates correctly.
            //bestTransformersForAtt.add( new DateToDatePartsSimple() );

            bestTransformersForAtt.add( new SplitOnStringToColumns("-"));
            bestTransformersForAtt.add( new SplitOnStringToColumns("/"));
            bestTransformersForAtt.add( new SplitOnStringToColumns("."));
            bestTransformersForAtt.add( new SplitOnStringToColumns(" "));
            bestTransformersForAtt.add( new SplitOnStringToColumns(":"));
            bestTransformersForAtt.add( new SplitOnStringToColumns("T"));
            bestTransformersForAtt.add( new SplitOnStringToColumns("+"));
            bestTransformersForAtt.add( new DateToDatePartsSingleFixedFormat("yyyyMMdd"));
            bestTransformersForAtt.add( new DateToDatePartsSingleFixedFormat("yyyyMd"));

            //Maybe it's a date with something else in there too (like a time). Or it's a date with weird delimiters.
            try {
                bestTransformersForAtt.addAll(getSplitOnGuessedDelimitersToRows(att));
            }
            catch( SQLException e ){
                System.err.println(e);
            }
		}
		else if( guessedType.equals("CSV") ){
            bestTransformersForAtt.add( new ReadCSVToTable() );
            bestTransformersForAtt.add( new ReadCSVToTableQuoteOnlyIfNeeded() );
            bestTransformersForAtt.add( new ReadCSVToTableAlwaysQuoted() );
            bestTransformersForAtt.add( new SplitOnStringToRows(","));
			bestTransformersForAtt.add( new SplitOnStringToRows(";"));
			bestTransformersForAtt.add( new SplitOnStringToRows("\r\n"));
			bestTransformersForAtt.add( new SplitOnStringToRows("\r"));
			bestTransformersForAtt.add( new SplitOnStringToRows("\n"));
			bestTransformersForAtt.add( new SplitOnStringToRows(" "));
            bestTransformersForAtt.add( new SplitOnStringToRows("\t"));
            try {
                bestTransformersForAtt.addAll(getSplitOnGuessedDelimitersToRows(att));
            }
            catch( SQLException e ){
                System.err.println(e);
            }
		}
		else{
            bestTransformersForAtt.add( new SplitOnStringToRows("\n"));
            bestTransformersForAtt.add( new SplitOnStringToRows(","));
            try {
                bestTransformersForAtt.addAll(getSplitOnGuessedDelimitersToRows(att));
            }
            catch( SQLException e ){
                System.err.println(e);
            }
		}

        //If this att takes on a limited number of values (like categories), and each value appears multiple times, try filtering to each individual value.
        try{
            if( !guessedType.equals("boolean") && (att.getUniqueRowsInTable()*2) < att.getNotNullRowsInTable() && att.getUniqueRowsInTable() >= 2 ){
                LinkedHashMultiset<String> uniqueRows = LinkedHashMultiset.create();
                uniqueRows.addAll(att.getData());
                for( String uniqueRow : uniqueRows.elementSet() ){
                    if( uniqueRow.length() < 100 && uniqueRows.count(uniqueRow) > 1 ){
                        bestTransformersForAtt.add( new FilterEqualsValue(uniqueRow));
                        //bestTransformersForAtt.add( new EqualsValue(uniqueRow));
                    }
                }
            }
        }
        catch( SQLException e ){
            e.printStackTrace();
        }

        //Splitting off static prefixes and suffixes is probably a good idea, whatever the guessed type of att.
        try {
            bestTransformersForAtt.addAll( getSplittersForStaticEnds(att));
        }
        catch( SQLException e ){
            System.err.println(e);
        }

        //If there are multiple rows with the same prefix, add "startsWithPrefix" processors.
        try{
            bestTransformersForAtt.addAll( getRepeatedRowPrefixDetectors(att, 3));
        }
        catch( SQLException e ){
            System.err.println(e);
        }

        //If there are multiple rows that contain the same value, add "Contains" processors.
        try{
            bestTransformersForAtt.addAll( getContainsWhereSomeRowsContain(att));
        }
        catch( SQLException e ){
            System.err.println(e);
        }

		
		//Add rest of transformers on the end of the list; it will be read in order.
		for( TransformationService transformer : possibleTransformers ){
			if( !bestTransformersForAtt.contains(transformer) ){
				bestTransformersForAtt.add(transformer);
			}
		}
		
		return new ArrayList<TransformationService>(bestTransformersForAtt);
	}

    /*
    If all rows in the att start with the same prefix, split after that prefix so we get an att of the interesting stuff, without the constant prefix.
    Same for suffixes.
     */
    public static List<TransformationService> getSplittersForStaticEnds(Att att) throws SQLException {
        ArrayList<TransformationService> ret = new ArrayList<>();

        if( att.getNotNullRowsInTable() > 1 && att.getFirstRow().length() > 1 ) {
            ArrayList<String> allData = att.getData();
            ArrayList<String> reversedData = new ArrayList<>();
            String commonPrefix = StringUtils.getCommonPrefix(allData.toArray(new String[0]));
            for (String row : allData) {
                reversedData.add(StringUtils.reverse(row));
            }
            String commonSuffix = StringUtils.reverse(StringUtils.getCommonPrefix(reversedData.toArray(new String[0])));
            //If it has both a static prefix AND static suffix, split both off in one go, leaving the interesting middle bit in its own column (rather than needing 2 rounds to first split off the start, then split off the end).
            if(!commonPrefix.equals("") && !commonSuffix.equals("") ) {
                ret.add(new SplitEndsToColumns(commonPrefix.length(), -1*commonSuffix.length()));
            }
            else if (!commonPrefix.equals("")) {
                ret.add(new SplitAtCharacterIdxToColumns(commonPrefix.length()));
            }
            else if (!commonSuffix.equals("")) {
                ret.add(new SplitAtCharacterIdxToColumns(-1*commonSuffix.length()));
            }
        }

        return ret;
    }

    public static List<TransformationService> getSplitOnGuessedDelimitersToRows(Att att) throws SQLException {
        ArrayList<TransformationService> splitTransformers = new ArrayList<>();

        //Is there something that looks like it might be a good thing to split on (eg. for tab-separated files, or files with weird delimiters.)
        //Find all 1,2 or 3-character strings that appear the same number of times in all rows.
        HashMap<String, Integer> nGrams = new HashMap<>();
        String firstRow = att.getFirstRow();
        ArrayList<String> allData = att.getData();
        if( att.getNotNullRowsInTable() > 1 ){

            //Doesn't make sense to split a 2-character string on one of its characters.
            if( firstRow.length() < 3){
                return splitTransformers;
            }

            nGrams = getNGrams(firstRow, 1, Math.min(3, firstRow.length()-2));
            //There's a reasonable chance of a random n-character substring being in all the rows the same number of times if the number of rows is small and the rows are quite long.
            //Try to filter out those where it could be down to chance that the delimiter popped up this many times.
            HashMap<String, Integer> unlikelyChanceNGrams = new HashMap<>();
            for( String nGram : nGrams.keySet() ){
                int nGramCount = nGrams.get(nGram);
                int possiblePositions = firstRow.length()-nGram.length()+1;
                double chanceOfNGramBeingAtAPoint = getChanceOfNGramBeingAtAPoint(nGram);
                //double chanceOfNGramBeingInRowKtimes = Math.exp(Math.log(IntMath.binomial(possiblePositions, nGramCount))+nGramCount*Math.log(chanceOfNGramBeingAtAPoint)+(possiblePositions-nGramCount)*Math.log(1-chanceOfNGramBeingAtAPoint));
                double chanceOfNGramBeingInRowKtimes = IntMath.binomial(possiblePositions, nGramCount )*Math.pow(chanceOfNGramBeingAtAPoint,nGramCount)*Math.pow(1-chanceOfNGramBeingAtAPoint, possiblePositions-nGramCount);
                //assert(noLogChanceOfNGramBeingInRowKtimes == chanceOfNGramBeingInRowKtimes);
                double chanceOfNGramBeingInAllRowsKtimes = Math.pow(chanceOfNGramBeingInRowKtimes, att.getNotNullRowsInTable() - 1); //-1 because it's definitely in the first row.
                if( chanceOfNGramBeingInAllRowsKtimes > 0.01 || chanceOfNGramBeingInAllRowsKtimes > 1.0/nGrams.size() ){
                    //System.out.println("There is a good chance ("+(chanceOfNGramBeingInAllRowsKtimes*100)+"%) that "+nGram+" could occur in all "+att.getNotNullRowsInTable()+" rows (of length "+firstRow.length()+") "+nGramCount+" times by chance! Not splitting on this nGram.");
                }
                else{
                    unlikelyChanceNGrams.put(nGram, nGramCount);
                }
            }
            nGrams = unlikelyChanceNGrams;
            unlikelyChanceNGrams = null;


            //Ensure the nGram appears the same number of times in at least 90% of the rows.
            //Remove nGrams that start or end a row (delimiters should be in the middle of some text, not starting/ending it).
            HashMap<String, AtomicLongMap<Integer>> nGramCountsPerRow = new HashMap<>();
            for( String nGram : nGrams.keySet() ){
                nGramCountsPerRow.put(nGram, AtomicLongMap.<Integer>create());
            }
            for( String row: allData) {
                HashMap<String, Integer> thisRowNGrams = getNGrams(row, 1, 3);
                //Add one to the number of rows that have this String and number of occurrences.
                for( String nGram : nGrams.keySet() ){
                    Integer countThisRow = thisRowNGrams.get(nGram);
                    if( countThisRow != null ) {
                        nGramCountsPerRow.get(nGram).incrementAndGet(countThisRow);
                    }
                    else{
                        nGramCountsPerRow.get(nGram).incrementAndGet(0);
                    }
                }
            }
            //Only keep nGrams where at least 90% of the rows contain the same number of occurrences.
            //eg. allow where there are 100 rows, but 9 of them contain extra delimiters.
            HashMap<String, Integer> keepNGrams = new HashMap<>();
            nGramLoop:
            for( String nGram : nGramCountsPerRow.keySet()){
                for( Integer occurrencesInRow : nGramCountsPerRow.get(nGram).asMap().keySet()){
                    long numRowsWithOccurrences = nGramCountsPerRow.get(nGram).get(occurrencesInRow);
                    if( occurrencesInRow != 0 && numRowsWithOccurrences > 0.9*att.getNotNullRowsInTable()){
                        keepNGrams.put(nGram, occurrencesInRow);
                        continue nGramLoop;
                    }
                }
            }
            nGrams = keepNGrams;
            keepNGrams = null;
        }
        else if( att.getNotNullRowsInTable() == 1 && firstRow.length() < 100){
            //Try any single characters that appear more than 3 times and never immediately next to each other or at the start or end of the row.
            //Only try to guess a delimiter for fairly short rows, not masses of text.
            nGrams = getNGrams(firstRow, 1, 2);
            HashMap<String, Integer> keepNGrams = new HashMap<>();
            for( String nGram : nGrams.keySet() ){
                //If nGram contains a number, make sure the row doesn't contain any other nGrams which are similar with a different number.
                //eg. if nGram is "0-", disallow if there are any "1-" or "2-" etc. anywhere else in the string because we should probably just be splitting on "-" instead.
                //Same for letters.
                String nGramCharClasses = ""+nGram;
                nGramCharClasses = nGramCharClasses.replaceAll("[\\Q<([{\\^-=$!|]})?*+.>\\E]", "\\\\$0"); //Escape nGramCharClasses so that nGrams including \ or ] or . work (but don't escape the [0-9] added on the line above).
                nGramCharClasses = nGramCharClasses.replaceAll("[0-9]", "[0-9]"); //Replace any non-escaped digit with a literal "[0-9]", so "0:0" goes to "[0-9]:[0-9]".
                nGramCharClasses = nGramCharClasses.replaceAll("(?<!\\\\)[A-Za-z]", "[A-Za-z]"); //Similar for characters (characters must be non-escaped- eg. don't want to replace the "n" in "\n".
                Matcher nGramCharClassesMatcher = Pattern.compile(nGramCharClasses).matcher(firstRow);
                int nGramCharClassesCount = 0;
                while (nGramCharClassesMatcher.find())
                    nGramCharClassesCount++;

                if( nGramCharClassesCount > nGrams.get(nGram) || nGrams.get(nGram) < 2 || firstRow.startsWith(nGram) || firstRow.endsWith(nGram) ) {
                    //Skip
                }else{
                    keepNGrams.put(nGram, nGrams.get(nGram));
                }
            }
            nGrams = keepNGrams;
        }

        //Extend the nGram in both directions until it does not match all rows (to avoid producing 3 splitters: "\", "n" and "\n").
        if( nGrams.size() > 0 ) {
            ArrayList<String> reversedAllData = new ArrayList<>();
            for( String row : allData ){
                reversedAllData.add(StringUtils.reverse(row));
            }
            HashMap<String, Integer> lengthenedNGrams = new HashMap<>();
            for (String nGram : nGrams.keySet()) {
                String extendedNGram = extendNGram(nGram, allData);
                String reversedNGram = StringUtils.reverse(extendedNGram);
                String extendedReversedNGram = extendNGram(reversedNGram, reversedAllData);
                String extendedBothDirectionsNGram = StringUtils.reverse(extendedReversedNGram);

                lengthenedNGrams.put(extendedBothDirectionsNGram, StringUtils.countMatches(firstRow, extendedBothDirectionsNGram));
            }
            nGrams = lengthenedNGrams;
            lengthenedNGrams = null;
        }

        for( String nGram : nGrams.keySet() ){
            if( nGrams.get(nGram) >= 1 && nGrams.get(nGram) < 50 && !firstRow.startsWith(nGram) && !firstRow.endsWith(nGram) && !StringUtils.isAlphanumeric(nGram)) {
                //System.out.println(nGram+" splits "+att.getDbColumnNameNoQuotes()+" FirstRow: "+att.getFirstRow());
                splitTransformers.add(new SplitOnStringToRows( nGram ));
                if( att.getNotNullRowsInTable() > 1 ) { //There's no point splitting to columns if we didn't yet split to rows.
                    splitTransformers.add(new SplitOnStringToColumns(nGram));
                }
            }
        }

        return splitTransformers;
    }

    private static double getChanceOfNGramBeingAtAPoint(String nGram) {
        ArrayList<Double> charProbabilities = new ArrayList<>();
        for( int charIdx = 0; charIdx < nGram.length(); charIdx++){
            String character = nGram.substring(charIdx, charIdx+1);
            if( StringUtils.isAlpha(character) ){
                charProbabilities.add( 0.05 ); //TODO: Use a lookup of letter frequency statistics to vary 0.05 according to whether the character is likely to occur in input.
            }
            else if(StringUtils.isNumeric(character)){
                charProbabilities.add( 0.05 ); //TODO: Vary. 0s and 1s are more likely than 8s and 9s.
            }
            else{ //Punctuation
                charProbabilities.add( 0.01 );
            }
        }
        double product = 1;
        for( double prob : charProbabilities ){
            product *= prob;
        }
        return product;
    }

    private static String extendNGram(String nGramIn, ArrayList<String> allData) {
        String firstRow = allData.get(0);
        int rowId = 1;
        while( !firstRow.contains(nGramIn)){
            firstRow = allData.get(rowId);
            rowId++;
        }
        String nGram = ""+nGramIn; //Copy so we don't modify the original.
        boolean extendedNGramForwards;
        do{
            extendedNGramForwards = false;
            if( firstRow.indexOf(nGram)+nGram.length() == firstRow.length() )
                break;

            String extendedNGram = firstRow.substring(firstRow.indexOf(nGram), firstRow.indexOf(nGram)+nGram.length()+1);
            assert( extendedNGram.startsWith(nGram));
            boolean canExtend = true;
            rowLoop:
            for( String row : allData) {
                //Look at each substring of the nGram, and see if it can be extended to the extendedNGram.
                int startIdx = 0;
                while( true ) {
                    int nextNGramLocation = row.indexOf(nGram, startIdx);
                    if( nextNGramLocation == -1 ){
                        break;
                    }
                    if( !row.substring(nextNGramLocation).startsWith(extendedNGram)){
                        //Skip delimiters that start inside or are immediately preceeded by another delimiter. (eg. if nGram is -- and we are extending it to ---, don't fail because in "a---b" the final 2 dashes are not followed by a dash).
                        boolean shiftingFoundNGram = false;
                        for( int shift=1; shift <= nGram.length(); shift++ ) {
                            if (row.substring(0, nextNGramLocation + nGram.length() - shift).endsWith(nGram)) {
                                shiftingFoundNGram = true;
                                break;
                            }
                        }
                        if( !shiftingFoundNGram ){
                            canExtend = false;
                            break rowLoop;
                        }
                    }
                    startIdx = nextNGramLocation+1;
                }
            }
            if( canExtend ){
                extendedNGramForwards = true;
                nGram = extendedNGram;
            }
        }while( extendedNGramForwards );
        return nGram;
    }

    public static HashMap<String, Integer> getNGrams(String inString, int minLength, int maxLength){
        HashMap<String, Integer> ret = new HashMap<>();
        for( int length = minLength; length <= maxLength; length++){
            for( int offset = 0; offset <= inString.length()-length; offset++) {
                String nGram = inString.substring(offset, offset + length);
                int newCount;
                if (ret.containsKey(nGram)) {
                    newCount = ret.get(nGram)+1;
                }
                else{
                    newCount = 1;
                }
                ret.put(nGram, newCount);
            }
        }
        return ret;
    }

    public static List<TransformationService> getRepeatedRowPrefixDetectors(Att att, int prefixLength) throws SQLException {
        ArrayList<TransformationService> ret = new ArrayList<>();

        if( att.getNotNullRowsInTable() > 1 ) {
            ArrayList<String> allData = att.getData();
            HashMap<String, Integer> prefixCounts = new HashMap<>();
            for (String row : allData) {
                if( row.length() >= prefixLength ) {
                    String prefix = row.substring(0, prefixLength);
                    int prefixCount = 1; //overridden below if already seen.
                    if (prefixCounts.containsKey(prefix)) {
                        prefixCount = prefixCounts.get(prefix) + 1;
                    }
                    prefixCounts.put(prefix, prefixCount);
                }
            }

            for( String prefix : prefixCounts.keySet() ){
                if( prefixCounts.get(prefix) > 2 ){
                    ret.add(new StartsWithFilter(prefix));
                }
            }
        }

        return ret;
    }

    public static List<TransformationService> getContainsWhereSomeRowsContain(Att att) throws SQLException{
        ArrayList<TransformationService> ret = new ArrayList<>();

        AtomicLongMap<String> nGramRowCounts = AtomicLongMap.create();
        ArrayList<String> allData = att.getData();
        ArrayList<String> nGramsSomeRowsContain = new ArrayList<>();
        if( att.getNotNullRowsInTable() > 1 ) {

            //Create a map of how many ROWS contain each nGram. If an nGram appears twice in a row, it still only counts as one for this.
            for( String row : allData ) {
                for( String nGram : getNGrams(row, 6, Math.min(6, row.length() - 5)).keySet() ){
                    nGramRowCounts.incrementAndGet(nGram);
                }
            }

            //Create a "containsX" processor where a substantial portion of the rows contain X, but not all of them.
            Ordering<String> valueComparator = Ordering.natural().onResultOf(Functions.forMap(nGramRowCounts.asMap()));
            for( String nGram : valueComparator.immutableSortedCopy(nGramRowCounts.asMap().keySet()).reverse() ){
                long count = nGramRowCounts.get(nGram);
                if( count > 1 && count > att.getNotNullRowsInTable()*0.2 ){
                    if( count < att.getNotNullRowsInTable() ) {
                        nGramsSomeRowsContain.add( nGram );
                    }
                }
                else{
                    //Count too low. Since we sorted, we know that no future nGrams in the list will have a count high enough to be used. Abort.
                    break;
                }
            }

            //Try to extend nGrams as far as possible.
            ArrayList<String> reversedAllData = new ArrayList<String>();
            for( String row : allData ){
                reversedAllData.add(StringUtils.reverse(row));
            }
            LinkedHashSet<String> lengthenedNGrams = new LinkedHashSet<>();
            for (String nGram : nGramsSomeRowsContain) {
                String extendedNGram = extendNGram(nGram, allData);
                String reversedNGram = StringUtils.reverse(extendedNGram);
                String extendedReversedNGram = extendNGram(reversedNGram, reversedAllData);
                String extendedBothDirectionsNGram = StringUtils.reverse(extendedReversedNGram);

                lengthenedNGrams.add(extendedBothDirectionsNGram);
            }

            //Actually create the ContainsX Processors.
            for( String nGram : lengthenedNGrams){
                ret.add( new ContainsFilter(nGram));
            }
        }

        return ret;
    }

	
	private Collection<? extends TransformationService> getSelectorsForTagsContainingConstant(Att att, Collection<String> constants){
		ArrayList<String> data; //Is iterating over ALL rows in the data a good idea? Could take a long time on atts with lots of rows. Is it against the "ideal" of the ProcessorSelector not needing to examine the data values themselves and only using the tree structure to decide what to try next? 
		try {
			data = att.getData();
		} catch (SQLException e) {
			return new ArrayList<>();
		}
		
		if( data.get(0) == null ){
			return new ArrayList<>();
		}
		
		Map<String, Integer> xPathCounts = new DefaultedMap<>(0);
		//Add any tags/classes/ids that directly contain a sought constant within them.
		//Could do better? Want classes/tags/ids that ALL contain a constant. eg. ".answer", not "div".
		for( String rowData : data ){
			Document doc = Jsoup.parse(rowData);
			Elements els = doc.getAllElements();
			for( Element el : els ){
				boolean elContainsConstant = false;
				for( String constant : constants){
					if( el.text().contains(constant) ){
						elContainsConstant = true;
						break;
					}
				}
				if( elContainsConstant ){
					if( !el.id().equals("")){
						xPathCounts.put("#"+el.id(), xPathCounts.get("#"+el.id())+1);
					}
					xPathCounts.put(el.tagName(), xPathCounts.get(el.tagName())+1);
					
					for( String className : el.classNames() ){
						if( !className.equals("")){
							xPathCounts.put("."+className, xPathCounts.get("."+className)+1);
						}
					}
				}
			}
		}
		
		//Sort so the classes / tagnames that contain constants most often come first.
		Ordering<String> ordering = Ordering.natural().onResultOf(Functions.forMap(xPathCounts));
		ImmutableList<String> pathsSortedByCounts = ordering.immutableSortedCopy(xPathCounts.keySet()).reverse();
		
		Set<TransformationService> selectors = new LinkedHashSet<>();
		for( String path : pathsSortedByCounts ){
			selectors.add( new HtmlXpathSelector(path));
		}
		
		return selectors;
	}
	
	private Collection<? extends TransformationService> getHtmlSelectorsForAllClasses(Att att) {
		ArrayList<String> data; //Is iterating over ALL rows in the data a good idea? Could take a long time on atts with lots of rows. Is it against the "ideal" of the ProcessorSelector not needing to examine the data values themselves and only using the tree structure to decide what to try next? 
		try {
			data = att.getData();
		} catch (SQLException e) {
			return new ArrayList<>();
		}
		
		if( data.get(0) == null ){
			return new ArrayList<>();
		}
		
		Map<String, Integer> classNameCounts = new HashMap<>();
		//Add all classes found in any row of the data as possible classes to extract.
		for( String rowData : data ){
			Document doc = Jsoup.parse(rowData);
			Elements els = doc.getAllElements();
			for( Element el : els ){
				for( String className : el.classNames() ){
					className = className.trim();
					if( !className.equals("") ){
						Integer count = classNameCounts.get(className);
						if( count == null ){
							count = 0;
						}
						classNameCounts.put(className, count+1);
					}
				}
			}
		}
		
		//Sort by number of occurances so the most popular classes appear at the top.
		Ordering<String> ordering = Ordering.natural().onResultOf(Functions.forMap(classNameCounts));
		ImmutableList<String> classesSortedByCounts = ordering.immutableSortedCopy(classNameCounts.keySet()).reverse();
		
		Set<TransformationService> classSelectors = new LinkedHashSet<>();
		for( String className : classesSortedByCounts ){
			classSelectors.add( new HtmlXpathSelector("."+className));
		}
		
		return classSelectors;
	}
	private Collection<? extends TransformationService> getHtmlSelectorsForAllTags(Att att) {
		ArrayList<String> data;
		try {
			data = att.getData();
		} catch (SQLException e) {
			return new ArrayList<>();
		}
		
		if( data.get(0) == null ){
			return new ArrayList<>();
		}
		
		LinkedHashSet<TransformationService> selectors = new LinkedHashSet<>();
		//Add all classes found in any row of the data as possible classes to extract.
		for( String rowData : data ){
			Document doc = Jsoup.parse(rowData);
			Elements els = doc.getAllElements();
			for( Element el : els ){
				String tagName = el.tagName();
				if( !tagName.equals("") && !tagName.toLowerCase().equals("body") && !tagName.toLowerCase().equals("html") && !tagName.toLowerCase().equals("head") && !tagName.toLowerCase().equals("#root") ){ //Don't add body or html as these can be added by the jsoup.parse and don't actually exist in the original input.
					selectors.add( new HtmlXpathSelector(tagName));
				}
			}
		}
		
		return selectors;
	}
	
	private Collection<? extends TransformationService> getHtmlSelectorsForAllIds(Att att) {
		ArrayList<String> data;
		try {
			data = att.getData();
		} catch (SQLException e) {
			return new ArrayList<>();
		}
		
		if( data.get(0) == null ){
			return new ArrayList<>();
		}
		
		LinkedHashSet<TransformationService> selectors = new LinkedHashSet<>();
		//Add all classes found in any row of the data as possible classes to extract.
		for( String rowData : data ){
			Document doc = Jsoup.parse(rowData);
			Elements els = doc.getAllElements();
			for( Element el : els ){
				if( el.id() != "" ){
					selectors.add( new HtmlXpathSelector("#"+el.id()));
				}
			}
		}
		
		return selectors;
	}
}
