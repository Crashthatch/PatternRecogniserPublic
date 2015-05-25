package junit.database.inout;

import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestUnknownNumberOfOutputAttsUpdate {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void test() throws Exception
	{
		//Create the root "const" att which contains no data.
		AttRelationshipGraph graph = new AttRelationshipGraph();
		Att constant = Main.initDb(graph);
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		Processor importer = new Processor( new getInputFromFile("testdata/soml-alphabetagamma-questions.csv"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		
		Processor splitToQuestions = new Processor( new ReadCSVToTable(), importer.getOutputAtts(), longStringAtt.getDataTable() );
		splitToQuestions.doWork();
		
		
		ArrayList<Att> a = new ArrayList<>();
		a.add(splitToQuestions.getOutputAtts().get(8) );
		Processor regexReplacer = new Processor( new RegexReplacer("(http\\:\\/\\/|https\\:\\/\\/|www\\.).*", ""), a, splitToQuestions.getOutputAtts().get(8).getDataTable() );
		regexReplacer.doWork();
		
		Processor wordSplitter = new Processor( new SplitOnStringToRows(" "), regexReplacer.getOutputAtts(), regexReplacer.getOutputAtts().get(0).getDataTable() );
		wordSplitter.doWork();
		ArrayList<Att> inputAtts2 = new ArrayList<>();
		inputAtts2.add(wordSplitter.getOutputAtts().get(1));
		
		Processor wordCounter = new Processor( new CountIdenticalRows(), inputAtts2, splitToQuestions.getOutputAtts().get(8).getDataTable() );
		wordCounter.doWork();

		graph.createDot();
		
		Att word = wordCounter.getOutputAtts().get(1);
		Att count = wordCounter.getOutputAtts().get(0);
		Att clas = splitToQuestions.getOutputAtts().get(15);
		
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
		Att classOfWordCount = classWordCounter.getOutputAtts().get(2);
		Att classOfClassCount = classCounter.getOutputAtts().get(1);
		Att countOfClassCount = classCounter.getOutputAtts().get(0);
		
		ArrayList<Att> c = new ArrayList<>();
		c.add(classOfWordCount);
		c.add(classOfClassCount);
		c.add(countOfClassCount);
		
		Processor joiner = new Processor( new JoinWhereEqual(), c, classOfWordCount.getDataTable() );
		joiner.doWork();
		
		
		
		//System.out.println( joiner.getOutputAtts().get(0).getData() );
		try{
            //There should be 3 "open" rows, which get a value of 2 (because there were 2 open questions)
            // And there should be 2 "closed" rows, which get a value of 1 (because there was 1 closed question).
            ArrayList<String> datarows = joiner.getOutputAtts().get(0).getData();
            assertEquals( 3, Collections.frequency(datarows, "2"));
            assertEquals( 2, Collections.frequency(datarows, "1"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		
	}

}
