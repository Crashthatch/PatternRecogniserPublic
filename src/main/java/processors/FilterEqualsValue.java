package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;

public class FilterEqualsValue extends TransformationService {
    private String value;

    public String getName() {
        return "FilterEquals"+ StringEscapeUtils.escapeJava(value);
    }

	public FilterEqualsValue(String value)
	{
        this.value = value;
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);

		if( stringin.equals(value) )
		{
			outTable.put(0, 0, "1" );
		}
		
		return outTable;
	}

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result = prime * result
                + ((value  == null) ? 0 : value. hashCode());
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
        FilterEqualsValue other = (FilterEqualsValue) obj;
        if (value  == null) {
            if (other.value  != null)
                return false;
        } else if (!value. equals(other.value) )
            return false;
        return true;
    }

}
