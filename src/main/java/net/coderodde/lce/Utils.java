package net.coderodde.lce;

import java.util.Objects;
import java.util.Random;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.ContractFactory;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;
import net.coderodde.lce.model.support.BasicContract;
import net.coderodde.lce.model.support.ContinuousContract;
import net.coderodde.lce.model.DebtCutAssignment;

/**
 * This class contains the bear necessities.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public final class Utils {
    
    /**
     * Defines the default epsilon.
     */
    private static double EPSILON = 0.001;
    
    /**
     * Defines the maximum epsilon.
     */
    private static final double MAX_EPSILON = 1.0;
    
    /**
     * Attempts to set a new epsilon.
     * 
     * @param epsilon the epsilon to set.
     */
    public static void setEpsilon(double epsilon) {
        if (Double.isInfinite(epsilon)
                || Double.isNaN(epsilon)
                || epsilon > MAX_EPSILON
                || epsilon <= 0.0) {
            return;
        }
        
        EPSILON = epsilon;
    }
    
    /**
     * Checks whether the number is <code>NaN</code>.
     * 
     * @param d       the number to check.
     * @param message the message to an exception possibly thrown.
     * 
     * @throws IllegalArgumentException if d is NaN.
     */    
    public static void checkNotNaN(double d, String message) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Checks whether the number is infinite in absolute value.
     * 
     * @param d       the number to check.
     * @param message the message passed to an exception object upon failure.
     * 
     * @throws IllegalArgumentException if d is infinite.
     */
    public static void checkNotInfinite(double d, String message) {
        if (Double.isInfinite(d)) {
            throw new IllegalArgumentException(message);
        }
    }
        
    /**
     * Checks whether a number is above 0.
     * 
     * @param d       the number to check.
     * @param message the message passed to the exception object upon failure.
     * 
     * @throws IllegalArgumentException if d is at most 0.
     */
    public static void checkPositive(double d, String message) {
        if (d <= 0.0) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Checks whether a number is at least 0.
     * 
     * @param d       the number to check.
     * @param message the message passed to exception upon failure.
     * 
     * @throws IllegalArgumentException if <code>d</code> is less than 0.
     */
    public static void checkNotNegative(double d, String message) {
        if (d < 0.0) {
            throw new IllegalArgumentException(message + " : " + d);
        }
    }
        
    /**
     * Checks that <code>a</code> is less than <code>b</code>.
     * 
     * @param a first number.
     * @param b second number.
     */
    public static void checkTimestamp(double a, double b) {
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
    public static void checkPrincipal(double principal) {
        checkNotNaN(principal, "The principal may not be NaN.");
        checkNotInfinite(principal, "The principal may not be infinite.");
        checkNotNegative(principal, "The principal must be at least 0.");
    }
        
    /**
     * Validates the interest rate.
     * 
     * @param interestRate the interest rate to validate.
     */
    public static void checkInterestRate(double interestRate) {
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
    public static void checkCompoundingPeriods(double compoundingPeriods) {
        checkNotNaN(compoundingPeriods, "The compouding periods are NaN.");
        checkPositive(compoundingPeriods, 
                      "The compounding periods setting must be above zero.");
    }
        
    /**
     * Validates a timestamp.
     * 
     * @param timestamp timestamp to validate.
     */
    public static void checkTimestamp(double timestamp) {
        checkNotNaN(timestamp, "The timestamp is NaN.");
        checkNotInfinite(timestamp, "The timestamp is infinite.");
    }
    
    /**
     * Validates the time assignment for a graph.
     * 
     * @param graph          the graph.
     * @param timeAssignment the time assignment object.
     */
    public static void checkTimeAssignment(Graph graph, 
                                           TimeAssignment timeAssignment) {
        if (timeAssignment.size() != graph.size()) {
            throw new IllegalArgumentException(
                    "The size of time map and graph differ. " + 
                    timeAssignment.size() + " versus " + graph.size());
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
     * @param contract          the contract to validate.
     * @param debtCutAssignment the debt cut assignment.
     */
    public static void checkContract(Contract contract, 
                                     DebtCutAssignment debtCutAssignment) {
        Objects.requireNonNull(contract, "Contract is null.");
        Objects.requireNonNull(debtCutAssignment, 
                               "Debt cut assignment is null.");
        
        if (debtCutAssignment.getContracts().contains(contract) == false) {
            throw new IllegalStateException("The contract is missing.");
        }
    }
     
    /**
     * Validates a debt cut.
     * 
     * @param debtCut the debt cut.
     * @param equity  the equity.
     */
    public static void checkDebtCut(double debtCut, double equity) {
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
     * within <code>epsilon</code> to each other.
     */
    public static boolean epsilonEquals(double a, double b) {
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
    public static boolean epsilonEquals(double a, double b, double e) {
        return Math.abs(a - b) <= e;
    }
    
    /**
     * Creates a random financial graph.
     * 
     * @param size           the amount of nodes in the output graph.
     * @param seed           the seed for PRNG.
     * @param edgeLoadFactor the edge load factor (should be between 0 and 1).
     * 
     * @return a random graph.
     */
    public static Graph createRandomGraph(int size, 
                                          long seed, 
                                          float edgeLoadFactor) {
        if (size < 1) {
            size = 1;
        }
        
        Graph g = new Graph("Random graph");
        
        for (int i = 0; i != size; ++i) {
            g.add(new Node("" + i));
        }
        
        Random r = new Random(seed);
        int contractCount = 0;
        
        for (Node lender : g.getNodes()) {
            for (Node debtor : g.getNodes()) {
                if (r.nextFloat() < edgeLoadFactor && lender != debtor) {
                    int contracts = r.nextInt(4);
                    
                    for (int i = 0; i != contracts; ++i) {
                        lender.addDebtor(
                                debtor,
                                createRandomContract(r, "" + contractCount));
                        
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
     * @param r    the PRNG.
     * @param name the name of a new contract.
     * 
     * @return a new random contract.
     */
    private static Contract createRandomContract(Random r, String name) {
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
     * @param seed  the seed for PRNG.
     * @param graph the graph for which to create a time assignment.
     * 
     * @return a random time assignment object. 
     */
    public static TimeAssignment 
    createRandomTimeAssignment(long seed, Graph graph) {
        Random r = new Random(seed);
        TimeAssignment ta = new TimeAssignment();
        Contract DUMMY_CONTRACT = ContractFactory
                                        .newContract()
                                        .withPrincipal(0)
                                        .withCompoundingPeriods(1.0)
                                        .withInterestRate(0.01)
                                        .withTimestamp(0.0)
                                        .create("Dummy contract");
        
        for (Node node : graph.getNodes()) {
            for (Contract contract : node.getIncomingContracts()) {
                ta.put(node, 
                       contract, 
                       10 * r.nextDouble() + node.getMaximumTimestamp());
            }
            
            if (ta.containsNode(node) == false) {
                ta.put(node, 
                       DUMMY_CONTRACT, 
                       10 * r.nextDouble() + node.getMaximumTimestamp());
            }
        }
        
        return ta;
    }
    
    /**
     * Prints a nifty title.
     * 
     * @param text the text of the title.
     */
    public static void title(String text) {
        int before = (80 - text.length() - 2) / 2;
        int after = 80 - 2 - text.length() - before;
        StringBuilder sb = new StringBuilder(80);
        
        for (int i = 0; i < before; ++i) {
            sb.append('*');
        }
        
        sb.append(' ').append(text).append(' ');
        
        for (int i = 0; i < after; ++i) {
            sb.append('*');
        }
        
        System.out.println(sb);
    }
}
