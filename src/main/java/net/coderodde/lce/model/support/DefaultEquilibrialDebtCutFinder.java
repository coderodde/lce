package net.coderodde.lce.model.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static net.coderodde.lce.Utils.checkTimeAssignment;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.DebtCutAssignment;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearOptimizer;
import org.apache.commons.math3.optim.linear.SimplexSolver;

/**
 * This class implements the default equilibrial debt cut finder, which relies
 * on serial simplex method for optimizing the debt cuts.
 * 
 * @author Rodion Efremove
 * @version 1.6
 */
public class DefaultEquilibrialDebtCutFinder 
implements EquilibrialDebtCutFinder {

    /**
     * This is a sentinel value to denote the fact that no solution found.
     */
    public static final DebtCutAssignment NO_SOLUTION =
            new DefaultDebtCutAssignment();
    
    /**
     * The graph this finder is working on.
     */
    private Graph graph;
    
    /**
     * The time assignment object for <code>graph</code>.
     */
    private TimeAssignment timeAssignment;
    
    /**
     * The time at which to attain equilibrium.
     */
    private double equilibriumTime;
    
    /**
     * This map maps contract to unique (column) indices.
     */
    private Map<Contract, Integer> mci;
    
    /**
     * This is the inverse map of <code>mci</code>.
     */
    private Map<Integer, Contract> mcii;
    
    /**
     * The duration of matrix reduction.
     */
    private long matrixReductionDuration;
    
    /**
     * The duration of minimization the cuts.
     */
    private long minimizationDuration;
    
    /**
     * Constructs this finder.
     */
    public DefaultEquilibrialDebtCutFinder() {
        this.mci = new HashMap<>();
        this.mcii = new HashMap<>();
    }
    
    /**
     * Computes the equilibrial debt cuts.
     * 
     * @param graph the graph to work on.
     * @param timeAssignment a map mapping each node <i>u</i> to the moment at 
     * which <i>u</i> is ready to pay its debt cuts.
     * @param time the time point at which the graph is supposed to be in 
     * equilibrium.
     * 
     * @return a map mapping each contract to its debt cut leading
     * to the global equilibrium.
     */
    @Override
    public DebtCutAssignment compute(final Graph graph, 
                                            final TimeAssignment timeAssignment,
                                            final double equilibriumTime) {
        checkTimeAssignment(graph, timeAssignment);
        this.graph = graph;
        this.timeAssignment = timeAssignment;
        this.equilibriumTime = equilibriumTime;
        this.buildMaps();
        Matrix m = loadMatrix();
        
        long ta = System.currentTimeMillis();
        int rank = m.reduceToReducedRowEchelonForm();
        long tb = System.currentTimeMillis();
        this.matrixReductionDuration = tb - ta;
        
        if (m.hasSolution() == false) {
            return NO_SOLUTION;
        }
        
        m.debugPrint();
        
        OptimizationData[] lp = convertMatrixToLinearProgram(m);
        PointValuePair pvp = new SimplexSolver().optimize(lp);
        
        
        
        return extractDebtCuts(pvp);
    }
    
    /**
     * Returns the time spent reducing the matrix. The return value makes
     * sense only after at least one run of this finder.
     * 
     * @return the time spent reducing the matrix.
     */
    @Override
    public final long getMatrixReductionTime() {
        return this.matrixReductionDuration;
    }
    
    private final DebtCutAssignment extractDebtCuts(PointValuePair pvp) {
        return null;
    }
    
    private final OptimizationData[]
        convertMatrixToLinearProgram(final Matrix m) {
            return null;
    }
    
    private final void buildMaps() {
        this.mci.clear();
        this.mcii.clear();
        int index = 0;
            
        for (final Node node : this.graph.getNodes()) {
            for (final Contract contract : node.getOutgoingContracts()) {
                this.mci.put(contract, index);
                this.mcii.put(index++, contract);
            }
        }
    }
    
    private final Matrix loadMatrix() {
        // +1 for the result matrix is augmented.
        double[][] m = new double[graph.size()][graph.getContractAmount() + 1];
        int row = 0;
        
        for (final Node node : graph.getNodes()) {
            loadRow(node, m[row++]);
        }
        
        return new Matrix(m);
    }
    
    private final void loadRow(final Node node, final double[] row) {
        // Compute the constant factor.
        row[row.length - 1] = computeConstantEntry(node);
        
        // Compute everything else.
        for (final Node n : this.graph.getNodes()) {
            for (final Contract c : node.getOutgoingContracts()) {
                row[mci.get(c)] += 
                        c.getGrowthFactor(this.equilibriumTime - 
                                          this.timeAssignment.get(n));
            }
            
            for (final Contract c : node.getIncomingContracts()) {
                row[mci.get(c)] -=
                        c.getGrowthFactor(this.equilibriumTime -
                                          this.timeAssignment.get(node));
            }
        }
    }
    
    private final double computeConstantEntry(final Node node) {
        double sum = 0;
        
        for (final Node debtor : node.getDebtors()) {
            for (final Contract contract : node.getContractsTo(debtor)) {
                sum += contract
                      .evaluate(equilibriumTime - 
                                this.timeAssignment.get(debtor));
            }
        }
        
        for (final Node lender : node.getLenders()) {
            for (final Contract contract : lender.getContractsTo(node)) {
                sum -= contract
                      .evaluate(equilibriumTime -
                                this.timeAssignment.get(node));
            }
        }
        
        return sum;
    }
    
    private final Map<Contract, Integer> loadMap(final Graph graph) {
        final Map<Contract, Integer> map = new HashMap<>(graph.size());
        
        int i = 0;
        
        for (final Node node : graph.getNodes()) {
            for (final Contract contract : node.getOutgoingContracts()) {
                map.put(contract, i++);
            }
        }
        
        if (i != graph.getContractAmount()) {
            throw new IllegalStateException(
                    "Cached contract count does not match the counter.");
        }
        
        return map;
    }
}
