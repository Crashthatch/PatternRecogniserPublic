package database;

public class AttributeNotInTestSetException extends Exception {

	public AttributeNotInTestSetException(Att att) {
		super(att.getName());
	}
}
