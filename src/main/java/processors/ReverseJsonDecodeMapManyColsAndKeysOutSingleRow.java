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

public class ReverseJsonDecodeMapManyColsAndKeysOutSingleRow extends TransformationServiceReversible {
	
	public ReverseJsonDecodeMapManyColsAndKeysOutSingleRow()
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

		Map outMap = new TreeMap();
		for( int col=0; col < input.columnKeySet().size(); col+=2 ){
			String key=input.get(0, col);
			if( key != null ){
				try {
					Object obj = mapper.readValue(input.get(0, col + 1), Object.class);
					outMap.put(key, obj);
				}
				catch( IOException e ){

				}
			}
		}
		try {
			outTable.put(0, 0, mapper.writeValueAsString(outMap));
		}
		catch( IOException e ){

		}
				
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new JsonDecodeMapManyColsAndKeysOutSingleRow();
	}
}
