package database;

import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.LogService;
import gui.GuiDatabase;
import processors.getInputFromFile;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

	
	public static Att initDb() throws SQLException
	{
		return initDb("pattern_activedata", new AttRelationshipGraph());
	}
	
	public static Att initDb(AttRelationshipGraph graph) throws SQLException
	{
		return initDb("pattern_activedata", graph);
	}
	
	public static Att initDb(String dbName, AttRelationshipGraph graph) throws SQLException
	{
        Database.reconnect(dbName); //Open new connection.
		
		String sql = "DROP DATABASE `"+dbName+"`";
		
		Database.doWriteQuery(sql);
		
		sql = "CREATE DATABASE `"+dbName+"`";
		
		Database.doWriteQuery(sql);

		Database.reconnect(dbName);
		
		//Create the root "const" att which contains no data.
		DataTable rootTable = new DataTable(); 
		Att constant = new Att(rootTable, graph);

		
		HashMap<Att, String> rootRowValues = new HashMap<>();
		rootRowValues.put(constant, "root");
		rootTable.insert(0, rootRowValues);
		rootTable.save();
		  
		return constant;
	}
	
	
	
	public static void main(String[] args) throws SQLException, OperatorCreationException, OperatorException, ParseException {
		
		Att constant = null;
		AttRelationshipGraph graph = new AttRelationshipGraph();
		try {
			constant = initDb("pattern_activedata", graph);
			
			LogService logger = LogService.getGlobal();
			RapidMiner.setExecutionMode(ExecutionMode.COMMAND_LINE);
			logger.setVerbosityLevel(LogService.ERROR);
			RapidMiner.init();
		} catch (SQLException e) {
			System.err.println("Couldn't initialize DB:");
			e.printStackTrace();
			System.exit(1);
		}
		
		ArrayList<Att> inputAtts = new ArrayList<Att>();
		inputAtts.add(constant);
		//Processor importer = new Processor( new getInputFromFile("testdata/soml-10000questions.csv"), inputAtts, constant.getDataTable() );
		//Processor importer = new Processor( new getInputFromFile("testdata/soml-100questions.csv"), inputAtts, constant.getDataTable() );
		//Processor importer = new Processor( new getInputFromFile("testdata/soml-20questions.csv"), inputAtts, constant.getDataTable() );
		//Processor importer = new Processor( new getInputFromFile("testdata/soml-3questions.csv"), inputAtts, constant.getDataTable() );
		//Processor importer = new Processor( new getInputFromFile("testdata/soml-alphabetagamma-questions.csv"), inputAtts, constant.getDataTable() );
		Processor importer = new Processor( new getInputFromFile("testdata/01010.dat"), inputAtts, constant.getDataTable() );
		//Processor importer = new Processor( new getInputFromFile("testdata/001100.dat"), inputAtts, constant.getDataTable() );
		importer.doWork();
		
		Att longStringAtt = importer.getOutputAtts().get(0);
		longStringAtt.setNotes("longStringAtt");
		
		
		GuiDatabase gui = new GuiDatabase();
		gui.addCenter(longStringAtt);
		gui.displayGraph();
		//gui.displayGraph(AttRelationshipGraph.getGraph());
		
		
		
		/*
		for( int i=0; i < 2; i++ )
		{
			ProcessorSelector processorSelector = new AttInterestingnessProcessorSelector();
			List<Processor> possibleProcessors = processorSelector.getBestProcessors(AttRelationshipGraph.getGraph());
			
			for( Processor processor : possibleProcessors )
			{
				System.out.println("Running "+processor.getName()+"...");
				processor.doWork();
			}

		}
		
		
		
		
		
		//AttRelationshipGraph.createDot();
		
		

		
		
		List<Relationship> possibleRelationships = RelationshipSelector.getBestRelationships(AttRelationshipGraph.getGraph());
		
		HashSet<Att> printedRelationships = new HashSet<>();
		
		for(Relationship rel : possibleRelationships )
		{
			String debugMsg = "Estimating performance for "+rel.getName()+", predicting "+rel.getLabel().getName() + " from ";
			for( Att inputAtt : rel.getInputAtts() )
			{
				debugMsg += inputAtt.getName() + ", ";
			}
			System.out.println(debugMsg);
			rel.estimatePerformanceUsingXValidation();
			if( rel.getAccuracy() > 0.9 && !printedRelationships.contains(rel.getLabel()) && rel.getLabel().getUniqueRowsInTable() > 1)
			{
				printedRelationships.add(rel.getLabel());
				rel.learn();
				System.out.println(rel);
				System.out.println();
			}
			
		}
		*/

		
		//AttRelationshipGraph.createDot();
		
		
		
		
	}
}
