package net.coderodde.lce.demo;

import java.util.ArrayList;
import java.util.List;
import net.coderodde.lce.Utils;
import net.coderodde.lce.model.DebtCutAssignment;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;
import net.coderodde.lce.model.support.DefaultEquilibrialDebtCutFinder;

/**
 * This class comprises the demo showing the performance of loan cut equilibrium
 * method.
 * 
 * @author Rodion Efremov
 * @version 0.1
 */
public class Demo {
    
    public static void main(final String... args) {
        profile();
//        profileLarge();
    }
   
    private static final void profile() {
        final long SEED = System.currentTimeMillis();
        final int N = 10;
        
        System.out.println("Seed: " + SEED);
        
        final Graph input = Utils.createRandomGraph(N, SEED, 5.0f / N);
        final EquilibrialDebtCutFinder finder = 
                new DefaultEquilibrialDebtCutFinder();
        
        input.setDebtCutFinder(finder);
        
        final TimeAssignment ta = 
                Utils.createRandomTimeAssignment(SEED, input);
        
        final List<Double> inlist = new ArrayList<>(10);
        final List<Double> outlist = new ArrayList<>(10);
        
        
        for (int i = 0; i != 10; ++i) {
            final double equilibriumTime = 
                    ta.getMaximumTimestamp() + 2 * (i + 1);
            
            final DebtCutAssignment dca = 
                    input.findEquilibrialDebtCuts(equilibriumTime, ta);
            
            final Graph output = input.applyDebtCuts(dca, ta);
            
            if (output.isInEquilibriumAt(equilibriumTime) == false) {
                // Should not happen.
                System.out.println("Error: " + output.funk(equilibriumTime));
                System.out.println("Equilibrium failed: " + i);
                return;
            }
            
            inlist.add(input.getTotalFlowAt(equilibriumTime));
            outlist.add(output.getTotalFlowAt(equilibriumTime));
        }
        
        for (int i = 0; i != inlist.size(); ++i) {
            System.out.printf("%2d % 5.3f : % 5.3f ; %-2.3f\n", 
                              (i + 1), 
                              outlist.get(i),
                              inlist.get(i),
                              1.0 * outlist.get(i) / inlist.get(i));
        }
    }
    
    /**
     * Profiles the algorithm.
     */
    private static final void profileLarge() {
        final long SEED = System.currentTimeMillis();
        final int N = 10;
        
        System.out.println("Seed: " + SEED);
        
        final Graph g = Utils.createRandomGraph(N, SEED, 5.0f / N);
        final TimeAssignment ta = Utils.createRandomTimeAssignment(SEED, g);
        final double eqtime = ta.getMaximumTimestamp() + 10;
        
        final EquilibrialDebtCutFinder finder = 
                new DefaultEquilibrialDebtCutFinder();
        
        g.setDebtCutFinder(finder);
        
        final DebtCutAssignment dca = g.findEquilibrialDebtCuts(eqtime, ta);
        
        final Graph other = g.applyDebtCuts(dca, ta);
        
        System.out.println("Equilibrium time: " + eqtime);
        
        System.out.println("Graph in equilibrium at " + (eqtime - 0.01) + ": " + other.isInEquilibriumAt(eqtime - 0.01));
        System.out.println("Graph in equilibrium at " + eqtime + ": " + other.isInEquilibriumAt(eqtime));
        System.out.println("Graph in equilibrium at " + (eqtime + 0.01) + ": " + other.isInEquilibriumAt(eqtime + 0.01));
        System.out.println("Reduced in " + finder.getMatrixReductionTime() + " ms.");
        System.out.println("Optimized in " + finder.getMinimizationTime() + " ms.");
        
        System.out.println(other.describe(eqtime));
        
        System.out.println("--- Equities ---");
        
        final TimeAssignment myta = g.copy(other, ta);
        showEquities(other, ta, eqtime);
        
        System.out.println("Total flow in the input graph at " + eqtime + " " +
                           "is " + g.getTotalFlowAt(eqtime));
        
        System.out.println("Total flow in the output graph at " + eqtime + 
                           " is " + other.getTotalFlowAt(eqtime));
    }
    
    private static final void showEquities
        (final Graph g, final TimeAssignment ta, final double eqtime) {
        double sum1 = 0;
        double sum2 = 0;
        
        for (final Node node : g.getNodes()) {
            double duration1 = ta.getMaximumTimestamp() - node.getMaximumTimestamp();
            double duration2 = eqtime - node.getMaximumTimestamp();
            
            double e1 = node.equity2(duration1);
            double e2 = node.equity2(duration2);
            
            System.out.println(node);
            System.out.println("  " + e1);
            System.out.println("  " + e2);
            
            sum1 += e1;
            sum2 += e2;
        }
        
        System.out.println("Sum 1: " + sum1);
        System.out.println("Sum 2: " + sum2);
    }
}
