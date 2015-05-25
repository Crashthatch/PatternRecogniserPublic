package database;

import processors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ProcessorSelector {

	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph )
	{
		return getBestProcessors(attGraph, getAllTransformers() );
	}

	//Ideally this would be static, but can't have abstract static, and creating an instance of *ProcessorSelector isn't too bad, might allow parameters to be tuned etc.
	//However, the idea is that processors should be selected based purely on the graph passed in (ie. the current situation of atts) rather than remembering and acting differently based on previous "processing rounds".
	public abstract List<Processor> getBestProcessors( AttRelationshipGraph attGraph, Collection<TransformationService> transformers );
	
	
	public static ArrayList<TransformationService> getAllSingleAttInTransformers(){
		//Filter list of transformers to those with only 1 input att.
		ArrayList<TransformationService> singleAttTransformers = new ArrayList<>();
		for( TransformationService transformer : getAllTransformers() ){
			if( transformer.getColsIn() == 1 ){
				singleAttTransformers.add(transformer);
			}
		}
		return singleAttTransformers;
	}
	
	public static ArrayList<TransformationService> getAllTransformers()
	{
		ArrayList<TransformationService> transformers = new ArrayList<>();
		
		//0-column atts:
		transformers.add( new constantCreator("0") );
		transformers.add( new constantCreator("1") );
		transformers.add( new constantCreator("2") );
		transformers.add( new constantCreator("3") );
		transformers.add( new constantCreator("4") );
		transformers.add( new constantCreator("10") );
		transformers.add( new constantCreator("X") );
		transformers.add( new constantCreator("3.14159") );
		transformers.add( new constantCreator("2.71828") );
		
		//1-col in, 1-col out, 1-row-in, 1-row-out:
		transformers.add( new isPrime() );
		transformers.add( new notProcessor() );
		transformers.add( new stringLength() );
		transformers.add( new capitalise() );
        transformers.add( new firstCharacter() );
		transformers.add( new Digits() );
		transformers.add( new AlphabetLetters() );
        transformers.add( new SpecialCharacters() );
		transformers.add( new stripPunctuation() );
		transformers.add( new stripDoubleQuotes() );
		transformers.add( new UnwrapDoubleQuotes() );
		transformers.add( new inWordList8() );
        transformers.add( new FilterFalsey() );
		//transformers.add( new isANoun() );
		//transformers.add( new isAVerb() );
		transformers.add( new Strpos("a") );
		transformers.add( new Strpos("b") );
		transformers.add( new Strpos("\r") );
		transformers.add( new Strpos("\n") );
		transformers.add( new htmlText() );
        transformers.add( new UnwrapOuterBrackets() );
        transformers.add( new CurrencySymbol() );
        transformers.add( new CurrencySymbolReversed() );
        transformers.add( new escapeSpaces() );
        transformers.add( new escapeSpacesAndSingleQuotes() );
		
		//1-col many-rows in, 1x1 out.
		transformers.add( new total() ); 
		transformers.add( new isConstant() );
		transformers.add( new md5Values() );
        transformers.add( new NumRows() );
		
		//1-column 1-row in, n columns, 1-row out:
		transformers.add( new HttpGet() );
		
		//1-column, many rows in, n-columns, many rows out:
		transformers.add( new ListSequel() );
		transformers.add( new ListNeighbours2() );
		transformers.add( new htmlToXhtml() );
		transformers.add( new xmlUnwrap() );

        //1-column, n rows in, 1-column n-rows out (eg. annotate existing tables)
        transformers.add( new rownum() );
		
		//1-row, 1-column in; 1-row, multiple-col out:
		//transformers.add( new SplitOnStringToColumns(",") );
		//transformers.add( new SplitOnStringToColumns(";") );
		//transformers.add( new SplitOnStringToColumns(" ") );
		//transformers.add( new SplitOnStringToColumns(":") );
		//transformers.add( new SplitOnStringToColumns("'") );
		//transformers.add( new SplitCharactersToColumns() );
		transformers.add( new JsonDecodeMapManyColsOut() );
		transformers.add( new JsonDecodeMapManyColsAndKeysOutSingleRow() );
		transformers.add( new xmlOutermostAttributeValuesToCols() );
		//transformers.add( new DateToDateParts() );
        //transformers.add( new DateToDatePartsSimple() );

		
		//1-column in multi-row out:
		transformers.add( new jsonDecodeList() );
		transformers.add( new jsonDecodeMap() );
		transformers.add( new normalise() );
		transformers.add( new primeFactors() );
		transformers.add( new SplitOnStringToRows("\r\n") );
		transformers.add( new SplitOnStringToRows("\n") );
		transformers.add( new SplitOnStringToRows(",") );
		transformers.add( new SplitOnStringToRows(";") );
		transformers.add( new SplitOnStringToRows(" ") );
		transformers.add( new SplitOnStringToRows("\t") );
        transformers.add( new SplitOnStringToRows("\n\n") );
		transformers.add( new SplitOnStringToRows("\r\n\r\n") );
		transformers.add( new SplitOnStringToRows("'") );
		transformers.add( new SplitCharactersToRows() );
		
		
		//1-col in, multi-row & multi-column out ("Table out"):
		//transformers.add( new SplitOnRegexesToTable("\n",",") );
		//transformers.add( new SplitOnRegexesToTable("\r\n",",") );
		transformers.add( new ReadCSVToTable() );
		transformers.add( new ReadCSVToTableAlwaysQuoted() );
		transformers.add( new ReadCSVToTableQuoteOnlyIfNeeded() );
		transformers.add( new ReadTSVToTable() );
		//transformers.add( new rownumRepeat(2));
		//transformers.add( new rownumRepeat(4));
		transformers.add( new JsonDecodeMapManyColsAndKeysOut() );
		transformers.add( new xmlManyRootEltsToRows() );
		transformers.add( new xmlOutermostAttributes() );
		
		//2-column-in:
		transformers.add( new divide() );
		transformers.add( new greaterThan() );
		transformers.add( new andProcessor() );
		transformers.add( new orProcessor() );
		transformers.add( new plus() );
		transformers.add( new modulo());
		transformers.add( new appearsInList() );
		transformers.add( new concatenate() );
		
		//many cols in
		/*
		transformers.add( new CountIdenticalRows() );
		transformers.add( new filterWhereTrue() );
		transformers.add( new removeDuplicateRows() );
		transformers.add( new SumIdenticalRows() );*/
		
		return transformers;
	}
}
