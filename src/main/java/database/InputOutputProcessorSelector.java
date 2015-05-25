package database;

import java.util.Collection;
import java.util.List;

public abstract class InputOutputProcessorSelector{

	abstract public List<Processor> getBestProcessors( AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph, Collection<Relationship> relationships);
}
