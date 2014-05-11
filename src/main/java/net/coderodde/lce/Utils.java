package net.coderodde.lce;

import java.util.Random;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;
import net.coderodde.lce.model.support.BasicContract;
import net.coderodde.lce.model.support.ContinuousContract;
import net.coderodde.lce.model.support.DefaultDebtCutAssignment;

/**
 * This class contains the bear necessities.
 * 
 * @author Rodion Efremov
 * @version 0.1
 */
public class Utils {
    
    /**
     * Defines the default epsilon.
     */
    private static double EPSILON = 0.001;
    
    /**
     * Defines the maximum epsilon.
     */
    private static double MAX_EPSILON = 1.0;
    
    /**
     * Attempts to set a new epsilon.
     * 
     * @param epsilon the epsilon to set.
     */
    public static final void setEpsilon(final double epsilon) {
        if (Double.isInfinite(epsilon)
                || Double.isNaN(epsilon)
                || epsilon > MAX_EPSILON
                || epsilon <= 0.0) {
            return;
        }
        
        EPSILON = epsilon;
    }
    
    /**
     * Checks whether the reference is null.
     * 
     * @param o the reference to check.
     * @param message the message to an exception possibly thrown.
     * 
     * @throws IllegalArgumentException if the reference is <code>null</code>.
     */
    public static final void checkNotNull
        (final Object o, final String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Checks whether the number is NaN.
     * 
     * @param d the number to check.
     * @param message the message to an exception possibly thrown.
     * 
     * @throws IllegalArgumentException if d is NaN.
     */    
    public static final void checkNotNaN(final double d, final String message) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Checks whether the number is infinite in absolute value.
     * 
     * @param d the number to check.
     * @param message the message passed to an exception object upon failure.
     * 
     * @throws IllegalArgumentException if d is infinite.
     */
    public static final void checkNotInfinite
        (final double d, final String message) {
        if (Double.isInfinite(d)) {
            throw new IllegalArgumentException(message);
        }
    }
        
    /**
     * Checks whether a number is above 0.
     * 
     * @param d the number to check.
     * @param message the message passed to the exception object upon failure.
     * 
     * @throws IllegalArgumentException if d is at most 0.
     */
    public static final void checkPositive
        (final double d, final String message) {
        if (d <= 0.0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Checks whether a number is at least 0.
     * 
     * @param d the number to check.
     * @param message the message passed to exception upon failure.
     * 
     * @throws IllegalArgumentException if <code>d</code> is less than 0.
     */
    public static final void checkNotNegative
        (final double d, final String message) {
        if (d < 0.0) {
            throw new IllegalArgumentException(message + " : " + d);
        }
    }
        
    /**
     * Checks that a is less than b.
     * 
     * @param a first time point.
     * @param b second time point.
     */
    public static final void checkTimestamp
        (final double a, final double b) {
        checkNotNaN(a, "'cmp' is NaN.");
        checkNotNaN(b, "'timestamp' is NaN.");
        checkNotInfinite(a, "'cmp' is infinite.");
        checkNotInfinite(b, "'timestamp' is infinite.");
        
        if (a > b) {
            throw new IllegalStateException("");
        }
    }
    
    /**
     * Validates a principal investment.
     * 
     * @param principal the principal to validate.
     */
    public static final void checkPrincipal(final double principal) {
        checkNotNaN(principal, "The principal may not be NaN.");
        checkNotInfinite(principal, "The principal may not be infinite.");
        checkNotNegative(principal, "The principal must be at least 0.");
    }
        
    /**
     * Validates the interest rate.
     * 
     * @param interestRate the interest rate to validate.
     */
    public static final void checkInterestRate(final double interestRate) {
        checkNotNaN(interestRate, "The interest rate may not be NaN.");
        checkNotInfinite(interestRate, 
                         "The interest rate may not be infinite.");
        checkNotNegative(interestRate, "The interest rate must be at least 0.");
    }
    
    /**
     * Validates compounding periods.
     * 
     * @param compoundingPeriods compounding periods to check.
     */
    public static final void checkCompoundingPeriods
        (final double compoundingPeriods) {
        checkNotNaN(compoundingPeriods, "The compouding periods are NaN.");
        checkPositive(compoundingPeriods, 
                      "The compounding periods setting must be above zero.");
    }
        
    /**
     * Validates a timestamp.
     * 
     * @param timestamp timestamp to validate.
     */
    public static final void checkTimestamp(final double timestamp) {
        checkNotNaN(timestamp, "The timestamp is NaN.");
        checkNotInfinite(timestamp, "The timestamp is infinite.");
    }
    
    /**
     * Validates the time assignment for a graph.
     * 
     * @param graph graph.
     * @param timeAssignment time assignment object.
     */
    public static final void checkTimeAssignment
        (final Graph graph, final TimeAssignment timeAssignment) {
        if (timeAssignment.size() != graph.size()) {
            throw new IllegalArgumentException(
                    "The size of time map and graph differ.");
        }
        
        for (Node node : timeAssignment.getNodes()) {
            if (!graph.contains(node)) {
                throw new IllegalArgumentException(
                    "The key set of the time map differs from graph.");
            }
        }
        
        for (Node node : graph.getNodes()) {
            if (!timeAssignment.containsNode(node)) {
                throw new IllegalArgumentException(
                    "The graph has a node not in the time assignment object.");
            }
        }
    }
        
    /**
     * Validates a contract against a debt cut assignment.
     * 
     * @param contract the contract to validate.
     * @param debtCutAssignment the debt cut assignment.
     */
    public static final void checkContract
        (final Contract contract, final DefaultDebtCutAssignment debtCutAssignment) {
        checkNotNull(contract, "Contract is null.");
        checkNotNull(debtCutAssignment, "Debt cut assignment is null.");
        if (debtCutAssignment.getContracts().contains(contract) == false) {
            throw new IllegalStateException("The contract is missing.");
        }
    }
     
    /**
     * Validates a debt cut.
     * 
     * @param debtCut the debt cut.
     * @param equity the equity.
     */
    public static final void checkDebtCut
        (final double debtCut, final double equity) {
        checkNotNaN(debtCut, "The debt cut is NaN.");
        checkNotInfinite(debtCut, "The debt cut is infinite.");
        checkNotNegative(debtCut, "The debt cut is negative.");
        
        checkNotNaN(equity, "The equity is NaN.");
        checkNotInfinite(equity, "The equity is infinite.");
        checkNotNegative(equity, "The equity is negative.");
        
        if (debtCut > equity) {
            throw new IllegalArgumentException("The debt cut exceeds equity.");
        }
    }
        
    /**
     * Performs an epsilon-comparison.
     * 
     * @param a first number.
     * @param b second number.
     * 
     * @return <code>true</code> if <code>a</code> and <code>b</code> are
     * within <code>epsilon</cod> to each other.
     */
    public static final boolean epsilonEquals(final double a, final double b) {
        return Math.abs(a - b) <= EPSILON;
    }
    
    /**
     * Performs an epsilon-comparison.
     * 
     * @param a first number.
     * @param b second number.
     * @param e epsilon value.
     * 
     * @return <code>true</code> or <code>false</code> 
     */
    public static final boolean epsilonEquals(final double a, final double b, final double e) {
        return Math.abs(a - b) <= e;
    }
    
    /**
     * Creates a random financial graph.
     * 
     * @param size the amount of nodes in the output graph.
     * @param seed the seed for PRNG.
     * @param edgeLoadFactor edge load factor (should be between 0 and 1).
     * 
     * @return a random graph.
     */
    public static final Graph createRandomGraph(int size,
                                                final long seed,
                                                final float edgeLoadFactor) {
        if (size < 1) {
            size = 1;
        }
        
        Graph g = new Graph("Random graph");
        
        for (int i = 0; i != size; ++i) {
            g.add(new Node("" + i));
        }
        
        Random r = new Random(seed);
        int contractCount = 0;
        
        for (final Node lender : g.getNodes()) {
            for (final Node debtor : g.getNodes()) {
                if (r.nextFloat() < edgeLoadFactor && lender != debtor) {
                    int contracts = r.nextInt(4);
                    for (int i = 0; i != contracts; ++i) {
                        lender.addDebtor(debtor,
                                         createRandomContract(r, 
                                                              "" + contractCount));
                        ++contractCount;
                    }
                }
            }
        }
        
        return g;
    }
    
    /**
     * Creates a random contract.
     * 
     * @param r the PRNG.
     * @param name the name of a new contract.
     * 
     * @return a new random contract.
     */
    private static final Contract createRandomContract(final Random r, 
                                                       final String name) {
        if (r.nextFloat() < 0.75f) {
            // Basic contract.
            return new BasicContract(name,
                                     1.0 * r.nextDouble(),
                                     0.25 * r.nextDouble(),
                                     12.0 * r.nextDouble(),
                                     5.0 * r.nextDouble());
        } else {
            // Contiguous contract.
            return new ContinuousContract(name,
                                          1.0 * r.nextDouble(),
                                          0.25 * r.nextDouble(),
                                          5.0 * r.nextDouble());
        }
    }
    
    /**
     * Creates a random time assignment.
     * 
     * @param seed the seed for PRNG.
     * @param graph the graph for which to create a time assignment.
     * 
     * @return a random time assignment object. 
     */
    public static final TimeAssignment 
    createRandomTimeAssignment(final long seed, final Graph graph) {
        final Random r = new Random(seed);
        final TimeAssignment ta = new TimeAssignment();

        for (final Node node : graph.getNodes()) {
            ta.put(node, 10 * r.nextDouble() + node.getMaximumTimestamp());
        }

        return ta;
    }
}
