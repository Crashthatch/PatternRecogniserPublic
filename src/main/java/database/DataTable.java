package database;

import com.google.common.collect.ArrayListMultimap;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class DataTable {
	private String tableName;
	private DataTable parentTable;
	private int tableOrder;
	private HashMap<Att,Integer> attToTableMapping = new LinkedHashMap<>();
	private int dbTables = 0;
	private int dbRows = 0;
	private HashMap<List<Att>, PreparedStatement> updatePreparedStatements = new HashMap<>();
	private HashMap<List<Att>, PreparedStatement> insertPreparedStatements = new HashMap<>();
	private Set<DataTable> childTables = new HashSet<>(); 
	private Set<DataTable> tableAncestorsCached = null;
	
	
	private static int nextTableOrder = 0;
	private static final int ATTS_IN_FIRST_TABLE = 10;
	private static final int ATTS_IN_SECOND_TABLE = 100;
	private static final int ATTS_IN_FURTHER_TABLES = 1000;
	
	public DataTable()
	{
		tableOrder = nextTableOrder++;
		this.tableName = "t"+tableOrder;
		
	}
	
	public DataTable( DataTable parentTable )
	{
		this.parentTable = parentTable;
		parentTable.addChildTable(this);
		tableOrder = nextTableOrder++;
		this.tableName = "t"+tableOrder;
		
	}
	
	private void createTableInDb( )
	{
		String sql = "CREATE TABLE `"+tableName+"_"+dbTables+"` ( "+
				  "`id` int(11) NOT NULL AUTO_INCREMENT,"+
				  "`ref` int(11) NOT NULL,";
		
		int startingCol = 0;
		int colsToCreate = 10;
		if( dbTables == 0 ){
			colsToCreate = ATTS_IN_FIRST_TABLE;
			startingCol = 0; 
		}
		else if(dbTables == 1 ){
			colsToCreate = ATTS_IN_SECOND_TABLE;
			startingCol = ATTS_IN_FIRST_TABLE;
		}
		else if(dbTables > 1 ){
			colsToCreate = ATTS_IN_FURTHER_TABLES;
			startingCol = ATTS_IN_FIRST_TABLE+ATTS_IN_SECOND_TABLE+ATTS_IN_FURTHER_TABLES*(dbTables-2);
		}
		
		for( int i=1; i <= colsToCreate; i++ )
		{
            //MEDIUMTEXT max length is 16MB. Should be long enough for most purposes.
			sql += "`"+(startingCol+i)+"` MEDIUMTEXT NULL,";
		}
		sql +=    "PRIMARY KEY (`id`),"+
				  "KEY `ref` (`ref`)"+
				  ") ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;";
        //InnoDb causes java.sql.BatchUpdateException: Row size too large. The maximum row size for the used table type, not counting BLOBs, is 8126. You have to change some columns to TEXT or BLOBs.
        //Aria slower than either InnodDb or MyISAM.
        //XtraDb is very slightly faster than InnoDB when running on mariaDb, but replaces InnoDB anyway.
		
		//Create rows in the new subtable. Ref and ID should match the head table.
		String createRowsSql = "INSERT INTO `"+tableName+"_"+dbTables+"` (`ref`) \n"+
				"SELECT `ref` FROM "+tableName+"_0";
		
		try {
			Database.doWriteQuery(sql);
			Database.doWriteQuery(createRowsSql);
			dbTables++;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addChildTable(DataTable child){
		childTables.add(child);
	}
	
	public Set<DataTable> getChildTables(){
		return childTables;
	}
	
	public String getTableName()
	{
		return tableName;
	}
	
	public String toString()
	{
		return tableName;
	}
	
	public DataTable getParentTable() {
		return parentTable;
	}
	
	public Set<DataTable> getSiblingTables() {
		if( parentTable == null ){
			return new HashSet<DataTable>();
		}
		Set<DataTable> siblings = new HashSet<>();
		for( DataTable child : parentTable.getChildTables() ){
			if( child != this ){
				siblings.add(child);
			}
		}
		return siblings;
	}
	
	public Set<DataTable> getOlderSiblingTables() {
		Set<DataTable> olderSiblings = new HashSet<>();
		for( DataTable sibling : getSiblingTables() ){
			if( sibling.getTableOrder() < this.getTableOrder() ){
				olderSiblings.add(sibling);
			}
		}
		return olderSiblings;
	}
	
	public static int getNextTableOrder()
	{
		return nextTableOrder;
	}

	public void setParentTable(DataTable parentTable) {
		this.parentTable = parentTable;
	}

	public String getJoinToParentClause()
	{
		if(parentTable == null)
		{
			return "";
		}
		else
		{
			String sql = "";
			//Join the _0 table to the _0 table of it's parent.
			sql += " INNER JOIN "+getTableName()+"_"+0+" ON "+parentTable.getTableName()+"_0.id = "+getTableName()+"_"+0+".ref\n";
			//Join any other subtables of this table to the _0 table.
			sql += getSubtableJoinClauseWithoutHead();
			return sql;
		
		}
		
	}
	
	public String getSubtableJoinClause() {
		
		String sql = getTableName()+"_0 \n";
		
		sql += getSubtableJoinClauseWithoutHead();
		
		return sql;
	}
	
	public String getSubtableJoinClauseWithoutHead() {
		
		String sql = "";
		
		for( int i=1; i<dbTables; i++ )
		{ 
			sql += " INNER JOIN "+getTableName()+"_"+i+" ON "+getTableName()+"_0.id = "+getTableName()+"_"+i+".id\n";
		}
		return sql;
	}
	
	public void clearPendingUpdates(){
		updatePreparedStatements.clear();
		insertPreparedStatements.clear();
	}
	
	public void save() throws SQLException
	{
		while( ( dbTables == 0 && attToTableMapping.size() > 0 )
				|| (dbTables == 1 && attToTableMapping.size() > ATTS_IN_FIRST_TABLE )
				|| (dbTables >= 2 && attToTableMapping.size() > ATTS_IN_FIRST_TABLE + ATTS_IN_SECOND_TABLE + (dbTables-2) * ATTS_IN_FURTHER_TABLES ) )
		{
			//Create a new subtable for new atts to live in.
			createTableInDb();
		}
		
		for( List<Att> key : updatePreparedStatements.keySet())
		{
			PreparedStatement ps = updatePreparedStatements.get(key);
			Database.getConnection().setAutoCommit(false);
			ps.executeBatch();
			Database.getConnection().setAutoCommit(true);
			ps.close();
		}
		updatePreparedStatements.clear();
		
		for( List<Att> key : insertPreparedStatements.keySet())
		{
			PreparedStatement ps = insertPreparedStatements.get(key);
			Database.getConnection().setAutoCommit(false);
			ps.executeBatch();
			Database.getConnection().setAutoCommit(true);
			ps.close();
		}
		insertPreparedStatements.clear();
	}
	
	public void update( int id, HashMap<Att, String> values ) throws SQLException
	{
		HashSet<Integer> dbTablesThatNeedUpdating = new HashSet<>();
		ArrayListMultimap<Integer, Att> attsToUpdateInDbTable = ArrayListMultimap.create();
		for ( Att att : values.keySet() )
		{
			assert( attToTableMapping.containsKey(att));
			dbTablesThatNeedUpdating.add( attToTableMapping.get(att) );
			attsToUpdateInDbTable.put( attToTableMapping.get(att), att );
		}
		
		
		
		for( int dbTableToUpdate : dbTablesThatNeedUpdating )
		{
			List<Att> attsToUpdateInThisTable = attsToUpdateInDbTable.get(dbTableToUpdate);
			
			PreparedStatement preparedStatement = updatePreparedStatements.get(attsToUpdateInThisTable); 
			
			if( preparedStatement == null )
			{
				String sql = "UPDATE "+tableName+"_"+dbTableToUpdate+" SET ";
				for( Att att : attsToUpdateInThisTable )
				{					
					sql += att.getDbColumnName()+"=?,";
				}
				sql = sql.substring(0, sql.length()-1);
				sql += " WHERE id = ?";
				
				preparedStatement = Database.getConnection().prepareStatement(sql);
				updatePreparedStatements.put(attsToUpdateInThisTable, preparedStatement);
			}
			
			int col = 1;
			for ( Att att : attsToUpdateInThisTable )
			{				
				String value = values.get(att);
				preparedStatement.setString(col, value);
				col++;
			}
			preparedStatement.setInt(col, id); //The "where id=?" clause at the end.
			preparedStatement.addBatch();
		}
		
		
	}
	
	public void insert( int ref, HashMap<Att, String> values ) throws SQLException
	{
		HashSet<Integer> subTablesThatNeedUpdating = new HashSet<>();
		ArrayListMultimap<Integer, Att> attsToUpdateInDbTable = ArrayListMultimap.create();
		for ( Att att : values.keySet() )
		{
			subTablesThatNeedUpdating.add( attToTableMapping.get(att) );
			attsToUpdateInDbTable.put( attToTableMapping.get(att), att );
		}

		
		for( int dbTableToUpdate : subTablesThatNeedUpdating )
		{
			List<Att> attsToUpdateInThisTable = attsToUpdateInDbTable.get(dbTableToUpdate);
			
			PreparedStatement preparedStatement = insertPreparedStatements.get(attsToUpdateInThisTable); 
			
			if( preparedStatement == null )
			{
				//Insert query
				String sql = "INSERT INTO "+tableName+"_"+dbTableToUpdate+" (`ref`,";
				for ( Att att : attsToUpdateInThisTable )
				{	
					sql += ""+att.getDbColumnName()+",";
				}
				sql = sql.substring(0,sql.length()-1);
				sql += ") VALUES (?,";
					
				for ( Att att : attsToUpdateInThisTable )
				{					
					sql += "?,";
				}
				sql = sql.substring(0,sql.length()-1);
				sql += ")";
				
				preparedStatement = Database.getConnection().prepareStatement(sql);
				insertPreparedStatements.put(attsToUpdateInThisTable, preparedStatement);
			}
			
			preparedStatement.setInt(1,ref);
			int col = 2;
			for ( Att att : attsToUpdateInThisTable )
			{				
				String value = values.get(att);
				preparedStatement.setString(col, value);
				col++;
			}
			preparedStatement.addBatch();
		}
		dbRows++;
	}

	public Set<DataTable> getAncestorTables() {
		if( tableAncestorsCached != null ){
			return tableAncestorsCached;
		}
		
		HashSet<DataTable> ancestors = new HashSet<>();
		if( parentTable == null )
			return ancestors;
		
		ancestors.add( parentTable );
		ancestors.addAll(parentTable.getAncestorTables());
		
		tableAncestorsCached = Collections.unmodifiableSet(ancestors);
		return tableAncestorsCached;
	}

	public int getTableOrder() {
		return tableOrder;
	}

	public List<DataTable> getTablesToJoinToReach(DataTable targetTable) 
	{
		ArrayList<DataTable> tablesToJoin = new ArrayList<>();
		
		if( targetTable == this )
		{
			tablesToJoin.add( this );
			return tablesToJoin;
		}
		
		DataTable latestCommonAncestor = DataTableRelationshipGraph.findLatestCommonAncestor(this, targetTable);
		
		if( latestCommonAncestor != targetTable )
		{
			List<DataTable> routeFromLCAToTarget = targetTable.getTablesToJoinToReach(latestCommonAncestor);
			Collections.reverse(routeFromLCAToTarget);
			tablesToJoin.addAll(routeFromLCAToTarget);
		}
		
		if( latestCommonAncestor != this )
		{
			//TODO: Don't need to find latestCommonAncestor after the first time: Just keep going up until latestCommonAncestor is reached.
			tablesToJoin.addAll( getParentTable().getTablesToJoinToReach(latestCommonAncestor) );
			tablesToJoin.add( this );
		}
		

		
		return tablesToJoin;
	}
	
	public List<DataTable> getTablesToJoinToReachAncestor(DataTable targetTable)
	{
		ArrayList<DataTable> tablesOnPath = new ArrayList<>();
		DataTable currentTable = this;
		while( currentTable != targetTable )
		{
			tablesOnPath.add(currentTable);
			currentTable = currentTable.getParentTable();
		}
		tablesOnPath.add(targetTable);
		
		Collections.reverse(tablesOnPath);
		
		return tablesOnPath;
	}
	
	public String addAtt(Att att )
	{
		int inTable = 0;
		if( attToTableMapping.size() < ATTS_IN_FIRST_TABLE ){
			inTable = 0;
		}
		else if( attToTableMapping.size() < ATTS_IN_FIRST_TABLE + ATTS_IN_SECOND_TABLE ){
			inTable = 1;
		}
		else{
			inTable = 2 + (int)Math.floor( ((float)attToTableMapping.size() - ATTS_IN_FIRST_TABLE - ATTS_IN_SECOND_TABLE ) / (float) ATTS_IN_FURTHER_TABLES );
		}
		attToTableMapping.put(att, inTable);
		return "`"+tableName+"_"+inTable+"`.`"+attToTableMapping.size()+"`";
	}

	public int getNumRows() {
		return dbRows;
	}
	
	public List<Att> getAllAttsInTable()
	{
		return new ArrayList<>( attToTableMapping.keySet() );
		//Collections.sort( listOfAtts, new AttOrderComparatorAtts());
		//return listOfAtts;
	}
	
	public static String getSqlStringJoiningInputs(Att rootRowAtt, List<Att> inputAtts){
		String sql = getSqlStringJoiningInputs(rootRowAtt.getDataTable(), inputAtts);
		sql += " AND "+rootRowAtt.getDbColumnName()+" IS NOT NULL \n";
		
		//TODO: Add an order-by-att (not the same as the rootrowatt because we might need to filter using one att (the rootrowatt) but sort by another.
		//sql += " ORDER BY ABS("+rootRowAtt.getDbColumnName()+") ASC";
				
		return sql;
	}

    public static String getSqlStringJoiningInputs(DataTable rootRowTable, Collection<Att> inputAtts){
        return getSqlStringJoiningInputs(rootRowTable, inputAtts, rootRowTable);
    }
	public static String getSqlStringJoiningInputs(DataTable rootRowTable, Collection<Att> inputAtts, DataTable tableForIds)
	{
		String sql = "SELECT "+tableForIds.getTableName()+"_0.id,";
		for( Att inputAtt : inputAtts )
		{
			sql += inputAtt.getDbColumnName()+",";
		}
		sql = sql.substring(0, sql.length()-1);
		
		//Create a list of all tables that contain either an attribute we need for input, or need to filter on (ie. rootRowTable).
		HashSet<DataTable> targetTablesToJoin = new HashSet<>();
		targetTablesToJoin.add(rootRowTable);
		for( Att inputAtt : inputAtts )
		{
			targetTablesToJoin.add( inputAtt.getDataTable() );
		}
		
		//Start the "join" from the latest ancestor of all the atts (both input atts and rootrowatt).
		DataTable latestAncestorOfAllTables = DataTableRelationshipGraph.findLatestCommonAncestor(targetTablesToJoin);
		HashSet<DataTable> tablesAlreadyJoined = new HashSet<>();
		sql += "\n FROM "+latestAncestorOfAllTables.getSubtableJoinClause();
		tablesAlreadyJoined.add(latestAncestorOfAllTables);
		
		
		//Join the tables that contain atts we want (and any other tables in between).
		for( DataTable targetTableToJoin : targetTablesToJoin )
		{
			if( !tablesAlreadyJoined.contains(targetTableToJoin ))
			{
				Collection<DataTable> tablesToJoin = targetTableToJoin.getTablesToJoinToReachAncestor( latestAncestorOfAllTables );
				for( DataTable tableToJoin : tablesToJoin )
				{
					if( !tablesAlreadyJoined.contains(tableToJoin) )
					{
						sql += " "+tableToJoin.getJoinToParentClause()+"\n";
						tablesAlreadyJoined.add(tableToJoin);
					}
				}
			}
		}
		
		sql += " WHERE 1 = 1 \n"; //Pointless where, but makes the concatenation of ANDs simpler below. Hopefully the SQL parser is clever enough that this doesn't slow it down?
		for( Att inputAtt : inputAtts)
		{
			sql += " AND "+inputAtt.getDbColumnName()+" IS NOT NULL \n";
		}
		
		return sql;
	}

    //TODO: Can estimateJoinedSize be improved by using the new Att.getNotNullRowIds?
    public static int estimateJoinedSize(List<Att> atts, DataTable RRT){
        return estimateJoinedSize( atts, RRT, false );
    }

	public static int estimateJoinedSize(List<Att> atts, DataTable RRT, boolean worstCase){
		Set<DataTable> tables = new HashSet<>();
		tables.add(RRT);
		for( Att att : atts ){
			tables.add(att.getDataTable());
		}
		return estimateJoinedSize(tables, atts, worstCase);
	}

    /**
     * If worstCase is true, estimates the "Worst case" (most possible rows) from joining all the tables.
     * Worst case happens when all the child atts in different tables correspond to the same parent row, causing a cross-join.
     * @param tables
     * @return
     */
    //TODO: Add BestCase flag, guaranteed to return the minimum possible number of rows that could be returned?
    // eg. If 2 atts with 4 not-null columns each are in the same table with 10 rows, assume the nulls are disjoint, so only 2 rows will be returned from that table.
	public static int estimateJoinedSize(Collection<DataTable> tables, Collection<Att> atts, boolean worstCase){
		if( tables.size() == 1){
            //Find the att that is being selected from in this table that has the least number of rows.
            DataTable thisTable = tables.iterator().next();
			int minSize = thisTable.getNumRows();
            for( Att att : atts ){
                try {
                    if( att.getDataTable().equals(thisTable) && att.getNotNullRowsInTable() < minSize ){
                        minSize = att.getNotNullRowsInTable();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return minSize;
		}
		
		DataTable latestCommonAncestor = DataTableRelationshipGraph.findLatestCommonAncestor(tables);
		
		ArrayList<Integer> childSizes = new ArrayList<>();
		for( DataTable childTable : latestCommonAncestor.getChildTables() ){
			//Find all the tables in our original list that descend from this child table.
			ArrayList<DataTable> grandchildren = new ArrayList<>();
			for( DataTable grandchildTable : tables ){
				if( grandchildTable.getAncestorTables().contains(childTable) || grandchildTable.equals( childTable ) ){
					grandchildren.add(grandchildTable);
				}
			}
			if( grandchildren.size() > 0 ){
				childSizes.add( estimateJoinedSize(grandchildren, atts, worstCase));
			}
		}

        if( worstCase ){
            int joinedSize = 1;
            for(int childSize : childSizes){
                joinedSize *= childSize; //Assume we're doing a cross-join between all children.
            }
            return joinedSize;
        }
        else {
            //Find the number of unique rows of the Latest Common Ancestor that could actually be returned.
            int LCASize = latestCommonAncestor.getNumRows();
            for( Att att : atts ){
                try {
                    if( att.getDataTable().equals(latestCommonAncestor) && att.getNotNullRowsInTable() < LCASize ){
                        LCASize = att.getNotNullRowsInTable();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            double joinedSize = 1;
            for (int childSize : childSizes) {
                joinedSize *= ((double) childSize / (double) latestCommonAncestor.getNumRows()); //# child rows per row of LCA. (child rows split between ALL rows of LCA, so use LCA.getNumRows, not LCASize here).
            }
            joinedSize = joinedSize * LCASize; //Multiply up by the number of rows from the LCA that are actually going to be returned.
            return (int)Math.round(joinedSize);
        }
		

	}


	/* Removed because inputAtts need a rootRowAtt so we can use filtered-rows of an att, and because joining on id is wrong (it shouldn't matter what order rows were added to the DB).
	public static String getSqlStringJoiningInputsOutputs(List<Att> inputAtts, Att toBePredicted) {
		
		String sql = "SELECT "+toBePredicted.getDbColumnName()+",";
		for( Att inputAtt : inputAtts )
		{
			sql += inputAtt.getDbColumnName()+",";
		}
		sql = sql.substring(0, sql.length()-1);
		
		//Create a list of all tables that contain either an attribute we need for input, or need to filter on (ie. rootRowTable).
		HashSet<DataTable> targetTablesToJoin = new HashSet<>();
		for( Att inputAtt : inputAtts )
		{
			targetTablesToJoin.add( inputAtt.getDataTable() );
		}
		
		//Start the "join" from the latest ancestor of all the atts (both input atts and rootrowatt).
		DataTable latestAncestorOfAllTables = DataTableRelationshipGraph.findLatestCommonAncestor(targetTablesToJoin);
		HashSet<DataTable> tablesAlreadyJoined = new HashSet<>();
		sql += "\n FROM "+latestAncestorOfAllTables.getSubtableJoinClause();
		tablesAlreadyJoined.add(latestAncestorOfAllTables);
		
		//Join the tables that contain atts we want (and any other tables in between).
		for( DataTable targetTableToJoin : targetTablesToJoin )
		{
			if( !tablesAlreadyJoined.contains(targetTableToJoin ))
			{
				Collection<DataTable> tablesToJoin = targetTableToJoin.getTablesToJoinToReachAncestor( latestAncestorOfAllTables );
				for( DataTable tableToJoin : tablesToJoin )
				{
					if( !tablesAlreadyJoined.contains(tableToJoin) )
					{
						sql += " "+tableToJoin.getJoinToParentClause()+"\n";
						tablesAlreadyJoined.add(tableToJoin);
					}
				}
			}
		}
		
		//Join the output att from the other tree.
		//TODO: Perhaps allow joining based on another column than ID? Ideally the 'order' of the rows in the db shouldn't make a difference.
		sql += "INNER JOIN "+toBePredicted.getDataTable().getTableName()+"_0 ON "+toBePredicted.getDataTable().getTableName()+"_0.id = "+inputAtts.get(0).getDataTable().getTableName()+"_0.id \n";
		sql += toBePredicted.getDataTable().getSubtableJoinClauseWithoutHead();
		
		
		sql += " WHERE "+toBePredicted.getDbColumnName()+" IS NOT NULL \n"; 
		for( Att inputAtt : inputAtts)
		{
			sql += " AND "+inputAtt.getDbColumnName()+" IS NOT NULL \n";
		}
		
		return sql;
	}*/


}
