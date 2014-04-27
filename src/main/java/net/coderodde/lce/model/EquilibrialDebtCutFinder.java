package net.coderodde.lce.model;

import net.coderodde.lce.model.support.DefaultDebtCutAssignment;

/**
 * This interface defines the API for algorithms computing equilibrial debt
 * cuts. 
 * 
 * @author Rodion Efremov
 * @version 
 */
public interface EquilibrialDebtCutFinder {
    
    /**
     * The entry point into a debt cut finder.
     * 
     * @param graph the graph to work on.
     * @param timeAssignment the time assignment object.
     * 
     * @return the debt cut assignment object.
     */
    public DefaultDebtCutAssignment compute(final Graph graph,
                                            final TimeAssignment timeAssignment, 
                                            final double time);    
}
