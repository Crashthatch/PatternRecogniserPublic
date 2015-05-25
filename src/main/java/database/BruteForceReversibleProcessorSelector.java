package database;

import database.featureProcessorSelector.transformerSelector.GuessedTypeTransformerSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteForceReversibleProcessorSelector extends BruteForceProcessorSelector{


	public List<Processor> getBestProcessors( AttRelationshipGraph attGraph )
	{
		ArrayList<TransformationService> reversibleTransformers = new ArrayList<>();
		ArrayList<TransformationService> transformers = getAllTransformers();


		for( TransformationService transformer : transformers ){
			if( transformer instanceof TransformationServiceReversible && !(transformer instanceof  TransformationServicePartialRowsReversible )){
				reversibleTransformers.add((TransformationService) transformer);
			}
		}

        List<Processor> bruteForcedProcessors = getBestProcessors(attGraph, reversibleTransformers);

        //Try to add splitters based on delimiters that are in the att's values.
        for( Att att : attGraph.getNonDuplicateAtts() ){
            if( att.isRootAtt() ){
                continue;
            }

            GuessedTypeTransformerSelector guessedTypeTransformerSelector = new GuessedTypeTransformerSelector();
            List<TransformationService> attSpecificTransformers = guessedTypeTransformerSelector.getBestProcessorsForAtt(att);
            for( TransformationService transformer : attSpecificTransformers ){
                if( transformer instanceof TransformationServiceReversible && !(transformer instanceof  TransformationServicePartialRowsReversible )){
                    bruteForcedProcessors.add(new Processor(transformer, Arrays.asList(att), att.getDataTable()));
                }
            }
        }

        return bruteForcedProcessors;
	}	

}