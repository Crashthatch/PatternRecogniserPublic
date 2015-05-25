package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationServiceReversible;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePartsToDate extends TransformationServiceReversible {
    private SimpleDateFormat formatter;

    public String getName() {
        return "DatePartsToDate" + formatter.toPattern();
    }

	public DatePartsToDate(String formatString)
	{
		colsIn = 3;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
        formatter = new SimpleDateFormat(formatString);
        formatter.setLenient(false);
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		int yearIn = Integer.parseInt(input.get(0,0));
        int monthIn = Integer.parseInt(input.get(0,1));
        int dayIn = Integer.parseInt(input.get(0,2));

        Calendar cal = Calendar.getInstance();
        cal.set(yearIn, monthIn-1, dayIn, 0, 0, 0);
        Date dt = cal.getTime();

        outTable.put(0, 0, formatter.format(dt) );
		
		return outTable;
	}

    public TransformationServiceReversible getReverseTransformer() {
        return new DateToDatePartsSingleFixedFormat(formatter.toPattern());
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
        DatePartsToDate other = (DatePartsToDate) obj;
        if (formatter.toPattern() == null) {
            if (other.formatter.toPattern() != null)
                return false;
        } else if (!formatter.toPattern().equals(other.formatter.toPattern()))
            return false;
        return true;
    }
}
