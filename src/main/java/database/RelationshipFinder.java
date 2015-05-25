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
import processors.getInputFromFile;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class RelationshipFinder {

	// TODO: Change return-type.
	public static AttRelationshipGraph processAndGetBestTree(String inputfile)
			throws SQLException {

		// This method should do a few rounds of processing on the inputFile,
		// train some relationships and pick a "good" (always correct to begin
		// with) subset of the relationships and return the tree that ends at
		// those relationships (other unfruitful atts can be pruned away).
		// Then another method "applyTreeAndTest(newfile, tree)" should take
		// some a new "testInputFile" and the tree and execute the processors
		// within the tree & test to see if the relationships hold for the
		// attributes created for the test file.
		// If there are relationships that were always true for the training
		// file, but fail for a test file, then it's a sign that that test-file
		// is NOT similar to the training-file.
		AttRelationshipGraph trainingGraph = new AttRelationshipGraph();
		Att constant = Main.initDb("pattern_activedata", trainingGraph);

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
		List<Processor> possibleProcessors = processorSelector
				.getBestProcessors(trainingGraph);
		for (Processor processor : possibleProcessors) {
			processor.doWork();
		}

		// Can any of the first-round attributes be predicted by a relationship?
		List<Relationship> firstRoundPossibleRelationships = RelationshipSelector
				.getBestRelationships(trainingGraph);
		for (Relationship rel2 : firstRoundPossibleRelationships) {
			/*String debugMsg = "Estimating performance for " + rel2.getName()
					+ ", predicting " + rel2.getLabel().getName() + " from ";
			for (Att inputAtt : rel2.getInputAtts()) {
				debugMsg += inputAtt.getName() + ", ";
			}
			System.out.println(debugMsg);*/
			try{
				rel2.estimatePerformanceUsingXValidation();
				rel2.learn();
			}catch( InsufficientRowsException E )
			{
				continue;
			} catch (IncorrectInputRowsException e) {
				continue;
			}
		}

		Collection<Att> atts = trainingGraph.getAllAtts();
		Collection<Processor> finishedProcessors = trainingGraph.getAllFinishedProcessors();
		Collection<Processor> processors = trainingGraph.getAllProcessors();
//		AttRelationshipGraph.getGraph().getAttsAndProcessorsOnly().createDot();
		// AttRelationshipGraph.createDotSingleInstance();

		// Second round of processing.
		// processorSelector = new BruteForceProcessorSelector();
		//processorSelector = new AttInterestingnessProcessorSelector();
		processorSelector = new BruteForceSingleAttInProcessorSelector();
		possibleProcessors = processorSelector.getBestProcessors(trainingGraph);
		//possibleProcessors = possibleProcessors.subList(0, 10);
		for (Processor processor : possibleProcessors) {
			processor.doWork();
		}


		List<Relationship> possibleRelationships = RelationshipSelector.getBestRelationships(trainingGraph);

		System.out.println("Found " + possibleRelationships.size()
				+ " possible relationships to try to learn...");

		for (Relationship rel2 : possibleRelationships) {
			/*String debugMsg = "Estimating performance for " + rel2.getName()
					+ ", predicting " + rel2.getLabel().getName() + " from ";
			for (Att inputAtt : rel2.getInputAtts()) {
				debugMsg += inputAtt.getName() + ", ";
			}
			System.out.println(debugMsg);*/
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
		
		System.out.println(trainingGraph.getAllRelationships().size()+" relationships trained.");

		// Filter to good relationships.
		Collection<Relationship> goodRelationships = new HashSet<>();
		for (Relationship rel2 : trainingGraph.getAllRelationships()) {
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
		
		HashSet<GraphVertex> filteredVertices = new HashSet<>();
		for( Relationship rel : filteredRelationships2 ){
			filteredVertices.add(rel);
			filteredVertices.add(rel.getLabel()); //We need the labels to be included (and the path to them from the root).	
		}
		filteredRelationships2 = null;
		
		
		AttRelationshipGraph filteredRelationshipsSubtree = trainingGraph.getSubGraph(filteredVertices);
		
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
