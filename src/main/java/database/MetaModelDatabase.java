package database;

import database.features.AttFeatureSet;
import database.features.RelationshipFeatureSet;
import database.features.UnknownPropertyTypeException;

import java.sql.*;

public class MetaModelDatabase {
	private static Connection conn;

    public static void connect(String dbName) throws SQLException
    {
        if( conn == null || conn.isClosed() ) {
            reconnect(dbName);
        }

        //If the metamodel DB table doesn't yet exist, create it.
        try {
            String SQL = AttFeatureSet.getCreateAttributeTableSql();

            MetaModelDatabase.doWriteQuery(SQL);
        }
        catch( SQLException | UnknownPropertyTypeException e ){
            e.printStackTrace();
        }

        //If the metamodel DB relationships table doesn't yet exist, create it.
        try {
            String SQL = RelationshipFeatureSet.getCreateTableSql();

            MetaModelDatabase.doWriteQuery(SQL);
        }
        catch( SQLException | UnknownPropertyTypeException e ){
            e.printStackTrace();
        }
    }
	
	public static void reconnect(String dbName) throws SQLException
	{
        if( conn != null && !conn.isClosed() ) {
            conn.close();
        }
		conn = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName+"?user=patternrec&password=patternRecPassword&useServerPrepStmts=false&rewriteBatchedStatements=true&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8mb4_unicode_ci");
	}

    public static void disconnect() throws SQLException
    {
        if( conn != null && !conn.isClosed() ) {
            conn.close();
        }
    }
	
	public static ResultSet doQuery( String SQL ) throws SQLException
	{
		//System.out.println(SQL);
		ResultSet rs = null;
		Statement stmt = conn.createStatement();
		rs = stmt.executeQuery( SQL );
		
		return rs;
	}
	
	public static boolean doWriteQuery( String SQL ) throws SQLException
	{	
		//System.out.println(SQL);
		Statement stmt = conn.createStatement();
		boolean rs = stmt.execute( SQL );
		stmt.close();
		
		return rs;
	}
	
	public static Connection getConnection()
	{
        return conn;
	}
	
	public static int getLastGeneratedKey() throws SQLException
	{
	    ResultSet rs = doQuery("SELECT LAST_INSERT_ID()");

	    if (rs.next()) {
	        return rs.getInt(1);
	    } else {
	    	throw new SQLException( "No Last Insert ID returned." );
	    }
	}
	
}
