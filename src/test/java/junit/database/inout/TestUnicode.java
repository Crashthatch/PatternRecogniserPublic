package junit.database.inout;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.*;
import net.didion.jwnl.data.Exc;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestUnicode {

	@BeforeClass public static void oneTimeSetUp()
	{

	}

	@AfterClass public static void oneTimeTearDown()
	{

	}

    @Test public void chineseJavaStrings() throws Exception
    {
        //Check Java strings work with Unicode.
        String chineseString = "道禾六藝文化館";
        assertTrue(chineseString.equals("道禾六藝文化館"));
        assertTrue(chineseString.substring(0, 1).equals("道"));
    }

    @Test public void transformationServiceLoadsChineseFromFile() throws Exception
    {
        String chineseString = "道禾六藝文化館";

        //Test getInputFromFile TransformationService without Processor or saving to the DB.
        Table<Integer, Integer, String> dummyTable = TreeBasedTable.create();
        TransformationService inputter = new getInputFromFile("testdata/utf8 chinese.txt");
        Table<Integer, Integer, String> output = inputter.doWork(dummyTable);
        assertEquals( chineseString, output.get(0, 0) );
    }

    @Test public void canWriteChineseToDb() throws Exception {
        String chineseString = "道禾六藝文化館";

        //Create the root "const" att and a table.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        //Try writing some UTF8 to the DB and then reading it back.
        String tableName = constant.getDataTable().getTableName()+"_0";
        Database.doWriteQuery("UPDATE `"+tableName+"` SET `2`=\"" + chineseString + "\"");
        ResultSet result = Database.doQuery("SELECT `2` FROM "+tableName);
        result.next();
        String stringFromDb = result.getString(1);
        assertEquals(chineseString, stringFromDb);
    }

    /**
     *
     * @throws java.sql.SQLException
     */
	@Test public void unicodeChineseProcess() throws Exception
	{
        String chineseString = "道禾六藝文化館";

        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        //Populate the inputTree. Create the att with 5 rows.
        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/utf8 chinese.txt"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        try{
            assertTrue( longStringAtt.getData().get(0).equals(chineseString) );
        }
        catch (SQLException e ){
            e.printStackTrace();
        }

        //Perform another transformation on the unicode string, to check loading unicode works correctly as part of a processor.
        Processor splitToChars = new Processor( new SplitCharactersToRows(), Arrays.asList(longStringAtt), longStringAtt.getDataTable());
        splitToChars.doWork();

        Att charAtt = splitToChars.getOutputAtts().get(1);
        try{
            assertTrue( charAtt.getNotNullRowsInTable() == 7 );
            assertEquals("道", charAtt.getData().get(0) );
            assertEquals("藝", charAtt.getData().get(3) );
        }
        catch (SQLException e ){
            e.printStackTrace();
        }
	}
}
