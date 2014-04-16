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
    
    @Before
    public void before() {
        graph = new Graph("MyGraph");
        a = new Node("A");
        b = new Node("B");
        c = new Node("C");
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
    public void testFindEquilibrialDebtCuts() {
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
        
        assertTrue(graph.isInEquilibriumAt(5.0));
        assertTrue(graph.isInEquilibriumAt(7.0));
        
        graph.remove(b);
        
        assertFalse(graph.isInEquilibriumAt(5.0));
        assertFalse(graph.isInEquilibriumAt(7.0));
    }

    @Test
    public void testSize() {
    }

    @Test
    public void testGetEdgeAmount() {
    }

    @Test
    public void testGetContractAmount() {
    }

    @Test
    public void testSetEdgeAmount() {
    }

    @Test
    public void testSetContractAmount() {
    }
    
}
