package models;

import com.google.common.collect.Table;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.*;
import com.rapidminer.tools.Ontology;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public abstract class RMLearner extends ModelLearner{
	
	public static ExampleSet createNumericalRMExampleSetWithLabelAtt(Table<Integer, Integer, String> inputTable)
	{
		ExampleTable exampleTable = createNumericalRMExampleTable(inputTable);
		
		//Create a view on the table and set the label to the first column.
		ExampleSet exampleSet = exampleTable.createExampleSet();
		exampleSet.getAttributes().setSpecialAttribute(exampleTable.getAttribute(0), "label");
		
		return exampleSet;
	}
	
	public static ExampleSet createNominalRMExampleSetWithLabelAtt(Table<Integer, Integer, String> inputTable, ArrayList<NominalMapping> mappings)
	{
		ExampleTable exampleTable = createNominalRMExampleTable(inputTable, mappings);
		
		//Create a view on the table and set the label to the first column.
		ExampleSet exampleSet = exampleTable.createExampleSet();
		exampleSet.getAttributes().setSpecialAttribute(exampleTable.getAttribute(0), "label");
		
		return exampleSet;
	}
	
	public static ExampleTable createNominalRMExampleTable(Table<Integer, Integer, String> inputTable, ArrayList<NominalMapping> mappings) {
		//Create a RapidMiner-format table:
		int columns = inputTable.columnKeySet().size();
		
		MemoryExampleTable exampleTable = new MemoryExampleTable();
		
		//Populate the table
		for( int row = 0; row < inputTable.rowKeySet().size(); row++ )
		{
			int[] primitiveArray = new int[columns];
			for( int col = 0; col < columns; col++ )
			{
				String value = inputTable.get(row, col);
				primitiveArray[col] = mappings.get(col).mapString(value); //Returns the integer mapping for the string
			}

			DataRow dr = new IntArrayDataRow(primitiveArray);
			exampleTable.addDataRow(dr);
		}
		
		ArrayList<Attribute> rmAttributes = new ArrayList<>();
		for( int col=0; col < columns; col++)
		{
			Attribute newAttribute = AttributeFactory.createAttribute(""+col, Ontology.NOMINAL );
			newAttribute.setMapping(mappings.get(col));
			rmAttributes.add(newAttribute);
		}

		exampleTable.addAttributes(rmAttributes);
		
		return exampleTable;
	}
	
	

	public static ExampleTable createNumericalRMExampleTable(Table<Integer, Integer, String> inputTable) {
		//Create a RapidMiner-format table:
		int columns = inputTable.columnKeySet().size();
		
		MemoryExampleTable exampleTable = new MemoryExampleTable();

		//Populate the table
		for( int row = 0; row < inputTable.rowKeySet().size(); row++ )
		{
			double[] primitiveArray = new double[columns];
			for( int col = 0; col < columns; col++ )
			{
				String value = inputTable.get(row, col);
				try{
					NumberFormat nf = NumberFormat.getInstance();
					Number numberValue = nf.parse(value);
					double doubleValue = numberValue.doubleValue();
					primitiveArray[col] = doubleValue;
				}
				catch( NumberFormatException | ParseException e )
				{
					primitiveArray[col] = 0;
				}
			}

			DataRow dr = new DoubleArrayDataRow(primitiveArray);
			exampleTable.addDataRow(dr);
		}
		
		ArrayList<Attribute> rmAttributes = new ArrayList<>();
		for( int col=0; col < columns; col++)
		{
			Attribute newAttribute = AttributeFactory.createAttribute(""+col, Ontology.NUMERICAL );
			rmAttributes.add(newAttribute);
		}

		exampleTable.addAttributes(rmAttributes);
		
		return exampleTable;
	}


}
