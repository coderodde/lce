package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static net.coderodde.lce.Utils.checkNotInfinite;
import static net.coderodde.lce.Utils.checkNotNaN;
import static net.coderodde.lce.Utils.checkNotNegative;

/**
 * This class represents the debt cut assignments as a map from contracts to 
 * their respective debt cuts.
 *  
 * @author Rodion Efremov
 * @version 1.618
 */
public final class DebtCutAssignment {
    
    /**
     * The map mapping a contract to its debt cut.
     */
    private final Map<Contract, Double> map = new HashMap<>();
    
    /**
     * The equilibrium time point.
     */
    private final double equilibriumTime;
    
    /**
     * The sum of all debt cuts.
     */
    private double sum;
    
    /**
     * Constructs a new debt cut assignment object.
     * 
     * @param equilibriumTime the equilibrium time.
     */
    public DebtCutAssignment(double equilibriumTime) {
        this.equilibriumTime = equilibriumTime;
    }
    
    /**
     * Retrieves the debt cut for a contract.
     * 
     * @param contract the contract whose debt cut to retrieve.
     * 
     * @return the debt cut.
     * 
     * @throws IllegalArgumentException if unknown contract is passed.
     */
    public double get(Contract contract) {
        Objects.requireNonNull(contract, "The contract may not be null.");
        
        if (this.map.containsKey(contract) == false) {
            throw new IllegalArgumentException(
                    "No contract in this DebtCutAssignment.");
        }
        
        return this.map.get(contract);
    }
    
    /**
     * Returns an unmodifiable view of all contracts in this assignment.
     * 
     * @return an unmodifiable view of all contracts in this assignment. 
     */
    public Collection<Contract> getContracts() {
        return Collections.<Contract>unmodifiableCollection(this.map.keySet());
    }
    
    /**
     * Puts the tuple (<code>contract</code>, <code>debtCut</code>) in this
     * assignment.
     * 
     * @param contract the contract as a key.
     * @param debtCut the debt cut as a value.
     */
    public void put(Contract contract, double debtCut) {
        Objects.requireNonNull(contract, "The contract may not be null.");
        checkNotNaN(debtCut, "The debt cut may not be NaN.");
        checkNotInfinite(debtCut, "The debt cut may not be infinite.");
        checkNotNegative(debtCut, "The debt cut may not be negative.");
        this.map.put(contract, debtCut);
        this.sum += debtCut;
    }
    
    /**
     * Checks whether this debt cut assignment has a cut for
     * <code>contract</code>.
     * 
     * @param contract the contract to query.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean containsFor(Contract contract) {
        return map.containsKey(contract);
    }
    
    /**
     * Returns the equilibrium time.
     * 
     * @return the equilibrium time.
     */
    public double getEquilibriumTime() {
        return equilibriumTime;
    }
    
    /**
     * Returns the sum of all the debt cuts in this assignment object.
     * 
     * @return the sum of debt cuts.
     */
    public double sum() {
        return this.sum;
    }
}
