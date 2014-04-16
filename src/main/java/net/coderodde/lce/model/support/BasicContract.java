package net.coderodde.lce.model.support;

import net.coderodde.lce.model.Contract;
import static net.coderodde.lce.Utils.checkCompoundingPeriods;;
import static net.coderodde.lce.Utils.checkInterestRate;
import static net.coderodde.lce.Utils.checkPrincipal;
import static net.coderodde.lce.Utils.checkTimestamp;
import static net.coderodde.lce.Utils.checkDebtCut;

/**
 * This class models a contract with periodical compounding scheme.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class BasicContract extends Contract {

    /**
     * The principal investment.
     */
    private double principal;
    
    /**
     * The interest rate.
     */
    private double interestRate;
    
    /**
     * The amount of compounding periods per year.
     */
    private double compoundingPeriods;
    
    /**
     * The moment at which the contract was admitted. One unit corresponds to
     * one year.
     */
    private double timestamp;
    
    /**
     * Constructs a new <code>BasicContract</code>.
     * 
     * @param principal the initial investment.
     * @param interestRate the annual interest rate.
     * @param compoundingPeriods the amount of compounding periods.
     * @param timestamp the timestamp of this contract.
     */
    public BasicContract(final String name,
                         final double principal,
                         final double interestRate,
                         final double compoundingPeriods,
                         final double timestamp) {
        super(name);
        setPrincipal(principal);
        setInterestRate(interestRate);
        setCompoundingPeriods(compoundingPeriods);
        setTimestamp(timestamp);
    }
    
    /**
     * Evaluates this contract at the specified moment.
     * 
     * @param time the time point.
     * 
     * @return the value of this contract at time <code>time</code>. 
     */
    @Override
    public final double evaluate(double time) {
        checkTimestamp(time, timestamp);
        return principal * Math.pow(1.0 + interestRate / compoundingPeriods, 
                           Math.floor(compoundingPeriods * (time - timestamp)));
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
    
    /**
     * Sets the compounding periods of this contract.
     * 
     * @param compoundingPeriods the compounding periods to set.
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
    
    @Override
    protected void applyDebtCut(final double debtCut, final double time) {
        checkTimestamp(this.timestamp, time);
        double equityAtTime = this.evaluate(time);
        checkDebtCut(debtCut, equityAtTime);
        this.principal = equityAtTime - debtCut;
        this.timestamp = time;
    }
}
