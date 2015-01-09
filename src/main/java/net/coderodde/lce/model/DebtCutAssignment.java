package net.coderodde.lce.model;

import java.util.Collection;

/**
 * This interface defines the API of a debt cut assignment.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public abstract class DebtCutAssignment {
    
    /**
     * Gets the debt cut associated with the specified contract.
     * 
     * @param contract the contract whose debt cut to query.
     * 
     * @return the debt cut for <code>contract</code>.
     */
    public abstract double get(final Contract contract);
    
    /**
     * Gets the unmodifiable view of contracts.
     * 
     * @return the unmodifiable view of contracts.
     */
    public abstract Collection<Contract> getContracts();
    
    /**
     * Maps the contract <code>contract</code> to the debt cut <code>cut</code>.
     * 
     * @param contract the contract to map.
     * @param cut      the debt cut to associate with <code>contract</code>.
     */
    public abstract void put(final Contract contract, final double cut);
    
    /**
     * Checks whether this debt cut assignment contains a cut for 
     * <code>contract</code>.
     * 
     * @param contract the contract to query.
     * 
     * @return <code>true</code> if this debt cut assignment contains a cut
     * for <code>contract</code>; <code>false</code> otherwise.
     */
    public abstract boolean containsFor(final Contract contract); 
    
    /**
     * Returns the equilibrium time of this debt cut assignment.
     * 
     * @return the equilibrium time.
     */
    public abstract double getEquilibriumTime();
    
    /**
     * Returns the sum of all debt cuts.
     * 
     * @return the sum of all debt cuts. 
     */
    public abstract double sum();
}
