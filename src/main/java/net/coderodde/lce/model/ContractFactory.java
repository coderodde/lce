package net.coderodde.lce.model;

import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.support.BasicContract;
import net.coderodde.lce.model.support.ContinuousContract;

/**
 * This class provides a static method for uniform contract creation.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public final class ContractFactory {
    
    private double principal;
    private double interestRate;
    private double compoundingPeriods;
    private double timestamp;
    
    /**
     * Creates a new contract factory.
     * 
     * @return a contract factory.
     */
    public static ContractFactory newContract() {
        return new ContractFactory();
    }
    
    /**
     * Creates a contract factory.
     */
    private ContractFactory() {}
         
    /**
     * Sets a principal for this factory.
     * 
     * @param principal the principal to set.
     * 
     * @return this contract factory.
     */
    public ContractFactory withPrincipal(double principal) {
        this.principal = (epsilonEquals(principal, 0) ? 0 : principal);
        return this;
    }
    
    /**
     * Sets an interest rate for this factory.
     * 
     * @param interestRate the interest rate to set.
     * 
     * @return this contract factory.
     */
    public ContractFactory withInterestRate(double interestRate) {
        this.interestRate = interestRate;
        return this;
    }
    
    /**
     * Sets the compounding periods for this factory.
     * 
     * @param compoundingPeriods the compounding periods to set.
     * 
     * @return this contract factory.
     */
    public ContractFactory withCompoundingPeriods(double compoundingPeriods) {
        this.compoundingPeriods = compoundingPeriods;
        return this;
    }
        
    /**
     * Sets the compounding periods to those of continuous contracts.
     * 
     * @return this contract factory.
     */
    public ContractFactory withContiguous() {
        this.compoundingPeriods = Double.POSITIVE_INFINITY;
        return this;
    }
        
    /**
     * Sets the time stamp of this contract.
     * 
     * @param timestamp time stamp to set.
     * 
     * @return this contract factory.
     */
    public ContractFactory withTimestamp(double timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    /**
     * Creates a new contract with the state that of this factory.
     * 
     * @param name the name for the new contract.
     * 
     * @return a new contract.
     */
    public Contract create(String name) {
        Contract c;
        
        if (Double.isInfinite(compoundingPeriods) && compoundingPeriods > 0.0) {
            c = new ContinuousContract(name, 
                                       principal, 
                                       interestRate, 
                                       timestamp);
        } else {
            c = new BasicContract(name,
                                  principal, 
                                  interestRate, 
                                  compoundingPeriods,
                                  timestamp);
        }
        
        return c;
    }
}
