package net.coderodde.lce.demo;

import net.coderodde.lce.Utils;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
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
        final long SEED = 313L;
        Graph graph = Utils.createRandomGraph(5, SEED, 0.4f);
        
        System.out.println("Contract amount: " + graph.getContractAmount());
        
        EquilibrialDebtCutFinder finder = new DefaultEquilibrialDebtCutFinder();
        TimeAssignment ta = Utils.createRandomTimeAssignment(SEED, graph);
        
        finder.compute(graph, ta, 20.4);
        
        System.out.println(
                "Reduced in " + finder.getMatrixReductionTime() + " ms.");
    }
}
