package database.featureProcessorSelector.attsToProcessRanker;

import database.featureProcessorSelector.AttsToProcessRanker;
import database.features.AttFeatureSet;

public class AttInterestingnessRanker extends AttsToProcessRanker{
	
	public double scoreAtt( AttFeatureSet features )
	{
		double interesting = 0;
		
		//Root Att.
		if( features.getNumAncestorAtts() == 1 ){ 
			interesting += 100;
		}
		
		
		//Att is interesting if it has more rows.
		if( features.getNotNullRowsInTable() > 1 )
		{
			interesting += 1;
		}
		if( features.getNotNullRowsInTable() >= 10 )
		{
			interesting += 0.5;
		}
		
		//Not constant
		if( features.getUniqueRowsInTable() > 1 )
		{
			interesting += 2;
		}
	
		//Higher level is more interesting (generally). ie. everything else being equal, favour a breadth-first search of the tree over depth-first.
		interesting -= 0.2*features.getNumAncestorAtts();
		
		String guessedType = features.getGuessedType(); 
		if( guessedType.equals("HTML") || guessedType.equals("JSON") || guessedType.equals("CSV") || guessedType.equals("date") ){
			interesting += 2;
		}
		else if( guessedType.equals("word") || guessedType.equals("integer") || guessedType.equals("URL") ){
			interesting += 1;
		}
        //Interesting if it looks like a filter-type att.
        else if(guessedType.equals("boolean") && features.getNotNullRowsInTable() < features.getNumRowsInTable() ){
            interesting += 3;
        }

        //Interesting if has previously been used in a relationship.
        if( features.getInputToRelationships() > 1 || features.getRRAToRelationships() > 1 ){
            interesting += 2;
        }
        //Interesting if an att in this datatable has previously been used in a relationship.
        if( features.getDatatableAttsUsedInRelationships() > 1 || features.getDatatableAttsUsedAsRRAsInRelationships() > 1 ){
            interesting += 1;
        }
		//Interesting if this is an att created by a modelApplier.
		if( features.getCreatedByModelApplier() ){
			interesting += 2;
		}

		
		//Not very interesting if it's a constant attribute from a constantCreator.
		if( features.generatedByConstantCreator() )
		{
			interesting -= 2;
		}
		
		//Not very interesting if the att has been identified as an "almost duplicate" (duplicate values, but with different ancestors).
		if( features.isAlmostDuplicate() ){
			interesting -= 3;
		}
		
		//Interesting if the att's values contain some of the constants we want to predict in the outputGraph.
		interesting += features.getBestAimForScore();
		
		return interesting;
	}
}
