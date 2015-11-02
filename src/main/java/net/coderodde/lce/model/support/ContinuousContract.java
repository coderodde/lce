package net.coderodde.lce.model.support;

import static net.coderodde.lce.Utils.checkNotNegative;
import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.Contract;

/**
 * This class models the contracts having continuous compounding scheme.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public final class ContinuousContract extends Contract {
    
    /**
     * Constructs a new contract with contiguous compounding scheme.
     * 
     * @param name         the name of the contract.
     * @param principal    the initial investment.
     * @param interestRate the annual interest rate.
     * @param timestamp    the timestamp of this contract.
     */
    public ContinuousContract(String name,
                              double principal,
                              double interestRate,
                              double timestamp) {
        super(name);
        setPrincipal(principal);
        setInterestRate(interestRate);
        setTimestamp(timestamp);
        setCompoundingPeriods(Double.POSITIVE_INFINITY);
    }
    
    /**
     * Redefines the equality test to suit the continuous contracts.
     * 
     * @param o the object to test against.
     * 
     * @return <code>true</code> if the two objects are of the same type and 
     * share the same state.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof ContinuousContract)) {
            return false;
        }
        
        ContinuousContract other = (ContinuousContract) o;
        double e = 0.001;
        
        return epsilonEquals(this.principal, other.principal, e)
                && epsilonEquals(this.interestRate, other.interestRate, e)
                && epsilonEquals(this.timestamp, other.timestamp, e);
    }
    
    /**
     * Computes the value of this contract at time <code>time</code>.
     * 
     * @param time the time point.
     * 
     * @return the value of this contract at time <code>time</code>.
     */
    @Override
    public double evaluate(double time) {
        checkNotNegative(time, "Duration is not allowed to be negative.");
        return principal * Math.pow(Math.E, interestRate * time);
    }
    
    /**
     * Returns the growth factor at specified moment.
     * 
     * @param time the target moment.
     * 
     * @return the growth factor. 
     */
    @Override
    public double getGrowthFactor(double time) {
        return Math.pow(Math.E, this.getInterestRate() * time);
    }
    
    /**
     * Returns always <code>true</code> as this contract has continuous
     * compounding scheme.
     * 
     * @return <code>true</code>.
     */
    @Override
    public boolean isContinuous() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @param time the time to apply the debt cut.
     * 
     * @return the shift correction which in the case of a contiguous 
     * compounding equals zero (0).
     */
    @Override
    public double getShiftCorrection(double time) {
        return 0.0;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return "[" + principal + ", " + interestRate + ", " + timestamp + "]";
    }
}
