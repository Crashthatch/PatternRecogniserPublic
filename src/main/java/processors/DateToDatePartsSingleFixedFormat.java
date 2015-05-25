package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServiceReversible;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToDatePartsSingleFixedFormat extends TransformationServiceReversible {
    private SimpleDateFormat formatter;

    public String getName() {
        return "DateToPartsSFF"+formatter.toPattern();
    }

	public DateToDatePartsSingleFixedFormat(String formatString)
	{
		colsIn = 1;
		colsOut = 3;
		rowsIn = 1;
		rowsOut = 1;
        formatter = new SimpleDateFormat(formatString);
        formatter.setLenient(false);
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );

        try {
            Date date = formatter.parse(stringin);

            //Test to make sure that the date really is in the defined format, and SimpleDateFormat isn't allowing eg. "1-4-2015" for "dd-mm-yyy"
            if( !stringin.equals(formatter.format(date)) ){
                throw new ParseException("stringin.parse().format() date did not look the same as stringin.",0);
            }

            DateFormat year = new SimpleDateFormat("yyyy");
            DateFormat month = new SimpleDateFormat("MM");
            DateFormat day = new SimpleDateFormat("dd");
            outTable.put(0, 0, year.format(date) );
            outTable.put(0, 1, month.format(date) );
            outTable.put(0, 2, day.format(date) );
        }
        catch( ParseException e ){
            //Do nothing. Try next format.
        }
		
		return outTable;
	}

    public TransformationServiceReversible getReverseTransformer() {
        return new DatePartsToDate(formatter.toPattern());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((formatter.toPattern() == null) ? 0 : formatter.toPattern().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DateToDatePartsSingleFixedFormat other = (DateToDatePartsSingleFixedFormat) obj;
        if (formatter.toPattern() == null) {
            if (other.formatter.toPattern() != null)
                return false;
        } else if (!formatter.toPattern().equals(other.formatter.toPattern()))
            return false;
        return true;
    }
}
