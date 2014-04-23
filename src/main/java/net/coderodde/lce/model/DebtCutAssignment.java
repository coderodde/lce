package net.coderodde.lce.model;

import java.util.Collection;

/**
 * This interface defines the API of a debt cut assignment.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public interface DebtCutAssignment {
    
    /**
     * Gets the debt cut associated with the specified contract.
     * 
     * @param contract the contract whose debt cut to query.
     * 
     * @return the debt cut for <code>contract</code>.
     */
    public double get(final Contract contract);
    
    /**
     * Gets the unmodifiable view of contracts.
     * 
     * @return the unmodifiable view of contracts.
     */
    public Collection<Contract> getContracts();
}
