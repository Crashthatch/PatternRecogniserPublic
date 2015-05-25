package processors;

import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.Table;
import database.TransformationService;

import java.util.ArrayList;
import java.util.List;

public class primeFactors extends TransformationService {
	
	public primeFactors()
	{
		colsIn = 1;
		colsOut = 1;
		rowsIn = 1;
		rowsOut = Integer.MAX_VALUE;
	}

	public Table<Integer, Integer, String> doWork(Table<Integer, Integer, String> input)
	{
		Table<Integer, Integer, String> outTable = TreeBasedTable.create();
		String stringin = (String) input.get(new Integer(0), new Integer(0) );
		int i = Integer.parseInt(stringin);
		int factorNum = 0;
		List<Integer> factors = findPrimeFactors(i);
		for( int factor :  factors )
		{
			outTable.put( factorNum, 0, ""+factor );
			factorNum++;
		}
		
		return outTable;
	}

	public static List<Integer> findPrimeFactors(int numbers) {
	    int n = numbers; 
	    List<Integer> factors = new ArrayList<Integer>();
	    for (int i = 2; i <= n / i; i++) {
	      while (n % i == 0) {
	        factors.add(i);
	        n /= i;
	      }
	    }
	    if (n > 1) {
	      factors.add(n);
	    }
	    return factors;
	  }
}
