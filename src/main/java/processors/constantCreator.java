package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

public class constantCreator extends TransformationService {
	private final String constant;
	
	public String getName()
	{
		return this.getClass().getSimpleName()+"-"+constant;
	}
	
	public constantCreator( String constant )
	{
		this.constant = constant;
		colsIn = 0;
		colsOut = 1;
		rowsIn = 0;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
				
		outTable.put(0, 0, constant);
		
		return outTable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((constant == null) ? 0 : constant.hashCode());
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
		constantCreator other = (constantCreator) obj;
		if (constant == null) {
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
			return false;
		return true;
	}
}
