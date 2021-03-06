package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static net.coderodde.lce.Utils.checkTimestamp;

/**
 * This class maps the nodes to their respective time at which they are ready
 * to pay their debt cuts.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public final class TimeAssignment {
    
    /**
     * The map mapping each node to their debt cut timestamps.
     */
    private final Map<Node, Map<Contract, Double>> map = new HashMap<>();
    
    /**
     * Caches the maximum timestamp.
     */
    private double maxTimestamp;
    
    /**
     * Constructs an empty time assignment object.
     */
    public TimeAssignment() {
        this.maxTimestamp = Double.NEGATIVE_INFINITY;
    }
    
    /**
     * Returns the number of nodes mapped.
     * 
     * @return the number of nodes mapped.
     */
    public int size() {
        return map.size();
    }
    
    /**
     * Assigns the timestamp <code>time</code> to node <code>node</code>.
     * 
     * @param node the node.
     * @param c    the contract.
     * @param time the timestamp.
     */
    public void put(Node node, Contract c, double time) {
        Objects.requireNonNull(node, "Node is null.");
        Objects.requireNonNull(c, "Contract is null.");
        checkTimestamp(time);
        
        if (this.map.containsKey(node) == false) {
            this.map.put(node, new HashMap<Contract, Double>());
        }
        
        Map<Contract, Double> m = this.map.get(node);
        m.put(c, time);
        this.maxTimestamp = Math.max(this.maxTimestamp, time);
    }
    
    /**
     * Queries whether this time assignment object has a mapping for
     * <code>node</code>.
     * 
     * @param node the node to query.
     * 
     * @return <code>true</code> if this time assignment object contains 
     * <code>node</code>, <code>false</code> otherwise.
     */
    public boolean containsNode(Node node) {
        Objects.requireNonNull(node, "Node is null.");
        return this.map.containsKey(node);
    }
    
    /**
     * Returns the time assignment of the node's <code>node</code> contract
     * <code>contract</code>.
     * 
     * @param node     the node to query.
     * @param contract the contract to query. 
     * 
     * @return the timestamp. 
     */
    public double get(Node node, Contract contract) {
        Objects.requireNonNull(node, "Node is null.");
        
        if (this.map.containsKey(node) == false) {
            throw new IllegalArgumentException(
                    "The node is not contained in this TimeAssignment.");
        }
        
        return this.map.get(node).get(contract);
    }
    
    /**
     * Returns an unmodifiable view of all the nodes in this assignment.
     * 
     * @return an unmodifiable view of all the nodes in this assignment.
     */
    public Collection<Node> getNodes() {
        return Collections.<Node>unmodifiableCollection(this.map.keySet());
    }
    
    /**
     * Returns the timestamp no less than the maximum timestamp in this 
     * object.
     * 
     * @return the timestamp.
     */
    public double getMaximumTimestamp() {
        return this.maxTimestamp;
    }
}
