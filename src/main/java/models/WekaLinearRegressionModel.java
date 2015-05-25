package models;

import com.google.common.collect.Table;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class WekaLinearRegressionModel extends Model{
	private Classifier nativeModel;

	public WekaLinearRegressionModel(Classifier nativeModel) {
		this.nativeModel = nativeModel;
	}
	
	public String toString(){
		return this.getClass().getSimpleName()+"-"+this.nativeModel.toString().replace("\n", " ");
	}

	@Override
	public String predictForRow(List<String> inputRow) {
	    FastVector atts = new FastVector();
	    for( int colKey=0; colKey < inputRow.size(); colKey++){
	    	atts.addElement(new Attribute("wekaAtt"+colKey));
	    }
	    Instances data = new Instances("MyRelation", atts, 0);
	    data.setClassIndex(0);

    	double[] vals = new double[data.numAttributes()];
    	for( int colKey=0; colKey < inputRow.size(); colKey++){
    		try{
    			vals[colKey] = Double.parseDouble(inputRow.get(colKey));
    		}
    		catch( NumberFormatException e){
    			vals[colKey] = 0;
    		}
    	}
	    data.add(new Instance(1.0, vals));
	    
	    try {
			return ""+nativeModel.classifyInstance(new Instance(1.0, vals));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		
	}

	@Override
	public List<String> predictForRows(Table<Integer, Integer, String> testTable) {
		ArrayList<String> predictions = new ArrayList<>();
	
		for( int rowKey=0; rowKey < testTable.rowKeySet().size(); rowKey++){
			predictions.add( this.predictForRow(new ArrayList<String>(testTable.row(rowKey).values()) ) );
		}
		
		return predictions;
	}
	
}
