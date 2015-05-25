package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;

public class ContainsFilter extends TransformationService {
    private String needle;

    public String getName() {
        return "contains"+ StringEscapeUtils.escapeJava(needle);
    }

	public ContainsFilter(String needle)
	{
        this.needle = needle;
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		if( stringin.contains(needle) )
			outTable.put(0,0,"1");
		//else
		//	outTable.put(0,0,"0");
		
		return outTable;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((needle == null) ? 0 : needle.hashCode());
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
        ContainsFilter other = (ContainsFilter) obj;
        if (needle == null) {
            if (other.needle != null)
                return false;
        } else if (!needle.equals(other.needle))
            return false;
        return true;
    }
}
