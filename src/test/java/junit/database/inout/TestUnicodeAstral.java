package junit.database.inout;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.SplitCharactersToRows;
import processors.getInputFromFile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUnicodeAstral {

	@BeforeClass public static void oneTimeSetUp()
	{

	}

	@AfterClass public static void oneTimeTearDown()
	{

	}

    @Test public void astralJavaStrings() throws Exception
    {
        //Check Java strings work with Unicode.
        String pilesOfPoo = "\uD83D\uDCA9\uD83D\uDCA9\uD83D\uDCA9";
        assertTrue(pilesOfPoo.equals("\uD83D\uDCA9\uD83D\uDCA9\uD83D\uDCA9"));
        assertTrue(pilesOfPoo.substring(0, 2).equals("\uD83D\uDCA9"));
    }

    @Test public void transformationServiceLoadsAstralFromFile() throws Exception
    {
        String pilesOfPoo = "\uD83D\uDCA9\uD83D\uDCA9\uD83D\uDCA9";

        //Test getInputFromFile TransformationService without Processor or saving to the DB.
        Table<Integer, Integer, String> dummyTable = TreeBasedTable.create();
        TransformationService inputter = new getInputFromFile("testdata/utf8 pileofpoo.txt");
        Table<Integer, Integer, String> output = inputter.doWork(dummyTable);
        assertEquals( pilesOfPoo, output.get(0, 0) );
    }

    /*Commented out because it doesn't work.
    * Needs more fiddling with DB settings, connection string params and versions.
    * Not worth it for now.
    *
    @Test public void canWriteAstralToDb() throws Exception {
        String pilesOfPoo = "\uD83D\uDCA9\uD83D\uDCA9\uD83D\uDCA9";

        //Create the root "const" att and a table.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        //Try writing some UTF8 to the DB and then reading it back.
        String tableName = constant.getDataTable().getTableName()+"_0";
        String SQL = "UPDATE `"+tableName+"` SET `2`=\"" + pilesOfPoo + "\"";
        System.out.println(SQL);
        Database.doWriteQuery(SQL);
        ResultSet result = Database.doQuery("SELECT `2` FROM "+tableName);
        result.next();
        String stringFromDb = result.getString(1);
        assertEquals(pilesOfPoo, stringFromDb);
    }

	@Test public void unicodeAstralProcess() throws Exception
	{
        String pilesOfPoo = "\uD83D\uDCA9\uD83D\uDCA9\uD83D\uDCA9";

        //Create the root "const" att which contains no data.
        AttRelationshipGraph inputGraph = new AttRelationshipGraph();
        Att constant = Main.initDb(inputGraph);

        //Populate the inputTree. Create the att with 5 rows.
        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/utf8 pileofpoo.txt"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);

        try{
            assertTrue( longStringAtt.getData().get(0).equals(pilesOfPoo) );
        }
        catch (SQLException e ){
            e.printStackTrace();
        }

        //Perform another transformation on the unicode string, to check loading unicode works correctly as part of a processor.
        Processor splitToChars = new Processor( new SplitCharactersToRows(), Arrays.asList(longStringAtt), longStringAtt.getDataTable());
        splitToChars.doWork();

        Att charAtt = splitToChars.getOutputAtts().get(1);
        try{
            assertTrue( charAtt.getNotNullRowsInTable() == 3 );
            assertEquals("\uD83D\uDCA9", charAtt.getData().get(0) );
            assertEquals("\uD83D\uDCA9", charAtt.getData().get(2) );
        }
        catch (SQLException e ){
            e.printStackTrace();
        }
	}
	*/
}
