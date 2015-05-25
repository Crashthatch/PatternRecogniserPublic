package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import processors.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class ChartTreeCreator {

	public static ArrayList<TransformationService> availableTransformers(){
		ArrayList<TransformationService> transformers = new ArrayList<>();

		//0-column atts:
		/*transformers.add( new constantCreator("0") );
		transformers.add( new constantCreator("1") );
		transformers.add( new constantCreator("2") );
		transformers.add( new constantCreator("3") );
		transformers.add( new constantCreator("4") );
		transformers.add( new constantCreator("10") );
		transformers.add( new constantCreator("X") );
		transformers.add( new constantCreator("3.14159") );
		transformers.add( new constantCreator("2.71828") );*/

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
		//transformers.add( new Strpos("a") );
		//transformers.add( new Strpos("b") );
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
		//transformers.add( new ListSequel() );
		//transformers.add( new ListNeighbours2() );
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
		//transformers.add( new normalise() );
		//transformers.add( new primeFactors() );
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

	// TODO: Change return-type.
	public static AttRelationshipGraph processAndGetBestTree(String inputfile)
			throws SQLException {

		AttRelationshipGraph inputGraph = new AttRelationshipGraph();
		Att constant = Main.initDb("pattern_activedata", inputGraph);

		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor(new getInputFromFile(inputfile), inputAtts, constant.getDataTable() );
		importer.doWork();

		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("longStringAtt");

		// Init Rapidminer.
		LogService logger = LogService.getGlobal();
		RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
		logger.setVerbosityLevel(LogService.ERROR);
		RapidMiner.init();

		// First round of processing.
		// ProcessorSelector processorSelector = new
		// BruteForceProcessorSelector();
		ProcessorSelector processorSelector = new AttInterestingnessProcessorSelector();
		List<Processor> possibleProcessors = processorSelector.getBestProcessors(inputGraph, availableTransformers() );
		for (Processor processor : possibleProcessors) {
			processor.doWork(false);
		}

		// Can any of the first-round attributes be predicted by a relationship?
		/*List<Relationship> firstRoundPossibleRelationships = RelationshipSelector
				.getBestRelationships(inputGraph);
		for (Relationship rel2 : firstRoundPossibleRelationships) {
			try{
				rel2.estimatePerformanceUsingXValidation();
				rel2.learn();
			}catch( InsufficientRowsException E )
			{
				continue;
			} catch (IncorrectInputRowsException e) {
				continue;
			}
		}*/

		// Second round of processing.
		// processorSelector = new BruteForceProcessorSelector();
		//processorSelector = new AttInterestingnessProcessorSelector();
		processorSelector = new BruteForceSingleAttInProcessorSelector();
		possibleProcessors = processorSelector.getBestProcessors(inputGraph, availableTransformers());
		//possibleProcessors = possibleProcessors.subList(0, 10);
		for (Processor processor : possibleProcessors) {
			processor.doWork(false);
		}


		/*List<Relationship> possibleRelationships = RelationshipSelector.getBestRelationships(inputGraph);

		System.out.println("Found " + possibleRelationships.size()
				+ " possible relationships to try to learn...");

		for (Relationship rel2 : possibleRelationships) {
			String debugMsg = "Estimating performance for " + rel2.getName()
					+ ", predicting " + rel2.getLabel().getName() + " from ";
			for (Att inputAtt : rel2.getInputAtts()) {
				debugMsg += inputAtt.getName() + ", ";
			}
			System.out.println(debugMsg);
			try{
				rel2.estimatePerformanceUsingXValidation();
				rel2.learn();
			}
			catch( InsufficientRowsException E ){
				//We need at least 3 training rows to estimate the performance. 
				continue;
			} catch (IncorrectInputRowsException e) {
				continue;
			}
		}
		*/
		System.out.println(inputGraph.getAllRelationships().size()+" relationships trained.");

		// Filter to good relationships.
		Collection<Relationship> goodRelationships = new HashSet<>();
		for (Relationship rel2 : inputGraph.getAllRelationships()) {
			if (rel2.getIncorrectPredictions() == 0) {
				goodRelationships.add(rel2);
			}
		}
		
		System.out.println(goodRelationships.size()+" relationships remain after removing inaccurate ones.");
		
		//If 2 relationships predict the same label, both with perfect accuracy, and one of them is capable of doing it based on "earlier" attributes, only keep that one.
		// eg. if rel1 predicts L from X, and rel2 predicts L from uppercase(X), we only need to keep rel1. Especially effective if X is "root"?
		// eg. if rel1 predicts L from X,Y and rel2 predicts L from X,Y,Z then only need to keep rel1.
		Collection<Relationship> filteredRelationships = new HashSet<>();
		for( Relationship rel1 : goodRelationships ){
			Collection<GraphVertex> rel1Ancestors = rel1.getAncestorAttsAndProcessors();
			boolean keepRelationship = true;
			for( Relationship predictor : goodRelationships ){ //Optimization: Could speed up this double loop by using a hash by label built before the outer loop if neccessary.
				if( goodRelationships.contains(predictor) && predictor != rel1){
					//If this predictor is better than this relationship (ie. only requires a subset of the ancestors), then we don't need to keep the relationship (we will keep predictor instead).
					// predictor must be strictly better (ie. not equal) to prevent 2 "joint-best" relationships from removing each other.
					Collection<GraphVertex> predictorAncestors = predictor.getAncestorAttsAndProcessors();
					if( rel1Ancestors.containsAll( predictorAncestors ) && !predictorAncestors.containsAll(rel1Ancestors) ){
						keepRelationship = false;
						break;
					}
				}
			}
			if( keepRelationship ){
				filteredRelationships.add(rel1);
			}
		}
		goodRelationships = null;
		
		System.out.println(filteredRelationships.size()+" relationships remain after only keeping the simplest relationships (earliest input atts) for a given label.");
		
		
		//Keep only the simplest relationship from a group with all the same inputs and label.
		// eg. if there's a linearRegressionLearner and a logarithmicRegressionLearner which are both perfect accuracy, and both predict L from X,Y and Z, then only keep the linearRegressionLearner.
		Collection<Relationship> filteredRelationships2 = new HashSet<>();
		for( Relationship rel1 : filteredRelationships ){
			boolean keepRelationship = true;
			for( Relationship predictor : filteredRelationships ){   //Optimization: Could speed up this double loop by using a hash by label built before the above outer loop if neccessary.
				if( filteredRelationships.contains(predictor) && predictor != rel1 && predictor.getLabel().equals( rel1.getLabel() )){
					//If this predictor is better than this relationship (ie. predicts the same thing from the same values, using a simpler model), then remove it and keep the better one instead. 
					if( predictor.getComplexityOrdering() < rel1.getComplexityOrdering()
							&& rel1.getLabel().equals( predictor.getLabel() )
							&& rel1.getInputAtts().equals( predictor.getInputAtts() ) ){
						keepRelationship = false;
						break;
					}
				}
			}
			if( keepRelationship ){
				filteredRelationships2.add(rel1);
			}
		}
		filteredRelationships = null;
		
		System.out.println(filteredRelationships2.size()+" relationships remain after only keeping the simplest type of model for each relationship for a given label.");

		//Create a graph that contains all of the vertices, but only the most useful relationships.
		AttRelationshipGraph filteredRelationshipsSubtree = inputGraph.getAttsAndProcessorsOnly();
		for( Relationship rel : filteredRelationships2 ){
			rel.addToGraph( filteredRelationshipsSubtree );
		}
		filteredRelationships2 = null;

		//filteredRelationshipsSubtree.createDot();
		filteredRelationshipsSubtree.getAttsAndProcessorsOnly().createDot();
		
		System.out.println("After filtering, the tree contains "+filteredRelationshipsSubtree.getAllAtts().size()+" atts, "+filteredRelationshipsSubtree.getAllProcessors().size()+" processors, and "+filteredRelationshipsSubtree.getAllRelationships().size()+" relationships." );
		
		return filteredRelationshipsSubtree;

	}

	public static double applyTreeAndTest(String testfile,
			AttRelationshipGraph tree) throws SQLException {

		Database.reconnect("pattern_testdata");

		AttRelationshipGraph testGraph = new AttRelationshipGraph();
		Att newConstant = Main.initDb("pattern_testdata", testGraph);

		/*
		 * ArrayList<Att> inputAtts = new ArrayList<Att>();
		 * inputAtts.add(constant); Processor importer = new Processor( new
		 * getInputFromFile(testfile), inputAtts, constant.getDataTable() );
		 * importer.doWork();
		 * 
		 * Att longStringAtt = importer.getOutputAtts().get(0);
		 * longStringAtt.setNotes("longStringAtt");
		 */

		// Starting from the root, create copies of the atts and processors in
		// turn to populate a new version of the same tree with the new data.
		HashMap<Att, Att> oldToNewMapping = new HashMap<>(); // Keeps track of
																// what Atts in
																// the old tree
																// correspond to
																// what atts in
																// the new tree.
		Att oldConstant = tree.getRootAtt();
		oldToNewMapping.put(oldConstant, newConstant);

		// Sort the processors into order that they were created so that they
		// aren't created out of order before their inputs have been created.
		// TODO: better way of doing this than sorting based on the attOrder of
		// their first output att (eg. tree traversal?) 
		List<Processor> allOldProcessors = new ArrayList<Processor>(
				tree.getAllFinishedProcessors());
		
		Collections.sort(allOldProcessors, new AttOrderComparator());

		for (Processor oldProcessor : allOldProcessors) {
			DataTable newRRT = null;
			// Find out what atts were in the old processor's root row table,
			// and translate them to the new atts so we can find out what the
			// corresponding new rootRowTable is.
			//TODO: More efficient method than looping over every att to see if it's in the oldToNewMapping yet (check ancestors of input atts first?)
			for (Att oldAttInRRT : oldProcessor.getRootRowTable().getAllAttsInTable()) {
				if (oldToNewMapping.containsKey(oldAttInRRT)) {
					newRRT = oldToNewMapping.get(oldAttInRRT).getDataTable();
					break;
				}
			}
			assert (newRRT != null); //Can happen if the training data differs substantially from the test data, so the rootrowatt can't be created.

			// Translate old inputs to this processor to their new equivalents.
			ArrayList<Att> newInputAtts = new ArrayList<Att>();
			for (Att oldInputAtt : oldProcessor.getInputAtts()) {
				newInputAtts.add(oldToNewMapping.get(oldInputAtt));
			}

			//Replace the old "getInputFromFile" transformer with one that loads the test set, not the training set.
			//Copy all other transformers (with their translated atts).
			Processor newProcessor;
			if(oldProcessor.getTransformer().getClass().getSimpleName().equals("getInputFromFile"))
			{
				newProcessor = new Processor( new getInputFromFile(testfile), newInputAtts, newRRT);				
			}
			else
			{
				newProcessor = new Processor( oldProcessor.getTransformer(), newInputAtts, newRRT);
			}
			newProcessor.doWork();

			// Add the old & new output atts to the mapping.
			List<Att> newOutputAtts = newProcessor.getOutputAtts();
			List<Att> oldOutputAtts = oldProcessor.getOutputAtts();

			if( oldOutputAtts.size() == newOutputAtts.size() )
			{
				for (int i = 0; i < oldOutputAtts.size(); i++) {
					oldToNewMapping.put(oldOutputAtts.get(i), newOutputAtts.get(i));
				}	
			}
			else
			{	//This processor returned a different number of atts during testing than during training, or returned 0 rows (so 0 atts).
				//Any further processing that depend on these output atts can no longer happen, and 
				//What happens if we had some rows that matched some filter in the Training set, but none match in the test set?
				//Those relationships always count as 'violated'? Or just can't be computed and so don't affect the score returned for the test set?
			}
		}

		//AttRelationshipGraph.createDotSingleInstance();

		// We have the processors and atts being applied to the new data. Now
		// recreate the sucessful relationships from the tree and test to see if
		// they predict correctly for the testset.
		// Don't actually make the predictions availible to outside, just use
		// them to evaluate the passed in data and return a %age that it's
		// similar to the training data.
		Collection<Relationship> allOldRelationships = tree
				.getAllRelationships();
		double percentageCorrect = 2;

		//Pair each relationship with its' score on this testset.
		class RelationshipScorePair implements Comparable<RelationshipScorePair>{
			public Relationship relationship;
			public double percentageCorrect;
			
			public RelationshipScorePair(Relationship relationship, Double percentageCorrect){
				this.relationship = relationship;
				this.percentageCorrect = percentageCorrect;
			}
			
			public double getTestSetLikelyhood(){
				
				double uncertainty = 1.0/relationship.getTrainingExamples();
				//Cap the max and min likelyhood of the test set.
				//return Math.max( Math.min( percentageCorrect, 1.0-uncertainty ), uncertainty );
				
				//If the model found something (anything!) wrong with any example in the test set, treat it as a "NO" vote with the confidence we have in the model (ie. models that vote "no" that were trained on large datasets and have never been wrong before result in a low probability of the test set being the same as the training data).
				//TODO: Confidences: Take into account the number of rows the model failed on, and how far out the predictions were. Probably do this quite a lot later, along with keeping all models rather than those that behaved perfectly on the training set.
				if( percentageCorrect == 1.0)
					return 1.0; //Relationship found nothing wrong. All test examples matched the trained model's predictions.
				else
					return uncertainty;
			}
			
			public int compareTo(RelationshipScorePair r2){
				if( this.getTestSetLikelyhood() > r2.getTestSetLikelyhood() )
					return 1;
				else if( this.getTestSetLikelyhood() < r2.getTestSetLikelyhood() )
					return -1;
				else
					return 0;
			}
		}	
		
		ArrayList<RelationshipScorePair> rankedRelationships = new ArrayList<>();
		
		for (Relationship oldRelationship : allOldRelationships) {
			try {
				rankedRelationships.add( new RelationshipScorePair(oldRelationship, oldRelationship.makePredictionsAndGetPercentageCorrect(oldToNewMapping) ) );
			} catch (AttributeNotInTestSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Collections.sort(rankedRelationships);
		
		//TODO: Print out the top 10 ranked relationships (ie. the best reasons why this testset was rejected).
		JSONArray relationshipsJson = new JSONArray();

		for( int i=0; i<20; i++ )
		{
			double thisRelLikelyhood = rankedRelationships.get(i).getTestSetLikelyhood();
			Relationship relationship = rankedRelationships.get(i).relationship;
			//System.out.println("Found a better reason why "+testfile+" is not a good fit with the training data ("+thisRelPercentageCorrect+"):");
			//System.out.println(relationship);
			try {
				JSONObject relationshipJson = relationship.createRelationshipJson(tree, oldToNewMapping);
				relationshipJson.put( "testPercentageCorrect", thisRelLikelyhood );
				relationshipsJson.add( relationshipJson );
			} catch (AttributeNotInTestSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncorrectInputRowsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			FileOutputStream fos2 = new FileOutputStream("js/app/test-data.json");
			OutputStreamWriter jsonoutfile = new OutputStreamWriter(fos2, "UTF-8");

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(relationshipsJson.toJSONString());
			String prettyJsonString = gson.toJson(je);
			
			jsonoutfile.write(prettyJsonString);
			jsonoutfile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch( Exception e){
			e.printStackTrace();
		}
		

		return rankedRelationships.get(0).getTestSetLikelyhood();

	}
}
