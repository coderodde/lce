package net.coderodde.lce.model;

import java.util.Objects;
import net.coderodde.lce.Utils;
import static net.coderodde.lce.Utils.checkCompoundingPeriods;
import static net.coderodde.lce.Utils.checkInterestRate;
import static net.coderodde.lce.Utils.checkPrincipal;
import static net.coderodde.lce.Utils.checkTimestamp;
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
    public static PrincipalSelector newContract(String name) {
        return new PrincipalSelector(name);
    }
    
    public static final class PrincipalSelector {
        
        private final String name;
        
        PrincipalSelector(String name) {
            Objects.requireNonNull(name, "The contract name is null.");
            this.name = name;
        }
        
        public InterestRateSelector withPrincipal(double principal) {
            checkPrincipal(principal);
            return new InterestRateSelector(name, principal);
        }
    }
    
    public static final class InterestRateSelector {
        
        private final String name;
        private final double principal;
        
        InterestRateSelector(String name, double principal) {
            this.name = name;
            this.principal = principal;
        }
        
        public CompoundingPeriodSelector 
            withInterestRateSelector(double interestRate) {
            checkInterestRate(interestRate);
            return new CompoundingPeriodSelector(name, principal, interestRate);
        }
    }
    
    public static final class CompoundingPeriodSelector {
        
        private final String name;
        private final double principal;
        private final double interestRate;
        
        CompoundingPeriodSelector(String name, 
                                  double principal, 
                                  double interestRate) {
            this.name = name;
            this.principal = principal;
            this.interestRate = interestRate;
        }
        
        public BasicCompoundingTimestampSelector 
            withCompoundingPeriods(double periods) {
            checkCompoundingPeriods(periods);
            return new BasicCompoundingTimestampSelector(name,
                                                         principal,
                                                         interestRate,
                                                         periods);
        }
        
        public ContinuousCompoundingTimestampSelector
            withContinuousCompounding() {
            return new ContinuousCompoundingTimestampSelector(name,
                                                              principal,
                                                              interestRate);
        }
    }
    
    public static final class BasicCompoundingTimestampSelector {
        
        private final String name;
        private final double principal;
        private final double interestRate;
        private final double compoundingPeriods;
        
        BasicCompoundingTimestampSelector(String name,
                                          double principal,
                                          double interestRate,
                                          double compoundingPeriods) {
            this.name = name;
            this.principal = principal;
            this.interestRate = interestRate;
            this.compoundingPeriods = compoundingPeriods;
        }
        
        public BasicContract withTimestamp(double timestamp) {
            checkTimestamp(timestamp);
            return new BasicContract(name, 
                                     principal, 
                                     interestRate, 
                                     compoundingPeriods, 
                                     timestamp);
        }
    }
    
    public static final class ContinuousCompoundingTimestampSelector {
        
        private final String name;
        private final double principal;
        private final double interestRate;
        
        ContinuousCompoundingTimestampSelector(String name,
                                          double principal,
                                          double interestRate) {
            this.name = name;
            this.principal = principal;
            this.interestRate = interestRate;
        }
        
        public ContinuousContract withTimestamp(double timestamp) {
            return new ContinuousContract(name, 
                                          principal, 
                                          interestRate, 
                                          timestamp);
        }
    }
    
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
