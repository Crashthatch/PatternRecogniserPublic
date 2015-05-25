package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class RegexReplacer extends TransformationService {
	private String regex;
	private String replacement;


	public RegexReplacer( String regex, String replacement)
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		this.regex = regex;
		this.replacement = replacement;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);
		
		String stringout = stringin.replaceAll(regex, replacement);
		
		outTable.put(0, 0, stringout);
		
		return outTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((regex == null) ? 0 : regex.hashCode());
		result = prime * result
				+ ((replacement == null) ? 0 : replacement.hashCode());
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
		RegexReplacer other = (RegexReplacer) obj;
		if (regex == null) {
			if (other.regex != null)
				return false;
		} else if (!regex.equals(other.regex))
			return false;
		if (replacement == null) {
			if (other.replacement != null)
				return false;
		} else if (!replacement.equals(other.replacement))
			return false;
		return true;
	}
}
