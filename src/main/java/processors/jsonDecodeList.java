package processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.TransformationServiceReversible;

import java.io.IOException;
import java.util.List;

public class jsonDecodeList extends TransformationServiceReversible {

	public jsonDecodeList()
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
		
		//Handle JSON strings of the "list" form [].
		try{
			List<String> jsonArray = mapper.readValue(stringin, List.class);
			int indexcol = 0;
			for( int jsonArrayIndex = 0; jsonArrayIndex < jsonArray.size(); jsonArrayIndex++ )
			{
				Object arrayValue = jsonArray.get(jsonArrayIndex);
				
				outTable.put(jsonArrayIndex, 0, ""+indexcol );
				outTable.put(jsonArrayIndex, 1, mapper.writer().writeValueAsString(arrayValue) );
				indexcol++;
			}
		}
		catch( IOException e ){
		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new ReverseJsonDecodeList();
	}
}
