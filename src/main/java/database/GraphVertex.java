package database;

import java.util.Collection;

public interface GraphVertex {

	public abstract Collection<GraphVertex> getAncestorAttsAndProcessors();
	
}
