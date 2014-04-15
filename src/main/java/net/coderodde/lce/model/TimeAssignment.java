package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static net.coderodde.lce.Utils.checkNotNull;
import static net.coderodde.lce.Utils.checkTimestamp;
import static net.coderodde.lce.Utils.checkTimestamp;
/**
 * This class maps the nodes to their respective time at which they are ready
 * to pay their debt cuts.
 * 
 * @author Rodion Efremov
 * @versioin 1.6
 */
public class TimeAssignment {
    
    private Map<Node, Double> map;
    
    public TimeAssignment() {
        this.map = new HashMap<>();
    }
    
    public final void put(final Node node, final double time) {
        checkNotNull(node, "Node is null.");
        checkTimestamp(time);
        this.map.put(node, time);
    }
    
    public final boolean containsNode(final Node node) {
        checkNotNull(node, "Node is null.");
        return this.map.containsKey(node);
    }
    
    public final double get(final Node node) {
        checkNotNull(node, "Node is null.");
        
        if (this.map.containsKey(node) == false) {
            throw new IllegalArgumentException(
                    "The node is not contained in this TimeAssignment.");
        }
        
        return this.map.get(node);
    }
    
    public Collection<Node> getNodes() {
        return Collections.<Node>unmodifiableCollection(this.map.keySet());
    }
}
