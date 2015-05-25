package processors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.common.primitives.Doubles;
import database.TransformationServiceReversible;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReverseJsonDecodeList extends TransformationServiceReversible {

	public ReverseJsonDecodeList()
	{
		colsIn = 2;
		colsOut = 1;
		rowsIn = Integer.MAX_VALUE;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		
		List jsonArray = new ArrayList();
		
		//Table has n rows. Create a permutation of 1..n by sorting by the "index-column".
		Map<Integer, String> indexCol = input.column(0);
		Ordering<Integer> valueComparator = Ordering.natural().onResultOf(Functions.compose(Doubles.stringConverter(), Functions.forMap(indexCol)));
		ImmutableList<Integer> rowKeysSortedByIndexColValue = valueComparator.immutableSortedCopy(indexCol.keySet());

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

		for( int rowId : rowKeysSortedByIndexColValue ){
			try {
				Object valueObj = mapper.readValue(input.get(rowId, 1), Object.class); //Must parse first so that integer/nested-json type things don't get toJsonString'd with quotes.
				jsonArray.add(valueObj);
			}
			catch( IOException e ){
				e.printStackTrace();
			}
		}

		try {
			outTable.put(0, 0, mapper.writeValueAsString(jsonArray));
		}
		catch(IOException e ){
			e.printStackTrace();
		}
		
		return outTable;
	}

	@Override
	public TransformationServiceReversible getReverseTransformer() {
		return new jsonDecodeList(); 
	}
}
