package net.coderodde.lce.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        checkOtherNode(debtor);
        
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
    
    public final double getMaximumTimestamp() {
        return maximumTimestamp;
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
 
    public final Collection<Node> getDebtors() {
        return Collections.<Node>unmodifiableSet(this.out.keySet());
    }
    
    public final Collection<Node> getLenders() {
        return Collections.<Node>unmodifiableSet(this.in.keySet());
    }
    
    public final Collection<Contract> getContractsTo(final Node debtor) {
        return Collections.unmodifiableCollection(this.out.get(debtor));
    }
    
    public final Collection<Contract> getOutgoingContracts() {
        List<Contract> contracts = new ArrayList<Contract>();
        
        for (List<Contract> tmp : this.out.values()) {
            contracts.addAll(tmp);
        }
        
        return Collections.<Contract>unmodifiableList(contracts);
    }
    
    public final Collection<Contract> getIncomingContracts() {
        List<Contract> contracts = new ArrayList<Contract>();
        
        for (List<Contract> tmp : this.in.values()) {
            contracts.addAll(tmp);
        }
        
        return Collections.<Contract>unmodifiableList(contracts);
    }
    
    public final double getOutgoingFlowAt(final double time) {
        double d = 0;
        
        for (final List<Contract> contractList : out.values()) {
            for (final Contract contract : contractList) {
                d += contract.evaluate(time);
            }
        }
        
        return d;
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
    
    private void checkOtherNode(final Node node) {
        if (node.ownerGraph != this.ownerGraph) {
            throw new IllegalStateException(
                    "The node '" + node.getName() + "' is not in the same " +
                    "graph with the node '" + this.getName() + "'.");
        }
    }
}