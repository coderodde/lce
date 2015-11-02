package net.coderodde.lce.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class models a node (a party) in a financial graph.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public final class Node {
    
    /**
     * The identity of this node. Must be unique in a graph.
     */
    private final String name;
    
    /**
     * The map from a lender to the list of contracts being lent.
     */
    private final Map<Node, List<Contract>> in = new HashMap<>(); 
    
    /**
     * The map from a borrower to the list of contracts being admitted.
     */
    private final Map<Node, List<Contract>> out = new HashMap<>();
    
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
    public Node(String name) {
        Objects.requireNonNull(name, "The name of a new node is null.");
        this.name = name;
        this.maximumTimestamp = Double.NEGATIVE_INFINITY;
    }
    
    /**
     * Returns the name of this node.
     * 
     * @return the name of this node.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Checks whether the two nodes are equal.
     * 
     * @param o the object to compare against.
     * 
     * @return <code>true</code> if the input object represents the same node;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) {
            return false;
        }
        
        return ((Node) o).getName().equals(this.getName());
    }
    
    /**
     * Returns the hash code of this node.
     * 
     * @return the hash code of this node. 
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    /**
     * Returns textual description of this node.
     * 
     * @return textual description.
     */
    @Override
    public String toString() {
        return "[Node " + getName() + "]";
    }
    
    /**
     * Adds a contract from this node to <code>debtor</code>.
     * 
     * @param debtor   the receiving party.
     * @param contract the contract.
     */
    public void addDebtor(Node debtor, Contract contract) {
        Objects.requireNonNull(debtor, "The lender is null.");
        Objects.requireNonNull(contract, "The contract is null.");
        checkOwnerGraph();
        checkOtherNode(debtor);
        
        if (maximumTimestamp < contract.getTimestamp()) {
            maximumTimestamp = contract.getTimestamp();
        }
        
        if (debtor.getMaximumTimestamp() < contract.getTimestamp()) {
            debtor.setMaximumTimestamp(maximumTimestamp);
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
        
        this.ownerGraph.setMaximumTimestamp(
                Math.max(contract.getTimestamp(),
                         ownerGraph.getMaximumTimestamp()));
    }
    
    /**
     * Returns the largest timestamp in this node.
     * 
     * @return the largest timestamp in this node. 
     */
    public double getMaximumTimestamp() {
        return maximumTimestamp;
    }
    
    /**
     * Computes the equity of this node at moment <code>time</code>.
     * 
     * @param time the time at which to compute equity.
     * 
     * @return equity at moment <code>time</code>.
     */
    public double equity(double time) {
        double equity = 0;
        
        for (List<Contract> contractList : out.values()) {
            for (Contract contract : contractList) {
                equity += contract.evaluate(time - contract.getTimestamp());
            }
        }
        
        for (List<Contract> contractList : in.values()) {
            for (Contract contract : contractList) {
                equity -= contract.evaluate(time - contract.getTimestamp());
            }
        }
        
        return equity;
    }
    
    /**
     * Returns unmodifiable view of this node's debtor nodes.
     * 
     * @return unmodifiable view of this node's debtor nodes.
     */
    public Collection<Node> getDebtors() {
        return Collections.<Node>unmodifiableSet(this.out.keySet());
    }
    
    /**
     * Returns unmodifiable view of this node's lender nodes.
     * 
     * @return unmodifiable view of this node's lender nodes.
     */
    public Collection<Node> getLenders() {
        return Collections.<Node>unmodifiableSet(this.in.keySet());
    }
    
    /**
     * Return unmodifiable view of all the contracts from this node to
     * <code>debtor</code>.
     * 
     * @param debtor the debtor node.
     * 
     * @return unmodifiable view of all the contracts from this node to
     * <code>debtor</code>.
     */
    public Collection<Contract> getContractsTo(Node debtor) {
        return Collections.unmodifiableCollection(this.out.get(debtor));
    }
    
    /**
     * Returns unmodifiable view of all the contracts given by this node.
     * 
     * @return view of outgoing contracts.
     */
    public Collection<Contract> getOutgoingContracts() {
        List<Contract> contracts = new ArrayList<>();
        
        for (List<Contract> tmp : this.out.values()) {
            contracts.addAll(tmp);
        }
        
        return Collections.<Contract>unmodifiableList(contracts);
    }
    
    /**
     * Returns unmodifiable view of all the contracts received by this node.
     * 
     * @return view of incoming contracts.
     */
    public Collection<Contract> getIncomingContracts() {
        List<Contract> contracts = new ArrayList<>();
        
        for (List<Contract> tmp : this.in.values()) {
            contracts.addAll(tmp);
        }
        
        return Collections.<Contract>unmodifiableList(contracts);
    }
    
    /**
     * Computes outgoing flow at the moment <code>time</code>.
     * 
     * @param time the time at which to calculate the flow.
     * 
     * @return outgoing flow at the moment <code>time</code>.
     */
    public double getOutgoingFlowAt(double time) {
        double d = 0;
        
        for (List<Contract> contractList : out.values()) {
            for (Contract contract : contractList) {
                d += contract.evaluate(time - contract.getTimestamp());
            }
        }
        
        return d;
    }
    
    /**
     * Sets the owner graph of this node.
     * 
     * @param graph the graph to set as an owner.
     */
    void setOwnerGraph(Graph graph) {
        in.clear();
        out.clear();
        this.ownerGraph = graph;
    }
    
    /**
     * Removes all contracts having something to do with this node.
     */
    void clear() {
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
    
    /**
     * Sets the maximum timestamp.
     * 
     * @param time timestamp to set.
     */
    void setMaximumTimestamp(double time) {
        this.maximumTimestamp = time;
    }
    
    /**
     * Checks whether this node has an owner graph.
     */
    private void checkOwnerGraph() {
        if (this.ownerGraph == null) {
            throw new IllegalStateException(
                    "The node '" + this.getName() + 
                    "' does not belong to any graph.");
        }
    }
    
    /**
     * Checks whether the two nodes are in the same graph.
     * 
     * @param node the second node.
     */
    private void checkOtherNode(Node node) {
        if (node.ownerGraph != this.ownerGraph) {
            throw new IllegalStateException(
                    "The node '" + node.getName() + "' is not in the same " +
                    "graph with the node '" + this.getName() + "'.");
        }
    }
}