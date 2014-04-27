package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static net.coderodde.lce.Utils.checkNotNull;
import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.support.DefaultEquilibrialDebtCutFinder;

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
    private int edgeAmount;
    
    /**
     * The amount of contracts in this graph. Modified by <code>Node</code>.
     */
    private int contractAmount;
    
    /**
     * The current debt cut finder.
     */
    private EquilibrialDebtCutFinder finder;
    
    /**
     * Constructs a graph with the specified name and finder.
     * 
     * @param name the name of this graph.
     * @param finder the finder used by this graph.
     */
    public Graph(final String name, final EquilibrialDebtCutFinder finder) {
        checkNotNull(name, "The graph name is null.");
        checkNotNull(finder, "The finder is null.");
        this.name = name;
        this.map = new HashMap<>();
    }
    
    /**
     * Constructs a graph with the specified name and the default finder.
     * 
     * @param name the name of this graph.
     */
    public Graph(final String name) {
        this(name, new DefaultEquilibrialDebtCutFinder());
    }
    
    public void add(final Node node) {
        checkNotNull(node, "The node is null.");
        checkUnique(node);
        node.clear();
        node.setOwnerGraph(this);
        this.map.put(node.getName(), node);
    }
    
    public boolean contains(final Node node) {
        checkNotNull(node, "The node is null.");
        return map.containsKey(node.getName());
    }
    
    public Node getNode(final String name) {
        checkNotNull(name, "The name for a node is null.");
        return map.get(name);
    }
    
    public Node getNode(final Node node) {
        checkNotNull(node, "The node is null.");
        return map.get(node.getName());
    }
    
    public void remove(final Node node) {
        checkNotNull(node, "The node is null.");
        
        if (map.containsKey(node.getName())) {
            node.clear();
        }
        
        map.remove(node.getName());
    }
    
    public final Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(this.map.values());
    }
    
    /**
     * Computes the debt cut assingment object.
     * 
     * @return 
     */
    public final DebtCutAssignment findEquilibrialDebtCuts
        (final double equilibriumTime) {
        return null;
    }
    
    public boolean isInEquilibriumAt(final double time) {
        for (Node node : map.values()) {
            if (epsilonEquals(node.equity(time), 0.0) == false) {
                return false;
            }
        }
        
        return true;
    }
        
    public final double getTotalFlowAt(final double time) {
        double d = 0;
        
        for (final Node node : map.values()) {
            d += node.getOutgoingFlowAt(time);
        }
        
        return d;
    }
    
    /**
     * Returns the amount of nodes in this graph.
     * 
     * @return the amount of nodes in this graph.
     */
    public int size() {
        return map.size();
    }
    
    public int getEdgeAmount() {
        return edgeAmount;
    }
    
    public int getContractAmount() {
        return contractAmount;
    }
    
    final void setEdgeAmount(final int edgeAmount) {
        this.edgeAmount = edgeAmount;
    }
    
    final void setContractAmount(final int contractAmount) {
        this.contractAmount = contractAmount;
    }
    
    private void checkUnique(Node node) {
        if (map.containsKey(node.getName())) {
            throw new IllegalArgumentException("Nodes must be unique.");
        }
    }
}
