package junit.database.inout;

import database.Att;
import database.AttRelationshipGraph;
import database.Main;
import database.Processor;
import database.features.TypeGuesser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitCharactersToRows;
import processors.SplitOnStringToRows;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class TestTypeGuesser {
    private AttRelationshipGraph inputGraph;
    private Att rootConstant;
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{
	}

    private Att convertToAtt(String inString) throws SQLException {
        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att rootConstant = Main.initDb(inputGraph);

        Processor constantCreator = new Processor( new processors.constantCreator(inString), Arrays.asList(rootConstant), rootConstant.getDataTable() );
        constantCreator.doWork();

        Att longStringAtt = constantCreator.getOutputAtts().get(0);

        //Split // to rows, so we have 3 rows to try to guess delimiters from.
        Processor splitter = new Processor(new SplitOnStringToRows("//"), Arrays.asList(longStringAtt), rootConstant.getDataTable() );
        splitter.doWork();

        return splitter.getOutputAtts().get(1);
    }


    @Test    public void guessWord() throws Exception{
        String inString = "elephant";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("word"));
    }

    @Test    public void guessHTML() throws Exception{
        String inString = "<html><body>Stuff</body></html>";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("HTML"));
    }


    @Test    public void guessFile() throws Exception{
        String inString = "my_filename.csv";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("file"));
    }


    @Test    public void guessFilePath() throws Exception{
        String inString = "/somewhere/something/dev/null";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("file"));
    }

    @Test    public void guessCharacter() throws Exception{
        String inString = "a";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("character"));
    }

    @Test    public void guessDollarCharacter() throws Exception{
        String inString = "$";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("character"));
    }

    @Test    public void guessManyRowsOfCharacter() throws Exception{
        String inString = "$//4//9//a//b//c";
        Att att = convertToAtt(inString);

        String guessedType = TypeGuesser.guessType(att);
        assertTrue(guessedType.equals("character"));
    }

    @Test    public void guessSplitCharacters() throws Exception{
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att rootConstant = Main.initDb(inputGraph);

        Processor importer = new Processor(new getInputFromFile("testdata/expenses to csv - currency name/trainIn.dat"), Arrays.asList(rootConstant), rootConstant.getDataTable());
        importer.doWork();

        Processor splitter = new Processor(new SplitCharactersToRows(), importer.getOutputAtts(), rootConstant.getDataTable() );
        splitter.doWork();

        String guessedType = TypeGuesser.guessType(splitter.getOutputAtts().get(1));
        assertTrue(guessedType.equals("character"));
    }

}
