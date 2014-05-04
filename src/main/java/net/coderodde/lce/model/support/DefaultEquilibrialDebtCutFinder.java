package net.coderodde.lce.model.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.coderodde.lce.Utils.checkTimeAssignment;
import static net.coderodde.lce.Utils.epsilonEquals;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.DebtCutAssignment;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.linear.Relationship;

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
    private final Map<Contract, Integer> mci;
    
    /**
     * This is the inverse map of <code>mci</code>.
     */
    private final Map<Integer, Contract> mcii;
    
    /**
     * This map maps a column index to the appearance index of an independent
     * variable.
     */
    private final Map<Integer, Integer> mivi;
    
    /**
     * This map is the inverse of <code>mivi</code>; i.e., it maps appearance
     * index to the column index.
     */
    private final Map<Integer, Integer> mivii;
    
    private final Map<Contract, Node> c2n;
    /**
     * The duration of matrix reduction.
     */
    private long matrixReductionDuration;
    
    /**
     * The duration of minimization the cuts.
     */
    private long minimizationDuration;
    
    /**
     * The rank of the underlying matrix.
     */
    private int rank;
    
    /**
     * Constructs this finder.
     */
    public DefaultEquilibrialDebtCutFinder() {
        this.mci = new HashMap<>();
        this.mcii = new HashMap<>();
        this.mivi = new HashMap<>();
        this.mivii = new HashMap<>();
        this.c2n = new HashMap<>();
    }
    
    /**
     * Computes the equilibrial debt cuts.
     * 
     * @param graph the graph to work on.
     * @param timeAssignment a map mapping each node <i>u</i> to the moment at 
     * which <i>u</i> is ready to pay its debt cuts.
     * @param equilibriumTime the time point at which the graph is supposed to be in 
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
        rank = m.reduceToReducedRowEchelonForm();
        long tb = System.currentTimeMillis();
        this.matrixReductionDuration = tb - ta;
        
        if (m.hasSolution() == false) {
            return NO_SOLUTION;
        }
        
        m.debugPrint();
        
        OptimizationData[] lp = convertMatrixToLinearProgram(m);
        
        ta = System.currentTimeMillis();
        
        PointValuePair pvp = new SimplexSolver().optimize(lp);
        
        tb = System.currentTimeMillis();
        
        minimizationDuration = tb - ta;
        
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
    
    @Override
    public final long getMinimizationTime() {
        return minimizationDuration;
    }
    
    private final DebtCutAssignment extractDebtCuts(PointValuePair pvp) {
        return null;
    }
    
    /**
     * Converts the matrix to a linear program.
     * 
     * @param m the matrix to extract from.
     * 
     * @return a linear program.
     */
    private final OptimizationData[]
        convertMatrixToLinearProgram(final Matrix m) {
        final OptimizationData[] od = getLPData(m);
        final NonNegativeConstraint nnc = new NonNegativeConstraint(true);
        return new OptimizationData[]{od[0], od[1], nnc};
    }
    
    /**
     * Extracts objective function from matrix <code>m</code>.
     * 
     * @param m the matrix to extract from.
     * 
     * @return an objective function.
     */
    private final OptimizationData[] getLPData(final Matrix m) {
        final int VARIABLE_AMOUNT = m.getColumnAmount() - 1;
        int index = 0;
        
        this.mivi.clear();
        this.mivii.clear();
        
        for (int r = 0; r != rank; ++r) {
            boolean leadingEntryFound = false;
            
            for (int c = r; c != VARIABLE_AMOUNT; ++c) {
                if (leadingEntryFound == false) {
                    if (epsilonEquals(m.get(c, r), 1)) {
                        leadingEntryFound = true;
                    }
                } else {
                    if (epsilonEquals(m.get(c, r), 0) == false) {
                        boolean unique = true;
                        
                        for (int rr = r - 1; rr >= 0; --rr) {
                            if (epsilonEquals(m.get(c, rr), 0) == false) {
                                unique = false;
                                break;
                            }
                        }
                        
                        if (unique) {
                            mivi.put(c, index);
                            mivii.put(index++, c);
                        }
                    }
                }
            }
        }
        
        // The length of the objective function is exactly the amount of
        // independent variables.
        final double[] coefficients = new double[mivi.size()];
        final List<LinearConstraint> constraintList = new ArrayList<>(2 * rank);
        
        for (int y = 0; y != rank; ++y) {
            int leadingEntryIndex = -1;
            double[] constraintCoefficients = new double[mivi.size()];

            for (int x = y; x != VARIABLE_AMOUNT; ++x) {
                if (epsilonEquals(m.get(x, y), 0)) {
                    continue;
                }

                if (leadingEntryIndex == -1 
                        && epsilonEquals(m.get(x, y), 1)) {
                    leadingEntryIndex = x;
                } else {
                    int i = mivi.get(x);
                    coefficients[i] -= m.get(x, y);
                    constraintCoefficients[i] = m.get(x, y);
                }
            }
            
            constraintList.add(new LinearConstraint(
                                   constraintCoefficients,
                                   Relationship.LEQ,
                                   m.get(VARIABLE_AMOUNT, y)));
            
            double[] constraintCoefficients2 = constraintCoefficients.clone();
            Contract contract = mcii.get(leadingEntryIndex);
            Node node = c2n.get(contract);
            double duration = timeAssignment.get(node) - 
                              contract.getTimestamp();
            
            constraintList.add(new LinearConstraint(
                                   constraintCoefficients2,
                                   Relationship.GEQ,
                                   m.get(VARIABLE_AMOUNT, y) - 
                                   contract.evaluate(duration)));
        }
        
        for (Integer i : mivi.keySet()) {
            final double[] constraintCoefficients = 
                    new double[mivi.size()];
            
            constraintCoefficients[mivi.get(i)] = 1.0;
            
            constraintList.add(new LinearConstraint(
                                   constraintCoefficients,
                                   Relationship.LEQ,
                                   ));
        }
        
        // Build the objective function.
        final LinearObjectiveFunction of = 
                new LinearObjectiveFunction(coefficients, 0.0);
        
        return new OptimizationData[]{
            new LinearConstraintSet(constraintList), 
            of
        };
    }
    
    private final LinearConstraintSet getConstraintSet(final Matrix m) {
        final LinearConstraintSet lcs = new LinearConstraintSet();
        
        
        
        return lcs;
    }
        
    private final void buildMaps() {
        this.mci.clear();
        this.mcii.clear();
        this.c2n.clear();
        
        int index = 0;
            
        for (final Node node : this.graph.getNodes()) {
            for (final Contract contract : node.getOutgoingContracts()) {
                this.mci.put(contract, index);
                this.mcii.put(index++, contract);
            }
        }
        
        for (final Node node : graph.getNodes()) {
            for (final Contract c : node.getIncomingContracts()) {
                this.c2n.put(c, node);
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
    
    /**
     * Loads a row for node <code>node</code>.
     * 
     * @param node the node whose row to fill up.
     * @param row the row to fill.
     */
    private final void loadRow(final Node node, final double[] row) {
        // Compute the constant factor.
        row[row.length - 1] = computeConstantEntry(node);
        
        // Compute everything else.
        for (final Node debtor : node.getDebtors()) {
            for (final Contract c : node.getContractsTo(debtor)) {
                row[mci.get(c)] += 
                        c.getGrowthFactor(equilibriumTime -
                                          timeAssignment.get(debtor));
            }
        }
        
        for (final Contract c : node.getIncomingContracts()) {
            row[mci.get(c)] -=
                        c.getGrowthFactor(equilibriumTime -
                                          timeAssignment.get(node));
        }
    }
    
    /**
     * Computes the constant entry of the matrix' row corresponding to
     * <code>node</code>
     * 
     * @param node the node.
     * 
     * @return the constant entry belonging the node <code>node</code>.
     */
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
}
