package database;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class InputOutputRelationshipSelector {

	public abstract List<Relationship> getBestRelationships( AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph, Collection<Relationship> existingGoodRelationships, HashSet<Relationship> attemptedToLearnRelationships, String runId, int processingRound );
}
