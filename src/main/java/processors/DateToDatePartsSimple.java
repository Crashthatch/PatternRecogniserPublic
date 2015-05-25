package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateToDatePartsSimple extends TransformationService {


	public DateToDatePartsSimple()
	{
		colsIn = 1;
		colsOut = 3;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );

        //TODO: Probably the text format should be a parameter, and we should have a processor for each of these. Would be reversible then too.
		List<SimpleDateFormat> possibleFormats = new ArrayList<>();
        possibleFormats.add( new SimpleDateFormat("yyyy-MM-dd"));
        possibleFormats.add( new SimpleDateFormat("MM-dd-yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd-MM-yyyy"));
        possibleFormats.add( new SimpleDateFormat("MM/dd/yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd/MM/yyyy"));
        possibleFormats.add( new SimpleDateFormat("MM.dd.yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd.MM.yyyy"));
        possibleFormats.add( new SimpleDateFormat("yyyyMMdd"));

        possibleFormats.add( new SimpleDateFormat("yyyy-M-d"));
        possibleFormats.add( new SimpleDateFormat("M-d-yyyy"));
        possibleFormats.add( new SimpleDateFormat("d-M-yyyy"));
        possibleFormats.add( new SimpleDateFormat("M/d/yyyy"));
        possibleFormats.add( new SimpleDateFormat("d/M/yyyy"));
        possibleFormats.add( new SimpleDateFormat("M.d.yyyy"));
        possibleFormats.add( new SimpleDateFormat("d.M.yyyy"));
        possibleFormats.add( new SimpleDateFormat("yyyyMd"));

        possibleFormats.add( new SimpleDateFormat("MM-dd-yy"));
        possibleFormats.add( new SimpleDateFormat("dd-MM-yy"));
        possibleFormats.add( new SimpleDateFormat("MM/dd/yy"));
        possibleFormats.add( new SimpleDateFormat("dd/MM/yy"));
        possibleFormats.add( new SimpleDateFormat("MM.dd.yy"));
        possibleFormats.add( new SimpleDateFormat("dd.MM.yy"));

        possibleFormats.add( new SimpleDateFormat("M-d-yy"));
        possibleFormats.add( new SimpleDateFormat("d-M-yy"));
        possibleFormats.add( new SimpleDateFormat("M/d/yy"));
        possibleFormats.add( new SimpleDateFormat("d/M/yy"));
        possibleFormats.add( new SimpleDateFormat("M.d.yy"));
        possibleFormats.add( new SimpleDateFormat("d.M.yy"));

        possibleFormats.add( new SimpleDateFormat("MMM d yy"));
        possibleFormats.add( new SimpleDateFormat("MMM d yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd yy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd yyyy"));

        possibleFormats.add( new SimpleDateFormat("MMM d'th' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM d'th' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'th' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'th' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'th' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'th' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'th' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'th' yyyy"));

        possibleFormats.add( new SimpleDateFormat("MMM d'rd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM d'rd' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'rd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'rd' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'rd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'rd' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'rd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'rd' yyyy"));

        possibleFormats.add( new SimpleDateFormat("MMM d'st' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM d'st' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'st' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'st' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'st' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'st' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'st' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'st' yyyy"));

        possibleFormats.add( new SimpleDateFormat("MMM d'nd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM d'nd' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'nd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMM dd'nd' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'nd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM d'nd' yyyy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'nd' yy"));
        possibleFormats.add( new SimpleDateFormat("MMMMM dd'nd' yyyy"));

        possibleFormats.add( new SimpleDateFormat("d MMM yy"));
        possibleFormats.add( new SimpleDateFormat("d MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd MMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("d MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("d MMMMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd MMMMM yyyy"));

        possibleFormats.add( new SimpleDateFormat("d'th' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'th' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'th' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'th' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("d'th' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'th' MMMMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'th' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'th' MMMMM yyyy"));

        possibleFormats.add( new SimpleDateFormat("d'rd' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'rd' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'rd' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'rd' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("d'rd' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'rd' MMMMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'rd' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'rd' MMMMM yyyy"));

        possibleFormats.add( new SimpleDateFormat("d'st' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'st' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'st' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'st' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("d'st' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'st' MMMMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'st' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'st' MMMMM yyyy"));

        possibleFormats.add( new SimpleDateFormat("d'nd' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'nd' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'nd' MMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'nd' MMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("d'nd' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("d'nd' MMMMM yyyy"));
        possibleFormats.add( new SimpleDateFormat("dd'nd' MMMMM yy"));
        possibleFormats.add( new SimpleDateFormat("dd'nd' MMMMM yyyy"));


		for( SimpleDateFormat dateFormat : possibleFormats){
            try {
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(stringin);

                DateFormat year = new SimpleDateFormat("yyyy");
                DateFormat month = new SimpleDateFormat("MM");
                DateFormat day = new SimpleDateFormat("dd");
                outTable.put(0, 0, year.format(date) );
                outTable.put(0, 1, month.format(date) );
                outTable.put(0, 2, day.format(date) );

                //Found a matching pattern! Stop looking.
                break;
            }
            catch( ParseException e ){
                //Do nothing. Try next format.
            }
        }

		//System.out.println("Date to Date Simple Formatter outputting:");
		//System.out.println(outTable);
		
		return outTable;
	}
}
