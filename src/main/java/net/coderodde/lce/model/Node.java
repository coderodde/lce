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
    
    /**
     * The graph to which this node belongs.
     */
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
    
    /**
     * Returns the name of this node.
     * 
     * @return the name of this node.
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Adds a contract from this node to <code>debtor</code>.
     * 
     * @param debtor the receiving party.
     * @param contract the contract.
     */
    public final void addDebtor(final Node debtor, final Contract contract) {
        checkNotNull(debtor, "The lender is null.");
        checkNotNull(contract, "The contract is null.");
        checkOwnerGraph();
        
        if (maximumTimestamp < contract.getTimestamp()) {
            maximumTimestamp = contract.getTimestamp();
        }
        
        List<Contract> contractList = out.get(debtor);
        
        if (contractList == null) {
            this.ownerGraph.setEdgeAmount(this.ownerGraph.getEdgeAmount() + 1);
            contractList = new ArrayList<>();
            contractList.add(contract);
            out.put(debtor, contractList);
        } else {
            contractList.add(contract);
        }
        
        if (debtor.in.get(this) == null) {
            debtor.in.put(this, contractList);
        }
        
        this.ownerGraph.setContractAmount(
                this.ownerGraph.getContractAmount() + 1);
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
    
    final void clear() {
        int edges = 0;
        int contracts = 0;
        
        for (Node borrower : out.keySet()) {
            ++edges;
            contracts += borrower.in.get(this).size();
            borrower.in.remove(this);
        }
        
        out.clear();
        
        for (Node lender : in.keySet()) {
            ++edges;
            contracts += lender.out.get(this).size();
            lender.out.remove(this);
        }
        
        in.clear();
        
        if (ownerGraph != null) {
            ownerGraph.setContractAmount( 
                    ownerGraph.getContractAmount() - contracts);
            ownerGraph.setEdgeAmount(ownerGraph.getEdgeAmount() - edges);
        }
    }
    
    private void checkOwnerGraph() {
        if (this.ownerGraph == null) {
            throw new IllegalStateException(
                    "The node '" + this.getName() + 
                    "' does not belong to any graph.");
        }
    }
}