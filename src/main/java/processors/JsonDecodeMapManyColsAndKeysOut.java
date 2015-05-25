package processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationServiceReversible;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JsonDecodeMapManyColsAndKeysOut extends TransformationServiceReversible {
	
	public JsonDecodeMapManyColsAndKeysOut()
	{
		colsIn = 1;
		colsOut = Integer.MAX_VALUE;
		rowsIn = Integer.MAX_VALUE;  //Must work with all rows so that we can be sure we are putting the same key from all rows into the same column. If we used 1 transformer per row, there's no guarantee that row1 "name" and row2 "age" wouldn't be both be returned as output column 1.
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

		//Find all the possible keys used in any hashed row and assign them a column number.
		TreeSet<String> allKeys = new TreeSet<>(); //Sort keys alphabetically. Just to be deterministic between runs.
		for( int row=0; row < input.rowKeySet().size(); row++){
			try {
				Map<String, String> jsonMap = mapper.readValue(input.get(row, 0), Map.class);
				allKeys.addAll( jsonMap.keySet() );
			}
			catch( IOException e ){
				//Leave the row blank.
			}
		}
		
		HashMap<String, Integer> keyToColumnMap = new HashMap<>();
		int keyNo = 0;
		for( String key : allKeys ){
			if( !keyToColumnMap.containsKey(key)){
				keyToColumnMap.put(key, keyNo);
				keyNo += 2;
			}
		}
		
		for( int row=0; row < input.rowKeySet().size(); row++){

			try {
				Map<String, String> jsonMap = mapper.readValue(input.get(row,0), Map.class);

				Set<String> keys = jsonMap.keySet();
				for( String key : keys )
				{
					//If the item to be saved as a string is actually an internal array or map, save it as a json string. The next level preprocessor can extract it.
					Object arrayValue = jsonMap.get(key);
					String arrayValueString = mapper.writeValueAsString(arrayValue); //Add quotes on the string so there is a difference between parsing {x: 4} and {x: "4"} and no information is lost (makes it reversible).

					outTable.put(row, keyToColumnMap.get(key), key );
					outTable.put(row, keyToColumnMap.get(key)+1, arrayValueString );
				}
			}
			catch( IOException e ){
				//Leave the row blank.
			}
		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new ReverseJsonDecodeMapManyColsAndKeysOut();
	}
}
