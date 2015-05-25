package junit.database.inout;

import database.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import processors.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEstimateJoinedSize {
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}

    private void compareRealAndEstimated( List<Att> toJoin, DataTable RRT ) throws SQLException {
        String SQL = DataTable.getSqlStringJoiningInputs( RRT, toJoin );
        ResultSet realJoinedResultSet = Database.doQuery(SQL);
        realJoinedResultSet.last();
        int realSize = realJoinedResultSet.getRow();
        int estimatedSize = DataTable.estimateJoinedSize( toJoin, RRT );
        assertEquals( realSize, estimatedSize );
    }

    private void compareRealAndMaximumEstimated( List<Att> toJoin, DataTable RRT ) throws SQLException {
        String SQL = DataTable.getSqlStringJoiningInputs( RRT, toJoin );
        ResultSet realJoinedResultSet = Database.doQuery(SQL);
        realJoinedResultSet.last();
        int realSize = realJoinedResultSet.getRow();
        int maximumEstimatedSize = DataTable.estimateJoinedSize(toJoin, RRT, true);
        int estimatedSize = DataTable.estimateJoinedSize(toJoin, RRT);
        String debugString = "Real size: "+realSize+ ", MaximumEstimate: "+maximumEstimatedSize+", Estimate: "+estimatedSize+ ", RRT:"+RRT+", Atts: ";
        for( Att att : toJoin){
            debugString += att.getDbColumnNameNoQuotes()+" ";
        }
        assertTrue(realSize <= maximumEstimatedSize);
    }

    private void parseTestData(String testDataFilename) throws SQLException{
        /*Construct a tree that looks like:
         [a LongStringAtt 1row]
           /          \
          [b 4]      [c 5]
          /  \       /   \
        [g6] [f4]   [d10] [e10]

         There are some intermediary atts (eg. to go from c to d, we do separate getDigits and splitCharactersToRows operators because there's no single transformer that does both).
        */

        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile(testDataFilename), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);
        Att A = longStringAtt;

        Processor splitLongStringAtt = new Processor( new JsonDecodeMapManyColsOut(), importer.getOutputAtts(), longStringAtt.getDataTable());
        splitLongStringAtt.doWork();

        Processor splitToBRows = new Processor( new jsonDecodeList(), Arrays.asList(splitLongStringAtt.getOutputAtts().get(0)), splitLongStringAtt.getOutputAtts().get(0).getDataTable());
        splitToBRows.doWork();
        Att B = splitToBRows.getOutputAtts().get(1);

        Processor splitToCRows = new Processor( new jsonDecodeList(), Arrays.asList(splitLongStringAtt.getOutputAtts().get(1)), splitLongStringAtt.getOutputAtts().get(1).getDataTable());
        splitToCRows.doWork();
        Att C = splitToCRows.getOutputAtts().get(1);

        //Make sure the atts were extracted correctly.
        assertTrue( B.getNotNullRowsInTable() == 4);
        assertTrue( C.getNotNullRowsInTable() == 5);
        assertTrue( longStringAtt.getNotNullRowsInTable() == 1);
        assertTrue( !B.getDataTable().equals(C.getDataTable()));

        //Estimate the sizes of various atts joined together.
        compareRealAndEstimated(Arrays.asList(A), A.getDataTable());
        compareRealAndEstimated(Arrays.asList(B), B.getDataTable());
        compareRealAndEstimated(Arrays.asList(C), C.getDataTable());
        compareRealAndEstimated(Arrays.asList(A,B), A.getDataTable()); //One att parent of another att, parent is RRT.
        compareRealAndEstimated(Arrays.asList(A,B), B.getDataTable()); //One att parent of another att, child is RRT.
        assertEquals( DataTable.estimateJoinedSize(Arrays.asList(B,C),B.getDataTable()), 20 );
        compareRealAndEstimated(Arrays.asList(B,C), C.getDataTable()); //Siblings. One sibling is RRT.
        compareRealAndEstimated(Arrays.asList(B,C), A.getDataTable()); //Siblings. Common Ancestor is RRT.

        //Create the right side 3rd "row" of the tree (D and E).
        Processor extractDigits = new Processor(new Digits(), Arrays.asList(C), C.getDataTable());
        extractDigits.doWork();
        Processor extractAlphas = new Processor(new AlphabetLetters(), Arrays.asList(C), C.getDataTable());
        extractAlphas.doWork();

        Processor digitsToRows = new Processor(new SplitCharactersToRows(), Arrays.asList(extractDigits.getOutputAtts().get(0)), extractDigits.getOutputAtts().get(0).getDataTable());
        digitsToRows.doWork();
        Processor alphasToRows = new Processor(new SplitCharactersToRows(), Arrays.asList(extractAlphas.getOutputAtts().get(0)), extractAlphas.getOutputAtts().get(0).getDataTable());
        alphasToRows.doWork();
        Att D = digitsToRows.getOutputAtts().get(1);
        Att E = alphasToRows.getOutputAtts().get(1);

        assertEquals( D.getNotNullRowsInTable(), 10);
        assertEquals( E.getNotNullRowsInTable(), 10);

        compareRealAndEstimated( Arrays.asList(D,C), C.getDataTable());
        compareRealAndEstimated(Arrays.asList(D, E), D.getDataTable());
        assertEquals( DataTable.estimateJoinedSize(Arrays.asList(D,E),D.getDataTable()), 20 );
        compareRealAndEstimated(Arrays.asList(D, E), E.getDataTable());
        compareRealAndEstimated(Arrays.asList(D, E), C.getDataTable());
        compareRealAndEstimated(Arrays.asList(D, E), A.getDataTable());
        compareRealAndEstimated( Arrays.asList(D,A), C.getDataTable());
        compareRealAndEstimated( Arrays.asList(D,E,C), C.getDataTable());
        compareRealAndEstimated(Arrays.asList(D, E, C), C.getDataTable());


        //Create the left side 3rd "row" of the tree (G and F).
        Processor splitBMaps = new Processor( new JsonDecodeMapManyColsOut(), Arrays.asList(B), B.getDataTable());
        splitBMaps.doWork();
        //todo: test replacing splitBMaps with a jsonDecodeMapManyColsAndKeysOut, which takes in the entire "B" att (all rows at once) by using
        // RRT = A, and so F and G's datatables will derive from A instead of B's datatable.
        // Does this affect the num rows returned when joining atts (F,A) or (F,B)? Is the estimate still correct?

        Att F = splitBMaps.getOutputAtts().get(0);

        Processor splitGToRows = new Processor( new jsonDecodeList(), Arrays.asList(splitBMaps.getOutputAtts().get(1)), splitBMaps.getOutputAtts().get(1).getDataTable());
        splitGToRows.doWork();

        Att G = splitGToRows.getOutputAtts().get(1);

        assertEquals( F.getNotNullRowsInTable(), 4);
        assertEquals(G.getNotNullRowsInTable(), 6);

        //Test within the left sub-tree.
        compareRealAndEstimated(Arrays.asList(F, G), G.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,G), F.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,G), B.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,B), B.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,B), F.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A), A.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A,G), A.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A,B,G), A.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A,B,G), B.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A,B,G), F.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A,B), G.getDataTable());

        //Test across entire tree.
        compareRealAndEstimated(Arrays.asList(D,E,F,G), A.getDataTable());
        assertEquals( DataTable.estimateJoinedSize(Arrays.asList(D,E,F,G),A.getDataTable()), 120 );

        for( Att RRA : Arrays.asList(A,B,C,D,E,F,G) ) {
            for (Att input1 : Arrays.asList(A, B, C, D, E, F, G)) {
                for (Att input2 : Arrays.asList(A, B, C, D, E, F, G, null)) {
                    for (Att input3 : Arrays.asList(A, B, C, D, E, F, G, null)) {
                        ArrayList<Att> inputs = new ArrayList<>();
                        inputs.add(input1);
                        if (input2 != null) {
                            inputs.add(input2);
                        }
                        if (input3 != null) {
                            inputs.add(input3);
                        }
                        compareRealAndEstimated(inputs, RRA.getDataTable());
                    }
                }
            }
        }
    }

	@Test public void testRegular() throws Exception
	{
        //In this test, every row has at least one child in all of its child atts. Ie. for every C, there is at least one D and E.
        this.parseTestData("testdata/testEstimateJoinedRowsData-regular.json");
    }

    @Test public void testIrregularSimple() throws Exception{
        /* Because the distributions are not regular, there are sometimes 3 child rows corresponding to 1 parent row, and sometimes 1 child row corresponding to the same parent row.
           So this makes the "average children per parent"-based calculation fail when there are 2 child atts with irregular distributions because:
           0x0 + 4x4 = 16 is greater than 2x2 + 2x2 = 8 even though both child atts have a total of 4 rows.
        */

        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new constantCreator("abcd1234,"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);
        Att A = longStringAtt;

        Processor splitLongStringAtt = new Processor( new SplitOnStringToRows(","), importer.getOutputAtts(), longStringAtt.getDataTable());
        splitLongStringAtt.doWork();
        Att twoRows = splitLongStringAtt.getOutputAtts().get(1);

        Processor extractDigits = new Processor(new Digits(), Arrays.asList(twoRows), twoRows.getDataTable());
        extractDigits.doWork();
        Processor extractAlphas = new Processor(new AlphabetLetters(), Arrays.asList(twoRows), twoRows.getDataTable());
        extractAlphas.doWork();

        Processor digitsToRows = new Processor(new SplitCharactersToRows(), Arrays.asList(extractDigits.getOutputAtts().get(0)), extractDigits.getOutputAtts().get(0).getDataTable());
        digitsToRows.doWork();
        Processor alphasToRows = new Processor(new SplitCharactersToRows(), Arrays.asList(extractAlphas.getOutputAtts().get(0)), extractAlphas.getOutputAtts().get(0).getDataTable());
        alphasToRows.doWork();
        Att B = digitsToRows.getOutputAtts().get(1);
        Att C = alphasToRows.getOutputAtts().get(1);

        assertTrue(B.getNotNullRowsInTable() == 4);
        assertTrue(C.getNotNullRowsInTable() == 4);

        //Check that the real size of the combined is 16 (a crossjoin between two atts of size 4).
        String SQL = DataTable.getSqlStringJoiningInputs( A.getDataTable(), Arrays.asList(B,C) );
        ResultSet realJoinedResultSet = Database.doQuery(SQL);
        realJoinedResultSet.last();
        int realSize = realJoinedResultSet.getRow();
        assertEquals(16, realSize);

        //Test that the worst-case-estimate matches what we actually have (because this is a worst-case situation).
        assertEquals(16, DataTable.estimateJoinedSize(Arrays.asList(B,C), A.getDataTable(), true));
    }

    @Test public void testIrregular() throws Exception
    {
        /*Same tree as above, but some of the rows of C have no corresponding rows in D or E.
		Likewise for B and G.
		Makes the estimates fail for the same reason as testIrregularSimple above.
		*/
        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new getInputFromFile("testdata/testEstimateJoinedRowsData-irregular.json"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);
        Att A = longStringAtt;

        Processor splitLongStringAtt = new Processor( new JsonDecodeMapManyColsOut(), importer.getOutputAtts(), longStringAtt.getDataTable());
        splitLongStringAtt.doWork();

        Processor splitToBRows = new Processor( new jsonDecodeList(), Arrays.asList(splitLongStringAtt.getOutputAtts().get(0)), splitLongStringAtt.getOutputAtts().get(0).getDataTable());
        splitToBRows.doWork();
        Att B = splitToBRows.getOutputAtts().get(1);

        Processor splitToCRows = new Processor( new jsonDecodeList(), Arrays.asList(splitLongStringAtt.getOutputAtts().get(1)), splitLongStringAtt.getOutputAtts().get(1).getDataTable());
        splitToCRows.doWork();
        Att C = splitToCRows.getOutputAtts().get(1);

        //Make sure the atts were extracted correctly.
        assertTrue( B.getNotNullRowsInTable() == 4);
        assertTrue( C.getNotNullRowsInTable() == 5);
        assertTrue( longStringAtt.getNotNullRowsInTable() == 1);
        assertTrue( !B.getDataTable().equals(C.getDataTable()));

        //Estimate the sizes of various atts joined together.
        compareRealAndMaximumEstimated(Arrays.asList(A), A.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(B), B.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(C), C.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(A,B), A.getDataTable()); //One att parent of another att, parent is RRT.
        compareRealAndMaximumEstimated(Arrays.asList(A,B), B.getDataTable()); //One att parent of another att, child is RRT.
        assertEquals( DataTable.estimateJoinedSize(Arrays.asList(B,C),B.getDataTable()), 20 );
        compareRealAndMaximumEstimated(Arrays.asList(B,C), C.getDataTable()); //Siblings. One sibling is RRT.
        compareRealAndMaximumEstimated(Arrays.asList(B,C), A.getDataTable()); //Siblings. Common Ancestor is RRT.

        //Create the right side 3rd "row" of the tree (D and E).
        Processor extractDigits = new Processor(new Digits(), Arrays.asList(C), C.getDataTable());
        extractDigits.doWork();
        Processor extractAlphas = new Processor(new AlphabetLetters(), Arrays.asList(C), C.getDataTable());
        extractAlphas.doWork();

        Processor digitsToRows = new Processor(new SplitCharactersToRows(), Arrays.asList(extractDigits.getOutputAtts().get(0)), extractDigits.getOutputAtts().get(0).getDataTable());
        digitsToRows.doWork();
        Processor alphasToRows = new Processor(new SplitCharactersToRows(), Arrays.asList(extractAlphas.getOutputAtts().get(0)), extractAlphas.getOutputAtts().get(0).getDataTable());
        alphasToRows.doWork();
        Att D = digitsToRows.getOutputAtts().get(1);
        Att E = alphasToRows.getOutputAtts().get(1);

        assertEquals( D.getNotNullRowsInTable(), 10);
        assertEquals( E.getNotNullRowsInTable(), 10);

        compareRealAndMaximumEstimated( Arrays.asList(D,C), C.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(D, E), D.getDataTable());
        assertEquals( DataTable.estimateJoinedSize(Arrays.asList(D,E),D.getDataTable()), 20 );
        compareRealAndMaximumEstimated(Arrays.asList(D, E), E.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(D, E), C.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(D, E), A.getDataTable());
        compareRealAndMaximumEstimated( Arrays.asList(D,A), C.getDataTable());
        compareRealAndMaximumEstimated( Arrays.asList(D,E,C), C.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(D, E, C), C.getDataTable());


        //Create the left side 3rd "row" of the tree (G and F).
        Processor splitBMaps = new Processor( new JsonDecodeMapManyColsOut(), Arrays.asList(B), B.getDataTable());
        splitBMaps.doWork();
        //todo: test replacing splitBMaps with a jsonDecodeMapManyColsAndKeysOut, which takes in the entire "B" att (all rows at once) by using
        // RRT = A, and so F and G's datatables will derive from A instead of B's datatable.
        // Does this affect the num rows returned when joining atts (F,A) or (F,B)? Is the estimate still correct?

        Att F = splitBMaps.getOutputAtts().get(0);

        Processor splitGToRows = new Processor( new jsonDecodeList(), Arrays.asList(splitBMaps.getOutputAtts().get(1)), splitBMaps.getOutputAtts().get(1).getDataTable());
        splitGToRows.doWork();

        Att G = splitGToRows.getOutputAtts().get(1);

        assertEquals( F.getNotNullRowsInTable(), 4);
        assertEquals(G.getNotNullRowsInTable(), 6);

        //Test within the left sub-tree.
        compareRealAndMaximumEstimated(Arrays.asList(F, G), G.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,G), F.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,G), B.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,B), B.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,B), F.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,A), A.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,A,G), A.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,A,B,G), A.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,A,B,G), B.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,A,B,G), F.getDataTable());
        compareRealAndMaximumEstimated(Arrays.asList(F,A,B), G.getDataTable());

        //Test across entire tree.
        compareRealAndMaximumEstimated(Arrays.asList(D,E,F,G), A.getDataTable());
        assertEquals( DataTable.estimateJoinedSize(Arrays.asList(D,E,F,G),A.getDataTable()), 120 );

        for( Att RRA : Arrays.asList(A,B,C,D,E,F,G) ) {
            for (Att input1 : Arrays.asList(A, B, C, D, E, F, G)) {
                for (Att input2 : Arrays.asList(A, B, C, D, E, F, G, null)) {
                    for (Att input3 : Arrays.asList(A, B, C, D, E, F, G, null)) {
                        ArrayList<Att> inputs = new ArrayList<>();
                        inputs.add(input1);
                        if (input2 != null) {
                            inputs.add(input2);
                        }
                        if (input3 != null) {
                            inputs.add(input3);
                        }
                        compareRealAndMaximumEstimated(inputs, RRA.getDataTable());
                    }
                }
            }
        }
    }

    @Test public void testSomeNulls() throws Exception
    {
        /*Some of the "F" keys in the JSON map are missing, so att "F" contains 2 nulls.
		*/
        Att constant = Main.initDb();

        ArrayList<Att> inputAtts = new ArrayList<Att>();
        inputAtts.add(constant);
        Processor importer = new Processor( new constantCreator("[{\"g\":[10,20], \"f\":15},{\"g\":[30,40]},{\"g\": [50,60], \"f\":55},{\"g\": [70,80]}]"), inputAtts, constant.getDataTable() );
        importer.doWork();

        Att longStringAtt = importer.getOutputAtts().get(0);
        Att A = longStringAtt;

        Processor splitListToMaps = new Processor( new jsonDecodeList(), Arrays.asList(A), A.getDataTable());
        splitListToMaps.doWork();

        Processor splitBMaps = new Processor( new JsonDecodeMapManyColsAndKeysOut(), Arrays.asList(splitListToMaps.getOutputAtts().get(1)), A.getDataTable());
        splitBMaps.doWork();

        Att F = splitBMaps.getOutputAtts().get(1);

        Processor splitGToRows = new Processor( new jsonDecodeList(), Arrays.asList(splitBMaps.getOutputAtts().get(3)), splitBMaps.getOutputAtts().get(3).getDataTable());
        splitGToRows.doWork();
        Att G = splitGToRows.getOutputAtts().get(1);

        assertEquals( 2, F.getNotNullRowsInTable());
        assertEquals( 8, G.getNotNullRowsInTable());

        compareRealAndEstimated(Arrays.asList(F,G), G.getDataTable()); //2x1 + 2x1 + 2x0 + 2x0 = 4. Estimate should average over rows and do: 2x0.5 + 2x0.5 + 2x0.5 + 2x0.5 = 4.
        compareRealAndEstimated(Arrays.asList(F,G), F.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A), A.getDataTable());
        compareRealAndEstimated(Arrays.asList(F,A,G), A.getDataTable());
    }


}
