package database;

import com.google.common.collect.Sets;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.featureProcessorSelector.FeatureProcessorSelector;
import database.featureProcessorSelector.attsToProcessRanker.AttInterestingnessRanker;
import database.featureProcessorSelector.transformerSelector.GuessedTypeTransformerSelector;
import database.featureRelationshipSelector.RelationshipSelectorCleverInputOutput;
import database.worker.StatusReporter;
import processors.getInputFromFile;
import processors.modelApplier;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RelationshipFinderInputOutput {


    public static AttRelationshipGraph processAndGetBestTree(String inputfile, String outputfile, StatusReporter status)
            throws SQLException {
        //Pass in a runId.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String runId = inputfile+outputfile+dateFormat.format(date);
        //String runId = null;

        return processAndGetBestTree(inputfile, outputfile, status, runId);
    }

    public static AttRelationshipGraph processAndGetBestTree(String inputfile, String outputfile)
            throws SQLException {
        //Pass in a runId.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String runId = inputfile+outputfile+dateFormat.format(date);
        //String runId = null;

        return processAndGetBestTree(inputfile, outputfile, null, runId);
    }

	public static AttRelationshipGraph processAndGetBestTree(String inputfile, String outputfile, StatusReporter status, String runId)
			throws SQLException {
		
		//For debugging, empty the debug/output graphs directory. Could add a flag for "createTrees" or "debugMode" or something?
		File folder = new File("runOutput/latest");
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
                if( !f.getName().startsWith(".")) {
                    f.delete();
                }
	        }
	    }

        //Set up recording for later generating a metamodel.
        if( runId != null ) {
            MetaModelDatabase.connect("pattern_metamodel");
        }

        //Create input graph and do import of initial data.
		AttRelationshipGraph inputGraph = new AttRelationshipGraph();
		Att constant = Main.initDb("pattern_inputdata", inputGraph);

		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor(new getInputFromFile(inputfile), inputAtts, constant.getDataTable() );
		importer.doWork();

		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("inputLongStringAtt");
		
		
		//Create output graph in same database and create output att.
		AttRelationshipGraph outputGraph = new AttRelationshipGraph();
		//Att outConstant = Main.initDb("pattern_outputdata", outputGraph);
		//Create the root "const" att which contains no data.
		DataTable rootTable = new DataTable(); 
		Att outConstant = new Att(rootTable, outputGraph);
		HashMap<Att, String> rootRowValues = new HashMap<>();
		rootRowValues.put(outConstant, "root");
		rootTable.insert(0, rootRowValues);
		rootTable.save();
		

		inputAtts = new ArrayList<Att>();
		inputAtts.add(outConstant);
		importer = new Processor(new getInputFromFile(outputfile), inputAtts, outConstant.getDataTable() );
		importer.doWork();

		longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("outputLongStringAtt");
		
		
		//Create combined input/output graph that contains all of the input and output vertices, so relationships can connect the two.
		HashSet<Relationship> attemptedToLearnRelationships = new HashSet<>();
		HashSet<Processor> attemptedOutputProcessors = new HashSet<>();
		HashSet<Processor> attemptedInputProcessors = new HashSet<>();

		// Init Rapidminer.
        if( !RapidMiner.isInitialized() ) {
            LogService logger = LogService.getGlobal();
            RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
            logger.setVerbosityLevel(LogService.ERROR);
            RapidMiner.init();
        }

		// First round of processing.
		// ProcessorSelector processorSelector = new
		// BruteForceProcessorSelector();
		int processingRound = 0;
		List<Relationship> allGoodRelationships = new ArrayList<>();
		int lastRoundAttCount = 0; //Stops us getting into an infinite loop trying processors which produce nothing (or get removed as duplicates).
		AttRelationshipGraph singleForwardTree = null;
		//while( inputGraph.getNonDuplicateAtts().size() < 500 && inputGraph.getNonDuplicateAtts().size() > lastRoundAttCount ){
		while( processingRound < 4 && (singleForwardTree == null || singleForwardTree.getFinalOutputAtts().size() == 0 ) ){
            processingRound++;
			lastRoundAttCount = inputGraph.getNonDuplicateAtts().size();

            if( status != null ){
                status.reportStatus("Processing Round "+processingRound);
            }
			
			// Create a version of the outputTree that has predictable atts removed. There's no point in further processing atts that can already be predicted.
			AttRelationshipGraph unpredictableOutputGraph = outputGraph.getUnpredictableSubgraph( allGoodRelationships, true);
            System.out.println("Created Unpredictable-Output-Graph. It contains "+unpredictableOutputGraph.getAllAtts().size()+" atts.");
            unpredictableOutputGraph.createDot("unpredictable");

            //Do a round of processing on the input tree.
			//InputOutputProcessorSelector inputProcessorSelector = new AttInterestingnessSingleAttInProcessorSelector( ProcessorSelector.getAllSingleAttInTransformers(), runId, processingRound);
			//InputOutputProcessorSelector inputProcessorSelector = new FeatureProcessorSelector(new BigMLAttRanker(), new GuessedTypeTransformerSelector( ProcessorSelector.getAllSingleAttInTransformers() ), runId, processingRound);
			InputOutputProcessorSelector inputProcessorSelector = new FeatureProcessorSelector(new AttInterestingnessRanker(), new GuessedTypeTransformerSelector( ProcessorSelector.getAllSingleAttInTransformers() ), runId, processingRound);
			List<Processor> possibleInputProcessors = inputProcessorSelector.getBestProcessors(inputGraph, unpredictableOutputGraph, allGoodRelationships);
			for (Processor processor : possibleInputProcessors) {
				if( !attemptedInputProcessors.contains(processor) ){
					attemptedInputProcessors.add(processor);
					processor.doWork(false);
					StringBuilder outAttNames = new StringBuilder();
					StringBuilder inAttNames= new StringBuilder();
					for( Att inAtt : processor.getInputAtts() ){
						inAttNames.append(inAtt.getDbColumnNameNoQuotes() + " ");
					}
					for( Att outAtt : processor.getOutputAtts() ){
						outAttNames.append(outAtt.getDbColumnNameNoQuotes() + " ");
					}
					System.out.println( processor.getName()+" did work on atts: "+inAttNames.toString()+" to produce atts: "+ outAttNames.toString() );
				}
				else{
					System.out.println("Skipping input processor because we already did it in a previous round."+processor);
				}
			}
            System.out.println("Finding duplicate atts...");
            //inputGraph.flagDuplicateAtts();
            inputGraph.createDot("input");
            System.out.println("After "+processingRound+" rounds of processing, Input tree is "+inputGraph );
            //System.out.println("Without duplicates, the input tree is:"+inputGraph );


            if( processingRound < 4 ){
				// Do a round of processing on the outputTree.
				
				BruteForceReversibleProcessorSelector outputProcessorSelector = new BruteForceReversibleProcessorSelector();
				//processorSelector = new AttInterestingnessProcessorSelector();
				List<Processor> possibleOutputProcessors = outputProcessorSelector.getBestProcessors(unpredictableOutputGraph);
				for (Processor processor : possibleOutputProcessors) {
					if( !attemptedOutputProcessors.contains(processor) ){
						attemptedOutputProcessors.add(processor);
						processor.doWork(false);
					}
					else{
						//System.out.println("Skipping processor because it has already been tried:"+processor);
					}
				}
				
				System.out.println("After "+processingRound+" rounds of processing, Output tree: "+outputGraph);
                //outputGraph.flagDuplicateAtts();
				//outputGraph.removeDuplicateAtts(); //Don't need to do this any more- the processor / relationship Selectors ignore flagged duplicates.
				System.out.println("After removing duplicate atts, Output tree: "+outputGraph);
				outputGraph.createDot("output");
			}
            else{
                System.out.println("Did no further processing rounds on the output tree: "+outputGraph );
            }


            //allGoodRelationships = learnBestRelationships(inputGraph, outputGraph.getUnpredictableSubgraph( allGoodRelationships , true), allGoodRelationships, attemptedToLearnRelationships);
            //Don't do the above because it might be that we need to learn a relationship not yet found (eg. using a new ).
            allGoodRelationships = learnBestRelationships(inputGraph, outputGraph, allGoodRelationships, attemptedToLearnRelationships, runId, processingRound);
			
			System.out.println("After adding the new relationships and removing duplicates, there were "+allGoodRelationships.size()+" relationships.");
			
			for( Relationship rel : allGoodRelationships ){
				String out = "";
				out += rel.getName();
				out += " Label:" +rel.getLabel().getDbColumnNameNoQuotes()+" "+rel.getLabel().getName();
				out += " RRA:" +rel.getRootRowAtt().getDbColumnNameNoQuotes()+" "+rel.getRootRowAtt().getName();
				out += " FeatureAtts:";
				for( Att inputAtt : rel.getInputAtts() )
				{
					out += inputAtt.getDbColumnName()+" "+inputAtt.getName()+", ";
				}
				System.out.println(out);
			}
			
			if( outputGraph.getUnpredictableSubgraph( allGoodRelationships , true).getVertices().size() == 1){
				//We have enough predictors for a potential path to the output root!
                System.out.println("Unpredictable graph is empty! Trying to create a singleForwardTree...");
                DataTableRelationshipGraph.createDataTableGraph(inputGraph).createDot("datatables");
                DataTableRelationshipGraph.createDataTableGraph(outputGraph).createDot("outputDatatables");
				singleForwardTree = createSingleForwardTree(inputGraph, outputGraph, allGoodRelationships );
			}
		}

        if( singleForwardTree == null ){
            System.out.println("Unlikely that we can create a forward tree, but trying anyway...");
            DataTableRelationshipGraph.createDataTableGraph(inputGraph).createDot("datatables");
            DataTableRelationshipGraph.createDataTableGraph(outputGraph).createDot("outputDatatables");
            singleForwardTree = createSingleForwardTree(inputGraph, outputGraph, allGoodRelationships );
        }

        //Create a tree for each finalOutputAtt so we can see how each prediction is made.
        int i = 0;
        for( Att finalOutputAtt : singleForwardTree.getFinalOutputAtts() ){
            i++;
            singleForwardTree.getSubGraph(finalOutputAtt).createDot("singleForwardTree-"+i);
        }
		
		//Update the metamodel DB to show which atts were used in the final model.
		if( runId != null && singleForwardTree.getFinalOutputAtts().size() > 0){
			try{
				if( singleForwardTree.getFinalOutputAtts().size() > 0){
					String SQL = "UPDATE attribute SET runFoundPredictor = 1 WHERE runId = '"+runId+"'";
					MetaModelDatabase.doWriteQuery(SQL);
                    SQL = "UPDATE relationship SET runFoundPredictor = 1 WHERE runId = '"+runId+"'";
                    MetaModelDatabase.doWriteQuery(SQL);
				}
				
				String SQL = "UPDATE attribute SET usedToMakeFinalPrediction = 1 WHERE runId = '"+runId+"' AND attId IN (";
                for( Att usefulAtt : singleForwardTree.getAllAtts() ){   //TODO: Should this be only non duplicate atts?
                    SQL += usefulAtt.getAttOrder()+",";
                }
                SQL = SQL.substring(0, SQL.length()-1);
                SQL += ");";
                MetaModelDatabase.doWriteQuery(SQL);


                SQL = "UPDATE relationship SET usedToMakeFinalPrediction = 1 WHERE runId = '"+runId+"' AND relId IN (";
                for( Relationship usefulRel : singleForwardTree.getAllRelationshipsFromModelAppliers() ){
                    SQL += usefulRel.hashCode() + ",";
                }
                SQL = SQL.substring(0, SQL.length()-1);
                SQL += ");";
                MetaModelDatabase.doWriteQuery(SQL);
			}
			catch( SQLException e ){
				System.out.println("Error while trying to update ");
				e.printStackTrace();
			}
		}
		
		//filteredRelationshipsSubtree.createDot();
		//filteredOutputSubtree.getAttsAndProcessorsOnly().createDot();


        Database.disconnect();
        if( runId != null) {
            MetaModelDatabase.disconnect();
        }
				
		return singleForwardTree;
	}

	public static List<Relationship> learnBestRelationships(
            AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph,
            List<Relationship> existingGoodRelationships,
            HashSet<Relationship> attemptedToLearnRelationships,
            String runId, int processingRound)
			throws SQLException {
		//Find relationships between attributes in the inputGraph and the outputGraph.
        InputOutputRelationshipSelector relationshipSelector = new RelationshipSelectorCleverInputOutput();
        //Use the FULL output graph (not the unpredictable version) because we want to learn ALL possible ways of predicting output atts, not just the first one as we might need to be able to predict it from a particular table so we get the correct number of rows when used in a processor, joined with other input atts.
        List<Relationship> possibleRelationships = relationshipSelector.getBestRelationships(inputGraph, outputGraph, existingGoodRelationships, attemptedToLearnRelationships, runId, processingRound);

		System.out.println("Found " + possibleRelationships.size() + " possible relationships to try to learn...");
		System.out.println();
		
		//Filter to only those untried.
		possibleRelationships.removeAll(attemptedToLearnRelationships);
		System.out.println(possibleRelationships.size()+" new relationships to learn that have not been tried before.");

        //Keep track of some stats on how many of the relationships trained this round completed successfully, how many had no successes etc.
        int trainedThisRound = 0;
        int skippedFoundSimplerRelationships = 0;
        int completelyFailed = 0;
        int partiallyFailed = 0;
        int partiallyFailedSomeCorrectSomeIncorrect = 0;
        int partiallyFailedOnesGuessedAllCorrect = 0;
        int partiallyFailedOnesGuessedAllIncorrect = 0;
        int noneFailed = 0;
        int noneFailedSomeCorrectSomeIncorrect = 0;
        int noneFailedOnesGuessedAllCorrect = 0;
        int noneFailedOnesGuessedAllIncorrect = 0;
		ArrayList<Relationship> successfullyTrainedGoodRelationships = new ArrayList<>();
        possibleRelationshipLoop:
		for (Relationship rel2 : possibleRelationships) {
			//System.out.println( "Estimating performance for " + rel2 );
			assert( !attemptedToLearnRelationships.contains(rel2) );

            //Check that we didn't already learn a better (eg. simpler) relationship already this round. (Might need removing when we parallelise because all relationships will be learned in parallel).
            //Only do this if we're not saving the results. If we are going to build a model off this, we should evaluate all possible relationships to avoid tagging models that would have produced good predictions as "bad" (because they were skipped).
            if( runId == null ) {
                for (Relationship possiblyBetterRel : successfullyTrainedGoodRelationships) {
                    if (possiblyBetterRel.isLessComplex(rel2) || possiblyBetterRel.requiresLess(rel2)) {
                        skippedFoundSimplerRelationships++;
                        continue possibleRelationshipLoop;
                    }
                }
            }
			
			try{
				rel2.estimatePerformanceUsingXValidation();

                if(rel2.getIncorrectPredictions() == 0 && rel2.getCorrectPredictions() == rel2.getLabel().getNotNullRowsInTable()) {
                    successfullyTrainedGoodRelationships.add(rel2);
                    //Update the metamodel to record that this was a successfully trained relationship.
                    //Could be extended to record the # of correct / incorrect predictions etc to train a better model.
                    if(runId != null){
                        String SQL = "UPDATE relationship SET relationshipMadeAllCorrectPredictions = 1 WHERE runId = '"+runId+"' AND relId = '"+rel2.hashCode()+"';";
                        MetaModelDatabase.doWriteQuery(SQL);
                    }
                }

                //The below if statements is all keeping track of the stats for output.
                int numPredictions = rel2.getIncorrectPredictions() + rel2.getCorrectPredictions();
                trainedThisRound++;
                if( numPredictions == 0){
                    completelyFailed++;
                }
                else if( numPredictions > 0 && numPredictions < rel2.getLabel().getNotNullRowsInTable() ){
                    partiallyFailed++;
                    if( rel2.getIncorrectPredictions() > 0 && rel2.getCorrectPredictions() > 0 ){
                        partiallyFailedSomeCorrectSomeIncorrect++;
                    }
                    if( rel2.getIncorrectPredictions() == 0 ){
                        partiallyFailedOnesGuessedAllCorrect++;
                    }
                    if( rel2.getCorrectPredictions() == 0 ){
                        partiallyFailedOnesGuessedAllIncorrect++;
                    }
                }
                else if( numPredictions > 0 && numPredictions == rel2.getLabel().getNotNullRowsInTable() ){
                    noneFailed++;
                    if( rel2.getIncorrectPredictions() > 0 && rel2.getCorrectPredictions() > 0 ){
                        noneFailedSomeCorrectSomeIncorrect++;
                    }
                    if( rel2.getIncorrectPredictions() == 0 ){
                        noneFailedOnesGuessedAllCorrect++;
                    }
                    if( rel2.getCorrectPredictions() == 0 ){
                        noneFailedOnesGuessedAllIncorrect++;
                    }
                }
                else{
                    assert(false);
                    //Should never get here. 3 above cases should handle all.
                }
			}
			catch( InsufficientRowsException E ){
				//We need at least 3 training rows to estimate the performance. 
				System.out.println("Need at least 3 rows.");
				continue;
			} catch (IncorrectInputRowsException e) {
				//TODO: Could be eliminated by better estimation of joined-size?
				//System.out.println(e.getMessage());
				continue;
			}
		}
		attemptedToLearnRelationships.addAll(possibleRelationships); //Rememeber what we tried so we don't try the same things on the next loop pass.
		
		System.out.println("Relationships trained this round: "+trainedThisRound);
        System.out.println("Skipped learning because we found a simpler relationship already this round:"+skippedFoundSimplerRelationships);
        System.out.println("  Completely failed (no predictions made):"+completelyFailed);
        System.out.println("  Partially failed (less predictions made than num of label rows):"+partiallyFailed);
        System.out.println("    Of which all predictions were correct:"+partiallyFailedOnesGuessedAllCorrect);
        System.out.println("    Of which all predictions were incorrect:"+partiallyFailedOnesGuessedAllIncorrect);
        System.out.println("    Of which some were correct, some incorrect:"+partiallyFailedSomeCorrectSomeIncorrect);
        System.out.println("  None failed (made a prediction for every label row):"+noneFailed);
        System.out.println("    Of which all predictions were correct:"+noneFailedOnesGuessedAllCorrect);
        System.out.println("    Of which all predictions were incorrect:"+noneFailedOnesGuessedAllIncorrect);
        System.out.println("    Of which some were correct, some incorrect:"+noneFailedSomeCorrectSomeIncorrect);
        assert( noneFailedOnesGuessedAllCorrect == successfullyTrainedGoodRelationships.size() );


        // Add the relationships to those we already found in previous rounds and re-filter to the best subset.
        successfullyTrainedGoodRelationships.addAll(existingGoodRelationships);
        List<Relationship> allGoodRelationships = filterToMinimalGoodRelationships(successfullyTrainedGoodRelationships);

        //Do the learning for the minimal set of good relationships if we didn't already.
        for( Relationship rel : allGoodRelationships){
            if( !rel.learned() ){
                rel.learn();
            }
        }

        return allGoodRelationships;
	}

	public static AttRelationshipGraph createSingleForwardTree(
			AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph,
			Collection<Relationship> goodRelationships) throws SQLException {
		//Create a single new tree which replaces the relationships with "modelAppliers", and inverts the output tree, selecting new rootrowatts from the input tree (as their selected rras will be atts "futher down" the tree after it's inverted) etc.
		//TODO: Is guessing based on number of rows in the training tables really the best we can do for deciding what rootRowTable to use for the modelApplier?
		// Actually, we want a rra where 1 row joined to all the input atts results in 1 row, and the rra # rows = # rows in label att. Is this only ancestors of all inputAtts? What then do we do for relationships with no input atts?


        System.out.println("Creating ModelAppliers from "+goodRelationships.size()+" relationships...");
        HashMap<Att, Collection<Att>> oldToNewMapping = new LinkedHashMap<>();
		for( Relationship rel : goodRelationships ){
			//Find out how many rows of the label were created in the training data.
			//int labelRowsInTrainingOut = rel.getLabel().getNotNullRowsInTable();

			DataTable RRT = rel.getRootRowAtt().getDataTable(); //The actual att doesn't matter as not sorting will just result in the output-label atts being in a different order (but still corresponding to the correct rows of the input atts).
			List<Att> relApplierInputAtts = new ArrayList<>();
			relApplierInputAtts.add(rel.getRootRowAtt());
			relApplierInputAtts.addAll(rel.getInputAtts());
			
			Processor relationshipApplier = new Processor( new modelApplier(rel), relApplierInputAtts, RRT );
			relationshipApplier.doWork(false);
			
			assert( relationshipApplier.getOutputAtts().get(0).getNotNullRowsInTable() == rel.getLabel().getNotNullRowsInTable());
			assert( inputGraph.containsVertex(relationshipApplier)); //If this fails, then the "graph" property of the inputAtts was not set to the same graph as inputGraph. Probably the passed in inputGraph was produced by getSubgraph, which actually returns a new graph. FIXME by removing the "graph" property of an att. AttRelGraphs then become views on the network of vertices, and atts are no longer tied to a particular graph.
			
			Collection<Att> mapping = oldToNewMapping.get( rel.getLabel() );
			if( mapping == null ){
				mapping = new LinkedHashSet<>();
				oldToNewMapping.put( rel.getLabel(), mapping );
				mapping.add( relationshipApplier.getOutputAtts().get(0) );
			}
			else{
				//There's already at least one mapping for this old att (ie. another relationship already predicts it).
				//Only add the new output of this new relationshipApplier if its predictions differ from those that already exist.
				//This reduces the number of duplicate attributes that get created, but if 2 different relationships would have made different predictions on test-data, we will only get one of those predictions (possibly the wrong one). 
				/*DOES NOT WORK- causes errors that happen on some runs but not others. Presumably depending on the order they get created and which one gets kept.
				String hashedPredictions = relationshipApplier.getOutputAtts().get(0).getColumnValuesHash();
				boolean foundIdenticalAlreadyExistingAtt = false;
				for( Att att : mapping ){
					if( att.getColumnValuesHash().equals(hashedPredictions) )
					{
						foundIdenticalAlreadyExistingAtt = true;
						break;
					}
				}
				if( !foundIdenticalAlreadyExistingAtt ){
					mapping.add( relationshipApplier.getOutputAtts().get(0) );
				}
				*/
				mapping.add( relationshipApplier.getOutputAtts().get(0) );
			}

            //Also add a mapping from any duplicates of the old label to the predicted-label of the new relationship.
            // ie. if Rel predicts L, then it also predicts all duplicates of L.
            // However, ignore duplicates that were produced by "not doing anything" (ie. where it's a duplicate of its parent) because there's no need to predict it- we can just predict the parent directly. Speeds up by reducing the possible routes through the tree. In the case of Json2KeysTo2Columns test, reduces combinations of input atts from 299200 ( 22 10 2 17 10 2 2 ) to 360 ( 9 1 2 5 1 2 2 ) due to no-op duplicates.
            for( Att dupe : outputGraph.getAllAtts() ){
                if( dupe.isDuplicate() && dupe.getDuplicateOf().equals( rel.getLabel() ) && !dupe.getParentAtts().contains( rel.getLabel() )){
                    Collection<Att> dupeMapping = oldToNewMapping.get( dupe );
                    if( dupeMapping == null ) {
                        dupeMapping = new HashSet<>();
                        oldToNewMapping.put(dupe, dupeMapping);
                    }
                    dupeMapping.add(relationshipApplier.getOutputAtts().get(0));
                }
            }
		}

        //Re-filter to include all the newly created modelAppliers and their outputs.
		ArrayList<GraphVertex> filteredVertices = new ArrayList<>();
		for( Collection<Att> mapping : oldToNewMapping.values() ){
			filteredVertices.addAll(mapping);
		}
		AttRelationshipGraph filteredInputSubtree = inputGraph.getSubGraph(filteredVertices);
		AttRelationshipGraph filteredOutputSubtree = outputGraph.getSubGraph( oldToNewMapping.keySet() );

        filteredInputSubtree.createDot("usefulInputs");
        filteredOutputSubtree.createDot("predictableOutput");
		
		System.out.println("After turning relationships into modelAppliers, the Input tree is :"+filteredInputSubtree );
		
		//Start at the "bottom" of the output tree, and create the reversed processors on the bottom of the input tree.
		List<Processor> allOldProcessors = new ArrayList<>( filteredOutputSubtree.getAllFinishedProcessors());
		Collections.sort(allOldProcessors, new AttOrderComparator());
		Processor getInputFileProcessor = allOldProcessors.remove(0);
		Collections.reverse(allOldProcessors);
		procloop:
		for( Processor oldProc : allOldProcessors ){
			ArrayList<Set<Att>> mappedOutputAtts = new ArrayList<>();
			for( Att outAtt : oldProc.getOutputAtts() ){
				if( oldToNewMapping.get(outAtt) != null ){
					mappedOutputAtts.add( new LinkedHashSet<Att> ( oldToNewMapping.get(outAtt) ) );
				}
				else{
					//This can happen for output-tree-leaves when we only have predictors for some of its output atts. eg. If we can predict the "index" output, but not the "content" output att.
                    //It can also happen for processors further up the tree if the relationships below them were skipped, and so didn't produce new outputs which this processor wants to use as an input.
					if( oldProc.getOutputAtts().size() == 1 && filteredOutputSubtree.isLeaf(oldProc.getOutputAtts().get(0)) ){
						System.out.println("Don't think this can ever happen. Comment above is wrong if it can.");
						assert(false);
					}
					System.out.println("Skipping processor "+oldProc.getName()+" because can't find a mapped input in the inputTree for old att "+outAtt.getDbColumnName());
					continue procloop;
				}
			}
			Set<List<Att>> inputAttCombinations = Sets.cartesianProduct(mappedOutputAtts);
			
			System.out.print("Found "+inputAttCombinations.size()+" combinations of input atts ( ");
			for( int inputAttIdx = 0; inputAttIdx < oldProc.getOutputAtts().size(); inputAttIdx++){
				Set<Att> nthInputPossibilities = mappedOutputAtts.get(inputAttIdx);
				System.out.print(nthInputPossibilities.size()+" ");
			}
			System.out.println(")");
			for( int inputAttIdx = 0; inputAttIdx < oldProc.getOutputAtts().size(); inputAttIdx++){
				Set<Att> nthInputPossibilities = mappedOutputAtts.get(inputAttIdx);
				System.out.println(nthInputPossibilities.size()+" new versions of Input "+inputAttIdx+" "+oldProc.getOutputAtts().get(inputAttIdx).getDbColumnNameNoQuotes()+" "+oldProc.getOutputAtts().get(inputAttIdx).getName()+":");
				for( Att newInput : nthInputPossibilities ){
					System.out.println(newInput.getDbColumnNameNoQuotes()+" "+newInput.getName());
				}
			}
			
			int inputAttCombinationLoop = 0;
			for( List<Att> newInputs : inputAttCombinations ){
				inputAttCombinationLoop++;
				//Find out how many rows of the rows of the old rra were created in the training data.
				int oldRRTRows = oldProc.getRootRowTable().getNumRows();
				
				//Find out if of the input atts or their ancestors contain exactly as many rows as the label.
				LinkedHashSet<DataTable> possibleRRTs = new LinkedHashSet<>();
                //Map the old RRT to the new one (more hassle than it sounds because we only have an Att -> Att mapping, not a Table -> Table one).
                //It can be the case that an old rootRowTable maps to multiple new rootRowTables.
                //possibleRRTs.add( oldToNewMapping.get(oldProc.getRootRowTable().getAllAttsInTable().get(0)).iterator().next().getDataTable() );
                for( Att oldRootRowAtt : oldProc.getRootRowTable().getAllAttsInTable() ){
                    if( oldToNewMapping.get(oldRootRowAtt) != null ) {
                        for (Att newRootRowAtt : oldToNewMapping.get(oldRootRowAtt)) {
                            possibleRRTs.add(newRootRowAtt.getDataTable());
                        }
                    }
                }
                //If the old RRT is not mapped to anything yet (because we start the mapping from the leaves up), try RRTs that are the best matches.
                //Best match is a RRT with the same number of rows as the old RRT.
                //Don't guess based the "estimated # rows when joined to newInputs == old processor output rows" because that estimate will be the same for LCA of all inputs AND ALL ANCESTORS. eg. guessing root-table (t0) might produce an estimate of 7 rows, but we're actually looking for the RRT that creates 7 single-row inputs, not a single seven-row table input.
                //TODO: Stop once we get to the latest common ancestor of all the input atts? Think any rras above that will result in identical input tables.
                if( possibleRRTs.size() == 0 ) {
                    for (Att inAtt : newInputs) {
                        if (inAtt.getDataTable().getNumRows() == oldRRTRows) {
                            possibleRRTs.add(inAtt.getDataTable());
                        }
                    }
                    for (Att inAtt : newInputs) {
                        for (Att att : inAtt.getAncestorAtts()) {
                            if (att.getDataTable().getNumRows() == oldRRTRows) {
                                possibleRRTs.add(att.getDataTable());
                            }
                        }
                    }
                }

				boolean foundGoodRRT = false;
				String inputAttsString = "";
				for( Att att : newInputs ){ inputAttsString += att.getDbColumnNameNoQuotes()+", "; }
				inputAttsString = inputAttsString.substring(0, inputAttsString.length()-2);
				//System.out.println(possibleRRTs.size()+" possibleRRTs for "+oldProc.getName()+" with input Atts "+inputAttsString);
				for( DataTable RRT : possibleRRTs ){
					//System.out.print("Trying RRT "+RRT+"..." );
					assert( oldProc.getOutputAtts().size() == newInputs.size() );

					//Estimate size of joined inputs.
					if( DataTable.estimateJoinedSize(newInputs, RRT ) != oldProc.getOutputAtts().get(0).getDataTable().getNumRows() ){
						//System.out.println("Estimated size of joined inputs ("+DataTable.estimateJoinedSize(newInputs, RRT )+") did not match old output table size ("+oldProc.getOutputAtts().get(0).getDataTable().getNumRows()+").");
						continue;
					}

					Processor newProc = new Processor( ((TransformationServiceReversible) oldProc.getTransformer() ).getReverseTransformer(), newInputs, RRT );
					newProc.doWork(false);
					//assert( newProc.getNumberOfTransformers() == oldProc.getNumberOfTransformers() );

					if( newProc.getSuccessfulTransformers() == 0 ){
						//System.out.println("Transformers all failed." );
						continue;
					}
					assert( newProc.getOutputAtts().size() == oldProc.getInputAtts().size() );

					// Or at least not rely on the strings firstInputSize / firstOutputSize meant for debugging (the Processor's toString).
					if( !newProc.getFirstInputSize().equals( oldProc.getFirstOutputSize() ) && !newProc.getFirstInputSize().equals("0x0") ){
						//System.out.println("First input size was:"+newProc.getFirstInputSize()+", different to oldProc first output size: "+oldProc.getFirstOutputSize()+". Continuing to next possible RRT." );
						continue;
					}
					else{
						foundGoodRRT = true;

						int i = 0;
						for( Att oldInputAtt : oldProc.getInputAtts() ){
							Collection<Att> mapping = oldToNewMapping.get( oldInputAtt );
							if( mapping == null ){
								mapping = new LinkedHashSet<>();
								oldToNewMapping.put( oldInputAtt, mapping );
							}
							//System.out.println("Adding oldToNewMapping for processor: ("+oldInputAtt.getDbColumnNameNoQuotes()+")"+oldInputAtt.getName()+" -> ("+newProc.getOutputAtts().get(i).getDbColumnNameNoQuotes()+")"+newProc.getOutputAtts().get(i).getName() );
							mapping.add( newProc.getOutputAtts().get(i) );
							i++;
						}
					}
				}
			}
		}
		
		//Re-filter
		filteredVertices.clear();
		int i = 0;
		if( oldToNewMapping.containsKey(getInputFileProcessor.getOutputAtts().get(0)) ){
			//Filter to include the new, "predicted" atts, the modelApplier processors, and anything on any path to any at corresponding to the old "output-tree root"/"longStringAtt" (eg. many ways of generating the output). 
			for(Att att : oldToNewMapping.get(getInputFileProcessor.getOutputAtts().get(0))){
				att.setFinalOutputAtt(true);
				if( i < 100 ){ //Limit the size of the created tree to 100 possible "final output" atts.
					filteredVertices.add(att);
				}
				i++;
			}
		}
		else{
			for( Collection<Att> mapping : oldToNewMapping.values() ){
				filteredVertices.addAll(mapping);
			}
			
			System.out.println("No versions of the output-tree's root were created. Creating partial tree dot.");
		}

		filteredInputSubtree = inputGraph.getSubGraph(filteredVertices);
		System.out.println("After creating all the reverse-processors, the input tree contains "+filteredInputSubtree.getAllAtts().size()+" atts, "+filteredInputSubtree.getAllProcessors().size()+" processors, and "+filteredInputSubtree.getAllRelationships().size()+" relationships." );
		filteredInputSubtree.createDot("singleForwardTree");
		return filteredInputSubtree;
	}

	public static AttRelationshipGraph createRelationshipsGraph(
			AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph,
			Collection<Relationship> allGoodRelationships) {
		AttRelationshipGraph relationshipsGraph;
		HashSet<GraphVertex> filteredVertices = new HashSet<>();
		for( Relationship rel : allGoodRelationships ){
			filteredVertices.add(rel);
			filteredVertices.add(rel.getLabel()); //We need the labels to be included (and the path to them from the root).	
		}
		
		//Create a graph that contains all of the input and output atts used by relationships so we can join the atts using relationships without polluting the inputGraph / outputGraph with atts from the other.
		AttRelationshipGraph filteredInputSubtree = inputGraph.getSubGraph(filteredVertices);
		AttRelationshipGraph filteredOutputSubtree = outputGraph.getSubGraph(filteredVertices);
		
		relationshipsGraph = new AttRelationshipGraph();
		relationshipsGraph.addAll(filteredInputSubtree);
		relationshipsGraph.addAll(filteredOutputSubtree);
		
		for( Relationship rel : allGoodRelationships ){
			rel.addToGraph(relationshipsGraph);
		}
		return relationshipsGraph;
	}

	public static List<Relationship> filterToMinimalGoodRelationships(Collection<Relationship> allGoodRelationships)
			throws SQLException {
		Collection<Relationship> goodRelationships = new ArrayList<>();
		for (Relationship rel2 : allGoodRelationships) {
			if (rel2.getIncorrectPredictions() == 0 && rel2.getCorrectPredictions() == rel2.getLabel().getNotNullRowsInTable()) {
				goodRelationships.add(rel2);
			}
		}
		
		System.out.println(goodRelationships.size()+" relationships remain after removing inaccurate ones.");
		
		//If 2 relationships predict the same label, both with perfect accuracy, and one of them is capable of doing it based on "earlier" attributes, only keep that one.
		// eg. if rel1 predicts L from X, and rel2 predicts L from uppercase(X), we only need to keep rel1. Especially effective if X is "root"?
		// eg. if rel1 predicts L from X,Y and rel2 predicts L from X,Y,Z then only need to keep rel1.
		List<Relationship> filteredRelationships = new ArrayList<>();
		for( Relationship rel1 : goodRelationships ){
			boolean keepRelationship = true;
			for( Relationship predictor : goodRelationships ){  //Optimization: Could speed up this double loop by using a hash by label built before the outer loop if necessary.
                //If this predictor is better than this relationship (ie. only requires a subset of the ancestors), then we don't need to keep the relationship (we will keep predictor instead).
                // predictor must be strictly better (ie. not equal) to prevent 2 "joint-best" relationships from removing each other.
                if( predictor.requiresLess(rel1) ){
                    keepRelationship = false;
                    break;
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
		List<Relationship> filteredRelationships2 = new ArrayList<>();
		for( Relationship rel1 : filteredRelationships ){
			boolean keepRelationship = true;
			for( Relationship potentialBetterPredictor : filteredRelationships ){         //Optimization: Could speed up this double loop by using a hash by label built before the above outer loop if neccessary.
                //If this potentialBetterPredictor is better than this relationship (ie. predicts the same thing from the same values, using a simpler model), then remove it and keep the better one instead.
                if( potentialBetterPredictor.isLessComplex(rel1) ){
                    keepRelationship = false;
                    break;
				}
			}
			if( keepRelationship ){
				filteredRelationships2.add(rel1);
			}
		}
		
		System.out.println(filteredRelationships2.size()+" relationships remain after only keeping the simplest type of model for each relationship for a given label.");
		
		return filteredRelationships2;
	}
	
	public static Collection<String> applyTree(String testfile, AttRelationshipGraph inputTree) throws SQLException{

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
		HashMap<Att, Att> oldToNewMapping = new HashMap<>(); // Keeps track of what Atts in the old tree correspond to what atts in the new tree.
		Att oldConstant = inputTree.getRootAtt();
		oldToNewMapping.put(oldConstant, newConstant);

		// Sort the processors into order that they were created so that they
		// aren't created out of order before their inputs have been created.
		// TODO: better way of doing this than sorting based on the attOrder of
		// their first output att (eg. tree traversal?) 
		List<Processor> allOldProcessors = new ArrayList<Processor>(
				inputTree.getAllFinishedProcessors());
		
		Collections.sort(allOldProcessors, new AttOrderComparator());

        oldProcessorLoop:
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
            if( newRRT == null){
                //The newRRT wasn't generated for some reason - maybe one of the eariler processors failed and produced no outputs?
                continue oldProcessorLoop;
            }

			// Translate old inputs to this processor to their new equivalents.
			ArrayList<Att> newInputAtts = new ArrayList<Att>();
			for (Att oldInputAtt : oldProcessor.getInputAtts()) {
				Att newInputAtt = oldToNewMapping.get(oldInputAtt);
				//If a previous processor failed to run / its outputs were not mapped to the old proc's atts (eg. it outputted a different number of atts during testing to during training)
                // then the input atts for THIS processor might not have been created. Skip this processor.
                if( newInputAtt == null ){
                    continue oldProcessorLoop;
                }
				newInputAtts.add(newInputAtt);
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
			newProcessor.doWork(false);

			// Add the old & new output atts to the mapping.
			List<Att> newOutputAtts = newProcessor.getOutputAtts();
			List<Att> oldOutputAtts = oldProcessor.getOutputAtts();

			if( oldOutputAtts.size() == newOutputAtts.size() )
			{
				for (int i = 0; i < oldOutputAtts.size(); i++) {
					assert(oldToNewMapping.get(oldOutputAtts.get(i)) == null);
					oldToNewMapping.put(oldOutputAtts.get(i), newOutputAtts.get(i));
				}
			}
			else
			{	//This processor returned a different number of atts during testing than during training, or returned 0 rows (so 0 atts).
				//Any further processing that depend on these output atts can no longer happen, and 
				//What happens if we had some rows that matched some filter in the Training set, but none match in the test set?
				//Those relationships always count as 'violated'? Or just can't be computed and so don't affect the score returned for the test set?
				System.out.println("Processor "+newProcessor.getName()+" returned a different number of atts during testing ("+newOutputAtts.size()+") than during training ("+oldOutputAtts.size()+").");
			}
		}
		
		testGraph.createDot("applied");
		
		
		//Return the value of a random "root" of the "output tree" (there may be several since the relationships won't all predict the same thing, and there might have been multiple choices for the reverse-root-row-atts).
		ArrayList<String> ret = new ArrayList<>();
		for( Att finalOutputAtt : inputTree.getFinalOutputAtts() ){
            if( oldToNewMapping.containsKey(finalOutputAtt) ) { //It is possible this finalOutputAtt was never produced due to some processors failing / producing different sized outputs (more or less columns) to the training data.
                ret.add(oldToNewMapping.get(finalOutputAtt).getFirstRow());
            }
		}

        Database.disconnect();

		return ret;
		
	}
}

