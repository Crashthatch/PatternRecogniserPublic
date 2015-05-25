package database;


public abstract class TransformationServiceReversible extends TransformationService
{		
	//TODO: Should we have reversed versions of "checkInputDimensions" / "checkOutputDimensions", or at least check them when reversing?
	
	//public abstract Table<Integer, Integer, String> reverseWork(Table<Integer, Integer, String> inputs);
	
	public abstract TransformationService getReverseTransformer();
}
