package processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationServiceReversible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Because this works on a transformer per-row basis, if 2 rows have different keys then those keys may be extracted to different columns.
 * eg {name: Adam, age: 12} => name | Adam | age | 12
 *    {age: 23, sex: male } => age  | 23   | sex | male
 * There is no single "age" column that we could attempt to predict.
 * However, this processor should work in the situations where each row has the same keys (many cases).
 */
public class JsonDecodeMapManyColsAndKeysOutSingleRow extends TransformationServiceReversible {
	
	
	public JsonDecodeMapManyColsAndKeysOutSingleRow()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
		
		try{
			Map<String, String> jsonMap = mapper.readValue(input.get(0,0), Map.class);

			List<String> keys = new ArrayList<>(jsonMap.keySet());
			Collections.sort(keys);
			int keyIdx = 0;
			for( String key : keys )
			{
				//If the item to be saved as a string is actually an internal array or map, save it as a json string. The next level preprocessor can extract it. 
				Object arrayValue = jsonMap.get(key);
				String arrayValueString = mapper.writeValueAsString(arrayValue); //Add quotes on the string so there is a difference between parsing {x: 4} and {x: "4"} and no information is lost (makes it reversible).
				
				outTable.put(0, keyIdx*2, key );
				outTable.put(0, keyIdx*2+1, arrayValueString );
				keyIdx++;
			}
		}
		catch( IOException e ){
			//Return empty.
		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new ReverseJsonDecodeMapManyColsAndKeysOutSingleRow();
	}
}
