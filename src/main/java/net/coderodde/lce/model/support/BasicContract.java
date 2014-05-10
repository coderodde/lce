package net.coderodde.lce.model.support;

import net.coderodde.lce.model.Contract;
import static net.coderodde.lce.Utils.checkNotNegative;

/**
 * This class models a contract with periodical compounding scheme.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class BasicContract extends Contract {
    
    /**
     * Constructs a new <code>BasicContract</code>.
     * 
     * @param name the name of this contract.
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
    public final double evaluate(final double time) {
        checkNotNegative(time, "Duration is not allowed to be negative.");
        return principal * Math.pow(1.0 + interestRate / compoundingPeriods, 
                           Math.floor(compoundingPeriods * time));
    }
    
    @Override
    public final double getGrowthFactor(final double time) {
        return Math.pow(1.0 + this.getInterestRate() / 
                              this.getCompoundingPeriods(),
                              Math.floor(this.getCompoundingPeriods() * time));
    }
    
    @Override
    public final double getTimestamp() {
        return timestamp;
    }
   
    @Override
    public boolean isContiguous() {
        return false;
    }
}
