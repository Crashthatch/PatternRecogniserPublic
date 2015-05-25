package database;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import models.Model;
import models.ModelLearner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class Relationship implements GraphVertex {
	private List<Att> inputAtts;
    private Collection<GraphVertex> ancestorAttsAndProcessors;
	private Att label;
	private Att rootRowAtt;
	private ModelLearner modelGenerator;
	private Model learnedModel;
	private int correctPredictions = 0;
	private int incorrectPredictions = 0;
	private int trainingExamples = 0;
	
	
	public Relationship( ModelLearner modelGenerator, List<Att> inputAtts, Att label )
	{
		this( modelGenerator, inputAtts, label, label );
		//Usually, use the label's as the rootRowTable. 
		if( inputAtts.size() == 0 || label.getGraph().equals(inputAtts.get(0).getGraph())){
			//Everything is fine, rootRowAtt not required because the relationship between inputAtts and labelAtt can be found through the tree they are both in. 
		}
		else{
			//In the case that input atts and label att are in different trees, or there is no inputAtt, a rootRowTable must be specifically chosen.
			throw new IllegalArgumentException();
		}
	}
	
	public Relationship( ModelLearner modelGenerator, List<Att> inputAtts, Att label, Att rootRowAtt )
	{
		this.modelGenerator = modelGenerator;
		this.inputAtts = inputAtts;
		this.label = label;
		this.rootRowAtt = rootRowAtt;
	}
	
	public void estimatePerformanceUsingXValidation() throws SQLException, InsufficientRowsException, IncorrectInputRowsException
	{
		estimatePerformanceUsingXValidation(3);
	}
	
	public void estimatePerformanceUsingXValidation( int splits ) throws SQLException, InsufficientRowsException, IncorrectInputRowsException
	{
		//int totalRows = label.getNotNullRowsInTable();
		Table<Integer, Integer, String> inputTable = executeQueryJoiningInputsAllRows();
        int inputTableColumns = inputTable.columnKeySet().size();
		
		int totalRows = inputTable.rowKeySet().size();
		
		if( splits > totalRows )
			throw new InsufficientRowsException();
		
		//Generate a random list of numbers for deciding which rows go in which split.
		ArrayList<Integer> permutation = new ArrayList<>();
		for( int i=0; i<totalRows; i++) { permutation.add(i); }
		Collections.shuffle(permutation, new Random(1));
		double sizeOfTestSet = (0.0+totalRows)/splits;

		for( int trial=1; trial <= splits; trial++ ) //Could be parallelised.
		{

			List<Integer> testSetIndices = permutation.subList((int)Math.round((trial-1)*sizeOfTestSet), (int)Math.round(trial*sizeOfTestSet));
			
			try {
				//Build the table to be fed to the learner.
				
				
				Table<Integer, Integer, String> trainingTable = TreeBasedTable.create();
				Table<Integer, Integer, String> testTable = TreeBasedTable.create();
				int testRowIdx = 0;
				int trainingRowIdx = 0;
				for( int row=0; row < inputTable.rowKeySet().size(); row++ )
				{
					//Optimization: Instead of copying rows from inputTable to test/training Tables, write and use some kind of immutable view on the tables.
					if( testSetIndices.contains(row))
					{
						for( int colIdx=0; colIdx < inputTableColumns; colIdx++){
							testTable.put(testRowIdx, colIdx, inputTable.get(row, colIdx));
						}
						testRowIdx++;
					}
					else
					{
						for( int colIdx=0; colIdx < inputTableColumns; colIdx++){
							trainingTable.put(trainingRowIdx, colIdx, inputTable.get(row, colIdx));
						}
						trainingRowIdx++;
					}
				}

                Model partialModel = modelGenerator.learnModelFromData(trainingTable);

				
				
				HashMap<String,Integer> predictionResults = testModelAndCountResults(testTable, partialModel);
				
				//System.out.println("Correct Predictions:"+correctPredictionsThisTrial);
				//System.out.println("Incorrect Predictions:"+incorrectPredictionsThisTrial);
				correctPredictions += predictionResults.get("correctPredictions");
				incorrectPredictions += predictionResults.get("incorrectPredictions");
				
				
			}
            catch( UnsuitableModelException e ){
                //System.out.println("Aborted because model is unsuitable:"+e.getMessage());
                continue;
            }
            catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private HashMap<String,Integer> testModelAndCountResults(Table<Integer, Integer, String> testTable, Model model) {
		int row;
		//Remove the label column from the testSet so the Model can't cheat.
		//Save the correct values so we can check later.
		ArrayList<String> answers = new ArrayList<>();
		for( row = 0; row < testTable.rowKeySet().size(); row++)
		{
			answers.add( testTable.get(row,0));
			testTable.put(row, 0, "LABELREMOVED-TOBEPREDICTED");
		}
					
		List<String> predictions = model.predictForRows(testTable);
        assert( predictions.size() == testTable.rowKeySet().size() );
		
		int correctPredictionsThisTrial = 0;
		int incorrectPredictionsThisTrial = 0;
		row = 0;
		for( String prediction : predictions )
		{
			if( prediction == null )
			{
				incorrectPredictionsThisTrial++;
				row++;
				continue;
			}
			
			//TODO: Decide (put thought into!) what sort of logic to use to determine the "accuracy" of the learned model.
			//Probably assume a normal error distribution for numerical values and do stuff with that... 

			String trueValue = answers.get(row);
			//If both prediction and trueValue are numerical, then count it as "correct" if they're within 5% of each other.
			/*try{
				if( !trueValue.matches("^[0-9]+\\.?[0-9]*$") || !prediction.matches("^[0-9]+\\.?[0-9]*$")){
					throw new NumberFormatException();
				}
				
				double trueValueDouble = Double.parseDouble(trueValue);
				double predictionDouble = Double.parseDouble(prediction);
				
				if( Math.abs(trueValueDouble - predictionDouble) < ((0.05*trueValueDouble) + 1e-8)
					&& Math.abs(trueValueDouble - predictionDouble) < ((0.05*predictionDouble) + 1e-8) )
				{
					correctPredictionsThisTrial++;
				}
				else
				{
					incorrectPredictionsThisTrial++;
				}
				
			}
			catch( NumberFormatException e )
			{*/
				//The predicted value (or True value) is not numerical.
				if( prediction.equals( trueValue ) )
				{
					correctPredictionsThisTrial++;
				}
				else
				{
					incorrectPredictionsThisTrial++;
				}
			//}
			
			row++;
		}
		
		//Want to return 2 ints, but Java doesn't really have an easy way to do that, so return a hashmap instead.
		HashMap<String,Integer> ret = new HashMap<>();
		ret.put("correctPredictions", correctPredictionsThisTrial);
		ret.put("incorrectPredictions", incorrectPredictionsThisTrial);
		
		return ret;
	}

	public void learn() {
		AttRelationshipGraph graph = label.getGraph();
		if( label.getGraph() == rootRowAtt.getGraph() ){ //Only add the relationship to a graph if both its inputs & its outputs are in the same graph.
			addToGraph(graph);
		}
		label.addPredictor(this);
		
		try {
			//Build the table to be fed to the learner.			
			Table<Integer, Integer, String> inputTable = executeQueryJoiningInputsAllRows();
			
			trainingExamples = inputTable.rowKeySet().size();
			learnedModel = modelGenerator.learnModelFromData( inputTable );
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

    public boolean learned(){
        return learnedModel != null;
    }
	
	public List<String> makePredictions(HashMap<Att, Att> oldToNewMapping) throws SQLException, AttributeNotInTestSetException{
		Table<Integer, Integer, String> testTable = executeQueryJoiningInputsAllRowsNoLabel(mapAtts(oldToNewMapping, inputAtts), mapAtt(oldToNewMapping, rootRowAtt));
		
		List<String> predictions = learnedModel.predictForRows(testTable);
        assert(predictions.size() == testTable.rowKeySet().size());
		return predictions;
	}
	
	public List<String> makePredictions() throws SQLException{
		Table<Integer, Integer, String> testTable = executeQueryJoiningInputsAllRowsNoLabel(inputAtts, rootRowAtt);
		
		List<String> predictions = learnedModel.predictForRows(testTable);
        assert(predictions.size() == testTable.rowKeySet().size());
		return predictions;
	}
	
	public List<String> makePredictions( Table<Integer, Integer, String> inputTable ){
		
		//REMOVED because the first column on the start of the table in a modelApplier now contains the rootRowAtt to allow filtering based on a column that is partially NULLs. Doesn't matter what's in it: model should ignore it anyway.
		//Add an empty column onto the start of the table (where the label would have been during training).
		/*TreeBasedTable<Integer, Integer, String> testTable = TreeBasedTable.create(inputTable.rowKeySet().size(), inputTable.columnKeySet().size() + 1);
		for( int row = 0; row < inputTable.rowKeySet().size(); row++){
			testTable.put(row, 0, "LABEL COL EMPTY");
			for( int col = 0; col < inputTable.columnKeySet().size(); col++){
				testTable.put(row, col+1, inputTable.get(row, col));
			}
		}*/
		
		List<String> predictions = learnedModel.predictForRows( inputTable );
        assert(predictions.size() == inputTable.rowKeySet().size());
        return predictions;
	}
	
	
	public double makePredictionsAndGetPercentageCorrect() throws SQLException{
		return makePredictionsAndGetPercentageCorrect(inputAtts, label, rootRowAtt);
	}
	public double makePredictionsAndGetPercentageCorrect(HashMap<Att, Att> oldToNewMapping) throws SQLException, AttributeNotInTestSetException{
		return makePredictionsAndGetPercentageCorrect(mapAtts(oldToNewMapping, inputAtts), mapAtt(oldToNewMapping, label), mapAtt(oldToNewMapping, rootRowAtt));
	}
	private double makePredictionsAndGetPercentageCorrect(List<Att> inputAtts, Att toBePredicted, Att rootRowAtt) throws SQLException {
		try{
			Table<Integer, Integer, String> testTable = executeQueryJoiningTestInputsAllRows(inputAtts, toBePredicted, rootRowAtt);
		
			//TODO: When considering the "score" a relationship should get for "reliability"/accuracy, consider the number of examples it was tested on in the training set.
			//IDEA: Also, perhaps consider whether it spots something that would not be observed in a stream of random numbers or some other predefined datasets (low entropy?) 
			//(idea is to weed out relationships that are always true, relationships that work for certain ranges but don't generalize to the given test set, ) 
			
			HashMap<String,Integer> predictionResults = testModelAndCountResults(testTable, learnedModel);
			//System.out.println(predictionResults.get("correctPredictions")+" / "+((double) (predictionResults.get("correctPredictions") + predictionResults.get("incorrectPredictions")))+" predictions correct.");
			
			return ((double) predictionResults.get("correctPredictions")) / ((double) (predictionResults.get("correctPredictions") + predictionResults.get("incorrectPredictions")));
		}
		catch ( IncorrectInputRowsException E ){
			return 0;
		}
	}

	private static Table<Integer, Integer, String> resultSetToInputTableNoLabel(ResultSet inputResultSet, List<Att> inputAtts) throws SQLException {
		Table<Integer, Integer, String> testTable = TreeBasedTable.create();
		int testRowIdx = 0;
		inputResultSet.beforeFirst();
		while (inputResultSet.next())
		{
			testTable.put(testRowIdx, 0, "LABELREMOVED");
			
			for(int col=0; col < inputAtts.size(); col++)
			{
				Att inputAtt = inputAtts.get(col);
				testTable.put(testRowIdx, col+1, inputResultSet.getString(inputAtt.getDbColumnNameNoQuotes()));
			}
			testRowIdx++;
		}
		return testTable;
	}
	
	private static Table<Integer, Integer, String> resultSetToInputTable(ResultSet inputResultSet, List<Att> inputAtts, Att toBePredicted) throws SQLException {
		Table<Integer, Integer, String> testTable = TreeBasedTable.create();
		int testRowIdx = 0;
		inputResultSet.beforeFirst();
		while (inputResultSet.next())
		{
			testTable.put(testRowIdx, 0, inputResultSet.getString(toBePredicted.getDbColumnNameNoQuotes()) );
			
			for(int col=0; col < inputAtts.size(); col++)
			{
				Att inputAtt = inputAtts.get(col);
				testTable.put(testRowIdx, col+1, inputResultSet.getString(inputAtt.getDbColumnNameNoQuotes()));
			}
			testRowIdx++;
		}
		return testTable;
	}
	
	
	
	private static Att mapAtt(HashMap<Att, Att> oldToNewMapping, Att oldInputAtt) throws AttributeNotInTestSetException{
			
		if( oldToNewMapping.containsKey(oldInputAtt) )
		{
			return oldToNewMapping.get(oldInputAtt);
		}
		else{
			throw new AttributeNotInTestSetException(oldInputAtt);
		}
	}
	
	private static List<Att> mapAtts(HashMap<Att, Att> oldToNewMapping, List<Att> oldInputAtts) throws AttributeNotInTestSetException{
		List<Att> newInputAtts = new ArrayList<>();
		for (Att oldInputAtt : oldInputAtts) {
			
			if( oldToNewMapping.containsKey(oldInputAtt) )
			{
				newInputAtts.add(oldToNewMapping.get(oldInputAtt));
			}
			else
			{
				throw new AttributeNotInTestSetException(oldInputAtt);
			}
		}
		
		return newInputAtts;
	}
	
	public void addToGraph(AttRelationshipGraph graph)
	{
		graph.addVertex(this);
		
		for( Att att : inputAtts )
		{
			assert( graph.containsVertex(att));
			graph.addEdge(new PredictionEdge(), att, this);
		}
		
		assert( graph.containsVertex(label));
		graph.addEdge(new PredictionEdge(), this, label);
	}
	
	
	private Table<Integer, Integer, String> executeQueryJoiningInputsAllRows() throws SQLException, IncorrectInputRowsException
	{
		return executeQueryJoiningTestInputsAllRows(inputAtts, label, rootRowAtt);
	}
	
	private static Table<Integer, Integer, String> executeQueryJoiningInputsAllRowsNoLabel(List<Att> inputAtts, Att rootRowAtt) throws SQLException
	{
		String sql;
		if( inputAtts.size() > 0 ){
			sql = DataTable.getSqlStringJoiningInputs(rootRowAtt, inputAtts);
		}
		else{
			sql = "SELECT 1;";
		}
		
		
		//System.out.println( sql );
		
		PreparedStatement ps = Database.getConnection().prepareStatement(sql);
		ResultSet data = ps.executeQuery();
		Table<Integer, Integer, String> ret = resultSetToInputTableNoLabel(data, inputAtts);
		data.close();
		ps.close();
		return ret;
	}
	
	private static Table<Integer, Integer, String> executeQueryJoiningTestInputsAllRows(List<Att> inputAtts, Att toBePredicted, Att rootRowAtt) throws SQLException, IncorrectInputRowsException
	{
		Table<Integer, Integer, String> inputTable;
		if( toBePredicted.equals( rootRowAtt ) ){
			//Case where the features and label are both derived from the same place, ie. "find all relationships within this inputfile".
			//Can use the knowledge of the tree to do the joins and know what rows of the inputAtts relate to what label rows.
			ArrayList<Att> attsToGetInTable = new ArrayList<>(inputAtts);
			attsToGetInTable.add( toBePredicted );
			
			String sql = DataTable.getSqlStringJoiningInputs( rootRowAtt.getDataTable(), attsToGetInTable);
			
			PreparedStatement ps = Database.getConnection().prepareStatement(sql);
			ResultSet data = ps.executeQuery();
			inputTable = resultSetToInputTable(data, inputAtts, toBePredicted);
			data.close();
			ps.close();

            if( inputTable.rowKeySet().size() != toBePredicted.getNotNullRowsInTable() ){
                //Bad choice of inputAtts / rootRowAtt. This relationship is no good for predicting the label.
                throw new IncorrectInputRowsException("Can not predict label "+toBePredicted.getDbColumnNameNoQuotes()+" "+toBePredicted.getName()+" with "+toBePredicted.getNotNullRowsInTable()+" rows from inputAtts / RRA which gives a table with "+inputTable.rowKeySet().size()+" rows. SQL to generate wrong-sized-input table was: "+sql);
            }

			assert( inputTable.rowKeySet().size() == toBePredicted.getData().size() );
		}
		else{
			//Case where we're trying to predict outputs from inputs, ie. there's a "input file" tree and a different, reversible, "outputs" tree.
			//Get the input atts in a table, then add the "label" values on to it.
			String inputAttsSql = DataTable.getSqlStringJoiningInputs( rootRowAtt, inputAtts);
			//System.out.println( inputAttsSql );
			PreparedStatement ps = Database.getConnection().prepareStatement(inputAttsSql);
			ResultSet inputData = ps.executeQuery();
			inputTable = resultSetToInputTableNoLabel(inputData, inputAtts);
			inputData.close();
			ps.close();
			
			if( inputTable.rowKeySet().size() > rootRowAtt.getNotNullRowsInTable()){
				throw new IncorrectInputRowsException("More than one row in input table ("+inputTable.rowKeySet().size()+" rows) per root-row ("+rootRowAtt.getNotNullRowsInTable()+" rows). Relationships must predict from a single row to a single prediction. SQL to generate wrong-sized-input table was: "+inputAttsSql);
			}
			
			if( inputTable.rowKeySet().size() != toBePredicted.getNotNullRowsInTable() ){
				//Bad choice of inputAtts / rootRowAtt. This relationship is no good for predicting the label.
				throw new IncorrectInputRowsException("Can not predict label "+toBePredicted.getDbColumnNameNoQuotes()+" "+toBePredicted.getName()+" with "+toBePredicted.getNotNullRowsInTable()+" rows from inputAtts / RRA which gives a table with "+inputTable.rowKeySet().size()+" rows. SQL to generate wrong-sized-input table was: "+inputAttsSql);
			}
			ArrayList<String> toBePredictedValues = toBePredicted.getData();
			assert( inputTable.rowKeySet().size() == toBePredictedValues.size() );
			
			
			//Overwrite column-0 of the inputTable with the actual values(Currently contains "TOBEPREDICTED").
			for( int rowIdx=0; rowIdx < inputTable.rowKeySet().size(); rowIdx++){
				inputTable.put(rowIdx, 0, toBePredictedValues.get(rowIdx));
			}
		}
		
		
		return inputTable;
	}
	
	public String toString(){
		return toString(true);
	}
	public String toString(boolean includeAtts)
	{
		DecimalFormat formatter = new DecimalFormat("0.00");
		
		String out = "";
		out += modelGenerator.getClass().getSimpleName()+"\n";
		out += "Correct Training Predictions: "+correctPredictions+" / "+(correctPredictions + incorrectPredictions)+"\n";
		if( includeAtts ){
			out += "Label:" +label.getName()+" ("+label.getDbColumnNameNoQuotes()+")\n";
			out += "From:";
			for( Att inputAtt : inputAtts)
			{
				out += inputAtt.getName()+" ("+inputAtt.getDbColumnNameNoQuotes()+") \n";
			}
			out += "\n";
		}
		if( learnedModel != null )
		{
			out += learnedModel+"\n";
		}
		return out;
	}
	
	public double getAccuracy()
	{
		//TODO: Sanity check. Train on whole dataset, then predict for the same dataset. Should "obviously" usually make the right prediction.
		//If it doesn't, perhaps cast doubt (lower the confidence?) of predictions made by this model.
		
		//TODO: Factor in the possibility that the X trials all produced good models, but that the final "used" model didn't. 
		//Maybe requires a prior that a model of that type will produce a good / bad model. 
		
		//Should we also take into account the size of the training set?
		
		if( correctPredictions == 0 && incorrectPredictions == 0 )
		{
			return Double.NaN;
		}
		else
		{
			double accuracy = (double) correctPredictions / (double) (correctPredictions + incorrectPredictions );
			
			return accuracy;
		}
	}

	public boolean madeSuccessfulPredictions() {
		return (correctPredictions + incorrectPredictions) > 0;
	}

	public int getCorrectPredictions() {
		return correctPredictions;
	}

	public int getIncorrectPredictions() {
		return incorrectPredictions;
	}
	
	public int getTrainingExamples() {
		return trainingExamples;
	}

	public Att getLabel() {
		return label;
	}
	
	public Att getRootRowAtt() {
		return rootRowAtt;
	}

	public String getName() {
		return modelGenerator.getClass().getSimpleName();
	}

	public List<Att> getInputAtts() {
		return inputAtts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputAtts == null) ? 0 : inputAtts.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((modelGenerator == null) ? 0 : modelGenerator.hashCode());
		result = prime * result + ((rootRowAtt == null) ? 0 : rootRowAtt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relationship other = (Relationship) obj;
		if (inputAtts == null) {
			if (other.inputAtts != null)
				return false;
		} else if (!inputAtts.equals(other.inputAtts))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (rootRowAtt == null) {
			if (other.rootRowAtt != null)
				return false;
		} else if (!rootRowAtt.equals(other.rootRowAtt))
			return false;
		if (modelGenerator == null) {
			if (other.modelGenerator != null)
				return false;
		} else if (!modelGenerator.equals(other.modelGenerator))
			return false;
		return true;
	}
	
	public Collection<GraphVertex> getAncestorAttsAndProcessors() {
        if( this.ancestorAttsAndProcessors == null ){
            ancestorAttsAndProcessors = new HashSet<>();
            ancestorAttsAndProcessors.add(rootRowAtt);
            ancestorAttsAndProcessors.addAll(inputAtts);
            ancestorAttsAndProcessors.addAll(rootRowAtt.getAncestorAttsAndProcessors());
            for (Att inputAtt : inputAtts) {
                ancestorAttsAndProcessors.addAll(inputAtt.getAncestorAttsAndProcessors());
            }
        }
        return this.ancestorAttsAndProcessors;
	}

    /** Returns true if this relationship requires less attributes than the passed in relationship (and the two predict the same thing etc.). **/
    public boolean requiresLess(Relationship compareRel){
        if( this != compareRel && this.getLabel().equals( compareRel.getLabel() )){
            Collection<GraphVertex> thisAncestors = this.getAncestorAttsAndProcessors();
            Collection<GraphVertex> compareRelAncestors = compareRel.getAncestorAttsAndProcessors();
            //if the compare requires strictly more ancestors, then this att requires less (and so is better). Return true.
            //or if the two have the same number of ancestors, but this one has less input atts, then prefer this (eg. rra = AttX in both rels, but this rel has no input atts, and compareRel has inputAtt = AttX; Same ancestor trees, but this is simpler). Return true.
            if( (compareRelAncestors.containsAll(thisAncestors) && !thisAncestors.equals(compareRelAncestors))
                 || (thisAncestors.equals(compareRelAncestors) && this.getInputAtts().size() < compareRel.getInputAtts().size()) ){
                return true;
            }
        }
        return false;
    }
	
	public int getComplexityOrdering(){
		return modelGenerator.getComplexityOrdering();
	}

    /** Returns true if this relationship is less complex than the passed in relationship (and the two predict the same thing etc.). **/
    public boolean isLessComplex(Relationship compareRel ){
        return ( this != compareRel
            && this.getComplexityOrdering() < compareRel.getComplexityOrdering()
            && compareRel.getLabel().equals( this.getLabel() )
            && compareRel.getRootRowAtt().equals( this.getRootRowAtt() )
            && compareRel.getInputAtts().equals( this.getInputAtts() ) );
    }
	
	public JSONObject createRelationshipJson(AttRelationshipGraph tree, HashMap<Att, Att> attributeMapping) throws SQLException, AttributeNotInTestSetException, IncorrectInputRowsException{
		Database.reconnect("pattern_activedata");
		
		//Create tree of this relationships ancestors to shows how it was generated.
		Collection<GraphVertex> graphLeaves = new HashSet<>();				
		graphLeaves.add(this);
		graphLeaves.add(this.getLabel());
		AttRelationshipGraph subgraph = tree.getSubGraph(graphLeaves); 
		subgraph.createDot(); //TODO: serialize this into the JSON instead of creating a dot.
		
		ArrayList<Att> ancestors = new ArrayList<>(subgraph.getAllAtts());
		ancestors.remove(label); //Gets added back in in executeQueryJoiningTestInputsAllRows.
		//Remove any ancestors that are in "sibling" tables, as they will cause a Cross-Product and blow up the number of rows to more than were used to train / test the relationship.
		HashSet<DataTable> dataTablesToKeep = new HashSet<>();
		dataTablesToKeep.add(label.getDataTable());
		dataTablesToKeep.addAll(label.getDataTable().getAncestorTables() );
		for( Att inputAtt : inputAtts )
		{
			dataTablesToKeep.add(inputAtt.getDataTable());
			dataTablesToKeep.addAll(inputAtt.getDataTable().getAncestorTables() );
		}
		
		ArrayList<Att> filteredAncestors = new ArrayList<>();  
		for( Att ancestor : ancestors ){
			if( dataTablesToKeep.contains( ancestor.getDataTable() ) )
			{
				filteredAncestors.add(ancestor);
			}
		}
		ancestors = filteredAncestors;
		filteredAncestors = null;
		Collections.sort(ancestors, new AttOrderComparatorAtts());
		
		//JSONObject treeJson = new JSONObject();
		//treeJson.put("vertices", subgraph.getVertices() );
		//json.put("tree", treeJson);
		
		//Recreate the training data for investigation purposes, and run the trained model over the training data (should generally get it all right since it was trained on exactly this data, but it might help debugging / be interesting to see in the table how it predicts for training data).
		Table<Integer, Integer, String> trainingData = executeQueryJoiningInputsAllRows();
		Table<Integer, Integer, String> trainingDataAndAncestors = executeQueryJoiningTestInputsAllRows(ancestors, label, rootRowAtt);		
		assert( trainingData.rowKeySet().size() == trainingDataAndAncestors.rowKeySet().size() );
		
		List<String> trainingPredictions = learnedModel.predictForRows(trainingData);
		ArrayList<Integer> trainingCorrectPredictionIndexes = getCorrectPredictionIndexes(trainingData, trainingPredictions);
		
		Database.reconnect("pattern_testdata");
		
		//Recreate the test data.
		Table<Integer, Integer, String> testData = executeQueryJoiningTestInputsAllRows(mapAtts(attributeMapping, inputAtts), mapAtt(attributeMapping, label), mapAtt(attributeMapping, rootRowAtt) );
		Table<Integer, Integer, String> testDataAndAncestors = executeQueryJoiningTestInputsAllRows(mapAtts(attributeMapping, ancestors), mapAtt(attributeMapping, label), mapAtt(attributeMapping, rootRowAtt)); 
		assert( testData.rowKeySet().size() == testDataAndAncestors.rowKeySet().size() );
		
		List<String> predictions = learnedModel.predictForRows(testData);
		ArrayList<Integer> correctPredictionIndexes = getCorrectPredictionIndexes(testData, predictions);
		
		JSONObject json = new JSONObject();
		JSONArray testArray = new JSONArray();
		for( int rowNum=0; rowNum < testDataAndAncestors.rowKeySet().size(); rowNum++ )
		{
			JSONArray testRow = new JSONArray();
			for( String value : testDataAndAncestors.row(rowNum).values() ){
				testRow.add( value );
			}
			testArray.add( testRow );
		}
		json.put("testData", testArray);
		JSONArray trainArray = new JSONArray();
		for( int rowNum=0; rowNum < trainingDataAndAncestors.rowKeySet().size(); rowNum++ )
		{
			JSONArray trainingRow = new JSONArray();
			for( String value : trainingDataAndAncestors.row(rowNum).values() ){
				trainingRow.add( value );
			}
			trainArray.add( trainingRow );
		}
		json.put("trainingData", trainArray);
		
		JSONArray attNames = new JSONArray();
		attNames.add(label.getName());
		for( Att ancestor : ancestors){
			attNames.add( ancestor.getName() );
		}
		json.put("attNames", attNames);
		JSONArray attColumns = new JSONArray();
		attColumns.add(label.getDbColumnNameNoQuotes());
		for( Att ancestor : ancestors){
			attColumns.add( ancestor.getDbColumnNameNoQuotes() );
		}
		json.put("attColumns", attColumns);
		JSONArray mappedAttColumns = new JSONArray();
		mappedAttColumns.add(attributeMapping.get(label).getDbColumnNameNoQuotes());
		for( Att ancestor : ancestors){
			mappedAttColumns.add( attributeMapping.get(ancestor).getDbColumnNameNoQuotes() );
		}
		json.put("mappedAttColumns", mappedAttColumns);
		json.put("trainingPredictions", trainingPredictions);
		json.put("predictions", predictions);
		json.put("trainingCorrectPredictionIndexes", trainingCorrectPredictionIndexes);
		json.put("correctPredictionIndexes", correctPredictionIndexes);
		
		
		json.put("modelClassName", modelGenerator.getClass().getSimpleName() );
		json.put("accuracy", ""+correctPredictions+" / "+(correctPredictions + incorrectPredictions));
		json.put("learnedModel", learnedModel.toString());
		
		
		
		return json;
	}

	private ArrayList<Integer> getCorrectPredictionIndexes(
			Table<Integer, Integer, String> testData, List<String> predictions) {
		ArrayList<Integer> correctPredictionIndexes = new ArrayList<>();
		for( int row = 0; row < predictions.size(); row++ )
		{
			//Figure out which rows were considered "correct" and which rows the model disagreed with the actual value for.
			String prediction = predictions.get(row);
			String trueValue = testData.get(row, 0);
			
			/*if( prediction == null ){
				continue;
			}
			
			try{
				double trueValueDouble = Double.parseDouble(trueValue);
				double predictionDouble = Double.parseDouble(prediction);
				
				if( Math.abs(trueValueDouble - predictionDouble) < ((0.05*trueValueDouble) + 1e-8)
					&& Math.abs(trueValueDouble - predictionDouble) < ((0.05*predictionDouble) + 1e-8) )
				{
					correctPredictionIndexes.add(row);
				}
				else{
					//Incorrect prediction
					System.out.println(trueValueDouble);
					System.out.println(predictionDouble);
				}
			}
			catch( NumberFormatException e )
			{*/
				//The predicted value (or True value) is not numerical.
				if( prediction.equals( trueValue ) )
				{
					correctPredictionIndexes.add(row);
				}
			//}
		}
		return correctPredictionIndexes;
	}
	
}
