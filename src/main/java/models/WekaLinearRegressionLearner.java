package models;

import com.google.common.collect.Table;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Set;

public class WekaLinearRegressionLearner extends ModelLearner{

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws Exception {

		int numCols = inputTable.columnKeySet().size();
	    FastVector atts = new FastVector();
	    for( int colKey = 0; colKey < numCols; colKey++){
	    	atts.addElement(new Attribute("wekaAtt"+colKey));
	    }
	    Instances data = new Instances("MyRelation", atts, 0);
	    data.setClassIndex(0);

		int numRows = inputTable.rowKeySet().size();
	    for( int rowKey=0; rowKey < numRows; rowKey++){
	    	double[] vals = new double[data.numAttributes()];
			for( int colKey = 0; colKey < numCols; colKey++){
	    		try{
	    			vals[colKey] = Double.parseDouble(inputTable.get(rowKey, colKey));
	    		}
	    		catch( NumberFormatException e){
	    			vals[colKey] = 0;
	    		}
	    	}
		    data.add(new Instance(1.0, vals));
	    }
		
	    SimpleLinearRegression nativeModel = new SimpleLinearRegression();
	    nativeModel.setSuppressErrorMessage(true);
	    nativeModel.buildClassifier(data);
		
		return new WekaLinearRegressionModel(nativeModel);		
	}

    public int getComplexityOrdering(){
        return 99;
    }

}
