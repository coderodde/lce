package net.coderodde.lce.model;

/**
 * This abstract class defines the common API for contracts.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public abstract class Contract {
    
    /**
     * Evaluates the equity of this contract at time <code>time</code>.
     * 
     * @param time the time at which to evaluate equity.
     * 
     * @return the equity of this contract at time <code>time</code>.
     */
    public abstract double evaluate(final double time);
    
    /**
     * Returns the time stamp at which this contract was granted.
     * 
     * @return the time stamp at which this contract was granted. 
     */
    public abstract double getTimestamp();
    
    /**
     * Applies a debt cut to this contract.
     * 
     * @param debtCutAssignment the debt cut assignment object.
     */
    protected abstract void applyDebtCut
        (final double debtCut, final double time);
}
