package net.coderodde.lce.model.support;

import static net.coderodde.lce.Utils.checkTimestamp;
import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.Contract;

/**
 * This class models the contracts having continuous compounding scheme.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class ContinuousContract extends Contract {
    
    /**
     * Constructs a new contract with contiguous compounding scheme.
     * 
     * @param principal the initial investment.
     * @param interestRate the annual interest rate.
     * @param timestamp the timestamp of this contract.
     */
    public ContinuousContract(final String name,
                              final double principal,
                              final double interestRate,
                              final double timestamp) {
        super(name);
        setPrincipal(principal);
        setInterestRate(interestRate);
        setTimestamp(timestamp);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof ContinuousContract)) {
            return false;
        }
        
        ContinuousContract other = (ContinuousContract) o;
        final double e = 0.001;
        
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
        checkTimestamp(timestamp, time);
        return principal * Math.pow(Math.E, interestRate * (time - timestamp));
    }
    
    @Override
    public boolean isContiguous() {
        return true;
    }
}
