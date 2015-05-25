package database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rapidminer.RapidMiner;
import com.rapidminer.RapidMiner.ExecutionMode;
import com.rapidminer.tools.LogService;
import database.features.TypeGuesser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import processors.getInputFromFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class ChartJsonCreator {

	public static boolean isInteresting(Att att ) throws SQLException{
		String guessedType = TypeGuesser.guessType(att);
		return !att.isDuplicate() && (guessedType.equals("integer") || guessedType.equals("float")) && att.getUniqueRowsInTable() > 4 && att.getNotNullRowsInTable() > 4 && att.getNotNullRowsInTable() < 100 && att.getFirstRow().length() <= 10 && att.getFirstRow().length() >= 1;
	}

	public static String createBestCharts(AttRelationshipGraph tree)
			throws SQLException {

		//Takes a tree of atts, processors and relationships, does analysis to determine what the most interesting charts that could be created are,
		// then returns the JSON required to create those charts.

		Collection<Att> atts = tree.getAllAtts();
		Collection<Att> interestingAtts = new ArrayList<>();
		for( Att att : atts ){
			if( isInteresting(att)){
				interestingAtts.add(att);
			}
		}
		
		JSONArray charts = new JSONArray();
		chartLoop:
		for( Att attX : interestingAtts ){
			for( Att attY : interestingAtts ){
				if( attX != attY && attX.getDataTable() == attY.getDataTable() && attX.getNotNullRowIds().equals( attY.getNotNullRowIds() ) && !attX.getAncestorAtts().contains(attY) && !attX.getDescendantAtts().contains(attY) ){
					JSONObject chart = new JSONObject();
					ArrayList<String> dataX = attX.getData();
					ArrayList<String> dataY = attY.getData();
					JSONArray values = new JSONArray();
					for( int i=0; i < dataX.size(); i++ ){
						try{
							JSONObject point = new JSONObject();
							point.put("x", Float.parseFloat(dataX.get(i)) );
							point.put("y", Float.parseFloat(dataY.get(i)) );
							values.add(point);
						}
						catch( NumberFormatException e ){ //Ignore points that are not integers.
						}
					}
					JSONObject series = new JSONObject();
					series.put("key", "series1");
					series.put("values", values);
					JSONArray seriesList = new JSONArray();
					seriesList.add(series);
					chart.put("series", seriesList);
					chart.put("xAxisLabel", attX.getGenerator().getName() );
					chart.put("xAxisAttOrder", attX.getAttOrder() );
					chart.put("yAxisLabel", attY.getGenerator().getName() );
					chart.put("yAxisAttOrder", attY.getAttOrder() );
					charts.add( chart );
				}
			}
		}

		//Create a png chart for each of the charts we have JSON for.
		int chartNum = 0;
		for( JSONObject jsonChart : (List<JSONObject>) charts ){
			XYSeriesCollection allSeries = new XYSeriesCollection();
			for( JSONObject jsonSeries : (List<JSONObject>)jsonChart.get("series") ){
				XYSeries series = new XYSeries((String)jsonSeries.get("key"));
				for( JSONObject point : (List<JSONObject>)jsonSeries.get("values") ){
					series.add( (Float)point.get("x"), (Float)point.get("y") );
				}
				allSeries.addSeries(series);
			}

			JFreeChart chart = ChartFactory.createScatterPlot(
				null,
				(String)jsonChart.get("xAxisLabel"),
				(String)jsonChart.get("yAxisLabel"),
				allSeries,
				PlotOrientation.VERTICAL,
				false,
				false,
				false);

			try{
				ChartUtilities.saveChartAsPNG(new File("runOutput/latest/chartThumbnails/"+jsonChart.get("xAxisAttOrder")+"vs"+jsonChart.get("yAxisAttOrder")+".png"), chart, 250, 250);
			}catch( IOException e ){
				e.printStackTrace();
			}

			chartNum++;
		}


		try{
			FileOutputStream fos = new FileOutputStream("runOutput/latest/charts.json");
			OutputStreamWriter jsonoutfile = new OutputStreamWriter(fos, "UTF-8");
	
			jsonoutfile.write(charts.toJSONString());
			jsonoutfile.close();
			
		}catch(FileNotFoundException e ){
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return charts.toJSONString();
	}

	public static void createAttGenerationJson( AttRelationshipGraph tree){
		for( Att att : tree.getAllAtts() ){
			try{
				if( isInteresting(att)){
					AttRelationshipGraph.createGraphJson(tree.getSubGraph(att), "att"+att.getAttOrder(), true);
				}
			}catch( SQLException e ){
				e.printStackTrace();
			}
		}
	}

	public void serve(){
		Server server = new Server(81);
		server.setHandler(new HelloHandler() );

		try{
			server.start();
			server.join();
		}
		catch( Exception e ){
			e.printStackTrace();
		}
	}

	public class HelloHandler extends AbstractHandler
	{
		public void handle(String target,Request baseRequest,HttpServletRequest request,HttpServletResponse response)
			throws IOException, ServletException
		{
			response.setContentType("text/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			response.getWriter().println("<h1>Hello World</h1>");
		}
	}
}
