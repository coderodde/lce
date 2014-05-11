package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.coderodde.lce.Utils;
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
    
    private double maximumTimestamp;
    
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
    
    public Graph(final Graph toCopy) {
        checkNotNull(toCopy, "The input graph is null.");
        this.name = toCopy.name;
        this.map = new HashMap<>();
        
        for (final Node node : toCopy.getNodes()) {
            final Node other = new Node(node.getName());
            this.add(other);
        }
    }
    
    /**
     * Adds a node to this graph.
     * 
     * @param node the node to add.
     */
    public void add(final Node node) {
        checkNotNull(node, "The node is null.");
        checkUnique(node);
        node.clear();
        node.setOwnerGraph(this);
        this.map.put(node.getName(), node);
    }
    
    /**
     * Checks whether a node is in this graph.
     * 
     * @param node the node to query.
     * 
     * @return <code>true</code> if <code>node</code> is in this graph;
     * <code>false</code> otherwise.
     */
    public boolean contains(final Node node) {
        checkNotNull(node, "The node is null.");
        return map.containsKey(node.getName());
    }
    
    /**
     * Gets a node by its name.
     * 
     * @param name the name of a node to fetch.
     * 
     * @return the node or <code>null</code> if there is no node with name
     * <code>name</code> in this graph.
     */
    public Node getNode(final String name) {
        checkNotNull(name, "The name for a node is null.");
        return map.get(name);
    }
    
    /**
     * Gets a node in this graph with the same name as <code>node</code>.
     * 
     * @param node the node to query.
     * 
     * @return the node with the same name as <code>node</code>, or 
     * <code>null</code> if there is no such.
     */
    public Node getNode(final Node node) {
        checkNotNull(node, "The node is null.");
        return map.get(node.getName());
    }
    
    /**
     * Removes a node from this graph.
     * 
     * @param node the node to remove. 
     */
    public void remove(final Node node) {
        checkNotNull(node, "The node is null.");
        
        if (map.containsKey(node.getName())) {
            node.clear();
        }
        
        map.remove(node.getName());
    }
    
    /**
     * Returns unmodifiable view of this graph's nodes.
     * 
     * @return unmodifiable view of this graph's nodes.
     */
    public final Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(this.map.values());
    }
    
    public final Graph setDebtCutFinder(final EquilibrialDebtCutFinder finder) {
        this.finder = finder;
        return this;
    }
    
    /**
     * Computes the debt cut assignment object.
     * 
     * @param equilibriumTime the moment at which to attain equilibrium.
     * @param ta the time assignment.
     * 
     * @return the debt cut assignment object.
     */
    public final DebtCutAssignment findEquilibrialDebtCuts
        (final double equilibriumTime, final TimeAssignment ta) {
        return finder.compute(this, ta, equilibriumTime);
    }
    
    public final Graph applyDebtCuts
        (final DebtCutAssignment dca, final TimeAssignment ta) {
        Graph other = new Graph(this);
        
        // Apply debt cuts.
        for (final Node node : this.getNodes()) {
            final Node target = other.getNode(node.getName());
            
            for (final Node debtorOfNode : node.getDebtors()) {
                final Node targetDebtor = other.getNode(debtorOfNode.getName());
                
                for (final Contract c : node.getContractsTo(debtorOfNode)) {
                    if (dca.containsFor(c)) {
                        target.addDebtor(targetDebtor,
                                         c.applyDebtCut(dca, 
                                                        ta.get(debtorOfNode)));
                    }
                }
            }
        }
        
        return other;
    }
        
    /**
     * Checks whether this graph is in equilibrium at time <code>time</code>.
     * 
     * @param time the time to query.
     * 
     * @return <code>true</code> if this graph attains equilibrium at moment
     * <code>time</code>.
     */
    public boolean isInEquilibriumAt(final double time) {
        for (Node node : map.values()) {
            if (epsilonEquals(node.equity(time), 0.0) == false) {
                return false;
            }
        }
        
        return true;
    }
    
    public double funk(final double time) {
        double d = Double.NEGATIVE_INFINITY;
        
        for (Node node : map.values()) {
            if (epsilonEquals(node.equity(time), 0.0) == false) {
                if (d < Math.abs(node.equity(time))) {
                    d = Math.abs(node.equity(time));
                }
            }
        }
        
        return d;
    }
        
    public final TimeAssignment copy(final Graph g, final TimeAssignment ta) {
        final TimeAssignment ret = new TimeAssignment();
        
        for (final Node node : this.getNodes()) {
            ret.put(g.getNode(node.getName()), ta.get(node));
        }
        
        return ret;
    }
    
    /**
     * Returns the total flow of this graph at moment <code>time</code>.
     * 
     * @param time the moment to calculate the flow at.
     * 
     * @return the total flow of this graph at moment <code>time</code>.
     */
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
    
    /**
     * Returns the amount of edges in this graph.
     * 
     * @return the amount of edges in this graph.
     */
    public int getEdgeAmount() {
        return edgeAmount;
    }
    
    /**
     * Returns the amount of contracts in this graph.
     * 
     * @return the amount of contracts in this graph.
     */
    public int getContractAmount() {
        return contractAmount;
    }
    
    public final double getMaximumTimestamp() {
        return maximumTimestamp;
    }
    
    public final String describe(final double time) {
        final StringBuilder sb = new StringBuilder();
        
        for (final Node node : this.getNodes()) {
            sb.append(node)
              .append("\n  Debtors:");
            
            for (final Node debtor : node.getDebtors()) {
                sb.append("\n    ")
                  .append(debtor);
                
                for (final Contract c : node.getContractsTo(debtor)) {
                    sb.append("\n      ")
                      .append(c.evaluate(time - c.getTimestamp()));
                }
            }
            
            sb.append("\n  Lenders:");
            
            for (final Node lender : node.getLenders()) {
                sb.append("\n    ")
                  .append(lender);
                
                for (final Contract c : lender.getContractsTo(node)) {
                    sb.append("\n      ")
                      .append(c.evaluate(time - c.getTimestamp()));
                }
            }
            
            sb.append("\n\n");
        }
        
        return sb.append('\n').toString();
    }
    
    final void setMaximumTimestamp(final double timestamp) {
        this.maximumTimestamp = timestamp;
    }
    
    /**
     * Sets the edge amount in this graph.
     * 
     * @param edgeAmount the edge amount to set.
     */
    final void setEdgeAmount(final int edgeAmount) {
        this.edgeAmount = edgeAmount;
    }
    
    /**
     * Sets the contract amount in this graph.
     * 
     * @param contractAmount the contract amount to set.
     */
    final void setContractAmount(final int contractAmount) {
        this.contractAmount = contractAmount;
    }
    
    /**
     * Checks the uniqueness of the node <code>node</code>.
     * 
     * @param node the node to check.
     * 
     * @throws IllegalArgumentException in case the node is no unique.
     */
    private void checkUnique(Node node) {
        if (map.containsKey(node.getName())) {
            throw new IllegalArgumentException("Nodes must be unique.");
        }
    }
}
