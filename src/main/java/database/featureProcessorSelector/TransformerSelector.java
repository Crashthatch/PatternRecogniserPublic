package database.featureProcessorSelector;

import database.Att;
import database.ProcessorSelector;
import database.TransformationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TransformerSelector {
	protected Collection<TransformationService> possibleTransformers = new ArrayList<>();
	
	public TransformerSelector(){
		this.possibleTransformers.addAll( ProcessorSelector.getAllTransformers() );
	}
	
	public TransformerSelector( Collection<TransformationService> transformers ){
		this.possibleTransformers.addAll(transformers);
	}

	//To extend this, we could split the Transformer Selector into a "TransformerFeaturesetCreator" and a "TransformerRanker" model
	// similar to how we currently select what Atts we want to process further.
	// Potential problem: The searchspace is much bigger for the transformers because each TransformationService could have millions of different parameters (eg. HTMLXPathSelector can take any valid Xpath string as a selector).
	abstract public List<TransformationService> getBestProcessorsForAtt(Att att);
}
