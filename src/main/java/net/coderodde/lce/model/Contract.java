package net.coderodde.lce.model;

import static net.coderodde.lce.Utils.checkCompoundingPeriods;import static net.coderodde.lce.Utils.checkDebtCut;
;
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
    
    /**
     * The name of this contract.
     */
    protected final String name;
    
    /**
     * The principal investment.
     */
    protected double principal;
    
    /**
     * The interest rate.
     */
    protected double interestRate;
    
    /**
     * The amount of compounding periods per year.
     */
    protected double compoundingPeriods;
    
    /**
     * The moment at which the contract was admitted. One unit corresponds to
     * one year.
     */
    protected double timestamp;
    
    public Contract(final String name) {
        checkNotNull(name, "The name of a contract is null.");
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
        
        Contract c = (Contract) o;
        final double e = 0.001;
        
        return epsilonEquals(principal, c.principal, e) 
                && epsilonEquals(interestRate, c.interestRate, e)
                && epsilonEquals(compoundingPeriods, c.compoundingPeriods, e)
                && epsilonEquals(timestamp, c.timestamp, e);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    public double getPrincipal() {
        return this.principal;
    }
    
    public double getInterestRate() {
        return this.interestRate;
    }
    
    public double getCompoundingPeriods() {
        return this.compoundingPeriods;
    }
    
    public double getTimestamp() {
        return this.timestamp;
    }
    
    public abstract boolean isContiguous();
    
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
     * Sets the compounding periods of this contract.
     * 
     * @param compoundingPeriods the compounding periods.
     */
    public void setCompoundingPeriods(final double compoundingPeriods) {
        checkCompoundingPeriods(compoundingPeriods);
        this.compoundingPeriods = compoundingPeriods;
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
     * Applies a debt cut to this contract.
     * @param debtCut
     * @param time 
     */
    public void applyDebtCut(final double debtCut, final double time) {
        checkTimestamp(this.timestamp, time);
        double equityAtTime = this.evaluate(time);
        checkDebtCut(debtCut, equityAtTime);
        setPrincipal(equityAtTime - debtCut);
        setTimestamp(time);
    }
    
    /**
     * Evaluates the equity of this contract at time <code>time</code>.
     * 
     * @param time the time at which to evaluate equity.
     * 
     * @return the equity of this contract at time <code>time</code>.
     */
    public abstract double evaluate(final double time);
    
    public abstract double getGrowthFactor(final double time);
}
