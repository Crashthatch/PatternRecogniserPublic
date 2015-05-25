package processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationServiceReversible;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ReverseJsonDecodeMap extends TransformationServiceReversible {

	public ReverseJsonDecodeMap()
	{
		colsIn = 2;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
		
		Map jsonMap = new TreeMap<>(); //Sort alphabetically by key.
		
		for( int rowId=0; rowId < input.rowKeySet().size(); rowId++ ){
			try {
				Object valueObj = mapper.readValue(input.get(rowId, 1), Object.class); //Must parse first so that integer/nested-json type things don't get toJsonString'd with quotes.
				jsonMap.put(input.get(rowId, 0), valueObj);
			}
			catch( IOException e ){

			}
		}

		try {
			outTable.put(0, 0, mapper.writeValueAsString(jsonMap));
		}
		catch( IOException e){

		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new jsonDecodeMap(); 
	}
}
