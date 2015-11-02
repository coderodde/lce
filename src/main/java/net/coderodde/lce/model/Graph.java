package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.support.DefaultEquilibrialDebtCutFinder;

/**
 * This class models the financial graph in which nodes may lease loan contracts
 * to other nodes.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public final class Graph {
    
    /**
     * The name of this graph.
     */
    private final String name;
    
    /**
     * Maps a name of a node to the node having that name.
     */
    private final Map<String, Node> map = new HashMap<>();
    
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
     * Caches the maximum timestamp of contracts.
     */
    private double maximumTimestamp;
    
    /**
     * Constructs a graph with the specified name and finder.
     * 
     * @param name   the name of this graph.
     * @param finder the finder used by this graph.
     */
    public Graph(String name, EquilibrialDebtCutFinder finder) {
        Objects.requireNonNull(name, "The graph name is null.");
        Objects.requireNonNull(finder, "The finder is null.");
        this.name = name;
    }
    
    /**
     * Constructs a graph with the specified name and the default finder.
     * 
     * @param name the name of this graph.
     */
    public Graph(String name) {
        this(name, new DefaultEquilibrialDebtCutFinder());
    }
    
    /**
     * Copy constructs a new graph.
     * 
     * @param toCopy source object.
     */
    public Graph(Graph toCopy) {
        Objects.requireNonNull(toCopy, "The input graph is null.");
        this.name = toCopy.name;
        
        for (Node node : toCopy.getNodes()) {
            Node other = new Node(node.getName());
            this.add(other);
        }
    }
    
    /**
     * Adds a node to this graph.
     * 
     * @param node the node to add.
     */
    public void add(Node node) {
        Objects.requireNonNull(node, "The node is null.");
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
    public boolean contains(Node node) {
        Objects.requireNonNull(node, "The node is null.");
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
    public Node getNode(String name) {
        Objects.requireNonNull(name, "The name for a node is null.");
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
    public Node getNode(Node node) {
        Objects.requireNonNull(node, "The node is null.");
        return map.get(node.getName());
    }
    
    /**
     * Removes a node from this graph.
     * 
     * @param node the node to remove. 
     */
    public void remove(Node node) {
        Objects.requireNonNull(node, "The node is null.");
        
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
    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(this.map.values());
    }
    
    /**
     * Sets the equilibrial debt cut finder.
     * 
     * @param finder the debt cut finder to set.
     * 
     * @return return this for chaining.
     */
    public Graph setDebtCutFinder(EquilibrialDebtCutFinder finder) {
        this.finder = finder;
        return this;
    }
    
    /**
     * Computes the debt cut assignment object.
     * 
     * @param equilibriumTime the moment at which to attain equilibrium.
     * @param ta              the time assignment.
     * 
     * @return the debt cut assignment object.
     */
    public DebtCutAssignment findEquilibrialDebtCuts(double equilibriumTime, 
                                                     TimeAssignment ta) {
        return finder.compute(this, ta, equilibriumTime);
    }
    
    /**
     * Creates a clone graph and applies the debt cuts to it.
     * 
     * @param dca the debt cut assignment object.
     * @param ta  the time assignment object.
     * 
     * @return a new graph resulting from applying the cuts to this graph. 
     */
    public Graph applyDebtCuts(DebtCutAssignment dca, TimeAssignment ta) {
        Graph other = new Graph(this);
        
        // Apply debt cuts.
        for (Node node : this.getNodes()) {
            Node target = other.getNode(node.getName());
            
            for (Node debtorOfNode : node.getDebtors()) {
                Node targetDebtor = other.getNode(debtorOfNode.getName());
                
                for (Contract c : node.getContractsTo(debtorOfNode)) {
                    if (dca.containsFor(c)) {
                        target.addDebtor(
                               targetDebtor,
                               c.applyDebtCut(dca, ta.get(debtorOfNode, c)));
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
    public boolean isInEquilibriumAt(double time) {
        for (Node node : map.values()) {
            if (epsilonEquals(node.equity(time), 0.0) == false) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns the maximum absolute value of a node's equity.
     * 
     * @param time the time at which to evaluate equities.
     * 
     * @return the maximum absolute value of a node's equity.
     */
    public double maxEquity(double time) {
        double max = 0.0;
        
        for (Node node : map.values()) {
            max = Math.max(max, Math.abs(node.equity(time)));
        }
        
        return max;
    }
    
    /**
     * Copies the input time assignment object to another graph <code>g</code>.
     * 
     * @param g  the target graph.
     * @param ta the source time assignment object.
     * 
     * @return a new time assignment object for graph <code>g</code>.
     */
    public TimeAssignment copy(Graph g, TimeAssignment ta) {
        TimeAssignment ret = new TimeAssignment();
        
        for (Node node : this.getNodes()) {
            for (Node debtor : node.getDebtors()) {
                for (Contract contract : node.getContractsTo(debtor)) {
                    ret.put(debtor, contract, ta.get(debtor, contract));
                }
            }
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
    public double getTotalFlowAt(double time) {
        double d = 0;
        
        for (Node node : map.values()) {
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
    
    /**
     * Returns the maximum timestamp.
     * 
     * @return the maximum timestamp.
     */
    public double getMaximumTimestamp() {
        return maximumTimestamp;
    }
    
    /**
     * Returns textual description of this graph at the specified moment.
     * 
     * @param time the target time.
     * 
     * @return textual description of this graph.
     */
    public String describe(double time) {
        StringBuilder sb = new StringBuilder();
        
        for (Node node : getNodes()) {
            sb.append(node)
              .append("\n  Debtors:");
            
            for (Node debtor : node.getDebtors()) {
                sb.append("\n    ")
                  .append(debtor);
                
                for (Contract c : node.getContractsTo(debtor)) {
                    sb.append("\n      ")
                      .append(c.evaluate(time - c.getTimestamp()));
                }
            }
            
            sb.append("\n  Lenders:");
            
            for (Node lender : node.getLenders()) {
                sb.append("\n    ")
                  .append(lender);
                
                for (Contract c : lender.getContractsTo(node)) {
                    sb.append("\n      ")
                      .append(c.evaluate(time - c.getTimestamp()));
                }
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Sets the maximum timestamp.
     * 
     * @param timestamp the timestamp to set.
     */
    void setMaximumTimestamp(double timestamp) {
        this.maximumTimestamp = timestamp;
    }
    
    /**
     * Sets the edge amount in this graph.
     * 
     * @param edgeAmount the edge amount to set.
     */
    void setEdgeAmount(int edgeAmount) {
        this.edgeAmount = edgeAmount;
    }
    
    /**
     * Sets the contract amount in this graph.
     * 
     * @param contractAmount the contract amount to set.
     */
    void setContractAmount(int contractAmount) {
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
