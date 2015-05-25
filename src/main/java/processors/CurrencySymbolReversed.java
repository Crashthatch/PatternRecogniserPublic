package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;
import database.TransformationServiceReversible;

public class CurrencySymbolReversed extends TransformationServiceReversible {


	public CurrencySymbolReversed()
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
        if( stringin.equals("GBP")){
            stringout = "£";
        }
        else if( stringin.equals("EUR")){
            stringout = "€";
        }
        else if( stringin.equals("USD")){
            stringout = "$";
        }
        else if( stringin.equals("JPY")){
            stringout = "¥";
        }

        if( !stringout.equals("Unknown")) {
            outTable.put(0, 0, stringout);
        }
		
		return outTable;
	}

    @Override
    public TransformationService getReverseTransformer() {
        return new CurrencySymbol();
    }
}
