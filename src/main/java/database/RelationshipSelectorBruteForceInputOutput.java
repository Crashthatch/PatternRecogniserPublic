package database;

import models.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelationshipSelectorBruteForceInputOutput {

	public static List<Relationship> getBestRelationships( AttRelationshipGraph inputGraph, AttRelationshipGraph outputGraph )
	{
		//Get all atts from the graph.
		Collection<Att> atts = inputGraph.getNonDuplicateAtts();
		Collection<Att> labelAtts = outputGraph.getNonDuplicateAtts();
		
		ArrayList<ModelLearner> learners = new ArrayList<>();
		learners.add( new ZeroLearner() );
		learners.add( new BestColumnLearner() );
		learners.add( new RMLinearRegressionLearner() );
		learners.add( new WekaLinearRegressionLearner() );
		learners.add( new RMKNNLearner() ); 
		//learners.add( new RMKNNNominalLearner() ); //Removed as RM nominal mappings (String -> integer) may not always be the same between training and testing.
		learners.add( new MostCommonValueLearner() );

		
		ArrayList<Relationship> bestRelationships = new ArrayList<>();
		
		
		for( Att labelAtt : labelAtts )
		{
			try{
				if( labelAtt.getNotNullRowsInTable() < 3)
					continue;
			}
			catch(SQLException e){
				e.printStackTrace();
				continue;
			}			
			
			
			
			
			for( Att rootRowAtt : atts ){
			{
				
				try {
					if( rootRowAtt.getNotNullRowsInTable() != labelAtt.getNotNullRowsInTable() ){
						continue;
					}
					
				} catch (SQLException e1) {
					continue;
				}
				
				
				//Use no atts to predict the label (eg. for constant labels).
				for( ModelLearner learner : learners )
				{
					bestRelationships.add( new Relationship( learner, new ArrayList<Att>(), labelAtt, rootRowAtt ) );
				}
				
				//Use each feature att to predict the label.
				for( Att featureAtt : atts )
					if( rootRowAtt.equals(featureAtt) 
							|| (rootRowAtt.getDataTable().equals(featureAtt.getDataTable()) ) //If in the same datatable, then must order the rows or filter differently to rootRowAtts already added.
							|| rootRowAtt.getDataTable().getAncestorTables().contains( featureAtt.getDataTable() )){ //The only reason to choose a RRA from a lower table is to change the number of rows that get predicted (ie. to filter or duplicate rows). 
						ArrayList<Att> featureAtts = new ArrayList<>();
						featureAtts.add( featureAtt );
						for( ModelLearner learner : learners )
						{
							bestRelationships.add( new Relationship( learner, featureAtts, labelAtt, rootRowAtt) );
						}
						
						//TODO: Use more than one feature att to predict the label.
					}
				}
			}
		}
		
		return bestRelationships;
		
	}
}
