package net.coderodde.lce.demo;

import java.util.ArrayList;
import java.util.List;
import net.coderodde.lce.Utils;
import net.coderodde.lce.model.DebtCutAssignment;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.TimeAssignment;
import net.coderodde.lce.model.support.DefaultEquilibrialDebtCutFinder;
import static net.coderodde.lce.Utils.title;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.support.BasicContract;
import net.coderodde.lce.model.support.ContinuousContract;

/**
 * This class comprises the demo showing the performance of loan cut equilibrium
 * method.
 * 
 * @author Rodion Efremov
 * @version 1.618
 */
public class Demo {
    
    public static void main(final String... args) {
        profileSmall();
        profile();
        profile2();
        profileLarge();
    }
   
    private static final void profileSmall() {
        title("profileSmall()");
        final double eqTime = 5.0;
        final Graph input = new Graph("Small graph");
        final Node u = new Node("u");
        final Node v = new Node("v");
        
        input.add(u);
        input.add(v);
        
        final Contract k_u = new BasicContract("k_u", 2.0, 0.1, 3.0, -1.0);
        final Contract k_v = new ContinuousContract("k_v", 1.0, 0.12, 0.0);
        
        System.out.println("k_u: " + k_u);
        System.out.println("k_v: " + k_v);
        
        u.addDebtor(v, k_u);
        v.addDebtor(u, k_v);
        
        // u issues a contract k_u to v
        // v issues a contract k_v to u
        final TimeAssignment ta = new TimeAssignment();
        
        ta.put(u, k_v, 3.1);
        ta.put(v, k_u, 2.5);
        
        final EquilibrialDebtCutFinder finder = 
                new DefaultEquilibrialDebtCutFinder();
        
        input.setDebtCutFinder(finder);
        
        final DebtCutAssignment dca = input.findEquilibrialDebtCuts(eqTime, ta);
        final Graph output = input.applyDebtCuts(dca, ta);
        
        System.out.println("Is in equilibrium at " + eqTime + ": " + 
                           output.isInEquilibriumAt(5.0));
        
        System.out.println(output.describe(5.0));
        System.out.println("Debt cut sum: " + dca.sum());
        
        System.out.println("k_u: " + k_u);
        System.out.println("k_v: " + k_v);
        
        final double t = 2.5 - k_u.getTimestamp();
        System.out.println(k_u.evaluate(t + 1.998 / k_u.getCompoundingPeriods()));
        System.out.println(k_u.evaluate(t + 1.999 / k_u.getCompoundingPeriods()));
        System.out.println(k_u.evaluate(t + 2.000 / k_u.getCompoundingPeriods()));
        System.out.println(k_u.evaluate(t + 2.001 / k_u.getCompoundingPeriods()));
    }
    
    private static final void profile() {
        title("profile()");
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
        
        Utils.setEpsilon(1e-6);
        
        for (int i = 0; i != 30; ++i) {
            // Keep computing with the equilibrium time increasing from
            // iteration to iteration.
            final double equilibriumTime = 
                    ta.getMaximumTimestamp() + 2 * (i + 1);
            
            final DebtCutAssignment dca = 
                    input.findEquilibrialDebtCuts(equilibriumTime, ta);
            
            final Graph output = input.applyDebtCuts(dca, ta);
            
            if (output.isInEquilibriumAt(equilibriumTime) == false) {
                // Should not happen.
                System.out.println(
                        "Equilibrium failed: " + i + 
                        ", max absolute equity: " + 
                        output.maxEquity(equilibriumTime));
                return;
            }
            
            inlist.add(input.getTotalFlowAt(equilibriumTime));
            outlist.add(output.getTotalFlowAt(equilibriumTime));
        }
        
        for (int i = 0; i != inlist.size(); ++i) {
            System.out.printf("%2d % 5.3f : % 5.3f ; %-2.3f\n", 
                              2 * (i + 1), 
                              outlist.get(i),
                              inlist.get(i),
                              1.0 * outlist.get(i) / inlist.get(i));
        }
        
        System.out.println();
    }
    
    private static final void profile2() {
        title("profile2()");
        
        final long SEED = System.currentTimeMillis();
        final int N = 10;
        
        System.out.println("Seed: " + SEED);
        
        final Graph input = Utils.createRandomGraph(N, SEED, 5.0f / N);
        
        final EquilibrialDebtCutFinder finder = 
                new DefaultEquilibrialDebtCutFinder();
        
        input.setDebtCutFinder(finder);
        
        final TimeAssignment ta = Utils.createRandomTimeAssignment(SEED, input);
        final double eqtime = ta.getMaximumTimestamp() + 10;
        final DebtCutAssignment dca = input.findEquilibrialDebtCuts(eqtime, ta);
        final Graph output = input.applyDebtCuts(dca, ta);
        
        System.out.println("Equilibrium achieved: " + 
                           output.isInEquilibriumAt(eqtime));
        
        System.out.println("Total flow at equilibrium time without applying " +
                           "the debt cuts: " + input.getTotalFlowAt(eqtime));
        System.out.println("The sum of debt cuts leading to equilibrium: " +
                           dca.sum());
        
        System.out.println();
    }
    
    /**
     * Profiles the algorithm.
     */
    private static final void profileLarge() {
        title("profileLarge()");
        final long SEED = System.currentTimeMillis();
        final int N = 50;
        
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
        System.out.println("Graph in equilibrium at " + (eqtime - 0.01) + ": " + 
                           other.isInEquilibriumAt(eqtime - 0.01));
        System.out.println("Graph in equilibrium at " + eqtime + ": " + 
                           other.isInEquilibriumAt(eqtime));
        System.out.println("Graph in equilibrium at " + (eqtime + 0.01) + ": " + 
                           other.isInEquilibriumAt(eqtime + 0.01));
        System.out.println("Reduced in " + finder.getMatrixReductionTime() + 
                           " ms.");
        System.out.println("Optimized in " + finder.getMinimizationTime() + 
                           " ms.");
        System.out.println(other.describe(eqtime));
  
        System.out.println("Total flow in the input graph at " + eqtime + " " +
                           "is " + g.getTotalFlowAt(eqtime));
        
        System.out.println("Total flow in the output graph at " + eqtime + 
                           " is " + other.getTotalFlowAt(eqtime));
        
        System.out.println();
    }
}
