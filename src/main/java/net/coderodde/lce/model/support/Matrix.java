package net.coderodde.lce.model.support;

import static net.coderodde.lce.Utils.checkNotNaN;
import static net.coderodde.lce.Utils.checkNotInfinite;
import static net.coderodde.lce.Utils.checkPositive;
import static net.coderodde.lce.Utils.epsilonEquals;

/**
 * This class implements a matrix of double-precision floating-point entries,
 * and some operations on it as to facilitate reducing the matrix into 
 * reduced row echelon form.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
class Matrix {
    
    private static final double DEFAULT_EPSILON = 0.001;
    private static final int ROW_NOT_FOUND = -1;
    
    /**
     * The matrix of double-precision floating-point entries.
     */
    private final double m[][];
    
    /**
     * The amount of rows;
     */
    private final int rows;
    
    /**
     * The total amount of columns (with the augmentation column).
     */
    private final int columns;
    
    /**
     * The epsilon value used for comparing.
     */
    private double epsilon;
    
    /**
     * Keeps track whether reduction is the most recent (modifying) operation on
     * this matrix.
     */
    private boolean solved;
    
    /**
     * Constructs a matrix with <code>rowAmount</code> rows and
     * <code>columnAmount</code> columns (with the augmentation column).
     * 
     * @param rowAmount the row amount in this matrix.
     * @param columnAmount the column amount in this matrix.
     */
    Matrix(final int rowAmount, final int columnAmount) {
        this.m = new double[rowAmount][columnAmount];
        this.rows = rowAmount;
        this.columns = columnAmount;
        this.epsilon = DEFAULT_EPSILON;
    }
    
    /**
     * Constructs the matrix adapted to hold the input 2D-array.
     * 
     * @param m the input 2D-array.
     */
    Matrix(final double[][] m) {
        this.rows = m.length;
        int cols = 0;
        
        for (double[] row : m) {
            cols = Math.max(cols, row.length);
        }
        
        this.m = new double[rows][cols];
        this.columns = cols;
        this.epsilon = DEFAULT_EPSILON;
        
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                this.m[r][c] = m[r][c]; 
            }
        }
    }
    
    /** 
     * Returns the amount of rows in this matrix.
     * 
     * @return the amount of rows in this matrix. 
     */
    int getRowAmount() {
        return m.length;
    }
    
    /**
     * Returns the amount of columns in this matrix.
     * 
     * @return the amount of columns in this matrix.
     */
    int getColumnAmount() {
        return m[0].length;
    }
    
    /**
     * Retrieves the entry at (<code>x</code>, <code>y</code>).
     * 
     * @param x the column coordinate starting from 0.
     * @param y the row coordinate starting from 0.
     * 
     * @return returns the entry at (x, y). 
     */
    double get(final int x, final int y) {
        return m[y][x];
    }
    
    /**
     * Sets the entry at (<code>x</code>, <code>y</code>) to <code>value</code>.
     * 
     * @param x the column coordinate starting from 0.
     * @param y the row coordinate starting from 0.
     * @param value the value to set.
     */
    void set(final int x, final int y, final double value) {
        solved = false;
        m[y][x] = value;
    }
    
    /**
     * Checks whether the linear system represented by this matrix
     * @return 
     */
    boolean hasSolution() {
        if (solved == false) {
            throw new IllegalStateException(
                    "Solution check is allowed only straight after reduction.");
        }
        
        outer:
        for (double[] row : m) {
            for (int i = 0; i != row.length - 1; ++i) {
                if (!epsilonEquals(row[i], 0.0, epsilon)) {
                    continue outer;
                }
            }
            
            if (!epsilonEquals(row[row.length - 1], 0.0)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Performs the Gauss-Jordan elimination as to solve the linear system 
     * represented by this matrix.
     * 
     * @return the rank, i.e., the amount of non-zero rows (also amount of
     * dependent variables) in this matrix.
     */
    int reduceToReducedRowEchelonForm() {
        int rowsProcessed = 0;
        
        for (int k = 0; k != columns - 1; ++k) {
            int ur = findUpmostRowWithAPivotAtColumn(k, rowsProcessed);
            
            if (ur == ROW_NOT_FOUND) {
                continue;
            }
            
            swapRows(ur, rowsProcessed);
            scaleRow(rowsProcessed, 1.0 / m[rowsProcessed][k]);
            
            for (int r = 0; r != rows; ++r) {
                if (r != rowsProcessed) {
                    addToRowMultipleOfAnotherRow(
                            r,             
                            rowsProcessed,
                            -m[r][k] / m[rowsProcessed][k]);
                }
            }
            
            ++rowsProcessed;
        }
        
        solved = true;
        return rowsProcessed;
    }
    
    /**
     * Row operation: add to row <code>targetRow</code> the multiple of 
     * the row <code>targetRow</code>, runs in time O(rows).
     * 
     * @param targetRow the target row.
     * @param sourceRow the source row.
     * @param factor the factor scaling the source row.
     */
    void addToRowMultipleOfAnotherRow(final int targetRow, 
                                      final int sourceRow,
                                      final double factor) {
        checkFactor(factor);
        for (int k = 0; k != columns; ++k) {
            m[targetRow][k] += m[sourceRow][k] * factor;
        }
    }
    
    /**
     * Swaps to rows, <code>r1</code> and <code>r2</code>, in O(1).
     * 
     * @param r1 the index of a row.
     * @param r2 the index of another row.
     */
    void swapRows(final int r1, final int r2) {
        double[] tmp = m[r1];
        m[r1] = m[r2];
        m[r2] = tmp;
    }
    
    /**
     * Scales the row <code>rowNumber</code> by <code>factor</code>.
     * 
     * @param rowNumber the y-coordinate of the row to scale.
     * @param factor the scaling factor.
     */
    void scaleRow(final int rowNumber, final double factor) {
        checkFactor(factor);
        double[] row = m[rowNumber];
        for (int i = 0; i != row.length; ++i) {
            row[i] *= factor;
        }
    }
    
    /**
     * Finds the upmost row index having non-zero entry at column
     * <code>columnIndex</code>. First <code>after</code> rows are skipped.
     * 
     * @param columnIndex the column being checked.
     * @param after the amount of topmost rows being skipped.
     * 
     * @return the row index if an entry is found, <code>ROW_NOT_FOUND</code> 
     * otherwise.
     */
    int findUpmostRowWithAPivotAtColumn(final int columnIndex,
                                        final int after) {
        for (int i = after; i < rows; ++i) {
            if (!epsilonEquals(m[i][columnIndex], 0.0, epsilon)) {
                return i;
            }
        }
        
        return ROW_NOT_FOUND;
    }
    
    /**
     * Sets a new epsilon value for comparisons.
     * 
     * @param epsilon the epsilon value to set.
     */
    void setEpsilon(final double epsilon) {
        checkNotNaN(epsilon, "'epsilon' is NaN.");
        checkNotInfinite(epsilon, "'epsilon' is infinite in absolute value.");
        checkPositive(epsilon, "'epsilon' is at most +0.0.");
        this.epsilon = epsilon;
    }
    
    /**
     * Checks the factor against infinity and NaN.
     * 
     * @param factor the factor to check.
     */
    private final void checkFactor(final double factor) {
        checkNotNaN(factor, "'factor' is NaN.");
        checkNotInfinite(factor, "'factor' is infinite.");
    }
    
    /**
     * Pretty-print this matrix.
     */
    public void debugPrint() {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                System.out.printf("% 4.5f ", m[r][c]);
            }
            
            System.out.println();
        }
    }
    
    public static void main(String... args) {
        Matrix m = new Matrix(new double[][] {
            { 2,  1, -1,   8},
            {-3, -1,  2, -11},
            {-2,  1,  2,  -3}
        });
        
        int rank = m.reduceToReducedRowEchelonForm();
        m.debugPrint();
        System.out.println("Has solution: " + m.hasSolution() + ", rank " + rank);
        System.out.println("---");
        
        m = new Matrix(new double[][] {
            {1,  3,  1,  9},
            {1,  1, -1,  1},
            {3, 11,  5, 35},
            {3, 11,  5, 30}
        });
        
        rank = m.reduceToReducedRowEchelonForm();
        m.debugPrint();
        System.out.println("Has solution: " + m.hasSolution() + ", rank " + rank);
    }
}
