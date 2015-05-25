package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import org.apache.commons.lang.StringEscapeUtils;

public class Strpos extends TransformationService {
	private final String needle;

	public String getName() {
		return "Strpos"+StringEscapeUtils.escapeJava(needle);
	}

	public Strpos( String needle)
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		this.needle = needle;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		
		int pos = stringin.indexOf(needle);
		outTable.put(0, 0, ""+pos );
		
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
		Strpos other = (Strpos) obj;
		if (needle == null) {
			if (other.needle != null)
				return false;
		} else if (!needle.equals(other.needle))
			return false;
		return true;
	}
}
