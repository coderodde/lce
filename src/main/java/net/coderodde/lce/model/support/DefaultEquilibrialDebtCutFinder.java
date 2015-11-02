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
 * @author Rodion Efremov
 * @version 1.618
 */
public strictfp class DefaultEquilibrialDebtCutFinder 
implements EquilibrialDebtCutFinder {

    /**
     * This is a sentinel value to denote the fact that no solution found.
     */
    public static final DebtCutAssignment NO_SOLUTION =
            new DebtCutAssignment(Double.NEGATIVE_INFINITY);
    
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
    
    /**
     * Maps a contract to the node it was issued to.
     */
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
     * The amount of variables.
     */
    private int variableAmount;
    
    /** 
     * The matrix for current graph.
     */
    private Matrix m;
    
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
     * @param graph           the graph to work on.
     * @param timeAssignment  a map mapping each node <i>u</i> to the moment at 
     *                        which <i>u</i> is ready to pay its debt cuts.
     * @param equilibriumTime the time point at which the graph is supposed to 
     *                        be in equilibrium.
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
        
        this.m = loadMatrix();
        this.variableAmount = m.getColumnAmount() - 1;
        
        long ta = System.currentTimeMillis();
        rank = m.reduceToReducedRowEchelonForm();
        long tb = System.currentTimeMillis();
        this.matrixReductionDuration = tb - ta;
        
        if (m.hasSolution() == false) {
            // This should not happen.
            return NO_SOLUTION;
        }
        
        OptimizationData[] lp = convertMatrixToLinearProgram(m);
        ta = System.currentTimeMillis();
        PointValuePair pvp = new SimplexSolver().optimize(lp);
        tb = System.currentTimeMillis();
        minimizationDuration = tb - ta;
        return extractDebtCuts(pvp);
    }
    
    /**
     * Returns the time spent on reducing the matrix. The return value makes
     * sense only after at least one run of this finder.
     * 
     * @return the time spent on reducing the matrix.
     */
    @Override
    public final long getMatrixReductionTime() {
        return this.matrixReductionDuration;
    }
    
    /**
     * Returns the time spent on minimizing the debt cuts.
     * 
     * @return the time spent on minimizing the debt cuts.
     */
    @Override
    public final long getMinimizationTime() {
        return minimizationDuration;
    }
    
    /**
     * Extracts the debt cuts from the solution of a linear program.
     * 
     * @param pvp the solution vector.
     * 
     * @return debt cut assignment object.
     */
    private DebtCutAssignment extractDebtCuts(PointValuePair pvp) {
        final DebtCutAssignment dca = 
                new DebtCutAssignment(equilibriumTime);
        
        // Process independent variables. mivii maps appearance index to
        // column index.
        for (final Map.Entry<Integer, Integer> e : this.mivii.entrySet()) {
            // e.key ~ appearance index,
            // e.value ~ column index
            dca.put(this.mcii.get(e.getValue()), pvp.getPointRef()[e.getKey()]);
        }
        
        // Process dependent variables.
        for (int r = 0; r != rank; ++r) {
            double cut = m.get(variableAmount, r);
            int leadingEntryIndex = -1;
            
            for (int x = r; x != variableAmount; ++x) {
                if (leadingEntryIndex == -1) {
                    if (epsilonEquals(m.get(x, r), 1)) {
                        leadingEntryIndex = x;
                    }
                } else {
                    if (epsilonEquals(m.get(x, r), 0) == false) {
                        cut -= pvp.getPointRef()[this.mivi.get(x)] * m.get(x, r);
                    }
                }
            }
            
            if (epsilonEquals(cut, 0)) {
                cut = 0;
            }
            
            dca.put(this.mcii.get(leadingEntryIndex), cut);
        }
        
        return dca;
    }
    
    /**
     * Converts the matrix to a linear program.
     * 
     * @param m the matrix to extract from.
     * 
     * @return a linear program.
     */
    private OptimizationData[] convertMatrixToLinearProgram(final Matrix m) {
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
    private OptimizationData[] getLPData(final Matrix m) {
        int index = 0;
        
        this.mivi.clear();
        this.mivii.clear();
        
        for (int r = 0; r != rank; ++r) {
            boolean leadingEntryFound = false;
            
            for (int c = r; c != variableAmount; ++c) {
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
        final List<LinearConstraint> constraintList = 
                new ArrayList<>(2 * variableAmount);
        
        // Create constraints for dependent variables.
        for (int y = 0; y != rank; ++y) {
            int leadingEntryIndex = -1;
            double[] constraintCoefficients = new double[mivi.size()];

            for (int x = y; x != variableAmount; ++x) {
                if (epsilonEquals(m.get(x, y), 0)) {
                    continue;
                }

                if (leadingEntryIndex == -1 
                        && epsilonEquals(m.get(x, y), 1)) {
                    leadingEntryIndex = x;
                } else {
                    int i = mivi.get(x);
                    coefficients[i] -= m.get(x, y);
                    constraintCoefficients[i] = -m.get(x, y);
                }
            }
            
            constraintList.add(new LinearConstraint(
                                   constraintCoefficients,
                                   Relationship.GEQ,
                                   -m.get(variableAmount, y)));
            
            double[] constraintCoefficients2 = constraintCoefficients.clone();
            Contract contract = mcii.get(leadingEntryIndex);
            Node node = c2n.get(contract);
            double duration = timeAssignment.get(node, contract) - 
                              contract.getTimestamp();
            
            constraintList.add(new LinearConstraint(
                                   constraintCoefficients2,
                                   Relationship.LEQ,
                                   contract.evaluate(duration) -
                                   m.get(variableAmount, y)));
        }
        
        // Create constraints for independent variables.
        for (Integer i : mivi.keySet()) {
            final double[] constraintCoefficients = 
                    new double[mivi.size()];
            
            constraintCoefficients[mivi.get(i)] = 1.0;
            
            final Contract c = mcii.get(i);
            final Node node = c2n.get(c);
            
            constraintList.add(new LinearConstraint(
                                   constraintCoefficients,
                                   Relationship.LEQ,
                                   c.evaluate(timeAssignment.get(node, c) -
                                              c.getTimestamp())));
            
            coefficients[mivi.get(i)] += 1.0;
        }
       
        // Build the objective function.
        final LinearObjectiveFunction of = 
                new LinearObjectiveFunction(coefficients, 0);
        
        return new OptimizationData[]{
            new LinearConstraintSet(constraintList), 
            of
        };
    }
    
    /**
     * Builds some of the maps.
     */
    private void buildMaps() {
        this.mci.clear();
        this.mcii.clear();
        this.c2n.clear();
        
        int index = 0;
            
        for (final Node node : graph.getNodes()) {
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
    
    /**
     * Loads the matrix.
     * 
     * @return a set up matrix. 
     */
    private Matrix loadMatrix() {
        // +1 because the result matrix is augmented.
        final int ROWS = graph.size();
        final int COLS = graph.getContractAmount() + 1;
        
        double[][] matrix = new double[ROWS][COLS];
        
        for (final Node node : graph.getNodes()) {
            for (final Node debtor : node.getDebtors()) {
                for (final Contract contract : node.getContractsTo(debtor)) {
                    contract.setTimestamp( 
                             contract.getTimestamp() - 
                             contract.getShiftCorrection(
                                     timeAssignment.get(debtor, contract)));
                }
            }
        }
        
        int row = 0;
        
        // Load the constant entries.
        for (final Node node : graph.getNodes()) {
            matrix[row++][COLS - 1] = computeConstantEntry(node);
        }
        
        row = 0;
        
        // Load everything else.
        for (Node node : graph.getNodes()) {
            loadRow(node, matrix[row++]);
        }
        
        return new Matrix(matrix);
    }
    
    /**
     * Loads a row for node <code>node</code>.
     * 
     * @param node the node whose row to fill up.
     * @param row  the row to fill.
     */
    private void loadRow(final Node node, final double[] row) {
        for (final Node debtor : node.getDebtors()) {
            for (final Contract c : node.getContractsTo(debtor)) {
                row[mci.get(c)] += 
                        c.getGrowthFactor(equilibriumTime -
                                          timeAssignment.get(debtor, c));
            }
        }
        
        for (final Contract c : node.getIncomingContracts()) {
            row[mci.get(c)] -=
                        c.getGrowthFactor(equilibriumTime -
                                          timeAssignment.get(node, c));
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
    private double computeConstantEntry(final Node node) {
        double sum = 0;
        Contract tmp;
        
        for (final Node debtor : node.getDebtors()) {
            for (final Contract contract : node.getContractsTo(debtor)) {
                tmp = contract.clone();
                tmp.setPrincipal(contract.
                                 evaluate(timeAssignment.get(debtor, contract) - 
                                          contract.getTimestamp()));
                
                sum += tmp.evaluate(equilibriumTime - 
                                    timeAssignment.get(debtor, contract));
            }
        }
        
        for (final Node lender : node.getLenders()) {
            for (final Contract contract : lender.getContractsTo(node)) {
                tmp = contract.clone();
                tmp.setPrincipal(contract.
                                 evaluate(timeAssignment.get(node, contract) -
                                          contract.getTimestamp()));
                
                sum -= tmp.evaluate(equilibriumTime - 
                                    timeAssignment.get(node, contract));
                
            }
        }
        
        return sum;
    }
}
