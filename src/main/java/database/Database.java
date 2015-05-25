package database;

import java.sql.*;

public class Database {
	private static Connection conn;
	
	public static void reconnect(String dbName) throws SQLException
	{
        if( conn != null && !conn.isClosed() ) {
            conn.close();
        }
		conn = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName+"?user=patternrec&password=patternRecPassword&useServerPrepStmts=false&rewriteBatchedStatements=true&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8mb4_general_ci");

		/*ResultSet result = doQuery("SHOW VARIABLES WHERE Variable_name LIKE 'character\\_set\\_%' OR Variable_name LIKE 'collation%';");
		System.out.println();
		while( result.next() ){
			System.out.println( result.getString(1)+": "+result.getString(2));
		}*/
	}

    public static void disconnect() throws SQLException
    {
        conn.close();
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
