package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import com.joestelmach.natty.CalendarSource;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import database.TransformationService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateToDateParts extends TransformationService {

	
	public DateToDateParts()
	{
		colsIn = 1;
		colsOut = 9;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );

		try {
			Date reference = DateFormat.getDateInstance(DateFormat.SHORT).parse("01/01/2015");
			CalendarSource.setBaseDate(reference);
		}
		catch(ParseException e){
			e.printStackTrace();
		}

		Parser p = new Parser();
		List<DateGroup> stringParts = p.parse(stringin);
		if( stringParts.size() > 0 ){
			DateGroup dateGrp = stringParts.get(0);
			List<Date> dates = dateGrp.getDates();
			Date date = dates.get(0);
	
			DateFormat year = new SimpleDateFormat("yyyy");
			DateFormat month = new SimpleDateFormat("MM");
			DateFormat day = new SimpleDateFormat("dd");
			DateFormat hour = new SimpleDateFormat("HH");
			DateFormat minute = new SimpleDateFormat("mm");
			DateFormat second = new SimpleDateFormat("ss");
			DateFormat millisecond = new SimpleDateFormat("S");
            DateFormat dayOfWeek = new SimpleDateFormat("EEE");
            DateFormat longDayOfWeek = new SimpleDateFormat("EEEE");
			
			outTable.put(0, 0, year.format(date) );
			outTable.put(0, 1, month.format(date) );
			outTable.put(0, 2, day.format(date) );
			outTable.put(0, 3, hour.format(date) );
			outTable.put(0, 4, minute.format(date) );
			outTable.put(0, 5, second.format(date) );
			outTable.put(0, 6, millisecond.format(date) );
            outTable.put(0, 7, dayOfWeek.format(date) );
            outTable.put(0, 8, longDayOfWeek.format(date) );
		}
		
		//System.out.println("Date to Date Formatter outputting:");
		//System.out.println(outTable);
		
		return outTable;
	}
}
