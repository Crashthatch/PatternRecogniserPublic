package junit.database.inout;

import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class Soml20questions {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void soml20questions() throws Exception
	{
		//Create the root "const" att which contains no data.
		Att constant = Main.initDb();
		
		long startTime = System.nanoTime();
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/soml-20questions.csv"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("longStringAtt");
		
		Processor splitToQuestions = new Processor( new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToQuestions.doWork();
		
		
		ArrayList<Att> a = new ArrayList<>();
		a.add(splitToQuestions.getOutputAtts().get(8) );
		Processor regexReplacer = new Processor( new RegexReplacer("(http\\:\\/\\/|https\\:\\/\\/|www\\.).*", ""), a, splitToQuestions.getOutputAtts().get(8).getDataTable() );
		regexReplacer.doWork();
		
		Processor wordSplitter = new Processor( new SplitOnRegexToRows(" "), regexReplacer.getOutputAtts(), regexReplacer.getOutputAtts().get(0).getDataTable() );
		wordSplitter.doWork();
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(wordSplitter.getOutputAtts().get(1));
		
		Processor wordCounter = new Processor( new CountIdenticalRows(), inputAtts2, splitToQuestions.getOutputAtts().get(8).getDataTable() );
		wordCounter.doWork();


		Att word = wordCounter.getOutputAtts().get(1);
		word.setNotes("word");
		Att count = wordCounter.getOutputAtts().get(0);
		count.setNotes("count");
		Att clas = splitToQuestions.getOutputAtts().get(15);
		clas.setNotes("clas");
		
		ArrayList<Att> wordClassCountsInput = new ArrayList<>();
		wordClassCountsInput.add(count);
		wordClassCountsInput.add(word);
		wordClassCountsInput.add(clas);
		
		Processor classWordCounter = new Processor( new SumIdenticalRows(), wordClassCountsInput, longStringAtt.getDataTable() );
		classWordCounter.doWork();
		
		
		//System.exit(0);
		

		
		//Aside: Calculate the class-counts, the number of questions of each class.
		ArrayList<Att> b = new ArrayList<>();
		b.add(clas);
		Processor classCounter = new Processor( new CountIdenticalRows(), b, longStringAtt.getDataTable());
		classCounter.doWork();
		
		
		//Join classcounts to the word-class-counts.
		Att countOfWordCount = classWordCounter.getOutputAtts().get(0);
		countOfWordCount.setNotes("countOfWordCount");
		Att wordOfWordCount = classWordCounter.getOutputAtts().get(1);
		wordOfWordCount.setNotes("wordOfWordCount");
		Att classOfWordCount = classWordCounter.getOutputAtts().get(2);
		wordOfWordCount.setNotes("wordOfWordCount");
		Att classOfClassCount = classCounter.getOutputAtts().get(1);
		wordOfWordCount.setNotes("wordOfWordCount");
		Att countOfClassCount = classCounter.getOutputAtts().get(0);
		wordOfWordCount.setNotes("wordOfWordCount");
		
		ArrayList<Att> c = new ArrayList<>();
		c.add(classOfWordCount);
		c.add(classOfClassCount);
		c.add(countOfClassCount);
		
		Processor joiner = new Processor( new JoinWhereEqual(), c, classOfWordCount.getDataTable() );
		joiner.doWork();
		
		
		//System.exit(0);
		
		//GuiTree guiTree = new GuiTree();
		//guiTree.displayGraph(graph);
		
		//Calculate the word frequency, ie. the number of times the word appears per question (in a specific class).
		ArrayList<Att> d = new ArrayList<>();
		d.add( countOfWordCount );
		d.add(joiner.getOutputAtts().get(0));
		
		Processor wordFreqCalculator = new Processor( new divide(), d, countOfWordCount.getDataTable() );
		wordFreqCalculator.doWork();
		
		//Calculate the sums of the frequencies so that we can normalise the wordFreqs found in the previous step.
		ArrayList<Att> e = new ArrayList<>();
		Att wordFreq = wordFreqCalculator.getOutputAtts().get(0);
		wordFreq.setNotes("wordFreq");
		e.add( wordFreq );
		e.add( wordOfWordCount );
		
		Processor wordFreqSumCalculator = new Processor( new SumIdenticalRows(), e, longStringAtt.getDataTable() );
		wordFreqSumCalculator.doWork();
		
		//Join the sum back to the wordFreqs.
		Att wordOfWordFreqSum = wordFreqSumCalculator.getOutputAtts().get(1);
		wordOfWordFreqSum.setNotes("wordOfWordFreqSum");
		Att sumOfWordFreqSum = wordFreqSumCalculator.getOutputAtts().get(0);
		sumOfWordFreqSum.setNotes("sumOfWordFreqSum");
		
		ArrayList<Att> f = new ArrayList<>();
		f.add( wordOfWordCount );
		f.add( wordOfWordFreqSum );
		f.add( sumOfWordFreqSum );
		Processor joiner2 = new Processor( new JoinWhereEqual(), f, wordOfWordCount.getDataTable() );
		joiner2.doWork();
		

		
		//Normalise wordFreqs.
		ArrayList<Att> g = new ArrayList<>();
		g.add( wordFreq );
		g.add(joiner2.getOutputAtts().get(0));
		
		Processor normaliseWordFreqs = new Processor( new divide(), g, wordFreq.getDataTable() );
		normaliseWordFreqs.doWork();
		
		//Find words that are common in this class
		//Create the constant of "2" so that we can keep any words that appear more than that many times.
		Processor constCreator = new Processor( new constantCreator("2"), inputAtts, constant.getDataTable());
		constCreator.doWork();
		
		//Common if > 2 mentions in any class.
		ArrayList<Att> h = new ArrayList<>();
		h.add(countOfWordCount);
		h.add(constCreator.getOutputAtts().get(0));
		Processor hasEnoughMentions = new Processor( new greaterThan(), h, countOfWordCount.getDataTable());
		hasEnoughMentions.doWork();
		
		//Common if used more than once per post on average (for the given class).
		Processor constCreator2 = new Processor( new constantCreator("1"), inputAtts, constant.getDataTable());
		constCreator2.doWork();
		
		ArrayList<Att> i = new ArrayList<>();
		i.add(wordFreq);
		i.add(constCreator2.getOutputAtts().get(0));
		Processor hasEnoughFrequency = new Processor( new greaterThan(), i, wordFreq.getDataTable());
		hasEnoughFrequency.doWork();
		
		
		
		//Common if EITHER of the two cases above happened.
		ArrayList<Att> j = new ArrayList<>();
		j.add(hasEnoughMentions.getOutputAtts().get(0));
		j.add(hasEnoughFrequency.getOutputAtts().get(0));
		Processor commonInThisClass = new Processor( new orProcessor(), j, hasEnoughMentions.getOutputAtts().get(0).getDataTable() );
		commonInThisClass.doWork();
		
		//Create a list of those words which are common
		ArrayList<Att> k = new ArrayList<>();
		k.add( commonInThisClass.getOutputAtts().get(0) );
		k.add( wordOfWordCount );
		Processor selectWhereTrue = new Processor( new filterWhereTrue(), k, longStringAtt.getDataTable() );
		selectWhereTrue.doWork();
		
		//Remove duplicates
		ArrayList<Att> l = new ArrayList<>();
		l.add( selectWhereTrue.getOutputAtts().get(0) );
		Processor removeDuplicates = new Processor( new removeDuplicateRows(), l, longStringAtt.getDataTable() );
		removeDuplicates.doWork();
		
		//Annotate the words with whether or not they are common.
		ArrayList<Att> m = new ArrayList<>();
		m.add( wordOfWordCount );
		m.add( removeDuplicates.getOutputAtts().get(0));
		Processor isACommonWordProcessor = new Processor( new appearsInList(), m, wordOfWordCount.getDataTable());
		isACommonWordProcessor.doWork();
		
		long endTime = System.nanoTime();

		long duration = endTime - startTime;
		
		System.out.println("Soml-20Questions completed in "+duration/1000000000.0+" seconds.");
		
		
		//TODO: Add some assert()s in here.
		
		
		
		
		//Do tests on the final output.
		Att isACommonWordAtt = isACommonWordProcessor.getOutputAtts().get(0);
		
		System.out.println(isACommonWordAtt);
		System.out.println(wordOfWordCount);
		System.out.println(isACommonWordAtt.getData());
		
		ArrayList<Att> attsForTestTable = new ArrayList<>();
		attsForTestTable.add(wordOfWordCount);
		attsForTestTable.add(isACommonWordAtt);
		String sql = DataTable.getSqlStringJoiningInputs(wordOfWordCount.getDataTable(), attsForTestTable);
		
		ResultSet testTable = Database.getConnection().prepareStatement(sql).executeQuery();
		
		int rowsInTestTable = 0;
		int commonWordCount = 0;
		int uncommonWordCount = 0;
		boolean andFound = false;
		boolean dateNumberFound = false;
		while( testTable.next() )
		{
			rowsInTestTable++;
			
			int id = testTable.getInt(1);
			String wordFromDb = testTable.getString(2);
			String isCommon = testTable.getString(3);
			
			if( wordFromDb.equals("and") )
			{
				andFound = true;
			}
			if( wordFromDb.equals("20110905154741"))
			{
				dateNumberFound = true;
			}
			
			//System.out.println(id+" "+wordFromDb+" "+isCommon);
			
			if( wordFromDb.equals( "and" ) || wordFromDb.equals("using") || wordFromDb.equals( "need" ) )
			{
				assertTrue( isCommon.equals("1"));
				
			}
			else if( wordFromDb.equals( "trans.setOutputProperty(OutputKeys.INDENT" ) || wordFromDb.equals("20110905154741") )
			{
				assertTrue( isCommon.equals("0"));
			}
			
			if( isCommon.equals("1") )
			{
				commonWordCount++;
			}
			else if( isCommon.equals("0"))
			{
				uncommonWordCount++;
			}
			
		}
		
		assertTrue( andFound );
		assertTrue( dateNumberFound );
		assertTrue( rowsInTestTable == 843);
		assertTrue( commonWordCount == 152 );
		assertTrue( uncommonWordCount == 691 );
		
		

		
		/*
		try{
			assertTrue(  );
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		*/
		
		
	}

}
