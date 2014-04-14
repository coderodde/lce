package net.coderodde.lce.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.coderodde.lce.Utils.checkNotNull;

/**
 * This class models a node (a party) in a financial graph.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class Node {
    
    /**
     * The identity of this node. Must be unique in a graph.
     */
    private final String name;
    
    /**
     * The map from a lender to the list of contracts being lent.
     */
    private final Map<Node, List<Contract>> in;
    
    /**
     * The map from a borrower to the list of contracts being admitted.
     */
    private final Map<Node, List<Contract>> out;
    
    /**
     * The maximum timestamp of all contract timestamps. Used at validating
     * the timestamp form computing equities.
     */
    private double maximumTimestamp;
    
    private Graph ownerGraph;
    
    /**
     * Constructs a new node.
     * 
     * @param name the identity of this node.
     */
    public Node(final String name) {
        checkNotNull(name, "The name of a new node is null.");
        this.name = name;
        this.in = new HashMap<>();
        this.out = new HashMap<>();
    }
    
    public final String getName() {
        return name;
    }
    
    public final void addDebtor(final Node debtor, final Contract contract) {
        checkNotNull(debtor, "The lender is null.");
        checkNotNull(contract, "The contract is null.");
        
        if (maximumTimestamp < contract.getTimestamp()) {
            maximumTimestamp = contract.getTimestamp();
        }
        
        List<Contract> contractList = out.get(debtor);
        
        if (contractList == null) {
            this.ownerGraph.edgeAmount++;
            contractList = new ArrayList<>();
            contractList.add(contract);
            out.put(debtor, contractList);
        } else {
            contractList.add(contract);
        }
        
        if (debtor.in.get(this) == null) {
            debtor.in.put(this, contractList);
        }
        
        if (this.ownerGraph != null) {
            this.ownerGraph.contractAmount++;
        }
    }
    
    public final double equity(final double time) {
        double equity = 0;
        
        for (List<Contract> contractList : out.values()) {
            for (Contract contract : contractList) {
                equity += contract.evaluate(time);
            }
        }
        
        for (List<Contract> contractList : in.values()) {
            for (Contract contract : contractList) {
                equity -= contract.evaluate(time);
            }
        }
        
        return equity;
    }
 
    final void setOwnerGraph(Graph graph) {
        in.clear();
        out.clear();
        this.ownerGraph = graph;
    }
}