package processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Converts {age: 4, name: "Bob"} to:
 * 4 | Bob
 * Keys are discarded.
 */
public class JsonDecodeMapManyColsOut extends TransformationService { //Can't make reversible because the keys don't get extracted anywhere. Maybe use JsonDecodeMap instead and then filter the outputs?
	
	public JsonDecodeMapManyColsOut()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

		try {
			Map<String, String> jsonMap = mapper.readValue(stringin, Map.class);

			List<String> keys = new ArrayList<>(jsonMap.keySet());
			Collections.sort(keys);
			int keyIdx = 0;
			for( String key : keys )
			{
				//If the item to be saved as a string is actually an internal array or map, save it as a json string. The next level preprocessor can extract it.
				Object arrayValue = jsonMap.get(key);
				String arrayValueString = mapper.writer().writeValueAsString(arrayValue); //Add quotes on the string so there is a difference between parsing {x: 4} and {x: "4"} and no information is lost (makes it reversible).

				outTable.put(0, keyIdx, arrayValueString );
				keyIdx++;
			}
		}
		catch( IOException e ){
			//Return an empty table. Should we throw a UnsuitableTransformerException or something instead?
		}
		
		return outTable;
	}
}
