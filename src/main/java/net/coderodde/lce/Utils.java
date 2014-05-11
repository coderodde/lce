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
    
    private static double EPSILON = 0.001;
    private static double MAX_EPSILON = 1.0;
    
    public static final void setEpsilon(final double epsilon) {
        if (Double.isInfinite(epsilon)
                || Double.isNaN(epsilon)
                || epsilon > MAX_EPSILON
                || epsilon <= 0.0) {
            return;
        }
        
        EPSILON = epsilon;
    }
    
    public static final void checkNotNull
        (final Object o, final String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static final void checkNotNaN(final double d, final String message) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static final void checkNotInfinite
        (final double d, final String message) {
        if (Double.isInfinite(d)) {
            throw new IllegalArgumentException(message);
        }
    }
        
    public static final void checkPositive
        (final double d, final String message) {
        if (d <= 0.0) {
            throw new IllegalArgumentException(message);
        }
    }
    
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
    
    public static final void checkPrincipal(final double principal) {
        checkNotNaN(principal, "The principal may not be NaN.");
        checkNotInfinite(principal, "The principal may not be infinite.");
        checkNotNegative(principal, "The principal must be at least 0.");
    }
        
    public static final void checkInterestRate(final double interestRate) {
        checkNotNaN(interestRate, "The interest rate may not be NaN.");
        checkNotInfinite(interestRate, 
                         "The interest rate may not be infinite.");
        checkNotNegative(interestRate, "The interest rate must be at least 0.");
    }
    
    public static final void checkCompoundingPeriods
        (final double compoundingPeriods) {
        checkNotNaN(compoundingPeriods, "The compouding periods are NaN.");
        checkPositive(compoundingPeriods, 
                      "The compounding periods setting must be above zero.");
    }
        
    public static final void checkTimestamp(final double timestamp) {
        checkNotNaN(timestamp, "The timestamp is NaN.");
        checkNotInfinite(timestamp, "The timestamp is infinite.");
    }
    
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
        
    public static final void checkContract
        (final Contract contract, final DefaultDebtCutAssignment debtCutAssignment) {
        checkNotNull(contract, "Contract is null.");
        checkNotNull(debtCutAssignment, "Debt cut assignment is null.");
        if (debtCutAssignment.getContracts().contains(contract) == false) {
            throw new IllegalStateException("The contract is missing.");
        }
    }
        
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
        
    public static final boolean epsilonEquals(final double a, final double b) {
        return Math.abs(a - b) <= EPSILON;
    }
    
    public static final boolean epsilonEquals(final double a, final double b, final double e) {
        return Math.abs(a - b) <= e;
    }
    
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
    
    public static final Contract createRandomContract(final Random r, 
                                                      final String name) {
        if (r.nextFloat() < 0.75f) {
            // Basic contract.
            return new BasicContract(name,
                                     10.0 * r.nextDouble(),
                                     0.25 * r.nextDouble(),
                                     12.0 * r.nextDouble(),
                                     5.0 * r.nextDouble());
        } else {
            // Contiguous contract.
            return new ContinuousContract(name,
                                          10.0 * r.nextDouble(),
                                          0.25 * r.nextDouble(),
                                          5.0 * r.nextDouble());
        }
    }
    
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
