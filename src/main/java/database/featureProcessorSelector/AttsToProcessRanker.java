package database.featureProcessorSelector;

import database.features.AttFeatureSet;

public abstract class AttsToProcessRanker{
	
	abstract public double scoreAtt(AttFeatureSet features);

}
