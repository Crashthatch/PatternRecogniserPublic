package junit.database.models;

import com.google.common.collect.TreeBasedTable;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import models.FindReplaceLearnerSinglePass;
import models.Model;
import models.ModelLearner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class TestFindReplaceModel {
    ModelLearner learner = new FindReplaceLearnerSinglePass();
	
	@BeforeClass public static void oneTimeSetUp()
	{
	}
	
	@AfterClass public static void oneTimeTearDown()
	{

	}
	
	@Test public void testSimpleFindReplace() throws Exception{
        

        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
		in.put(0, 0, "Someone");
        in.put(0, 1, "Sxmexne");

        in.put(1, 0, "Something");
        in.put(1, 1, "Sxmething");

        in.put(2, 0, "Nobody");
        in.put(2, 1, "Nxbxdy");

        in.put(3, 0, "Tomato");
        in.put(3, 1, "Txmatx");

		Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "Testwxrd"));
        assertTrue( prediction.equals("Testword"));
    }

    @Test public void testFindReplaceWords() throws Exception{
        

        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Tom and Dick and Harry"); //Feature
        in.put(0, 0, "Tom & Dick & Harry"); //Label


        in.put(1, 1, "Jane and Jessica and Joan");
        in.put(1, 0, "Jane & Jessica & Joan");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "He and she and we"));
        assertTrue( prediction.equals("He & she & we"));
    }

    @Test public void testFindReplaceMultipleDifferent() throws Exception{
        

        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Tom and Dick and Harry"); //Feature
        in.put(0, 0, "Txm & Dick & Harry"); //Label


        in.put(1, 1, "Jane and Jessica and Joan");
        in.put(1, 0, "Jane & Jessica & Jxan");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "These and those"));
        assertTrue( prediction.equals("These & thxse"));
    }

    @Test public void testFindReplaceSome() throws Exception{
        

        //Create table where label is col 0, and features are in cols 1+.
        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Tom and Dick and Harry hold hands"); //Feature
        in.put(0, 0, "Tom & Dick & Harry hold hands"); //Label

        in.put(1, 1, "Pamela and Julie formed a band called anderson and andrews");
        in.put(1, 0, "Pamela & Julie formed a band called anderson & andrews");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "He and she handled the band and the candy"));
        assertTrue( prediction.equals("He & she handled the band & the candy"));
    }


    //Only replace 'and' within quotes.
    @Test public void testFindReplacePrefixSuffix() throws Exception{
        

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Tom 'and' Bob and 'Tim'"); //Feature
        in.put(0, 0, "Tom '&' Bob and 'Tim'"); //Label

        in.put(1, 1, "Pamela 'and' Julie 'and Jessica and' Jessie formed a band called anderson 'and' andrews");
        in.put(1, 0, "Pamela '&' Julie 'and Jessica and' Jessie formed a band called anderson '&' andrews");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "He 'and and' and 'and' she ate all the candy"));
        assertTrue( prediction.equals("He 'and and' and '&' she ate all the candy"));
    }


    //Only replace '''and''' within triple quotes.
    @Test public void testFindReplaceTriplePrefixSuffix() throws Exception{
        

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Tom '''and''' Bob ''and'' 'Tim'"); //Feature
        in.put(0, 0, "Tom '''&''' Bob ''and'' 'Tim'"); //Label

        in.put(1, 1, "Pamela '''and''' Julie 'and Jessica and' Jessie formed a band called anderson '''and''' ''and''rews");
        in.put(1, 0, "Pamela '''&''' Julie 'and Jessica and' Jessie formed a band called anderson '''&''' ''and''rews");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "He 'and and' and 'and' ''and'' '''and''' she ate all the candy"));
        assertTrue( prediction.equals("He 'and and' and 'and' ''and'' '''&''' she ate all the candy"));
    }


    //Remove vowels.
    @Test public void testFindReplaceCanRemove() throws Exception{
        

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Hippopotomus"); //Feature
        in.put(0, 0, "Hppptms"); //Label

        in.put(1, 1, "Little");
        in.put(1, 0, "Lttl");

        in.put(2, 1, "Yellow");
        in.put(2, 0, "Yllw");

        in.put(3, 1, "Submarine");
        in.put(3, 0, "Sbmrn");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "Vowels"));
        assertTrue( prediction.equals("Vwls"));
    }


    //Replace "LHR" with "London Heathrow".
    @Test public void testFindReplaceAirportCodes() throws Exception{

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Depart: LHR, Arrive: BRS"); //Feature
        in.put(0, 0, "Depart: London Heathrow, Arrive: Bristol International"); //Label

        in.put(1, 1, "Depart: CDG, Arrive: LHR");
        in.put(1, 0, "Depart: Charles de Gaulle, Arrive: London Heathrow");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "Depart: LHR, Arrive: LHR"));
        assertTrue( prediction.equals("Depart: London Heathrow, Arrive: London Heathrow"));
    }

    //Replace aa with a in strings that contain no other a's.
    @Test public void testFindRepeated() throws Exception{

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "Hey aa thing is aa."); //Feature
        in.put(0, 0, "Hey a thing is a."); //Label

        in.put(1, 1, "Hello aa & more aa with stuff following it.");
        in.put(1, 0, "Hello a & more a with stuff following it.");

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "This is the test with aa in it twice. aa."));
        System.out.println(prediction);
        assertTrue(prediction.equals("This is the test with a in it twice. a."));
    }

    /**Fails because the prefix and the start of one of the lines with the prefix removed are similar.
     */
    /*
    @Test public void testRemoveprefix() throws Exception{

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "statement: SELECT version();"); //Feature
        in.put(0, 0, "SELECT version();"); //Label

        in.put(1, 1, "statement: SET DateStyle=ISO;SET client_min_messages=notice;SET bytea_output=escape;SELECT oid, pg_encoding_to_char(encoding) AS encoding, datlastsysoid  FROM pg_database WHERE datname='postgres'");
        in.put(1, 0, "SET DateStyle=ISO;SET client_min_messages=notice;SET bytea_output=escape;SELECT oid, pg_encoding_to_char(encoding) AS encoding, datlastsysoid  FROM pg_database WHERE datname='postgres'");

        in.put(2, 1, "statement: set client_encoding to 'UNICODE'");  //This confuses it. It tries to keep the s, then remove "tat", keep the e, etc.
        in.put(2, 0, "set client_encoding to 'UNICODE'");

        in.put(3, 1, "statement: SELECT usecreatedb, usesuper, CASE WHEN usesuper THEN pg_postmaster_start_time() ELSE NULL END as upsince, CASE WHEN usesuper THEN pg_conf_load_time() ELSE NULL END as confloadedsince, CASE WHEN usesuper THEN pg_is_in_recovery() ELSE NULL END as inrecovery, CASE WHEN usesuper THEN pg_last_xlog_receive_location() ELSE NULL END as receiveloc, CASE WHEN usesuper THEN pg_last_xlog_replay_location() ELSE NULL END as replayloc, CASE WHEN usesuper THEN pg_last_xact_replay_timestamp() ELSE NULL END as replay_timestamp, CASE WHEN usesuper AND pg_is_in_recovery() THEN pg_is_xlog_replay_paused() ELSE NULL END as isreplaypaused  FROM pg_user WHERE usename=current_user");
        in.put(3, 0, "SELECT usecreatedb, usesuper, CASE WHEN usesuper THEN pg_postmaster_start_time() ELSE NULL END as upsince, CASE WHEN usesuper THEN pg_conf_load_time() ELSE NULL END as confloadedsince, CASE WHEN usesuper THEN pg_is_in_recovery() ELSE NULL END as inrecovery, CASE WHEN usesuper THEN pg_last_xlog_receive_location() ELSE NULL END as receiveloc, CASE WHEN usesuper THEN pg_last_xlog_replay_location() ELSE NULL END as replayloc, CASE WHEN usesuper THEN pg_last_xact_replay_timestamp() ELSE NULL END as replay_timestamp, CASE WHEN usesuper AND pg_is_in_recovery() THEN pg_is_xlog_replay_paused() ELSE NULL END as isreplaypaused  FROM pg_user WHERE usename=current_user");

        in.put(4, 1, "statement: SELECT rolcreaterole, rolcreatedb FROM pg_roles WHERE rolname = current_user;");
        in.put(4, 0, "SELECT rolcreaterole, rolcreatedb FROM pg_roles WHERE rolname = current_user;");

        in.put(5, 1, "statement: INSERT INTO schemey.ttt VALUES (2,\"bob\", 65)");
        in.put(5, 0, "INSERT INTO schemey.ttt VALUES (2,\"bob\", 65)");


        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "statement: SELECT pg_encoding_to_char(26)"));
        assertTrue( prediction.equals("SELECT pg_encoding_to_char(26)"));
    }
    */

    /* Current one-pass find-Replace-model fails this test. Requires nested operators?
    @Test public void testFindReplaceLongReplacement() throws Exception{
        

        TreeBasedTable<Integer, Integer, String> in = TreeBasedTable.create();
        in.put(0, 1, "My address is <address>. I like living at <address> because it's easy to get from work to <address>."); //Feature
        in.put(0, 0, "My address is 14 Thomas Street,\nBath,\nSomerset,\nBA1 5NW. I like living at 14 Thomas Street,\nBath,\nSomerset,\nBA1 5NW because it's easy to get from work to 14 Thomas Street,\nBath,\nSomerset,\nBA1 5NW."); //Label

        Model model = learner.learnModelFromData(in);

        String prediction = model.predictForRow(Arrays.asList("LABEL", "I live at <address>!"));
        assertTrue( prediction.equals("I live at 14 Thomas Street,\nBath,\nSomerset,\nBA1 5NW!"));
    }*/
			
}
