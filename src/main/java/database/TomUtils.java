package database;

import com.google.common.collect.Table;

public class TomUtils {
	
	public static void printTable( Table table )
	{
		for (final Object columnKey : table.columnKeySet()) {
			  System.out.println(table.column(columnKey).values());
			}
	}
}
