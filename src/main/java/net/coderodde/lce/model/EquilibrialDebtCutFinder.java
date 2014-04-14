package net.coderodde.lce.model;

import java.util.Map;

/**
 * This interface defines the API for algorithms computing equilibrial debt
 * cuts. 
 * 
 * @author Rodion Efremov
 * @version 
 */
public interface EquilibrialDebtCutFinder {
    
    public Map<Contract, Double> compute(final Graph graph, 
                                         final Map<Node, Double> timeMap);
    
}
