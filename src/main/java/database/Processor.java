package database;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Processor implements GraphVertex{

	private final TransformationService transformer;
	private final List<Att> inputAtts;
	private List<Att> outputAtts;
	
	private final DataTable rootRowTable;
	private DataTable outputDataTable;
	private boolean createNewLogicalTable;
	private boolean finished = false;
    private HashSet<DataTable> targetTablesToJoin = new HashSet<>();
	
	private int numberOfTransformers = 0;
	private int successfulTransformers = 0;
	private String firstInputSize;
	private String firstOutputSize;
	
	private AttRelationshipGraph graph;
	
	public Processor ( TransformationService transformer, List<Att> inputAtts2, DataTable rootRowTable )
            throws AnnotateExistingRowsException {
		this.transformer = transformer;
		this.inputAtts = inputAtts2;
		outputAtts = new ArrayList<Att>();
		this.rootRowTable = rootRowTable;

        targetTablesToJoin.add(rootRowTable);
        for( Att inputAtt : inputAtts )
        {
            targetTablesToJoin.add( inputAtt.getDataTable() );
        }

        if( transformer.getAnnotateExistingRows() && targetTablesToJoin.size() > 2){
            //Technically, we could allow atts from many tables (eg. parents of the root row att table, or from tables between the RRT and the output table), so long as there is a unambiguous single output table.
            throw new AnnotateExistingRowsException("Attributes must be in the rootRowTable or the outputTable for transformers that annotate existing rows.");
        }
		
		//Figure out whether we need to create a new table or not.
		/* Now done at runtime if a transformer produces > 1 output row.
		int numOutputRows = transformer.getRowsOut();
		if( numOutputRows == 1 )
		{
			//Add att(s) onto rootRowTable
			createNewLogicalTable = false;
		}
		else 
		{
			//Create new table and add att(s) into that.
			createNewLogicalTable = true;
		}*/
        createNewLogicalTable = false;

        if( rootRowTable.getAllAttsInTable().size() > 0 ) {
            this.graph = rootRowTable.getAllAttsInTable().iterator().next().getGraph();
        }
        else{
            System.err.println("Unable to find what graph to add processor to. ");
            assert( false );
        }
	}
	
	public void doWork(){
		doWork(true);
	}
	
	public void doWork(boolean saveDuplicates )
	{
		//System.out.println("Running "+getName()+"...");
		
		//"rootrowatt" contains the different rows that should be processed separately using different transformers (maybe in parallel?)
		try {
			addToGraph();
			//Set up prepared statements which will be used to update / insert the new data.
			//TODO: If we want to parallelise over the rootRowIds, we should move the DB queries inside so that each thread can commit its own results to the DB independently.
			//For now they are outside since we're only single-threaded.
			PreparedStatement getDataQuery = getPreparedQueryJoiningInputs();

            /*  OPTIMIZATION: uncomment this, which should speed up by reducing the number of DB queries, at the cost of more complex code.
                Not currently a slow point (learning relationships take the lions' share of the time).
                Doesn't currently work- some assertions fail. Not sure if it matters.
			//If we're not doing a join, we can speed up by getting all the rows at once rather than doing one query for each rootrow id.
			ResultSet allData = null;
			if( targetTablesToJoin.size() == 1 )
			{
				allData = executeQueryJoiningInputsAllRows();
			}*/
			
			int maximumOutputCols = 0;
			TreeMap<Integer, Table<Integer, Integer, String>> allRowsToSave = new TreeMap<>(); //Use a treemap instead of a hashmap to preserve the order of the rows produced by the transformers.
																							  //TODO: Fix the other code (change how input-feature-atts and output-label atts are matched up [sort both columns? Sort the input column based on another column?]) so that it doesn't matter what order the rows are in the DB / what IDs each row recieves from MySQL. 
			for( int rootRowId=1; rootRowId <= rootRowTable.getNumRows(); rootRowId++ )
			{
				numberOfTransformers++;
				
				//Table<Integer, Integer, String> inputAttsTable = rootrowCell.createInputTable(inputAtts);
				
			
				
				Table<Integer, Integer, String> inputTable = TreeBasedTable.create();
                ArrayList<Integer> inputRowIds = new ArrayList<>();
				if( inputAtts.size() == 0 ){
					//If Processor has no input atts, still create a 1x1 table so processors like "createConstant" or "modelApplier(constantPredictor)" still run.
					inputTable.put(0, 0, "noCols");
				}
				/* OPTIMIZATION: part of the optimization above.
				else if( targetTablesToJoin.size() == 1 )
				{
					//Only a single row per transformer. We can select the correct single row from allData, rather than doing multiple simple queries to the DB.
                    //Might be empty if one of the atts is empty for this rootRoowId (eg. the allData query might return rows with id 1,3,5 and 9)
                    while( allData.next() ){
                        if( allData.getInt(1) == rootRowId ){
                            for(int col=1; col < inputAtts.size()+1; col++)
                            {
                                inputTable.put(0, col-1, allData.getString(col+1));
                            }
                            break;
                        }
                    }
				}*/
				else
				{
					//Query for the data for the table to be passed into the processor and convert it to a Guava Table.
					getDataQuery.setInt(1, rootRowId);
					//System.out.println(getDataQuery);
					ResultSet inputResultSet = getDataQuery.executeQuery();
				
					int row = 0;
					while (inputResultSet.next())
					{
                        if( transformer.getAnnotateExistingRows() ){
                            //Record the ids from the input rows so we can annotate them later with additional columns produced by the transformer.
                            inputRowIds.add(inputResultSet.getInt(1));
                        }
                        else{
                            assert( inputResultSet.getInt(1) == rootRowId );
                        }

						for(int col=1; col < inputAtts.size()+1; col++)
						{
							inputTable.put(row, col-1, inputResultSet.getString(col+1));
						}
						row++;
					}
					inputResultSet.close();
				}
				
				
				//Save the size of the first table for the name / toString.
				if( firstInputSize == null )
				{
					firstInputSize = inputTable.rowKeySet().size()+"x"+inputTable.columnKeySet().size();
				}
				
				//Run the transformer on the selected input columns derived from the rootrowatt.
				Table<Integer, Integer, String> outputTable = null;
				try{
					transformer.checkInputDimensions(inputTable);
					outputTable  = transformer.doWork(inputTable);
					transformer.checkOutputDimensions(inputTable, outputTable);
				}
				catch (IncorrectInputColumnsException e) 
				{
					//This can happen if the input atts are NULL (ie. their generating processor failed) for this RRA, but not for all rows (else the processor would have been classes as 'failed' and the att removed).
					//eg. isPrime( splitCharactersToRows( "abc246")) will be NULL,NULL,NULL,1,0,0. If that att is then used as an input att and rra, then rootRowIds 1,2 and 3 will give empty input tables. 
					//e.printStackTrace();
					continue;
				}
				catch (IncorrectInputRowsException e) 
				{
					//This happens if there was a "bad" choice of rootRowTable. eg. we chose a rootRowTable with one row, but one of the input atts has 3 rows corresponding to that row, resulting in a 3x2 input table.
					System.out.println("Caught a not-unexpected IncorrectInputRowsException exception in "+transformer.getName()+" while doing work: "+e.getMessage()+" Continuing with the next Transformer...");
					continue;
				}
				catch (IncorrectOutputDimensionsException e) 
				{
					//System.out.println("Caught a not-unexpected IncorrectOutputDimensionsException exception in "+transformer.getName()+" while doing work: "+e.getMessage()+" Continuing with the next Transformer...");
					continue;
				}
				catch( Exception e )
				{
					//System.out.println("Caught a not-unexpected "+e.getClass().getSimpleName()+" in "+transformer.getName()+" while doing work. Continuing with the next Transformer...");
					continue;
				}
				inputTable = null;
				
				//Save the size of the first table for the name / toString.
				if( firstOutputSize == null ){
					firstOutputSize = outputTable.rowKeySet().size()+"x"+outputTable.columnKeySet().size();
				}
				
				if( outputTable.columnKeySet().size() > maximumOutputCols ){
					maximumOutputCols = outputTable.columnKeySet().size(); 
				}

                //If we created more than one row per input row, create a new table to house the new atts.
                //POSSIBLE PROBLEM: What happens if the training data only creates a single output row per input row, but the test data creates more than one (eg. splitToCharacters, where the training data is all < 10)? Will this make the oldToNewMappings fail later (do we map tables? Or just atts?)
                if( outputTable.rowKeySet().size() != 1 && !transformer.getAnnotateExistingRows() ){
                    createNewLogicalTable = true;
                }

                if( !transformer.getAnnotateExistingRows() ) {
                    //Will save on the rootRowTable (or a new table).
                    allRowsToSave.put(rootRowId, outputTable);
                }
                else{
                    //Will save on the table that contains the attributes. (ie. the table that is not the rootRowTable).
                    assert( outputTable.rowKeySet().size() == inputRowIds.size() );
                    for( int rowNum = 0; rowNum < outputTable.rowKeySet().size(); rowNum++ ) {
                        Table<Integer, Integer, String> outputRow = TreeBasedTable.create();
                        for(int colNum=0; colNum < outputTable.columnKeySet().size(); colNum++)
                        {
                            outputRow.put(0, colNum, outputTable.get(rowNum, colNum));
                        }

                        allRowsToSave.put(inputRowIds.get(rowNum), outputRow);
                    }
                }
				
				successfulTransformers++;
			}
			
			getDataQuery.close();
			/*if( allData != null )
			{
				allData.getStatement().close();
				allData.close();
			} */
			
			if( successfulTransformers > 0 ){
                ArrayList<Integer> nonDuplicateColumns = new ArrayList<>();

				if( createNewLogicalTable ) {
                    outputDataTable = new DataTable(rootRowTable);
                }
                else {
                    outputDataTable = rootRowTable;

                    if( transformer.getAnnotateExistingRows() ){
                        //Override the output table: Save onto the table that is not the rootRowTable.
                        for( DataTable tbl : targetTablesToJoin ){
                            if( !tbl.equals(rootRowTable)){
                                outputDataTable = tbl;
                            }
                        }
                    }
                }
                assert(outputDataTable != null);

                //Create all of the outputAtts and compute their hashes.
                for( int colId=0; colId < maximumOutputCols; colId++ ) {
                    Att outputAtt = new Att(this,outputDataTable, graph, colId);
                    String attValuesString = "";
                    for (int rootRowId : allRowsToSave.keySet()) {
                        attValuesString += StringUtils.join(allRowsToSave.get(rootRowId).column(colId).values(), "-,-") + "-,-";  //Have to use a stupid separator -,- to make 3 rows: "1" "2" "3" not get the same hash as 1 row "1,2,3"
                    }
                    attValuesString = attValuesString.substring(0, attValuesString.length() - 3);
                    String valuesHash = DigestUtils.sha1Hex(attValuesString);
                    outputAtt.setColumnValuesHash( valuesHash );
                    outputAtts.add( outputAtt );
                }

                
                if( createNewLogicalTable ) {
                    //If it's a new table, to be counted as a duplicate, ALL the atts in the table must exist in the same table above somewhere, because although they might exist singley in different tables, they might not correspond to each other, making this table useful.

                    DataTable betterOlderSibling = null;
                    if( !saveDuplicates ) {
                        //Remove any columns that were duplicated within the output atts (ie. where this processor returned 2 identical columns).
                        for( Att att1 : outputAtts ){ //Optimization: Use better method than a double loop for duplicate detection.
                            for( Att att2 : outputAtts ){
                                if( !att1.isDuplicate() && att1.getColumnValuesHash().equals( att2.getColumnValuesHash() ) && att1.getAttOrder() < att2.getAttOrder() ){
                                    att2.setDuplicateOf(att1);
                                }
                            }
                        }


                        //Create hashes of each output column.
                        Set<String> hashesToFind = new HashSet<>();
                        for( Att outputAtt : outputAtts ) {
                            if( !outputAtt.isDuplicate() ) {
                                hashesToFind.add(outputAtt.getColumnValuesHash());
                            }
                        }

                        //Try to find an older sibling table that contains atts with identical hashes.
                        Set<DataTable> olderSiblingTables = outputDataTable.getOlderSiblingTables();
                        olderSiblingTables.add( outputDataTable.getParentTable() );
                        for (DataTable siblingTable : olderSiblingTables) {
                            HashSet<String> hashesInSiblingTable = new HashSet<>();
                            for (Att att : siblingTable.getAllAttsInTable()) {
                                hashesInSiblingTable.add(att.getColumnValuesHash());
                            }
                            if (hashesInSiblingTable.containsAll(hashesToFind)) {
                                betterOlderSibling = siblingTable;
                                break;
                            }
                        }
                    }


                    if(betterOlderSibling == null){
                        //Not duplicates.
                        for( int colId=0; colId < maximumOutputCols; colId++ ) {
                            if( !outputAtts.get(colId).isDuplicate() ) {
                                outputAtts.get(colId).addToDataTable();
                                nonDuplicateColumns.add(colId);
                            }
                        }
                    }
                    else{
                        //The atts in this new table are duplicates of those in the older sibling.
                        for( Att outputAtt : outputAtts ) {
                            for( Att original : betterOlderSibling.getAllAttsInTable() ){
                                if( outputAtt.getColumnValuesHash().equals( original.getColumnValuesHash() ) ){
                                    outputAtt.setDuplicateOf(original);
                                }
                            }
                        }
                    }

				}
				else {
                    //Find out if these values already exist in the datatable. If they do, then this col is a duplicate and there's no need to save them.
                    // Each column of the output tables contains values for a different att.
                    for( int colId=0; colId < maximumOutputCols; colId++ ) {
                        Att outputAtt = outputAtts.get(colId);
                        Att duplicateOf = null;
                        if (!saveDuplicates) {
                            //If this table already has a column with these values, don't need to create this one.
                            //for( Att att : outputAtt.getAncestorAtts() ){
                            for (Att att : outputDataTable.getAllAttsInTable()) {     //TODO: Figure out why when 2 identical output columns are returned, the second one isn't tagged as a dupe.
                                if (!att.isDuplicate() && att.getColumnValuesHash().equals(outputAtt.getColumnValuesHash())) {
                                    //System.out.println("Found a duplicate");
                                    duplicateOf = att;
                                    break;
                                }
                            }
                        }

                        if( duplicateOf == null){
                            outputAtts.get(colId).addToDataTable();
                            nonDuplicateColumns.add(colId);
                        }
                        else{
                            //Change this new att to point at the column in the DB that we're copying. We won't actually write to it, but we do want to be able to read from it (although, usually we'll ignore it because isDuplicate flag is set).
                            outputAtt.setDuplicateOf(duplicateOf);
                        }
                    }
				}


				//Save the data into the DB.
				if( nonDuplicateColumns.size() > 0 ){
					for( int rootRowId : allRowsToSave.keySet() ){
						Table<Integer, Integer, String> outputTable = allRowsToSave.get(rootRowId);
						//Convert to map from Atts -> values for the DataTable to save to the DB.
						for( int row=0; row < outputTable.rowKeySet().size(); row++ )
						{
							HashMap<Att, String> rowToSave = new HashMap<>();
							for( int colKey = 0; colKey < maximumOutputCols; colKey++)
							{
								if( nonDuplicateColumns.contains(colKey) ){  //if this column wasn't removed as a duplicate
									rowToSave.put( outputAtts.get(colKey), outputTable.get(row,colKey) );
								}
							}
							
							if( !createNewLogicalTable )
							{
								outputDataTable.update(rootRowId, rowToSave);
							}
							else
							{		
								outputDataTable.insert(rootRowId, rowToSave);
							}
						}
					}
	
					//System.out.print("Finished running "+this.getName()+" on "+numberOfTransformers+" transformers. Saving...");
					outputDataTable.save();
					//System.out.println("Saved");
				}
				else{
					outputDataTable.clearPendingUpdates();
				}
			}
			
		} catch (SQLException e1) {

			e1.printStackTrace();
			assert( false );
		}

		finished = true;
	}

	private void addToGraph() {
		graph.addVertex(this);
		//Add this processor to the input att's "processors" (ie. children).
		for( Att att : inputAtts )
		{
			graph.addEdge(new GenerationEdge(), att, this);
			att.addProcessor(this);
		}
	}

	private PreparedStatement getPreparedQueryJoiningInputs() throws SQLException
	{
        String sql;
        if( transformer.getAnnotateExistingRows() ){
            //Don't get the rootRowIds in col 0 (we don't use them anyway). Use the other table so we know where to save back to.
            DataTable tableForIds = rootRowTable;
            for( DataTable tbl : targetTablesToJoin ){
                if( !tbl.equals(rootRowTable)){
                    tableForIds = tbl;
                }
            }
            sql = DataTable.getSqlStringJoiningInputs(rootRowTable, inputAtts, tableForIds);
        }
        else{
            sql = DataTable.getSqlStringJoiningInputs(rootRowTable, inputAtts);
        }
		
		sql += " AND "+rootRowTable.getTableName()+"_0.id = ?";
		
		//System.out.println( sql );
		
		return Database.getConnection().prepareStatement(sql);
	}
	
	private ResultSet executeQueryJoiningInputsAllRows() throws SQLException
	{
		String sql = DataTable.getSqlStringJoiningInputs(rootRowTable, inputAtts);
		
		//System.out.println( sql );
		
		PreparedStatement ps = Database.getConnection().prepareStatement(sql);
		ResultSet ret = ps.executeQuery();
		ps.close();
		return ret;
	}

	public TransformationService getTransformer() {
		return transformer;
	}

	public List<Att> getInputAtts() {
		return inputAtts;
	}

	public List<Att> getOutputAtts() {
		return outputAtts;
	}

	public DataTable getRootRowTable() {
		return rootRowTable;
	}

	public String getName()
	{
		String name = transformer.getName();

		
		return name;
	}
	
	public String toString()
	{
		String name = transformer.getExtendedName()+"\n";
		name += "Transformers: "+successfulTransformers+" / "+numberOfTransformers+"\n"+
				"RootRowTable:"+rootRowTable+"\n"+
				"Size of first input table: "+firstInputSize+"\n"+
				"Size of first output table: "+firstOutputSize;
		
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inputAtts == null) ? 0 : inputAtts.hashCode());
		result = prime * result
				+ ((rootRowTable == null) ? 0 : rootRowTable.hashCode());
		result = prime * result
				+ ((transformer == null) ? 0 : transformer.hashCode());
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
		Processor other = (Processor) obj;
		if (inputAtts == null) 
		{
			if (other.inputAtts != null)
				return false;
		} else if (!inputAtts.equals(other.inputAtts))
			return false;
		if (rootRowTable == null) 
		{
			if (other.rootRowTable != null)
				return false;
		} else if (!rootRowTable.equals(other.rootRowTable))
			return false;
		if (transformer == null) 
		{
			if (other.transformer != null)
				return false;
		} else if (!transformer.equals(other.transformer))
			return false;
		return true;
	}

	public int getNumberOfTransformers() {
		return numberOfTransformers;
	}

	public int getSuccessfulTransformers() {
		return successfulTransformers;
	}
	
	public boolean finished()
	{
		return finished;
	}
	
	
	public Collection<GraphVertex> getAncestorAttsAndProcessors() {
		Collection<GraphVertex> ancestors = new HashSet<>();
		ancestors.addAll(inputAtts);
		for( Att inputAtt : inputAtts )
		{
			ancestors.addAll(inputAtt.getAncestorAttsAndProcessors());
		}
		
		return ancestors;
	}
	
	public Collection<Att> getAncestorAtts() {
		Collection<Att> ancestors = new HashSet<>();
		ancestors.addAll(inputAtts);
		for( Att inputAtt : inputAtts )
		{
			ancestors.addAll(inputAtt.getAncestorAtts());
		}
		
		return ancestors;
	}

	public String getFirstInputSize() {
		return firstInputSize;
	}

	public String getFirstOutputSize() {
		return firstOutputSize;
	}
	
	

}
