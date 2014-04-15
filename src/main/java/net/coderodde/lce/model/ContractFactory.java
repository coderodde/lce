package net.coderodde.lce.model;

import static net.coderodde.lce.Utils.checkNotNull;
import net.coderodde.lce.model.support.BasicContract;
import net.coderodde.lce.model.support.ContinuousContract;

/**
 * This class provides a static method for uniform contract creation.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class ContractFactory {
    
    private Node lender;
    private Node debtor;
    private double principal;
    private double interestRate;
    private double compoundingPeriods;
    private double timestamp;
    
    public static ContractFactory newContract() {
        return new ContractFactory();
    }
    
    public ContractFactory from(final Node lender) {
        checkNotNull(lender, "The lender node is null.");
        this.lender = lender;
        return this;
    }
    
    public ContractFactory to(final Node debtor) {
        checkNotNull(debtor, "The debtor node is null.");
        this.debtor = debtor;
        return this;
    }
          
    public ContractFactory withPrincipal(final double principal) {
        this.principal = principal;
        return this;
    }
    
    public ContractFactory withInterestRate(final double interestRate) {
        this.interestRate = interestRate;
        return this;
    }
    
    public ContractFactory withCompoundingPeriods
        (final double compoundingPeriods) {
        this.compoundingPeriods = compoundingPeriods;
        return this;
    }
        
    public ContractFactory withTimestamp(final double timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public Contract create() {
        Contract c = null;
        
        if (Double.isInfinite(compoundingPeriods) && compoundingPeriods > 0.0) {
            c = new ContinuousContract(principal, interestRate, timestamp);
        } else {
            c = new BasicContract(principal, 
                                  interestRate, 
                                  compoundingPeriods,
                                  timestamp);
        }
        
        return c;
    }
    
    private ContractFactory() {}
}
