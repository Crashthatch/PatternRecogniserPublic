package junit.database.models;

import com.google.common.collect.TreeBasedTable;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import models.ChopEndsLearner;
import models.Model;
import models.ModelLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class TestChopEndsModel {
    ModelLearner learner = new ChopEndsLearner();
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSimpleChopEnds() throws Exception{
        

        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "aaa");
        in.put(0, 1, "BEGINaaaEND");

        in.put(1, 0, "bbb");
        in.put(1, 1, "BEGINbbbEND");

        in.put(2, 0, "ccc");
        in.put(2, 1, "55555ccc333");

        in.put(3, 0, "ddd");
        in.put(3, 1, "cakesdddpie");

		Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "FruitBatMan"));
        assertTrue( prediction.equals("Bat"));
    }

    @Test public void testAppearsAtStart() throws Exception{
        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "aaa");
        in.put(0, 1, "aaa123");

        in.put(1, 0, "bbb");
        in.put(1, 1, "bbb456");

        in.put(2, 0, "ccc");
        in.put(2, 1, "ccc789");

        in.put(3, 0, "ddd");
        in.put(3, 1, "ddd012");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "Bat999"));
        assertTrue( prediction.equals("Bat"));
    }

    @Test public void testAppearsAtEnd() throws Exception{
        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "aaa");
        in.put(0, 1, "PIEaaa");

        in.put(1, 0, "bbb");
        in.put(1, 1, "ABCbbb");

        in.put(2, 0, "ccc");
        in.put(2, 1, "123ccc");

        in.put(3, 0, "ddd");
        in.put(3, 1, "234ddd");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "Batman"));
        assertTrue( prediction.equals("man"));
    }

    /**
     * Where the substring is not ALWAYS at the same offset, choose the offset where it is most often.
     */
    @Test public void testBestGuessChopEnds() throws Exception{
        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "aaa");
        in.put(0, 1, "BEGINaaaEND");

        in.put(1, 0, "bbb");
        in.put(1, 1, "BEGINbbbENDD");

        in.put(2, 0, "ccc");
        in.put(2, 1, "666666ccc333");

        in.put(3, 0, "ddd");
        in.put(3, 1, "cakesddd22");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "FruitBatMan"));
        assertTrue( prediction.equals("Bat"));
    }

    /**
     * The benefit of choosing a endIdx is that we can have varying length labels, and so long as they're
     */
    @Test public void testVaryingLengthLabel() throws Exception{
        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "aaa");
        in.put(0, 1, "1234aaa56");

        in.put(1, 0, "bbbbb");
        in.put(1, 1, "4321bbbbb65");

        in.put(2, 0, "ccc ccc ccc");
        in.put(2, 1, "Fishccc ccc ccc78");

        in.put(3, 0, "dddddddddddddddddddddddddddddddddddddddddddd");
        in.put(3, 1, "ABCDddddddddddddddddddddddddddddddddddddddddddddEF");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "4444target22"));
        assertTrue( prediction.equals("target"));
    }

    /**
     * When the label appears multiple times in the feature, should choose the offset where it appears most often.
     */
    @Test public void testAppearsTwice() throws Exception{

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 0, "aaa");
        in.put(0, 1, "stuff aaa things aaa cows");

        in.put(1, 0, "bbb");
        in.put(1, 1, "12bbb dog whosit bbb cats");


        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "55555 333 666666 XXX 4444"));
        assertTrue( prediction.equals("XXX"));
    }

    /**
     * If label appears multiple times, overlapping, choose the offset where it appears most often in all the rows.
     */
    @Test public void testOverlapping() throws Exception{

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();

        in.put(0, 0, "bbb");
        in.put(0, 1, "12bbbbbbb6789");

        in.put(1, 0, "ccc");
        in.put(1, 1, "123456cccccc9");


        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "ABCDEFGHIJKLM"));
        assertTrue( prediction.equals("GHI"));
    }
}
