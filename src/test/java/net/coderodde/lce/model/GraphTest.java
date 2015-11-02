package net.coderodde.lce.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class tests <code>net.coderodde.lce.model.Graph</code>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class GraphTest {
    
    private Graph graph;
    private Node a;
    private Node b;
    private Node c;
    private Contract contract;
    
    @Before
    public void before() {
        graph = new Graph("MyGraph");
        a = new Node("A");
        b = new Node("B");
        c = new Node("C");
        contract = ContractFactory
                   .newContract().withPrincipal(10.0)
                                 .withContiguous()
                                 .withInterestRate(0.12)
                                 .withTimestamp(1.0)
                                 .create("Default contract");
    }
    
    @Test
    public void testAdd() {
        assertEquals(0, graph.size());
        graph.add(a);
        assertEquals(1, graph.size());
        graph.add(b);
        assertEquals(2, graph.size());
        graph.add(c);
        assertEquals(3, graph.size());
    }

    @Test
    public void testContains() {
        assertFalse(graph.contains(a));
        assertFalse(graph.contains(b));
        assertFalse(graph.contains(c));
        
        graph.add(a);
        graph.add(b);
        graph.add(c);
        
        assertTrue(graph.contains(a));
        assertTrue(graph.contains(b));
        assertTrue(graph.contains(c));
    }

    @Test
    public void testGetNodeByName() {
        assertNull(graph.getNode("nosuch"));
        
        graph.add(a);
        graph.add(b);
        
        assertEquals(a, graph.getNode(a.getName()));
        assertNotEquals(a, graph.getNode(b.getName()));
        
        assertEquals(b, graph.getNode(b.getName()));
        assertNotEquals(b, graph.getNode(a.getName()));
    }
    
    @Test
    public void testGetNodeByReference() {
        assertNull(graph.getNode(new Node("X")));
        
        graph.add(a);
        graph.add(b);
        
        assertEquals(a, graph.getNode(a));
        assertNotEquals(a, graph.getNode(b));
        
        assertEquals(b, graph.getNode(b));
        assertNotEquals(b, graph.getNode(a));
    }

    @Test
    public void testRemove() {
        graph.add(b);
        graph.add(c);
        graph.add(a);
        
        assertEquals(3, graph.size());
        
        graph.remove(a);
        
        assertEquals(2, graph.size());
        
        graph.remove(c);
        
        assertEquals(1, graph.size());
        
        graph.remove(a);
        
        assertEquals(1, graph.size());
        
        graph.remove(b);
        
        assertEquals(0, graph.size());
    }

    @Test
    public void testIsInEquilibriumAt() {
        graph.add(a);
        graph.add(b);
        graph.add(c);
        
        Contract contract = ContractFactory
                            .newContract()
                            .withPrincipal(10.0)
                            .withInterestRate(0.15)
                            .withCompoundingPeriods(Double.POSITIVE_INFINITY)
                            .withTimestamp(3.0)
                            .create("MyContract");
        
        graph.getNode(a).addDebtor(graph.getNode(b), contract);
        graph.getNode(b).addDebtor(graph.getNode(c), contract);
        graph.getNode(c).addDebtor(graph.getNode(a), contract);
        
        assertEquals(30.0, graph.getTotalFlowAt(3.0), 0.001);
        assertEquals(40.4957642273, graph.getTotalFlowAt(5.0), 0.001);
        
        assertTrue(graph.isInEquilibriumAt(5.0));
        assertTrue(graph.isInEquilibriumAt(7.0));
        
        graph.remove(b);
        
        assertFalse(graph.isInEquilibriumAt(5.0));
        assertFalse(graph.isInEquilibriumAt(7.0));
    }

    @Test
    public void testGetEdgeAmount() {
        assertEquals(0, graph.getEdgeAmount());
        assertEquals(0, graph.getContractAmount());
        graph.add(c);
        assertEquals(0, graph.getEdgeAmount());
        assertEquals(0, graph.getContractAmount());
        graph.add(a);
        assertEquals(0, graph.getEdgeAmount());
        assertEquals(0, graph.getContractAmount());
        
        graph.getNode(a).addDebtor(graph.getNode(c), contract);
        
        assertEquals(1, graph.getEdgeAmount());
        assertEquals(1, graph.getContractAmount());
        
        graph.getNode(c).addDebtor(graph.getNode(a),
                ContractFactory.newContract()
                               .withPrincipal(4.0)
                               .withCompoundingPeriods(2.0)
                               .withInterestRate(0.04)
                               .withTimestamp(1.4)
                               .create("Another contract"));
        
        assertEquals(2, graph.getEdgeAmount());
        assertEquals(2, graph.getContractAmount());
        
        graph.getNode(c).addDebtor(graph.getNode(a),
                ContractFactory.newContract()
                               .withPrincipal(2.5)
                               .withInterestRate(0.034)
                               .withCompoundingPeriods(4.0)
                               .withTimestamp(0.8)
                               .create("Another contract 2"));
        
        assertEquals(2, graph.getEdgeAmount());
        assertEquals(3, graph.getContractAmount());
    }    
    
    @Test(expected = IllegalStateException.class)
    public void testNodesMustBeInTheSameGraph() {
        Graph other = new Graph("Another graph");
        other.add(a);
        graph.add(b);
        other.getNode(a).addDebtor(graph.getNode(b), contract);
    }
}
