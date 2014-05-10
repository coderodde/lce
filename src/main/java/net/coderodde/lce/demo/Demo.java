package net.coderodde.lce.demo;

import net.coderodde.lce.Utils;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.ContractFactory;
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
        profileLarge();
        final long SEED = 313L;
//        Graph graph = Utils.createRandomGraph(5, SEED, 0.4f);
        
//        System.out.println("Contract amount: " + graph.getContractAmount());
        
        Graph easy = getVeryEasyGraphOld();
        EquilibrialDebtCutFinder finder = new DefaultEquilibrialDebtCutFinder();
        TimeAssignment ta = Utils.createRandomTimeAssignment(SEED, easy);
        
        ta = new TimeAssignment();
        
        ta.put(easy.getNode("A"), 4.0);
        ta.put(easy.getNode("B"), 6.0);
        ta.put(easy.getNode("C"), 7.0);
        
        double eqtime = ta.getMaximumTimestamp() + 10.0;
        DebtCutAssignment dca = finder.compute(easy, ta, eqtime);
        
        System.out.println(
                "Reduced in " + finder.getMatrixReductionTime() + " ms.");
        
        System.out.println(
                "Optimized in " + finder.getMinimizationTime() + " ms.");
        
        Graph other = easy.applyDebtCuts(dca, ta);
        
        System.out.println("Equilibrium at " + (eqtime - 0.1) + ": " + other.isInEquilibriumAt(eqtime - 0.1));
        System.out.println("Equilibrium at " + (eqtime - 0.01) + ": " + other.isInEquilibriumAt(eqtime - 0.01));
        System.out.println("Equilibrium at " + eqtime + ": " + other.isInEquilibriumAt(eqtime));
        System.out.println("Equilibrium at " + (eqtime + 0.01) + ": " + other.isInEquilibriumAt(eqtime + 0.01));
        System.out.println("Equilibrium at " + (eqtime + 0.1) + ": " + other.isInEquilibriumAt(eqtime + 0.1));
    }
    
    /**
     * Apparently, scattered timing might produce situations where there is no
     * equilibrium.
     * 
     * @return 
     */
    public static final Graph getVeryEasyGraphOld() {
        Graph g = new Graph("Easy graph");
        
        g.add(new Node("A"));
        g.add(new Node("B"));
        g.add(new Node("C"));
        
        Contract cab = ContractFactory.newContract()
                                      .withPrincipal(3.0)
                                      .withInterestRate(0.14)
                                      .withContiguous()
                                      .withTimestamp(1.0)
                                      .create("cAB");
        
        Contract cbc = ContractFactory.newContract()
                                      .withPrincipal(2.0)
                                      .withInterestRate(0.11)
                                      .withCompoundingPeriods(4.0)
                                      .withTimestamp(1.4)
                                      .create("cBC");
        
        Contract cca = ContractFactory.newContract()
                                      .withPrincipal(2.5)
                                      .withInterestRate(0.05)
                                      .withCompoundingPeriods(6.0)
                                      .withTimestamp(0.5)
                                      .create("cCA");
        
        Contract cca2 = ContractFactory.newContract()
                                      .withPrincipal(1.0)
                                      .withInterestRate(0.27)
                                      .withContiguous()
                                      .withTimestamp(1.7)
                                      .create("cCA2");
        
        g.getNode("A").addDebtor(g.getNode("B"), cab);
        g.getNode("B").addDebtor(g.getNode("C"), cbc);
        g.getNode("C").addDebtor(g.getNode("A"), cca);
        g.getNode("C").addDebtor(g.getNode("A"), cca2);
        
        return g;
    }
    
    public static final Graph getVeryEasyGraph() {
        Graph g = new Graph("Easy graph");
        
        g.add(new Node("A"));
        g.add(new Node("B"));
        g.add(new Node("C"));
        
        Contract cab = ContractFactory.newContract()
                                      .withPrincipal(3.0)
                                      .withInterestRate(0.14)
                                      .withContiguous()
                                      .withTimestamp(1.0)
                                      .create("cAB");
        
        Contract cbc = ContractFactory.newContract()
                                      .withPrincipal(2.0)
                                      .withInterestRate(0.11)
                                      .withCompoundingPeriods(4.0)
                                      .withTimestamp(1.4)
                                      .create("cBC");
        
        Contract cca = ContractFactory.newContract()
                                      .withPrincipal(2.5)
                                      .withInterestRate(0.05)
                                      .withCompoundingPeriods(6.0)
                                      .withTimestamp(0.5)
                                      .create("cCA");
        
        Contract cca2 = ContractFactory.newContract()
                                      .withPrincipal(1.0)
                                      .withInterestRate(0.07)
                                      .withContiguous()
                                      .withTimestamp(1.7)
                                      .create("cCA2");
        
        g.getNode("A").addDebtor(g.getNode("B"), cab);
        g.getNode("B").addDebtor(g.getNode("C"), cbc);
        g.getNode("C").addDebtor(g.getNode("A"), cca);
        g.getNode("C").addDebtor(g.getNode("A"), cca2);
        
        return g;
    }
    
    private static final void profileLarge() {
        final Graph g = Utils.createRandomGraph(10, 313L, 0.45f);
        final TimeAssignment ta = Utils.createRandomTimeAssignment(313L, g);
        final double eqtime = g.getMaximumTimestamp() + 10;
        final EquilibrialDebtCutFinder finder = 
                new DefaultEquilibrialDebtCutFinder();
        
        g.setDebtCutFinder(finder);
        
        final DebtCutAssignment dca = g.findEquilibrialDebtCuts(eqtime, ta);
        
        final Graph other = g.applyDebtCuts(dca, ta);
        
        System.out.println("Graph in equilibrium: " + other.isInEquilibriumAt(eqtime));
        System.out.println("Reduced in " + finder.getMatrixReductionTime() + " ms.");
        System.out.println("Optimized in " + finder.getMinimizationTime() + " ms.");
    }
}
