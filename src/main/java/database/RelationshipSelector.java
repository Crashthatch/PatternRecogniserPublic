package database;

import models.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelationshipSelector {

	public static List<Relationship> getBestRelationships( AttRelationshipGraph attGraph )
	{
		//Get all atts from the graph.
		Collection<Att> atts = attGraph.getNonDuplicateAtts();
		Att rootAtt = attGraph.getRootAtt();
		
		
		//Remove atts where the generator of that att failed, or the att somehow else ended up with no rows, or the att only has 1 or 2 rows (We need at least 2 rows to learn a pattern, and a third to test it / estimate performance).
		Collection<Att> attsWithAtLeastThreeRows = new ArrayList<>(); //Improvement opportunity: Choice of 3 somewhat arbitrary. Only leaves us with 2 examples in the training set. Is this really enough to try to spot a pattern?
		Collection<Att> attsWithAtLeastOneRow = new ArrayList<>(); 
		for( Att att : atts )
		{
			try {
				if( att.getNotNullRowsInTable() > 0 )
				{
					attsWithAtLeastOneRow.add(att);
				}
				if( att.getNotNullRowsInTable() >= 3 )
				{
					attsWithAtLeastThreeRows.add(att);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<Relationship> bestRelationships = new ArrayList<>();
		
		for( Att labelAtt : attsWithAtLeastThreeRows )
		{
			bestRelationships.addAll( getAllRelationshipsForLabel(labelAtt) );
			
			//TODO: Use more than one feature att to predict the label.
		}
		
		return bestRelationships;
		
	}

	/**
	 * This should only be called externally for testing (consider it private).
	 * Normally, you should call getBestRelationships which gets the best relationships the relationshipSelector decides, and allows it to "understand" all of the relations between the inner attributes.
	 * Ideally you don't need access to the relationships and atts directly, you just provide examples with missing values to be predicted or checked. 
	 * @param labelAtt
	 * @return
	 */
	public static ArrayList<Relationship> getAllRelationshipsForLabel(Att labelAtt) 
	{
		ArrayList<ModelLearner> learners = new ArrayList<>();
		learners.add( new ZeroLearner() );
		learners.add( new BestColumnLearner() );
		learners.add( new RMLinearRegressionLearner() );
		learners.add( new WekaLinearRegressionLearner() );
		learners.add( new RMKNNLearner() ); 
		//learners.add( new RMKNNNominalLearner() ); //Removed as RM nominal mappings (String -> integer) may not always be the same between training and testing.
		learners.add( new MostCommonValueLearner() );
		
		ArrayList<Relationship> bestRelationships = new ArrayList<>();
		
		//Use no atts to predict the label (eg. for constant labels).
		for( ModelLearner learner : learners )
		{
			bestRelationships.add( new Relationship( learner, new ArrayList<Att>(), labelAtt) );
		}
		
		
		//Only try to predict atts based on other atts in the same table or tables "above" this one.
		//Disallow using atts which don't have multiple rows corresponding to a single row in the labelatt (ie. "lower" tables). The relationships can't predict a single value from a list.
		Collection<Att> inputAttCandidates = labelAtt.getDataTable().getAllAttsInTable(); //atts in the SAME table.
		//TODO: atts in above tables (are these ever useful?)
		
		System.out.println("Found "+inputAttCandidates.size()+" input att candidates for label "+labelAtt.getName());
		System.out.println("Found "+learners.size()+" learners.");
		
		for( Att featureAtt : inputAttCandidates )
		{
			//Disallow using the label to predict itself.
			if( featureAtt == labelAtt )
				continue;
			
			//Disallow using parents of the label as finding a relationship a => processed(a) is equivalent to just applying the processing operator.
			if( labelAtt.getAncestorAtts().contains(featureAtt) )
				continue;
			
			//Disallow using children of the label. Finding a relationship processed(a) => a is equivalent to applying the inverse of process (which we are assuming to exist).
			//if( labelAtt.getDescendantAtts().contains(featureAtt) )
			if( featureAtt.getAncestorAtts().contains(labelAtt))
				continue;
			
			ArrayList<Att> featureAtts = new ArrayList<>();
			featureAtts.add( featureAtt );
			for( ModelLearner learner : learners )
			{
				bestRelationships.add( new Relationship( learner, featureAtts, labelAtt) );
			}
		}
		
		return bestRelationships;
	}
		

}
