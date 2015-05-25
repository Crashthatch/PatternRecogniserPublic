package junit.database.inout;

import database.*;
import database.featureProcessorSelector.transformerSelector.GuessedTypeTransformerSelector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitOnStringToColumns;
import processors.SplitOnStringToRows;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestGuessDelimiter {
    private AttRelationshipGraph inputGraph;
    private Att rootConstant;
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{
	}

    //Utility method to turn a string into an att on which splitOnGuessedDelimitersToRows can operate.
    //Use "//" in the inString to create multiple rows.
    private Att convertToAtt(String inString) throws SQLException {
        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att rootConstant = Main.initDb(inputGraph);

        Processor constantCreator = new Processor( new processors.constantCreator(inString), Arrays.asList(rootConstant), rootConstant.getDataTable() );
        constantCreator.doWork();

        Att longStringAtt = constantCreator.getOutputAtts().get(0);

        //Split on // to rows, so we have 3 rows to try to guess delimiters from.
        Processor splitter = new Processor(new SplitOnStringToRows("//"), Arrays.asList(longStringAtt), rootConstant.getDataTable() );
        splitter.doWork();

        return splitter.getOutputAtts().get(1);
    }


    @Test    public void guessDelimiterManyRows() throws Exception{
        String inString = "aaa,bbb,ccc//ddd,eee,fff//ggg,hhh,iii";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns(",")));
    }

    @Test    public void guessMultipleDelimiterManyRows() throws Exception{
        String inString = "aaa,bbb;ccc,ddd;eee,fff//ggg,hhh;iii,jjj;kkk,lll//mmm,nnn;ooo,ppp;qqq,rrr";

        //Create the root "const" att which contains no data.
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns(",")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(";")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns(";")));
    }

    @Test    public void guessMultiCharacterDelimiterManyRows() throws Exception{
        String inString = "aaa,-bbb,-ccc//ddd,-eee,-fff//ggg,-hhh,-iii";

        //Create the root "const" att which contains no data.
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",-")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns(",-")));
    }

    @Test    public void guessLongDelimiterManyRows() throws Exception{
        String inString = "aaa--------------bbb--------------ccc//ddd--------------eee--------------fff//ggg--------------hhh--------------iii";

        //Create the root "const" att which contains no data.
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("--------------")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns("--------------")));
    }

    @Test    public void guessLongNonRepeatingDelimiterManyRows() throws Exception{
        String inString = "aaa-,;this@:bbb-,;this@:ccc//ddd-,;this@:eee-,;this@:fff//ggg-,;this@:hhh-,;this@:iii";

        //Create the root "const" att which contains no data.
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-,;this@:")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns("-,;this@:")));
    }

    @Test    public void guessDelimiterContainingDigitManyRows() throws Exception{
        String inString = "aaa,0bbb,0ccc//ddd,0eee,0fff//ggg,0hhh,0iii";

        //Create the root "const" att which contains no data.
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",0")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToColumns(",0")));
    }




    @Test    public void guessDelimiterSingleRow() throws Exception{
        String inString = "aaa,bbb,ccc";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",")));
    }

    @Test    public void guessMultipleDelimitersSingleRow() throws Exception{
        String inString = "aaa,bbb,ccc;ddd,eee,fff;ggg,hhh";
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",")));
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(";")));
    }

    @Test    public void guessMultiCharacterDelimiterSingleRow() throws Exception{
        String inString = "aaa,-bbb,-ccc,-ddd,-eee,-fff,-ggg,-hhh";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows(",-")));
    }

    @Test    public void guessLongDelimiterSingleRow() throws Exception{
        String inString = "aaa------bbb------ccc------ddd------eee------fff------ggg------hhh";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("------")));
    }
    @Test    public void guessLongNonRepeatingDelimiterSingleRow() throws Exception{
        String inString = "aaa-:this@;bbb-:this@;ccc-:this@;ddd-:this@;eee-:this@;fff-:this@;ggg-:this@;hhh";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-:this@;")));
    }

    @Test    public void guessDelimiterContainingDigitSingleRow() throws Exception{
        String inString = "aaa-0bbb-0ccc-0ddd-0eee-0fff-0ggg-0hhh";
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-0")));
    }

    @Test    public void guessDelimiterRepeatedOnce() throws Exception{
        String inString = "a-,-,b-,c";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-,")));
    }
    @Test    public void guessDelimiterRepeatedEverywhere() throws Exception{
        String inString = "a-,-,b-,-,c";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-,-,")));
    }

    @Test    public void guessUniformDelimiterRepeatedOnce() throws Exception{
        String inString = "a----b--c";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("--")));
    }

    @Test    public void guessUniformDelimiterRepeatedEverywhere() throws Exception{
        String inString = "a----b----c";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("----")));
    }

    @Test    public void guessDelimiterExtended() throws Exception{
        String inString = "a-,.b-,.c";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-,.")));
    }

    //Not sure if this is the desired output or not really...
    @Test    public void guessDelimiterExtendedWithinRepeated() throws Exception{
        String inString = "a-,-,b-,-c";
        Att att = convertToAtt(inString);

        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-,-")));
    }


    //Negative examples. Technically it might be ok to guess "-A" is a delimiter in some situations, but most of the time (like here) it's not going to be correct & result in a useful att.
    @Test    public void dontGuessDelimiterContainingCharacterSingleRow() throws Exception{
        String inString = "Apple-Aardvark-Ball-Aubergine-Bear-Australia-Animal-Cat-Algeria";
        Att att = convertToAtt(inString);


        List<TransformationService> guessedDelimiterTransformers = GuessedTypeTransformerSelector.getSplitOnGuessedDelimitersToRows(att);
        assertTrue(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-")));

        assertFalse(guessedDelimiterTransformers.contains(new SplitOnStringToRows("-A")));
        assertFalse(guessedDelimiterTransformers.contains(new SplitOnStringToRows("A")));
    }

}
