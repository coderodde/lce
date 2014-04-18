package net.coderodde.lce.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This test class tests <code>net.coderodde.lce.model.Node</code>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class NodeTest {
    
    private Node a = new Node("A");
    private Node b = new Node("B");
    private Node c = new Node("C");
    
    public NodeTest() {
        
    }

    @Test
    public void testGetName() {
        assertEquals("A", a.getName());
        assertEquals("B", b.getName());
        assertEquals("C", c.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDebtor() {
        a.addDebtor(b, ContractFactory.newContract().create("contract"));
    }    
}
