package net.coderodde.lce.model;

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
     * @param equilibriumTime the time at which the graph should be in
     * equilibrium.
     * 
     * @return the debt cut assignment object.
     */
    public DebtCutAssignment compute(final Graph graph,
                                     final TimeAssignment timeAssignment, 
                                     final double equilibriumTime);
    
    /**
     * Queries for the time in milliseconds spent on matrix reduction.
     * 
     * @return time in milliseconds.
     */
    public long getMatrixReductionTime();
    
    /**
     * Queries for the time in milliseconds spent on minimizing the cuts.
     * 
     * @return time in milliseconds.
     */
    public long getMinimizationTime();
}
