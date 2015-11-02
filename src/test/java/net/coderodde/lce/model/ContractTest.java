package net.coderodde.lce.model;

import net.coderodde.lce.model.support.BasicContract;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * This class tests <code>net.coderodde.lce.model.Contract</code>.
 * 
 * @author Rodion Efremov
 */
public class ContractTest {

    private Contract c1;
    private Contract c2;
    
    @Before
    public void before() {
        c1 = new BasicContract("contract", 10.0, 0.1, 3.0, 1.0);
        c2 = new BasicContract("contract", 10.0, 0.1, 3.0, 1.0);
    }
    
    @Test
    public void testGetName() {
        assertEquals("contract", c1.getName());
        assertEquals("contract", c2.getName());
    }

    @Test
    public void testEquals() {
        assertEquals("contract", c1.getName());
        assertEquals("contract", c2.getName());
        assertEquals(c1.getName(), c2.getName());
    }

    @Test
    public void testHashCode() {
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    //// principal ////
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetPrincipalNaN() {
        c1.setPrincipal(Double.NaN);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetPrincipalInfinity() {
        c1.setPrincipal(Double.POSITIVE_INFINITY);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetPrincipalNegativeInfinity() {
        c1.setPrincipal(Double.NEGATIVE_INFINITY);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetPrincipalNegative() {
        c1.setPrincipal(-0.01);
    }

    //// interest rate ////
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetInterestRateNaN() {
        c1.setInterestRate(Double.NaN);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetInterestRateInfinity() {
        c1.setInterestRate(Double.POSITIVE_INFINITY);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetInterestRatelNegativeInfinity() {
        c1.setInterestRate(Double.NEGATIVE_INFINITY);
    }
    
    @Test
    public void testSetInterestRateZero() {
        c1.setInterestRate(0);
        assertEquals(0, c1.getInterestRate(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetInterestRateNegative() {
        c1.setInterestRate(-0.01);
    }

    //// compounding periods ////
    
    @Test(expected = IllegalArgumentException.class)
    public void testCompundingPeriodsNaN() {
        c1.setCompoundingPeriods(Double.NaN);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testCompundingPeriodsNegativeInfinity() {
        c1.setCompoundingPeriods(Double.NEGATIVE_INFINITY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetCompundingPeriodsZero() {
        c1.setCompoundingPeriods(0);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testSetCompundingPeriodsNegative() {
        c1.setCompoundingPeriods(-0.01);
    }

    //// timestamp ////
    
    @Test(expected = IllegalArgumentException.class)
    public void testTimestampNaN() {
        c1.setCompoundingPeriods(Double.NaN);
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testTimestampNegativeInfinity() {
        c1.setCompoundingPeriods(Double.NEGATIVE_INFINITY);
    }   
}
