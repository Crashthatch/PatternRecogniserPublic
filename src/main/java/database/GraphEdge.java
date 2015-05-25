package database;

import org.apache.commons.collections15.Factory;

public class GraphEdge {

    public static Factory<GraphEdge> getFactory()
    {
        return new Factory<GraphEdge>() {

            public GraphEdge create()
            {
                return new GraphEdge();
            }

        }
;
    }
}
