package net.coderodde.lce.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static net.coderodde.lce.Utils.checkNotInfinite;
import static net.coderodde.lce.Utils.checkNotNaN;
import static net.coderodde.lce.Utils.checkNotNegative;
import static net.coderodde.lce.Utils.checkNotNull;

/**
 * This class represents the debt cut assignments as a map from contracts to 
 * their respective debt cuts.
 *  
 * @author Rodion Efremov
 * @version 1.6
 */
public class DebtCutAssignment {
    
    private final Map<Contract, Double> map;
    
    public DebtCutAssignment() {
        this.map = new HashMap<>();
    }
    
    public final double get(final Contract contract) {
        checkNotNull(contract, "The contract may not be null.");
        
        if (this.map.containsKey(contract) == false) {
            throw new IllegalArgumentException(
                    "No contract in this DebtCutAssignment.");
        }
        
        return this.map.get(contract);
    }
    
    /**
     * Returns an unmodifiable set of all contracts in this assignment.
     * 
     * @return an unmodifiable set of all contracts in this assignment. 
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
    final void put(final Contract contract, final double debtCut) {
        checkNotNull(contract, "The contract may not be null.");
        checkNotNaN(debtCut, "The debt cut may not be NaN.");
        checkNotInfinite(debtCut, "The debt cut may not be infinite.");
        checkNotNegative(debtCut, "The debt cut may not be negative.");
        this.map.put(contract, debtCut);
    }
}
