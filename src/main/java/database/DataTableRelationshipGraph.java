package database;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.apache.commons.collections15.Factory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;

public class DataTableRelationshipGraph extends DelegateForest<DataTable, GraphEdge>{
	
	public DataTableRelationshipGraph()
	{	}
	
	public DelegateForest<DataTable, GraphEdge> copyGraph()
	{
		DelegateForest<DataTable, GraphEdge> copy = new DelegateForest<>();
		
		for(DataTable v : this.getVertices())
		{
			copy.addVertex(v);
		}
		
		for(GraphEdge e : this.getEdges())
		{
			copy.addEdge(e, this.getIncidentVertices(e));
		}
		
		return copy;
	}
	
    public static Factory<UndirectedGraph<DataTable, GraphEdge>> getUndirectedSparseGraphFactory()
    {
        return new Factory<UndirectedGraph<DataTable, GraphEdge>>() {

            public UndirectedGraph<DataTable, GraphEdge> create()
            {
                return new UndirectedSparseGraph<DataTable, GraphEdge>();
            }

        };
    }
    
    public static DataTableRelationshipGraph createDataTableGraph( AttRelationshipGraph attGraph ){
    	Set<DataTable> dataTables = attGraph.getAllDatatables();
    	
    	DataTableRelationshipGraph ret = new DataTableRelationshipGraph();
    	for( DataTable dataTable : dataTables ){
    		ret.addVertex(dataTable);
    	}
    	for( DataTable dataTable : dataTables ){
    		for( DataTable childTable : dataTable.getChildTables() ){
    			if( dataTables.contains( childTable) ){
    				ret.addEdge( new GraphEdge(), dataTable, childTable);
    			}
    		}
    	}
    	
    	return ret;
    }
    
    public void createDot(String filename)
    {
    	createDot( this, filename );
    }
    
    public static void createDot( Graph<DataTable, GraphEdge> graph, String filename )
    {
        System.out.print("Creating datatable relationship graph dot...");

        String out = "digraph G {";
    	
    	for( DataTable vertex : graph.getVertices() )
    	{
    		String vertexName = vertex.toString().replace("\"", "\\\"").replace("\n", "\\n");
    		if( vertexName.length() > 50 )
    		{
    			vertexName = vertexName.substring(0, 47)+"...";
    		}
    		String shape = "box";
    		
    		out += "\n	"+vertex.hashCode()+" [shape="+shape+",label=\""+vertexName+"\",id="+vertex.hashCode()+"]";
    	}
    	
    	for(GraphEdge edge : graph.getEdges() )
    	{    		
    		ArrayList<DataTable> ends = new ArrayList<>( graph.getIncidentVertices(edge) );
    		
    		DataTable source = ends.get(0);
    		DataTable dest = ends.get(1);
    		
    		out += "\n	\""+source.hashCode()+"\" -> \""+dest.hashCode()+"\" ";
    	}
    	out += "\n}";
    	
    	
    	JSONObject parentsAndChildrenJson = createParentChildJson( graph );
    	
		try {
			FileOutputStream fos = new FileOutputStream("runOutput/latest/"+filename+".dot");
			OutputStreamWriter graphoutfile = new OutputStreamWriter(fos, "UTF-8");
			
			graphoutfile.write(out);
			graphoutfile.close();
			
			FileOutputStream fos2 = new FileOutputStream("runOutput/latest/"+filename+".json");
			OutputStreamWriter jsonoutfile = new OutputStreamWriter(fos2, "UTF-8");
			
			jsonoutfile.write(parentsAndChildrenJson.toJSONString());
			jsonoutfile.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		runDot(filename);

        System.out.println("Ran Dot.");

    }
    
    private static void runDot(String filename) {
		ProcessBuilder pb = new ProcessBuilder("dot", filename+".dot", "-Tsvg", "-o"+filename+".svg" );
		 Map<String, String> env = pb.environment();
		 pb.directory(new File("runOutput/latest"));
		 try {
			Process p = pb.start();
			//Print the output of dot.
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while((line = in.readLine()) != null) {
			  System.out.println(line);
			}
			
			//Print any errors with dot.
			BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String lineer = null;
			while((lineer = er.readLine()) != null) {
			  System.err.println(lineer);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static JSONObject createParentChildJson( Graph<DataTable, GraphEdge> graph )
	{
		JSONObject out = new JSONObject();
		for( DataTable vertex : graph.getVertices() )
		{
			JSONArray parents = new JSONArray();
			JSONArray children = new JSONArray();
			
			for( DataTable parent : graph.getPredecessors(vertex) )
			{
				parents.add(parent.hashCode());
			}
			for( DataTable child : graph.getSuccessors(vertex) )
			{
				children.add( child.hashCode() );
			}

			JSONObject relatives = new JSONObject();
			relatives.put("parents", parents);
			relatives.put("children", children);
			
			out.put(vertex.hashCode(), relatives);
		}
		return out;
	}
	
	public static DataTable findLatestCommonAncestor( DataTable att1, DataTable att2 )
	{
		if( att1 == att2 )
		{
			return att1;
		}
		HashSet<DataTable> ancestors1 = new HashSet<>( att1.getAncestorTables() );
		ancestors1.add( att1 );
		HashSet<DataTable> ancestors2 = new HashSet<>(att2.getAncestorTables() );
		ancestors2.add( att2 );
		
		SetView<DataTable> commonAncestors = Sets.intersection(ancestors1, ancestors2);
		
		DataTable latestTable = null;
		int latestAttOrder = -1;
		for( DataTable ancestor : commonAncestors )
		{
			if( ancestor.getTableOrder() > latestAttOrder )
			{
				latestTable = ancestor;
				latestAttOrder = ancestor.getTableOrder();
			}
		}
		return latestTable;
	}
	
	public static DataTable findLatestCommonAncestor( Collection<DataTable> tables )
	{
		DataTable bestAncestor = tables.iterator().next(); 
		if( tables.size() == 1 )
		{
			return bestAncestor;
		}
		for( DataTable table : tables)
		{
			bestAncestor = findLatestCommonAncestor(bestAncestor, table);
		}
		return bestAncestor;
	}
}
