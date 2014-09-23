package net.coderodde.lce.model;

import static net.coderodde.lce.Utils.checkCompoundingPeriods;
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
    
    /**
     * Constructs a new contract with a given name.
     * 
     * @param name the name of a new contract
     */
    public Contract(final String name) {
        checkNotNull(name, "The name of a contract is null.");
        this.name = name;
    }
    
    /**
     * Returns a textual description of this contract.
     * 
     * @return a textual description.
     */
    @Override
    public final String toString() {
        return "[Contract " + getName() + "]";
    }
    
    /**
     * Returns a name of this contract.
     * 
     * @return a name of this contract. 
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Checks whether the two object are equal. 
     * 
     * @param o the object to test against.
     * 
     * @return <code>true</code> if <code>o</code> is also a contract, and holds
     * the same data.
     */
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
    
    /**
     * Returns the hash of this contract.
     * 
     * @return the hash of this contract.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    /**
     * Returns a contract that results from applying a debt cut to this 
     * contract.
     * 
     * @param dca the debt cut assignment object.
     * @param time the time point at which to apply the cut.
     * 
     * @return a new contract.
     */
    public Contract applyDebtCut(final DebtCutAssignment dca, 
                                 final double time) {
        Contract c;
        c = ContractFactory
                .newContract()
                .withCompoundingPeriods(this.compoundingPeriods)
                .withInterestRate(this.interestRate)
                .withTimestamp(time)
                .withPrincipal(this.evaluate(time - this.getTimestamp()) 
                               - dca.get(this))
                .create("Copy of " + this.getName());
        
        return c;
    }
    
    /**
     * Returns the principal of this contract.
     * 
     * @return the principal of this contract.
     */
    public double getPrincipal() {
        return this.principal;
    }
    
    /**
     * Returns the interest rate of this contract.
     * 
     * @return the interest rate of this contract.
     */
    public double getInterestRate() {
        return this.interestRate;
    }
    
    /**
     * Returns the compounding periods of this contract.
     * 
     * @return the compounding periods of this contract.
     */
    public double getCompoundingPeriods() {
        return this.compoundingPeriods;
    }
    
    /**
     * Returns the timestamp of this contract.
     * 
     * @return the timestamp of this contract.
     */
    public double getTimestamp() {
        return this.timestamp;
    }
    
    /**
     * Checks whether this contract has contiguous compounding scheme.
     * 
     * @return <code>true</code> if this contract is a continuous contract;
     * <code>false</code> otherwise.
     */
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
     * Clones this contact.
     * 
     * @return a clone contract.
     */
    public Contract clone() {
        return ContractFactory
                .newContract()
                .withPrincipal(this.getPrincipal())
                .withInterestRate(this.getInterestRate())
                .withCompoundingPeriods(this.getCompoundingPeriods())
                .withTimestamp(this.getTimestamp())
                .create("Clone of " + this.getName());
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
     * Returns the growth factor of this contract with duration 
     * <code>time</code>.
     * 
     * @param duration the duration.
     * 
     * @return the growth factor at <code>duration</code>.
     */
    public abstract double getGrowthFactor(final double duration);
    
    /**
     * If the implementation does not have contiguous compounding, returns
     * a time interval needed to be subtracted from the time stamp; otherwise
     * returns zero (0).
     * 
     * @param time for applying a debt cut.
     * 
     * @return shift correction. 
     */
    public abstract double getShiftCorrection(final double time); 
}
