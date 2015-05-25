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

public class jsonDecodeMap extends TransformationServiceReversible {
	
	public jsonDecodeMap()
	{
		colsIn = 1;
		colsOut = 2;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(0,0);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

		try {
			Map jsonObj = mapper.readValue(stringin, Map.class);

			List<String> keys = new ArrayList<>(jsonObj.keySet());
			Collections.sort(keys);
			int row = 0;
			for( String key : keys )
			{
				//If the item to be saved as a string is actually an internal array or map, save it as a json string. The next level preprocessor can extract it.
				outTable.put(row, 0, key );
				outTable.put(row, 1, mapper.writeValueAsString( jsonObj.get(key) ));
				row++;
			}
		}
		catch(IOException e ){

		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new ReverseJsonDecodeMap();
	}
}
