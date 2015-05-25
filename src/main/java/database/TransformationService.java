package database;


import com.google.common.collect.Table;

public abstract class TransformationService
{	
	
	protected int colsIn;
	protected int colsOut;
	protected int rowsIn;
	protected int rowsOut;
    protected boolean annotateExistingRows = false; //Default to false.

    public String getExtendedName() { return getName(); }
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public int getColsIn() {
		return colsIn;
	}

	public int getColsOut() {
		return colsOut;
	}

	public int getRowsIn() {
		return rowsIn;
	}

	public int getRowsOut() {
		return rowsOut;
	}

    public boolean getAnnotateExistingRows() {
        return annotateExistingRows;
    }
	
	public boolean checkInputDimensions(Table<Integer, Integer, String> input) throws IncorrectInputRowsException, IncorrectInputColumnsException{
		
		if (colsIn == Integer.MAX_VALUE || colsIn == 0 || colsIn == input.columnKeySet().size() || (input.rowKeySet().size() == 0 && (rowsIn == 0 || rowsIn == Integer.MAX_VALUE) ) ) //Don't fail on columns if it has 0 rows (the table might actually be [0 rows]x[correct num of columns] but that is still empty and appears as 0x0.		
		{
			if( rowsIn == input.rowKeySet().size() || rowsIn == Integer.MAX_VALUE || rowsIn == 0 || (input.columnKeySet().size() == 0 && (colsIn == 0 || colsIn == Integer.MAX_VALUE) ) )
			{
                return true;
			}
			else
			{
				throw new IncorrectInputRowsException( this.getClass()+" Expected an input of size "+rowsIn+"x"+colsIn+", but got "+input.rowKeySet().size()+"x"+input.columnKeySet().size()+"." );
			}
		}
		else
		{
			throw new IncorrectInputColumnsException( this.getClass()+" Expected an input of size "+rowsIn+"x"+colsIn+", but got "+input.rowKeySet().size()+"x"+input.columnKeySet().size()+"." );			
		}
	}
	
	public boolean checkOutputDimensions(Table<Integer, Integer, String> input, Table<Integer, Integer, String> output) throws IncorrectOutputDimensionsException{
		if( (rowsOut == output.rowKeySet().size() || rowsOut == Integer.MAX_VALUE ) &&
			(colsOut == Integer.MAX_VALUE || colsOut == output.columnKeySet().size() ) )
		{
            //In order to annotate existing rows, the output size must be exactly equal to the input size.
            if( annotateExistingRows ){
                if(input.rowKeySet().size() == output.rowKeySet().size()){
                    return true;
                }
                else{
                    throw new IncorrectOutputDimensionsException(this.getClass()+" should be able to annotate rows, but the number of input rows ("+input.rowKeySet().size()+") did not match the output rows ("+output.rowKeySet().size()+")");
                }
            }
            else {
                return true;
            }
		}
		else
		{
			throw new IncorrectOutputDimensionsException( this.getClass()+" Expected an output of size "+rowsOut+"x"+colsOut+", but got "+output.rowKeySet().size()+"x"+output.columnKeySet().size()+"." );
		}
	}

	//TODO: Make doWork private and expose a doWorkChecked method instead which checks the input / output size so inputs/outputs are always tested and transformers with the wrong numbers don't get through unit testing.
	public abstract Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> inputs) throws UnsuitableTransformerException;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return true;
	}
	
	public int hashCode() {
		final int prime = 47;
		int result = 1;
		result = prime * result
				+ ((getClass() == null) ? 0 : getClass().hashCode());
		return result;
	}
}
