package net.coderodde.lce.model;

/**
 * This interface defines the common API for contracts.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public interface Contract {
    
    /**
     * Evaluates the equity of this contract at time <code>time</code>.
     * 
     * @param time the time at which to evaluate equity.
     * 
     * @return the equity of this contract at time <code>time</code>.
     */
    public double evaluate(final double time);
    
    /**
     * Returns the time stamp at which this contract was granted.
     * 
     * @return the time stamp at which this contract was granted. 
     */
    public double getTimestamp();
}
