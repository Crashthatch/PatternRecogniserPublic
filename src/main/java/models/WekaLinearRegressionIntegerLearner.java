package models;

import com.google.common.collect.Table;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

//TODO: Add an Integer learner form of the RMLinearRegressionLearner too.
public class WekaLinearRegressionIntegerLearner extends ModelLearner{

	@Override
	public Model learnModelFromData(Table<Integer, Integer, String> inputTable) throws Exception {
		
	    FastVector atts = new FastVector();
	    for( int colKey : inputTable.columnKeySet()){
	    	atts.addElement(new Attribute("wekaAtt"+colKey));
	    }
	    Instances data = new Instances("MyRelation", atts, 0);
	    data.setClassIndex(0);

	    
	    for( int rowKey=0; rowKey < inputTable.rowKeySet().size(); rowKey++){
	    	double[] vals = new double[data.numAttributes()];
	    	for( int colKey : inputTable.columnKeySet() ){
	    		try{
	    			vals[colKey] = Integer.parseInt(inputTable.get(rowKey, colKey));
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
		
		return new WekaLinearRegressionIntegerModel(nativeModel);
	}

    public int getComplexityOrdering(){
        return 89;
    }


}
