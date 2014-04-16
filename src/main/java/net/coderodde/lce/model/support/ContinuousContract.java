package net.coderodde.lce.model.support;

import static net.coderodde.lce.Utils.checkInterestRate;
import static net.coderodde.lce.Utils.checkPrincipal;
import static net.coderodde.lce.Utils.checkTimestamp;
import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.DebtCutAssignment;

/**
 * This class models the contracts having continuous compounding scheme.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class ContinuousContract extends Contract {

    /**
     * The principal investment;
     */
    private double principal;
    
    /**
     * The interest rate.
     */
    private double interestRate;
    
    /**
     * The moment at which the contract was admitted. One unit corresponds to
     * one year.
     */
    private double timestamp;
    
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
        
        return epsilonEquals(this.principal, other.principal)
                && epsilonEquals(this.interestRate, other.interestRate)
                && epsilonEquals(this.timestamp, other.timestamp);
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
    public final double getTimestamp() {
        return timestamp;
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
    
    public void setTimestamp(final double timestamp) {
        checkTimestamp(timestamp);
        this.timestamp = timestamp;
    }

    @Override
    protected void applyDebtCut(double debtCut, double time) {
        
    }
}
