package database;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import processors.modelApplier;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class AttRelationshipGraph extends DelegateForest<GraphVertex, GraphEdge>{
	
	public AttRelationshipGraph()
	{	}
	
	public DelegateForest<GraphVertex, GraphEdge> copyGraph()
	{
		DelegateForest<GraphVertex, GraphEdge> copy = new DelegateForest<>();
		
		for(GraphVertex v : this.getVertices())
		{
			copy.addVertex(v);
		}
		
		for(GraphEdge e : this.getEdges())
		{
			copy.addEdge(e, this.getIncidentVertices(e));
		}
		
		return copy;
	}
	
    public static Factory<UndirectedGraph<GraphVertex, GraphEdge>> getUndirectedSparseGraphFactory()
    {
        return new Factory<UndirectedGraph<GraphVertex, GraphEdge>>() {

            public UndirectedGraph<GraphVertex, GraphEdge> create()
            {
                return new UndirectedSparseGraph<GraphVertex, GraphEdge>();
            }

        }
;
    }
    
    public void createDotWithoutDuplicates( String filename )
    {
        AttRelationshipGraph subGraph = this.getSubGraph(this.getNonDuplicateAtts());
        subGraph.createDot(filename);
    }
    
    public void createDot(){
    	createDot(this, "graph-att");
    }
    
    public void createDot( String filename )
    {
    	createDot(this, filename);
    }
    
    public void createDot( AttRelationshipGraph graph, String filename )
    {
		System.out.print("Creating dot "+filename+"...");
		createGraphJson(graph, filename);

    	/*String out = "digraph G {";
    	
    	for( GraphVertex vertex : graph.getVertices() )
    	{
    		String vertexName = StringEscapeUtils.escapeJava(vertex.toString());
    		if( vertexName.length() > 200 )
    		{
    			vertexName = vertexName.substring(0, 200)+"...";
    		}

    		String shape = "none";
    		String color = "";
    		if( vertex.getClass() == Att.class)
    		{
    			Att vertexAtt = (Att)vertex;
    			shape="box";
    			try {
					if( vertexAtt.getNotNullRowsInTable() == 0 )
					{
						color = "style=filled,fillcolor=\"#C00000\"";
					}
					if( vertexAtt.isDuplicate() ){
						color = "style=filled,fillcolor=\"#C000C0\"";
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		else if( vertex.getClass() == Processor.class)
    		{
    			Processor vertexProcessor = (Processor) vertex;
    			shape="ellipse";
    			if( vertexProcessor.getSuccessfulTransformers() == 0 )
    			{
    				color = "style=filled,fillcolor=\"#C00000\"";
    			}
    			else if( vertexProcessor.getSuccessfulTransformers() < vertexProcessor.getNumberOfTransformers() )
    			{
    				color = "style=filled,fillcolor=\"#C06060\"";
    			}
    			else if( vertexProcessor.getTransformer().getClass() == modelApplier.class ){
    				color = "style=filled,fillcolor=\"#90F090\"";
    			}
    			
    			
    		}
    		else if( vertex.getClass() == Relationship.class)
    		{
    			Relationship vertexRelationship = (Relationship) vertex;
    			shape="hexagon";   	
    			
    			if( ! vertexRelationship.madeSuccessfulPredictions() )
    			{
    				color = "style=filled,fillcolor=\"#C00000\"";
    			}
    			else
    			{
	    			if( vertexRelationship.getAccuracy() > 0.95 )
	    			{
	    				color = "style=filled,fillcolor=\"#90F090\""; //pale green
	    			}
	    			else if( vertexRelationship.getAccuracy() > 0.0 )
	    			{
	    				color = "style=filled,fillcolor=\"#FFE866\""; //pale yellow-orange
	    			}
	    			else 
	    			{
	    				color = "style=filled,fillcolor=\"#FF8C75\""; //pale red.
	    			}
    			}
    			
    		}
    		
    		out += "\n	"+vertex.hashCode()+" [shape="+shape+",label=\""+vertexName+"\",id="+vertex.hashCode()+" "+color+"]";
    	}
    	
    	for(GraphEdge edge : graph.getEdges() )
    	{    		
    		ArrayList<GraphVertex> ends = new ArrayList<>( graph.getIncidentVertices(edge) );
    		
    		GraphVertex source = ends.get(0);
    		GraphVertex dest = ends.get(1);
    		
    		out += "\n	\""+source.hashCode()+"\" -> \""+dest.hashCode()+"\" ";
    	}
    	out += "\n}";
    	
    	
    	//JSONObject parentsAndChildrenJson = createParentChildJson( graph );
    	
		try {
			FileOutputStream fos = new FileOutputStream("runOutput/latest/"+filename+".dot");
			OutputStreamWriter graphoutfile = new OutputStreamWriter(fos, "UTF-8");
			
			graphoutfile.write(out);
			graphoutfile.close();
			
			//FileOutputStream fos2 = new FileOutputStream("runOutput/latest/"+filename+".json");
			//OutputStreamWriter jsonoutfile = new OutputStreamWriter(fos2, "UTF-8");
			
			//jsonoutfile.write(parentsAndChildrenJson.toJSONString());
			//jsonoutfile.close();
			
			
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

        System.out.println("Ran Dot.");*/

    }

    private void runDot(){
    	runDot( "graph-att");
    }
	private void runDot(String filename) {
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

	private static JSONObject createParentChildJson( Graph<GraphVertex, GraphEdge> graph )
	{
		JSONObject out = new JSONObject();
		for( GraphVertex vertex : graph.getVertices() )
		{
			JSONArray parents = new JSONArray();
			JSONArray children = new JSONArray();
			
			for( GraphVertex parent : graph.getPredecessors(vertex) )
			{
				parents.add(parent.hashCode());
			}
			for( GraphVertex child : graph.getSuccessors(vertex) )
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

    public void createGraphJson( String filename )
    {
        createGraphJson(this, filename);
    }

	public static void createGraphJson(AttRelationshipGraph graph, String filename){
		createGraphJson(graph, filename, false );
	}

    public static void createGraphJson(AttRelationshipGraph graph, String filename, boolean includeData){
        JSONArray verticesJson = new JSONArray();
        for( GraphVertex vertex : graph.getVertices() ) {
            try {
                JSONObject vertexJson = new JSONObject();
                vertexJson.put("id", vertex.hashCode());
                switch (vertex.getClass().getSimpleName()) {
                    case "Att":
                        Att att = (Att) vertex;
                        vertexJson.put("type", "Att");
                        vertexJson.put("name", att.getName());
						vertexJson.put("attOrder", att.getAttOrder());
                        vertexJson.put("dbColumnName", att.getDbColumnNameNoQuotes());
                        vertexJson.put("notNullRowsInTable", att.getNotNullRowsInTable());
                        vertexJson.put("uniqueRowsInTable", att.getUniqueRowsInTable());
                        vertexJson.put("firstRow", att.getFirstRow());
                        vertexJson.put("secondRow", att.getRow(1));
                        vertexJson.put("isDuplicate", att.isDuplicate()?1:0);
						vertexJson.put("duplicateOf", att.isDuplicate()?att.getDuplicateOf().hashCode():0);
						if( includeData ){
							vertexJson.put("data", att.getData(100) );
						}
                    break;
                    case "Processor":
                        Processor proc = (Processor) vertex;
                        vertexJson.put("type", "Processor");
                        vertexJson.put("name", proc.getName());
                        vertexJson.put("rootRowTable", proc.getRootRowTable().getTableName());
                        vertexJson.put("numberOfTransformers", proc.getNumberOfTransformers());
                        vertexJson.put("successfulTransformers", proc.getSuccessfulTransformers());
                        vertexJson.put("firstInputSize", proc.getFirstInputSize());
                        vertexJson.put("firstOutputSize", proc.getFirstOutputSize());
						if( proc.getTransformer().getClass().equals( modelApplier.class ) ){
							Relationship relationship = ((modelApplier)proc.getTransformer()).getRelationship();
							vertexJson.put( "isModelApplier", true);
							vertexJson.put( "originalLabel", relationship.getLabel().getDbColumnNameNoQuotes());
						}
						else{
							vertexJson.put( "isModelApplier", false);
						}
                    break;
                }

                verticesJson.add(vertexJson);
            }
            catch( SQLException e){
                e.printStackTrace();
            }
        }

        JSONArray edgesJson = new JSONArray();
        for(GraphEdge edge : graph.getEdges() )
        {
            ArrayList<GraphVertex> ends = new ArrayList<>( graph.getIncidentVertices(edge) );

            JSONObject edgeJson = new JSONObject();
            edgeJson.put("source", ends.get(0).hashCode() );
            edgeJson.put("dest", ends.get(1).hashCode() );

            edgesJson.add(edgeJson);
        }

        JSONObject graphJson = new JSONObject();
        graphJson.put("nodes", verticesJson);
        graphJson.put("edges", edgesJson);

        try {
            FileOutputStream fos = new FileOutputStream("runOutput/latest/" + filename + ".json");
            OutputStreamWriter jsonoutfile = new OutputStreamWriter(fos, "UTF-8");

            jsonoutfile.write(graphJson.toJSONString());
            jsonoutfile.close();
        }
        catch( IOException e){
            e.printStackTrace();
        }
    }
	
	public static Att findLatestCommonAncestor( Collection<Att> atts )
	{
		Att latestAncestor = atts.iterator().next();
		for( Att att : atts){
			latestAncestor = findLatestCommonAncestor( latestAncestor, att );
		}
		return latestAncestor;
	}
	
	public static Att findLatestCommonAncestor( Att att1, Att att2 )
	{
		HashSet<Att> ancestors1 = att1.getAncestorAtts( );
		ancestors1.add( att1 );
		HashSet<Att> ancestors2 = att2.getAncestorAtts( );
		ancestors2.add( att2 );
		
		SetView<Att> commonAncestors = Sets.intersection(ancestors1, ancestors2);
		
		Att latestAtt = null;
		int latestAttOrder = -1;
		for( Att ancestor : commonAncestors )
		{
			if( ancestor.getAttOrder() > latestAttOrder )
			{
				latestAtt = ancestor;
				latestAttOrder = ancestor.getAttOrder();
			}
		}
		return latestAtt;
	}
	
	public Collection<Att> getAllAtts()
	{
		Collection<GraphVertex> vertices = getVertices();
		Collection<Att> atts = new ArrayList<>();

		for( GraphVertex vertex : vertices ){
			if( vertex.getClass() == Att.class ){
				atts.add((Att) vertex);
			}
		}
		
		return atts;
	}
	
	public Collection<Att> getNonDuplicateAtts(){
		Collection<Att> atts = new ArrayList<>();
		for( Att att : this.getAllAtts() ){
			if( !att.isDuplicate() ){
				atts.add(att);
			}
		}
		return atts;
	}
	
	public Collection<Processor> getAllFinishedProcessors()
	{
		Collection<Processor> procs = getAllProcessors();
		Collection<Processor> finishedprocs = new HashSet<>();
		
		for( Processor proc : procs )
		{
			if( proc.finished() )
			{
				finishedprocs.add(proc);
			}
		}
		
		return finishedprocs;
	}
	
	public Collection<Processor> getAllProcessors()
	{
		Collection<GraphVertex> vertices = getVertices();
		Collection<Processor> processors = new HashSet<>();
		
		Iterator<GraphVertex> iter = vertices.iterator();
		while( iter.hasNext() )
		{
			GraphVertex vertex = iter.next();
			if( vertex.getClass() == Processor.class )
			{
				processors.add((Processor) vertex);
			}
		}
		
		return processors;
	}
	
	public Collection<Relationship> getAllRelationships()
	{
		Collection<GraphVertex> vertices = getVertices();
		Collection<Relationship> relationships = new HashSet<>();
		
		Iterator<GraphVertex> iter = vertices.iterator();
		while( iter.hasNext() )
		{
			GraphVertex vertex = iter.next();
			if( vertex.getClass() == Relationship.class )
			{
				relationships.add((Relationship) vertex);
			}
		}
		
		return relationships;
	}

    public Collection<Relationship> getAllRelationshipsFromModelAppliers()
    {
        Collection<GraphVertex> vertices = getVertices();
        Collection<Relationship> relationships = new HashSet<>();

        Iterator<GraphVertex> iter = vertices.iterator();
        while( iter.hasNext() )
        {
            GraphVertex vertex = iter.next();
            if( vertex.getClass() == Processor.class )
            {
                Processor proc = (Processor)vertex;
                if( proc.getTransformer().getClass() == modelApplier.class ) {
                    modelApplier applier = (modelApplier) proc.getTransformer();
                    relationships.add(applier.getRelationship());
                }
            }
        }

        return relationships;
    }
	
	public Set<DataTable> getAllDatatables(){
		HashSet<DataTable> datatables = new HashSet<>(); 
		for( Att att : getAllAtts() ){
			datatables.add( att.getDataTable() );
		}
		return datatables;
	}

	public Att getRootAtt() {
		Collection<GraphVertex> collectionOfOneRoot = getRoots();
		
		for( GraphVertex a : collectionOfOneRoot )
		{
			if( a.getClass().equals(Att.class) )
			{
				Att b = (Att) a;
				if( b.isRootAtt())
					return b;
			}
		}
		assert(false);
		return null;
	}
	
	private class SelectAttsAndProcessors implements Predicate<GraphVertex>
	{
		public boolean evaluate(GraphVertex vertex) {
			if( vertex.getClass() == Att.class || vertex.getClass() == Processor.class )
				return true;
			else
				return false;
		}
	}
	
	private class SelectVerticesInCollection implements Predicate<GraphVertex>
	{
		private Collection<GraphVertex> toSelect; 
		
		SelectVerticesInCollection(Collection<GraphVertex> toSelect)
		{
			this.toSelect = toSelect;
		}

		@Override
		public boolean evaluate(GraphVertex vertex) {
			if( toSelect.contains(vertex) )
				return true;
			else
				return false;
		}
	}
	
	public AttRelationshipGraph getSubGraph( GraphVertex vertex )
	{
		Collection<GraphVertex> verticesInSubgraph = new ArrayList<>();
		verticesInSubgraph.add(vertex);
		verticesInSubgraph.addAll(vertex.getAncestorAttsAndProcessors());
		//verticesInSubgraph.addAll( this.getChildren(vertex));
		
		VertexPredicateFilter<GraphVertex, GraphEdge> subgraphSelector = new VertexPredicateFilter<>(new SelectVerticesInCollection(verticesInSubgraph));
		AttRelationshipGraph subGraph = (AttRelationshipGraph) subgraphSelector.transform(this);
		
		return subGraph;
	}
	
	public AttRelationshipGraph getSubGraph( Collection<? extends GraphVertex> vertices )
	{
		Collection<GraphVertex> verticesInSubgraph = new HashSet<>();
		verticesInSubgraph.add(this.getRootAtt()); //Must always include the root. Never prune back to nothing.
		for( GraphVertex vertex : vertices )
		{
			verticesInSubgraph.add(vertex);
			verticesInSubgraph.addAll(vertex.getAncestorAttsAndProcessors());
			//verticesInSubgraph.addAll( this.getChildren(vertex));
		}
		
		VertexPredicateFilter<GraphVertex, GraphEdge> subgraphSelector = new VertexPredicateFilter<>(new SelectVerticesInCollection(verticesInSubgraph));
		AttRelationshipGraph subGraph = (AttRelationshipGraph) subgraphSelector.transform(this);

		return subGraph;
	}
	
	public AttRelationshipGraph getAttsAndProcessorsOnly( )
	{
		VertexPredicateFilter<GraphVertex, GraphEdge> subgraphSelector = new VertexPredicateFilter<>(new SelectAttsAndProcessors());
		AttRelationshipGraph subGraph = (AttRelationshipGraph) subgraphSelector.transform(this);
		
		return subGraph;
	}
	
	/**
	 * Sets duplicateOf on atts that are duplicates of other atts ("useless" attributes).
	 * TODO: Find the "minimum" non-duplicate set and flag other atts that so this method is deterministic, and there's no chance of running it once, setting A as a duplicate of B then running it again and setting B as a duplicate of A.
	 * @return
	 * @throws SQLException
	 */
    public void flagDuplicateAtts() throws SQLException {
        //Loop through each table and if 2 atts in a table have exactly the same values, only keep one of them.
        HashSet<Att> attsToKeep = new HashSet<>();
        for( DataTable datatable : this.getAllDatatables() ){
            HashMap<String, Att> columnsSeen = new HashMap<>();
            for( Att att : datatable.getAllAttsInTable() ){
                if( this.containsVertex(att) && !att.isDuplicate() ){
                    String colHash = att.getColumnValuesHash();
                    if( columnsSeen.get(colHash) == null){
                        attsToKeep.add(att);
                        columnsSeen.put( colHash, att );
                    }
                    else{
                        att.setDuplicateOf(columnsSeen.get(colHash));
                    }
                }
            }
        }

        //Loop through each att and check that it's not equal to one of it's parents. (eg. "bob" -> splitOnComma -> "bob". Pointless transformation. )
        //TODO: Whay happens if it's "idx, bob" -> neighbours -> "idx-1, idx+1, bob". Should we keep "bob" in the new table?
        HashSet<Att> attsToKeep2 = new HashSet<>();
        for( Att att : attsToKeep ){
            boolean foundIdenticalParent = false;
            for(Att parentAtt : att.getParentAtts() ){
                if( att.getColumnValuesHash().equals( parentAtt.getColumnValuesHash() ) ){
                    foundIdenticalParent = true;
                    att.setDuplicateOf(parentAtt);
                }
            }
            if( !foundIdenticalParent) {
                attsToKeep2.add(att);
            }
        }
        attsToKeep = attsToKeep2;
        attsToKeep2 = null;

        //Loop through each table and if there is an older sibling table that contains all the atts that this table contains, remove this table.
        HashSet<Att> attsToKeep3 = new HashSet<>();
        HashSet<DataTable> allTables = new HashSet<>();
        for( Att att : attsToKeep ){
            allTables.add( att.getDataTable() );
        }
        for( DataTable table : allTables){
            HashSet<String> hashesToFind = new HashSet<>();
            for( Att att : table.getAllAttsInTable() ){
                if( attsToKeep.contains(att) ){
                    hashesToFind.add( att.getColumnValuesHash() );
                }
            }
            DataTable betterOlderSibling = null;
            //TODO: Add the parentTable to the list of "older siblings" this loop iterates through?
            for( DataTable siblingTable : table.getOlderSiblingTables() ){
                HashSet<String> hashesInSiblingTable = new HashSet<>();
                for( Att att : siblingTable.getAllAttsInTable() ){
                    if( attsToKeep.contains(att) ){
                        hashesInSiblingTable.add( att.getColumnValuesHash() );
                    }
                }
                if( hashesInSiblingTable.containsAll(hashesToFind)){
                    betterOlderSibling = siblingTable;
                    break;
                }
            }
            if(betterOlderSibling == null){
                //Add all atts from this table that we were already planning to keep.
                for( Att att : table.getAllAttsInTable() ){
                    if( attsToKeep.contains(att) ){
                        attsToKeep3.add(att);
                    }
                }
            }
            else{
                //Mark the atts in this tables as duplicates of those in the older sibling.
                for( Att dupe : table.getAllAttsInTable() ){
                    for( Att original : betterOlderSibling.getAllAttsInTable() ){
                        if( !dupe.isDuplicate() && dupe.getColumnValuesHash().equals( original.getColumnValuesHash() ) ){
                            dupe.setDuplicateOf(original);
                        }
                    }
                }
            }
        }
    }


	/*
	public void flagDuplicateAtts() throws SQLException{
		Set<Att> attsToKeep = findNonDuplicateAtts();
		
		AttRelationshipGraph subGraph = this.getSubGraph(attsToKeep);
		for( Att att : this.getAllAtts() ){
			if( !subGraph.containsVertex(att) ){
				att.setDuplicate(true);
			}
		}
	} */
	
	/**
	 * Removes duplicate atts from the tree. Parent / Child attributes of the atts themselves are unaffected and will still point to atts that were removed as duplicates.
	 * @throws SQLException
	 */
	public void removeDuplicateAtts() throws SQLException{
        this.flagDuplicateAtts();
		Collection<Att> attsToKeep = this.getNonDuplicateAtts();
		
		//Remove elements from this tree that aren't in the subgraph.
		HashSet<GraphVertex> toRemove = new HashSet<>();
		AttRelationshipGraph subGraph = this.getSubGraph(attsToKeep);
		for( GraphVertex vert : this.getVertices() ){
			if( !subGraph.containsVertex(vert) ){
				toRemove.add( vert );
			}
		}
		
		for( GraphVertex vert : toRemove ){
			this.removeVertex(vert);
		}
	}

	public Collection<Att> getFinalOutputAtts() {
		HashSet<Att> finalOutputAtts = new HashSet<>(); 
		for( Att att: getAllAtts() ){
			if( att.isFinalOutputAtt() ){
				finalOutputAtts.add(att);
			}
		}
		return finalOutputAtts;
	}
	
	/**
	 * Merges the specified graph into this one. Adds any vertices and edges that don't already exist in this graph.
	 * @param graph
	 */
	public void addAll( Graph<GraphVertex, GraphEdge> graph ){
		for( GraphVertex v : graph.getVertices() ){
			if( !this.containsVertex(v)){
				this.addVertex(v);
			}
		}
		for( GraphEdge e : graph.getEdges() ){
			if( !this.containsEdge(e)){
				this.addEdge(e, graph.getSource(e), graph.getDest(e));
			}
		}
	}

	public AttRelationshipGraph getUnpredictableSubgraph(Collection<Relationship> relationships, boolean processorsAreReversible) {
		HashSet<Att> predictable = new HashSet<>();
		for( Relationship rel : relationships ){
			assert( this.containsVertex( rel.getLabel() ) );
			predictable.add( rel.getLabel() );
		}
		
		int lastNumberOfPredictableAtts = -1;
		while( lastNumberOfPredictableAtts != predictable.size() ){ //Loop until predictable atts stop changing.
			lastNumberOfPredictableAtts = predictable.size();
			for( Att att : this.getAllAtts() ){	
				if( att.isRootAtt() || att.isFinalOutputAtt() ){
					continue;
				}
				
				if( predictable.contains( att ) )
					continue;

                //Any duplicates of predictable atts must also be predictable.
                if( att.isDuplicate() && predictable.contains( att.getDuplicateOf() ) ){
                    predictable.add(att);
                    continue;
                }

				//If this att's parents can all be predicted, then this att can be too (since we can just predict the parents, then apply the processor).
				if( predictable.containsAll(att.getParentAtts())){
					predictable.add(att);
                    continue;
				}
				
				//If there's a reversible processor that we can predict all the outputs of, then we can reverse it to get this att.
				if( processorsAreReversible ){
					for( Processor childProcessor : att.getProcessors() ){
						assert( childProcessor.getTransformer() instanceof TransformationServiceReversible );
						if( predictable.containsAll( childProcessor.getOutputAtts() ) && childProcessor.getOutputAtts().size() > 0 ){
							predictable.add(att);
							break;
						}
					}
				}
			}
		}
		
		
		ArrayList<Att> unpredictableAtts = new ArrayList<>();
		for(Att att : this.getAllAtts()){
			if( !predictable.contains(att) ){
				unpredictableAtts.add(att);
			}
		}
		
		return this.getSubGraph(unpredictableAtts);
	}

	//Returns the first vertex found that .equals the passed in Vertex.
	public GraphVertex getVertex(GraphVertex needle) {
		for( GraphVertex ver : getVertices()){
			if( needle.equals(ver)){
				return ver;
			}
		}
		return null;
	}

    public String toString(){
        return "Tree, containing "+this.getAllAtts().size()+" atts ("+this.getNonDuplicateAtts().size()+" without tagged duplicates), "+this.getAllProcessors().size()+" processors, and "+this.getAllRelationships().size()+" relationships.";
    }
	

	
}
