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

public class ReverseJsonDecodeMapManyColsAndKeysOut extends TransformationServiceReversible {
	
	public ReverseJsonDecodeMapManyColsAndKeysOut()
	{
		colsIn = Integer.MAX_VALUE;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		
		for( int row=0; row < input.rowKeySet().size(); row++ ){
			Map<String,Object> outMap = new TreeMap(); //Sort alphabetically by key.
			for( int col=0; col < input.columnKeySet().size(); col+=2 ){
				String key=input.get(row, col);
				if( key != null ){
					try {
						Object obj = mapper.readValue(input.get(row, col + 1), Object.class);
						outMap.put(key, obj);
					}
					catch( IOException e ){

					}
				}
			}
			try {
				outTable.put(row, 0, mapper.writeValueAsString(outMap));
			}
			catch( IOException e ){

			}
		}
				
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new JsonDecodeMapManyColsAndKeysOut();
	}
}
