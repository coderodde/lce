package net.coderodde.lce.model;

import java.util.HashMap;
import java.util.Map;
import static net.coderodde.lce.Utils.checkNotNull;

/**
 * This class models the financial graph in which nodes may lease loan contracts
 * to other nodes.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class Graph {
    
    /**
     * The name of this graph.
     */
    private final String name;
    
    /**
     * Maps a name of a node to the node having that name.
     */
    private final Map<String, Node> map;
    
    /**
     * The amount of edges in this graph. Modified by <code>Node</code>.
     */
    int edgeAmount;
    
    /**
     * The amount of contracts in this graph. Modified by <code>Node</code>.
     */
    int contractAmount;
    
    public Graph(final String name) {
        checkNotNull(name, "The graph name is null.");
        this.name = name;
        this.map = new HashMap<>();
    }
    
    public void add(final Node node) {
        checkNotNull(node, "The node is null.");
        checkUnique(node);
        node.setOwnerGraph(this);
    }
    
    public boolean contains(final Node node) {
        return map.containsKey(node);
    }
    
    public int size() {
        return map.size();
    }
    
    public int getEdgeAmount() {
        return edgeAmount;
    }
    
    public int getContractAmount() {
        return contractAmount;
    }
    
    private void checkUnique(Node node) {
        if (map.containsKey(node.getName())) {
            throw new IllegalArgumentException("Nodes must be unique.");
        }
    }
}
