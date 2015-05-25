package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServicePartialRowsReversible;

public class CurrencySymbol extends TransformationServicePartialRowsReversible {


	public CurrencySymbol()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = 1;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );
        String stringout = "Unknown";
        if( stringin.equals("£")){
            stringout = "GBP";
        }
        else if( stringin.equals("€")){
            stringout = "EUR";
        }
        else if( stringin.equals("$")){
            stringout = "USD";
        }
        else if( stringin.equals("¥")){
            stringout = "JPY";
        }

        if( !stringout.equals("Unknown")) {
            outTable.put(0, 0, stringout);
        }
		
		return outTable;
	}

    @Override
    public TransformationService getReverseTransformer() {
        return new CurrencySymbolReversed();
    }
}
