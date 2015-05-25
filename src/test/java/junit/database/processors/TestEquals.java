package junit.database.processors;

import database.Att;
import database.Main;
import database.Processor;
import database.TransformationService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEquals {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void equalsTest() throws Exception
	{
		TransformationService isPrimeOne = new isPrime();
		TransformationService isPrimeTwo = new isPrime();
		
		TransformationService stringLengthOne = new stringLength();
		TransformationService stringLengthTwo = new stringLength();
		
		
		assertTrue( isPrimeOne.equals(isPrimeOne));
		
		assertTrue( isPrimeOne.equals(isPrimeTwo));
		assertTrue( isPrimeTwo.equals(isPrimeOne));
		
		assertTrue( stringLengthOne.equals(stringLengthTwo));
		assertTrue( stringLengthTwo.equals(stringLengthOne));
		
		assertFalse( isPrimeOne.equals( stringLengthOne));
		assertFalse( stringLengthOne.equals( isPrimeOne));
		
		//Test Transformers that have attributes.
        TransformationService splitterCommaOne = new SplitOnStringToRows(",");
        TransformationService splitterCommaTwo = new SplitOnStringToRows(",");
        TransformationService splitterDash = new SplitOnStringToRows("-");
		
		assertTrue( splitterCommaOne.equals( splitterCommaTwo ));
		assertFalse( splitterCommaOne.equals( splitterDash ));
		
		
		
		//Generate some data.
		Att constant = Main.initDb();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/12345.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToLines = new Processor( new SplitOnStringToRows("\n"), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToLines.doWork();
		
		Att numberAtt = splitToLines.getOutputAtts().get(1);
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(numberAtt);

		Processor summer = new Processor( new total(), inputAtts2, longStringAtt.getDataTable() );
		summer.doWork();
		
		Att totalAtt = summer.getOutputAtts().get(0);
		
		assertFalse( importer.equals(splitToLines));
		assertFalse( splitToLines.equals(summer));
		
		Processor summerTwo = new Processor( new total(), inputAtts2, longStringAtt.getDataTable() );
		assertTrue( summer.equals(summerTwo));
		
		//Change the rootrow table. 
		Processor summerThree = new Processor( new total(), inputAtts2, numberAtt.getDataTable() );
		assertFalse( summer.equals(summerThree));
		
		//A different att with the same rootrow table.
		Processor summerSeven = new Processor( new total(), inputAtts2, constant.getDataTable() );
		assertTrue( summer.equals(summerSeven));	
		
		//Change the input atts:
		//Empty list of input atts
		ArrayList<Att> inputAttsToSummerFour = new ArrayList<>();
		Processor summerFour = new Processor( new total(), inputAttsToSummerFour, longStringAtt.getDataTable() );
		assertFalse( summer.equals(summerFour));	
		
		//Different list of input atts
		ArrayList<Att> inputAttsToSummerFive = new ArrayList<>();
		inputAttsToSummerFive.add(longStringAtt);
		Processor summerFive = new Processor( new total(), inputAttsToSummerFive, longStringAtt.getDataTable() );
		assertFalse( summer.equals(summerFive));
		
		//Create a new ArrayList containing the same Atts as the original summer inputAtts.
		ArrayList<Att> inputAttsToSummerSix = new ArrayList<>();
		inputAttsToSummerSix.add(numberAtt);
		Processor summerSix = new Processor( new total(), inputAttsToSummerSix, longStringAtt.getDataTable() );
		assertTrue( summer.equals(summerSix));		
		
		
		//Test a processor with 2 inputs:
		ArrayList<Att> twoInputs = new ArrayList<>();
		twoInputs.add(totalAtt);
		twoInputs.add(numberAtt);
		Processor procWith2Inputs = new Processor( new divide(), twoInputs, numberAtt.getDataTable() );
		
		//If inputs are in a different order, the processor may behave differently, so they should be classed as different (not equal). 
		ArrayList<Att> twoInputsReversed = new ArrayList<>();
		twoInputsReversed.add(numberAtt);
		twoInputsReversed.add(totalAtt);
		Processor procWith2InputsReversed = new Processor( new divide(), twoInputsReversed, numberAtt.getDataTable() );
		
		assertFalse( procWith2Inputs.equals(procWith2InputsReversed));
		

		
		
	}

}
