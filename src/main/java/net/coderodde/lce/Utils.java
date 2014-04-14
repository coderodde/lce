package net.coderodde.lce;

import java.util.Map;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;

/**
 * This class contains the bear necessities.
 * 
 * @author Rodion Efremov
 * @version 0.1
 */
public class Utils {
    
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
            throw new IllegalArgumentException(message);
        }
    }
        
    public static final void checkTimestamp
        (final double cmp, final double timestamp) {
        checkNotNaN(cmp, "'cmp' is NaN.");
        checkNotNaN(timestamp, "'timestamp' is NaN.");
        checkNotInfinite(cmp, "'cmp' is infinite.");
        checkNotInfinite(timestamp, "'timestamp' is infinite.");
    }
    
    public static final void checkPrincipal(final double principal) {
        checkNotNaN(principal, "The principal may not be NaN.");
        checkNotInfinite(principal, "The principal may not be infinite.");
        checkPositive(principal, "The principal must be above 0.");
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
    
    public static final void checkTimeMap
        (final Graph graph, final Map<Node, Double> timeMap) {
        if (timeMap.size() != graph.size()) {
            throw new IllegalArgumentException(
                    "The size of time map and graph differ.");
        }
        
        for (Node node : timeMap.keySet()) {
            if (!graph.contains(node)) {
                throw new IllegalArgumentException(
                    "The key set of the time map differs from graph.");
            }
        }
    }
}
