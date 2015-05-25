package database.featureProcessorSelector.attsToProcessRanker;

import database.featureProcessorSelector.AttsToProcessRanker;
import database.features.AttFeatureSet;

public class BigMLAttRanker extends AttsToProcessRanker {
	
	/*public double scoreAtt(FeatureSet features) {
		if( shouldProcessAttribute(features.getNumRowsInTable(), features.getUniqueRowsInTable(), features.getDepth(), features.getGuessedType(),
				(features.isAlmostDuplicate()?"1":"0"), features.getBestAimForScore(), features.getTotalLength(), features.getNullRowsInTable() ) ){
			return 1.0;
		}
		else{
			return 0.0;
		}
	}*/

	/**
	*  Predictor for usedToMakeFinalPrediction from model/536b9715c8db63179200747b
	*  Predictive model by BigML - Machine Learning Made Easy
	*/
    public double scoreAtt(AttFeatureSet inputData) {
    Integer numRows = (Integer) inputData.get("numRows");
    Integer uniqueRows = (Integer) inputData.get("uniqueRows");
    Integer depth = (Integer) inputData.get("depth");
    String guessedType = (String) inputData.get("guessedType");
    String almostDuplicate = (String) inputData.get("almostDuplicate");
    Double coversOutputAttScore = (Double) inputData.get("coversOutputAttScore");
    Integer lengthFirstRow = (Integer) inputData.get("lengthFirstRow");
    Integer lengthTotal = (Integer) inputData.get("lengthTotal");
    Double lengthAverageRow = (Double) inputData.get("lengthAverageRow");
    Double bestPercentageOfTargetAttCovered = (Double) inputData.get("bestPercentageOfTargetAttCovered");
    Integer bestTargetAttTotalLength = (Integer) inputData.get("bestTargetAttTotalLength");
    Integer bestTargetAttRowsCovered = (Integer) inputData.get("bestTargetAttRowsCovered");
    
    if( depth == 3){ //Always put root-att top.
    	return 2.0;
    }
    
        if (depth == null) {
            return 0.0;
        }
        else if (depth > 6) {
            if (bestPercentageOfTargetAttCovered == null) {
                return 0.0;
            }
            else if (bestPercentageOfTargetAttCovered > 0.94964) {
                if (coversOutputAttScore == null) {
                    return 1.0;
                }
                else if (coversOutputAttScore > 0.14666) {
                    if (bestTargetAttRowsCovered == null) {
                        return 1.0;
                    }
                    else if (bestTargetAttRowsCovered > 8) {
                        return 1.0;
                    }
                    else if (bestTargetAttRowsCovered <= 8) {
                        return 0.0;
                    }
                }
                else if (coversOutputAttScore <= 0.14666) {
                    return 0.0;
                }
            }
            else if (bestPercentageOfTargetAttCovered <= 0.94964) {
                if (numRows == null) {
                    return 0.0;
                }
                else if (numRows > 11) {
                    return 0.0;
                }
                else if (numRows <= 11) {
                    if (numRows > 9) {
                        if (almostDuplicate == null) {
                            return 1.0;
                        }
                        else if (almostDuplicate.equals("0")) {
                            if (lengthTotal == null) {
                                return 1.0;
                            }
                            else if (lengthTotal > 167) {
                                return 0.0;
                            }
                            else if (lengthTotal <= 167) {
                                if (uniqueRows == null) {
                                    return 1.0;
                                }
                                else if (uniqueRows > 7) {
                                    return 1.0;
                                }
                                else if (uniqueRows <= 7) {
                                    if (lengthTotal > 10) {
                                        return 0.0;
                                    }
                                    else if (lengthTotal <= 10) {
                                        return 1.0;
                                    }
                                }
                            }
                        }
                        else if (!almostDuplicate.equals("0")) {
                            return 0.0;
                        }
                    }
                    else if (numRows <= 9) {
                        if (coversOutputAttScore == null) {
                            return 0.0;
                        }
                        else if (coversOutputAttScore > 1.68448) {
                            return 1.0;
                        }
                        else if (coversOutputAttScore <= 1.68448) {
                            if (numRows > 5) {
                                if (depth > 8) {
                                    if (guessedType == null) {
                                        return 0.0;
                                    }
                                    else if (guessedType.equals("boolean")) {
                                        if (depth > 10) {
                                            return 0.0;
                                        }
                                        else if (depth <= 10) {
                                            if (uniqueRows == null) {
                                                return 0.0;
                                            }
                                            else if (uniqueRows > 1) {
                                                return 0.0;
                                            }
                                            else if (uniqueRows <= 1) {
                                                return 0.0;
                                            }
                                        }
                                    }
                                    else if (!guessedType.equals("boolean")) {
                                        return 0.0;
                                    }
                                }
                                else if (depth <= 8) {
                                    return 0.0;
                                }
                            }
                            else if (numRows <= 5) {
                                return 0.0;
                            }
                        }
                    }
                }
            }
        }
        else if (depth <= 6) {
            if (depth > 4) {
                if (uniqueRows == null) {
                    return 1.0;
                }
                else if (uniqueRows > 4) {
                    if (uniqueRows > 19) {
                        return 0.0;
                    }
                    else if (uniqueRows <= 19) {
                        if (lengthAverageRow == null) {
                            return 1.0;
                        }
                        else if (lengthAverageRow > 175.5) {
                            return 0.0;
                        }
                        else if (lengthAverageRow <= 175.5) {
                            if (bestTargetAttRowsCovered == null) {
                                return 1.0;
                            }
                            else if (bestTargetAttRowsCovered > 8) {
                                return 1.0;
                            }
                            else if (bestTargetAttRowsCovered <= 8) {
                                if (bestTargetAttTotalLength == null) {
                                    return 1.0;
                                }
                                else if (bestTargetAttTotalLength > 15) {
                                    return 0.0;
                                }
                                else if (bestTargetAttTotalLength <= 15) {
                                    if (guessedType == null) {
                                        return 1.0;
                                    }
                                    else if (guessedType.equals("CSV")) {
                                        return 0.0;
                                    }
                                    else if (!guessedType.equals("CSV")) {
                                        if (uniqueRows > 12) {
                                            if (almostDuplicate == null) {
                                                return 1.0;
                                            }
                                            else if (almostDuplicate.equals("0")) {
                                                return 1.0;
                                            }
                                            else if (!almostDuplicate.equals("0")) {
                                                return 0.0;
                                            }
                                        }
                                        else if (uniqueRows <= 12) {
                                            if (uniqueRows > 11) {
                                                return 1.0;
                                            }
                                            else if (uniqueRows <= 11) {
                                                if (lengthFirstRow == null) {
                                                    return 1.0;
                                                }
                                                else if (lengthFirstRow > 56) {
                                                    return 0.0;
                                                }
                                                else if (lengthFirstRow <= 56) {
                                                    if (lengthFirstRow > 49) {
                                                        return 1.0;
                                                    }
                                                    else if (lengthFirstRow <= 49) {
                                                        if (almostDuplicate == null) {
                                                            return 1.0;
                                                        }
                                                        else if (almostDuplicate.equals("0")) {
                                                            if (coversOutputAttScore == null) {
                                                                return 1.0;
                                                            }
                                                            else if (coversOutputAttScore > 1.12814) {
                                                                return 1.0;
                                                            }
                                                            else if (coversOutputAttScore <= 1.12814) {
                                                                return 0.0;
                                                            }
                                                        }
                                                        else if (!almostDuplicate.equals("0")) {
                                                            if (uniqueRows > 5) {
                                                                if (uniqueRows > 6) {
                                                                    if (uniqueRows > 9) {
                                                                        return 1.0;
                                                                    }
                                                                    else if (uniqueRows <= 9) {
                                                                        return 0.0;
                                                                    }
                                                                }
                                                                else if (uniqueRows <= 6) {
                                                                    return 1.0;
                                                                }
                                                            }
                                                            else if (uniqueRows <= 5) {
                                                                return 1.0;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (uniqueRows <= 4) {
                    if (guessedType == null) {
                        return 0.0;
                    }
                    else if (guessedType.equals("CSV")) {
                        return 1.0;
                    }
                    else if (!guessedType.equals("CSV")) {
                        return 0.0;
                    }
                }
            }
            else if (depth <= 4) {
                return 1.0;
            }
        }
        return 0.0;
    }

}
