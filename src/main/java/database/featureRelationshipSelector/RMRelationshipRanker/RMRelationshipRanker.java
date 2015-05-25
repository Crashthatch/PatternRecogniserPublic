package database.featureRelationshipSelector.RMRelationshipRanker;

import com.rapidminer.Process;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOContainer;
import database.features.RelationshipFeatureSet;
import database.features.UnknownPropertyTypeException;

import java.io.File;
import java.util.List;

/**
 * Created by Tom on 31/08/2014.
 */
public class RMRelationshipRanker {

    public void predictWillMakeAllCorrectPredictions(List<RelationshipFeatureSet> featuresList){
        try {
            Process myProcess = new Process(new File("src/main/java/database/featureRelationshipSelector/RMRelationshipRanker/model-applier.rmp"));
            Process sampleLoader = new Process(new File("src/main/java/database/featureRelationshipSelector/RMRelationshipRanker/aml-importer.rmp"));

            IOContainer sampleResult = sampleLoader.run();
            //Empty this exampleSet and refill it with data from the features.
            Attribute[] rmAttributes = ((ExampleSet)sampleResult.getElementAt(0)).getExampleTable().getAttributes();
            MemoryExampleTable exampleTable = new MemoryExampleTable(rmAttributes);

            //RelationshipFeatureSet firstFeatures = featuresList.get(0);
            //LinkedHashMap<String, Attribute> rmAttributes = null;// = getAttributes(firstFeatures);
            //MemoryExampleTable exampleTable = new MemoryExampleTable();
            for( RelationshipFeatureSet features : featuresList ) {
                exampleTable.addDataRow(getDataRow(features, rmAttributes));
            }
            //exampleTable.addAttributes(rmAttributes.values());

            // execute the process and get the resulting objects
            SimpleExampleSet exampleSet = new SimpleExampleSet(exampleTable);
            IOContainer ioInput = new IOContainer(exampleSet);
            IOContainer ioResult = myProcess.run(ioInput);

            //Parse the result and save the prediction onto the features.
            if (ioResult.getElementAt(0) instanceof ExampleSet) {
                ExampleSet resultSet = (ExampleSet)ioResult.getElementAt(0);
                assert( resultSet.size() == featuresList.size());
                int row = 0;
                for( Example example : resultSet) {
                    double confidence = example.getConfidence("true");
                    if( Double.isNaN(confidence)){
                        featuresList.get(row).setScoreDuringRun(Double.NaN);
                    }
                    else{
                        featuresList.get(row).setScoreDuringRun(((double)Math.round(confidence*1000000))/1000000);
                    }

                    row++;
                }
            }
            else{
                throw new Exception("RapidMiner process did not return an annotated ExampleSet.");
            }
        }
        catch( Exception e ){
            e.printStackTrace();
        }
    }

    public DataRow getDataRow(RelationshipFeatureSet features, Attribute[] rmAttributes) throws UnknownPropertyTypeException {
        int columns = rmAttributes.length;
        int col = 0;
        double[] primitiveArray = new double[columns];
        for( Attribute att : rmAttributes ){
            String key = att.getName();
            Object value = features.get(key);
            if( value == null ){
                primitiveArray[col] = Double.NaN;
            }
            else if( value.getClass().equals( Integer.class) ){
                primitiveArray[col] = (Integer)value;
            }
            else if( value.getClass().equals( Double.class) ){
                primitiveArray[col] = (Double)value;
            }
            else if( value.getClass().equals( Boolean.class) ){
                if( (Boolean)value ){
                    primitiveArray[col] = 1;
                }
                else{
                    primitiveArray[col] = 0;
                }

            }
            else if( value.getClass().equals( String.class) ){
                primitiveArray[col] = att.getMapping().mapString((String)value);
            }
            else {
                throw new UnknownPropertyTypeException("Don't know how to create property of type '"+value.getClass().getSimpleName()+"' in a RM example row.");
            }
            col++;
        }
        return new DoubleArrayDataRow(primitiveArray);
    }
}
