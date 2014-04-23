package net.coderodde.lce.model.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static net.coderodde.lce.Utils.checkNotInfinite;
import static net.coderodde.lce.Utils.checkNotNaN;
import static net.coderodde.lce.Utils.checkNotNegative;
import static net.coderodde.lce.Utils.checkNotNull;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.DebtCutAssignment;

/**
 * This class represents the debt cut assignments as a map from contracts to 
 * their respective debt cuts.
 *  
 * @author Rodion Efremov
 * @version 1.6
 */
public class DefaultDebtCutAssignment implements DebtCutAssignment {
    
    private final Map<Contract, Double> map;
    
    DefaultDebtCutAssignment() {
        this.map = new HashMap<>();
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
    @Override
    public final double get(final Contract contract) {
        checkNotNull(contract, "The contract may not be null.");
        
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
    @Override
    public final Collection<Contract> getContracts() {
        return Collections.<Contract>unmodifiableCollection(this.map.keySet());
    }
    
    /**
     * Puts the tuple (<code>contract</code>, <code>debtCut</code>) in this
     * assignment.
     * 
     * @param contract the contract as a key.
     * @param debtCut the debt cut as a value.
     */
    final void put(final Contract contract, final double debtCut) {
        checkNotNull(contract, "The contract may not be null.");
        checkNotNaN(debtCut, "The debt cut may not be NaN.");
        checkNotInfinite(debtCut, "The debt cut may not be infinite.");
        checkNotNegative(debtCut, "The debt cut may not be negative.");
        this.map.put(contract, debtCut);
    }
}
