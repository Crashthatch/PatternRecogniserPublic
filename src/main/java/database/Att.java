package database;

import org.apache.commons.lang.StringEscapeUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Att implements GraphVertex{
	private Processor generator;
	private HashSet<Relationship> predictors = new HashSet<>();
	private HashSet<Processor> processors = new HashSet<>();
	private HashSet<Att> attAncestorsCached = new HashSet<>();
	private final int attOrder;
	private int cachedHashCode;
	private String attDbName;
	private DataTable dataTable;
    private int outputIdx; //The number output it was from its generator (eg. often, index = output att 0 )
	private boolean existsInDb = false;
	
	private String notes;
	private AttRelationshipGraph graph;
	private Integer notNullRowsInTable = null;
    private ArrayList<Integer> notNullRowIds = null;
	private Integer uniqueRowsInTable = null;
	private String columnValuesHash;
	private boolean isFinalOutputAtt = false;
	private Att duplicateOf = null;
	
	//TODO: Add caching the first row if it's short, or the truncated version used for debug display if it's not.
	
	private static int nextAttOrder = 0;
	
	public Att( Processor generator, DataTable tableToStoreAtt, AttRelationshipGraph graph, int outputIdx ) throws SQLException
	{
		this.generator = generator;
		attOrder = nextAttOrder++;
		dataTable = tableToStoreAtt;
        this.outputIdx = outputIdx;
		
		graph.addVertex(this);
		for( Att parent : generator.getInputAtts() )
		{
			graph.addEdge(new GenerationEdge(), generator, this);
		}
		
		this.graph = graph;
	}


	
	public Att(DataTable tableToStoreAtt, AttRelationshipGraph graph) throws SQLException {
		attOrder = nextAttOrder++;
		dataTable = tableToStoreAtt;
		attDbName = dataTable.addAtt(this);
        outputIdx = -1;
		
		graph.addVertex(this);
		
		this.graph = graph;
	}

    //Must be called to find a place in the DB to save the att before we try to read or write rows.
    //Not part of the constructor because we only want to create a place for it if it's not a duplicate.
    public void addToDataTable(){
        attDbName = dataTable.addAtt(this);
    }
	
	
	public AttRelationshipGraph getGraph(){
		return graph;
	}
	

	public String toString()
	{
		try
		{
			if( generator == null )
				return "root";
			
			String name = "";
			
			if(notes != null)
			{
				name += notes+"\n";
			}
			
			
			int numberOfRows = getNotNullRowsInTable();
			
			
			
			
			name += getDbColumnName()+"\n";
			if( isFinalOutputAtt ){
				name += "Final Output Att\n";
			}
			
			if( getName().length() > 40 )
				name+= getName().substring(0,37)+"...\n";
			else
				name += getName()+"\n";
			
			
			name += "Non-Null Rows: "+numberOfRows+"\n";
			
			
			if( numberOfRows > 0 )
			{
				int numberUniqueRows = getUniqueRowsInTable();
				name += "Unique Rows:"+numberUniqueRows+"\n";
				
				String firstRow = StringEscapeUtils.escapeJava(getFirstRow());
				if( firstRow.length() > 200 )
				{
					firstRow = firstRow.substring(0,197)+"...";
				}
				name += "First Row:"+firstRow+"\n";
			}
			
			return name;
		}
		catch(SQLException E)
		{
			E.printStackTrace();
			return "unsavedAttFrom-"+this.generator.getName();
		}
	}	
		
		
		
	public String getName()
	{
		if( isRootAtt() )
			return "root";
		
		String name = generator.getName();
		if(generator.getOutputAtts().size() > 1)
		{
			name += "-"+generator.getOutputAtts().indexOf(this);
		}
		
		name += "(";
		if( generator.getTransformer().getColsIn() > 0)
		{
			for( Att att : generator.getInputAtts() )
			{
				name += att.getName() + ",";
			}
			name = name.substring(0, name.length()-1);
		}
		
		name += ")";
		
		return name;
		

	}
	
	
	public Processor getGenerator() {
		return generator;
	}
	public Set<Relationship> getPredictors() {
		return predictors;
	}
	public Set<Processor> getProcessors() {
		return processors;
	}

	public int getAttOrder(){
		return attOrder;
	}
	public DataTable getDataTable(){
        if( !this.isDuplicate() ) {
            return dataTable;
        }
        else{
            return this.getDuplicateOf().getDataTable();
        }
	}
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public boolean isRootAtt()
	{
		return generator == null;
	}

	
	/*
	public Table<Integer, Att, Datacell> getDataAsTable()
	{
		return convertArrayOfDatacellsToColumnOfTable( getData(), this );
	}
	
	public static Table<Integer, Att, Datacell> convertArrayOfDatacellsToColumnOfTable( ArrayList<Datacell> datacells, Att colKey )
	{
		Table<Integer, Att, Datacell> outtable = TreeBasedTable.create();
		int rowKey = 0;
		for( Datacell datacell : datacells )
		{
			outtable.put(rowKey, colKey, datacell);
			rowKey++;
		}
		return outtable;
	}
	*/
	
	public List<Att> getParentAtts()
	{
		if( generator != null )
		{
			List<Att> inputAtts = generator.getInputAtts();
			return inputAtts;
		}
		else
		{
			return new ArrayList<Att>();
		}
	}
	
	
	public ArrayList<Att> getChildAtts()
	{
		ArrayList<Att> childAtts = new ArrayList<Att>();
		for( Processor processor : processors )
		{
			childAtts.addAll(processor.getOutputAtts());
		}
		
		return childAtts;
	}
	
	public HashSet<Att> getAncestorAtts()
	{
		if( !attAncestorsCached.isEmpty() )
		{
			return attAncestorsCached;
		}
		
		HashSet<Att> attAncestors = new HashSet<>();
		List<Att> parents = getParentAtts( );
		attAncestors.addAll( parents );
		for( Att parent : parents )
		{
			attAncestors.addAll(parent.getAncestorAtts());
		}
		
		attAncestorsCached = attAncestors;
		return attAncestors;
	}
	
	
	public HashSet<Att> getDescendantAtts(  )
	{		
		HashSet<Att> attDescendants = new HashSet<>();
		List<Att> children = getChildAtts( );
		attDescendants.addAll( children );
		for( Att child : children )
		{
			attDescendants.addAll(child.getDescendantAtts());
		}
		
		return attDescendants;
	}
	
	
	
	public void addProcessor( Processor processor)
	{
		processors.add(processor);
	}
	

	public int getUniqueRowsInTable() throws SQLException
	{
		if( uniqueRowsInTable != null){
			return uniqueRowsInTable;
		}
		else{
			return countUniqueRowsInTable();
		}
	}
	
	private int countUniqueRowsInTable() throws SQLException
	{
        if( this.isDuplicate() ) {
            return this.getDuplicateOf().getUniqueRowsInTable();
        }
        else{
            String sql = "SELECT COUNT(DISTINCT " + this.getDbColumnName() + ") FROM " + dataTable.getSubtableJoinClause() + ";";

            ResultSet result = Database.doQuery(sql);

            result.next();
            uniqueRowsInTable = result.getInt(1);
            result.getStatement().close();
            result.close();

            return uniqueRowsInTable;
        }
	}
	
	public int getNotNullRowsInTable() throws SQLException{
		if( notNullRowsInTable != null ){
			return notNullRowsInTable;
		}
		else{
			return countNotNullRowsInTable();
		}
	}

    public List<Integer> getNotNullRowIds() throws SQLException
    {
        if( this.isDuplicate() ){
            return duplicateOf.getNotNullRowIds();
        }

        if( notNullRowIds != null ){
            return notNullRowIds;
        }

        String sql = "SELECT "+dataTable.getTableName()+"_0.id FROM "+ dataTable.getSubtableJoinClause() + " WHERE +" + this.getDbColumnName() + " IS NOT NULL";

        ResultSet result = Database.doQuery(sql);
        notNullRowIds = new ArrayList<>();

        while( result.next() ) {
            int notNullRowId = result.getInt(1);
            notNullRowIds.add(notNullRowId);
        }
        result.getStatement().close();
        result.close();

        //Might as well add the number of not null rows in table while we know it.
        if( notNullRowsInTable == null ){
            notNullRowsInTable = notNullRowIds.size();
        }
        else{
            assert( notNullRowsInTable == notNullRowIds.size() );
        }


        return notNullRowIds;
    }
	
	private int countNotNullRowsInTable() throws SQLException
	{
        if( this.isDuplicate() ){
            return duplicateOf.getNotNullRowsInTable();
        }
        else {
            String sql = "SELECT COUNT(" + dataTable.getTableName() + "_0.id) FROM " + dataTable.getSubtableJoinClause() + " WHERE +" + this.getDbColumnName() + " IS NOT NULL";

            //System.out.println(sql);

            ResultSet result = Database.doQuery(sql);

            result.next();
            int notNullRows = result.getInt(1);
            result.getStatement().close();
            result.close();

            //Cache for next time.
            notNullRowsInTable = notNullRows;

            return notNullRows;
        }
	}
	
	
	public String getFirstRow() throws SQLException {
        return getRow(0);
	}

    /**
     * ZERO-indexed rowNum.
     * @param rowNum
     * @return
     * @throws SQLException
     */
    public String getRow(int rowNum) throws SQLException{
        if( this.isDuplicate() ){
            return this.getDuplicateOf().getRow(rowNum);
        }
        else{
            String sql = "SELECT " + this.getDbColumnName() + " FROM " + dataTable.getSubtableJoinClause() + " LIMIT "+rowNum+",1;";
            ResultSet result = Database.doQuery(sql);

            String row = "NULL";
            if(result.next() && result.getString(1) != null) {
                row = result.getString(1);
            }
            result.getStatement().close();
            result.close();

            return row;
        }
    }

	public ArrayList<String> getData() throws SQLException {
		return getData(0);
	}

	public ArrayList<String> getData(int limit) throws SQLException {
        if( this.isDuplicate() ){
            return this.getDuplicateOf().getData();
        }
        else {
            String sql = "SELECT " + this.getDbColumnName() + " FROM " + dataTable.getSubtableJoinClause() + " WHERE " + this.getDbColumnName() + " IS NOT NULL";
            if( limit > 0 ){
            	sql += " LIMIT "+limit;
			}
            ArrayList<String> out = new ArrayList<>();
            ResultSet result = Database.doQuery(sql);

            //System.out.println(sql);

            while (result.next()) {
                out.add(result.getString(1));
            }

            result.getStatement().close();
            result.close();

            return out;
        }
	}
	
	public String getDbColumnName()
	{
		if( !this.isDuplicate() ) {
            return attDbName;
        }
        else{
            return this.duplicateOf.getDbColumnName();
        }
	}
	
	public String getDbColumnNameNoQuotes()
	{
		return getDbColumnName().replace("`","");
	}



	public boolean existsInDb() {
		return existsInDb;
	}

	public void setExistsInDb(boolean b) {
		existsInDb = b;
	}
	
	
	public void addPredictor( Relationship rel )
	{
		predictors.add(rel);
	}
	
	public Relationship getBestPredictor()
	{
		Relationship bestRel = null;
		double bestAccuracy = -1;
		for( Relationship rel : predictors )
		{
			if( rel.getAccuracy() > bestAccuracy )
			{
				bestRel = rel;
				bestAccuracy = bestRel.getAccuracy();
			}
			else if( rel.getAccuracy() == bestAccuracy )
			{
				//In case of a tie for accuracy, favor the simpler model (model with least ancestors). 
				//Could try different metrics here: eg. shortest chain from root to input atts, etc. 
				if( rel.getAncestorAttsAndProcessors().size() < bestRel.getAncestorAttsAndProcessors().size()  )
				{
					bestRel = rel;
				}
			}
		}
		
		return bestRel;
	}



	@Override
	public Collection<GraphVertex> getAncestorAttsAndProcessors() {
		Collection<GraphVertex> ancestors = new ArrayList<>();
		if( generator != null )
		{
			ancestors.add( generator);
		}
		ancestors.addAll(getAncestorAtts());
		
		
		for( Att att : getAncestorAtts())
		{
			if( att.getGenerator() != null )
			{
				ancestors.add(att.getGenerator());
			}
		}
		
		
		return ancestors;
	}
	
	public String getColumnValuesHash() throws SQLException{
		if( columnValuesHash != null ){
			return columnValuesHash;
		}
		else{
			//GROUP CONCAT truncates at 1024 characters by default. Increased in my.ini file (group_concat_max_len = 16M), but maybe a better solution is needed? Perhaps sha the rows of the columns before concatting them together?
			String sql = "SELECT SHA1(GROUP_CONCAT("+getDbColumnName()+" SEPARATOR '-,-')) FROM "+getDataTable().getSubtableJoinClause()+";";
			
			ResultSet result = Database.doQuery(sql);
			
			result.next();
			String hash = result.getString(1);
			result.getStatement().close();
			result.close();
			
			if( hash != null ){
				columnValuesHash = hash;
				return hash;
			}
			else{
				columnValuesHash = "EMPTY";
				return "EMPTY";
			}
		}
	}
	
	public void setColumnValuesHash( String hash ){
		columnValuesHash = hash;
	}



	public void setFinalOutputAtt(boolean b) {
		isFinalOutputAtt  = b;
	}
	
	public boolean isFinalOutputAtt(){
		return isFinalOutputAtt;
	}

	public boolean isDuplicate() {
		return duplicateOf != null;
	}

    public Att getDuplicateOf(){
        return this.duplicateOf;
    }

    public void setDuplicateOf( Att duplicateOf ){
        if( duplicateOf.isDuplicate() ) { //If we want to set this att as a duplicate of X, where X is a duplicate of Y, then set this att as a duplicate of Y: no need to go via X.
            this.setDuplicateOf(duplicateOf.getDuplicateOf());
        }
        else{
            this.duplicateOf = duplicateOf;
        }


    }

    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Att other = (Att) obj;
        if( other.hashCode() != this.hashCode() )
        	return false;

        return true;
    }

    @Override
    public int hashCode() {
    	if( this.cachedHashCode != 0 ){
    		return this.cachedHashCode;
		}
        final int prime = 43;

        //Roots don't have generators.
        if( this.isRootAtt() ) {
            return super.hashCode();
        }

        int result = generator.hashCode();

        result = prime * result + outputIdx;

        this.cachedHashCode = result;
        return result;
    }

	
}
