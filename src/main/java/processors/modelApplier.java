package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.Relationship;
import database.TransformationService;

import java.util.List;

public class modelApplier extends TransformationService {
	private final Relationship relationship;

	public String getExtendedName()
	{
		return this.getClass().getSimpleName()+"-"+relationship.getName()+"\nOriginal Label:"+relationship.getLabel().getDbColumnNameNoQuotes();
	}
    public String getName(){
        return this.getClass().getSimpleName()+"-"+relationship.getName();
    }

    public Relationship getRelationship(){
        return this.relationship;
    }
	
	public modelApplier( Relationship relationship )
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
		
		this.relationship = relationship;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		
		
		List<String> predictions = relationship.makePredictions(input);
		
		if( predictions.size() == 1){
			outTable.put(0,0, predictions.get(0));
		}

		return outTable;
	}
	
	//TODO: Check modelAppliers are correctly identified as the "same" or "different" in a variety of situations.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
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
		modelApplier other = (modelApplier) obj;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		return true;
	}
}
