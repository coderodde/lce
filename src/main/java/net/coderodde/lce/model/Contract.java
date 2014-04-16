package net.coderodde.lce.model;

import static net.coderodde.lce.Utils.checkInterestRate;
import static net.coderodde.lce.Utils.checkNotNull;
import static net.coderodde.lce.Utils.checkPrincipal;
import static net.coderodde.lce.Utils.checkTimestamp;
import static net.coderodde.lce.Utils.epsilonEquals;

/**
 * This abstract class defines the common API for contracts.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public abstract class Contract {
    
    protected final String name;
    protected double principal;
    protected double interestRate;
    protected double compoundingPeriods;
    protected double timestamp;
    
    public Contract(final String name) {
        checkNotNull(name, "The name of contract is null.");
        this.name = name;
    }
    
    public final String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof Contract)) {
            return false;
        }
        
        return getName().equals(((Contract) o).getName());
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    /**
     * Sets the principal of this contract.
     * 
     * @param principal the principal to set.
     */
    public void setPrincipal(final double principal) {
        checkPrincipal(principal);
        this.principal = principal;
    }
    
    /**
     * Sets the interest rate of this contract.
     * 
     * @param interestRate the interest rate to set.
     */
    public void setInterestRate(final double interestRate) {
        checkInterestRate(interestRate);
        this.interestRate = interestRate;
    }
    
    /**
     * Sets the timestamp of this contract.
     * 
     * @param timestamp the timestamp to set. 
     */
    public void setTimestamp(final double timestamp) {
        checkTimestamp(timestamp);
        this.timestamp = timestamp;
    }
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
