package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;

public class StartsWithFilter extends TransformationService {
    private String prefix;

    public String getName() {
        return "startsWith"+ StringEscapeUtils.escapeJava(prefix);
    }

	public StartsWithFilter(String prefix)
	{
        this.prefix = prefix;
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		if( stringin.startsWith(prefix) )
			outTable.put(0,0,"1");
		//else
		//	outTable.put(0,0,"0");
		
		return outTable;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
        StartsWithFilter other = (StartsWithFilter) obj;
        if (prefix == null) {
            if (other.prefix != null)
                return false;
        } else if (!prefix.equals(other.prefix))
            return false;
        return true;
    }
}
