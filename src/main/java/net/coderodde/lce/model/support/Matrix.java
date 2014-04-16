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
    
    private final double m[][];
    private final int rows;
    private final int columns;
    private double epsilon;
    
    Matrix(final int rowAmount, final int columnAmount) {
        this.m = new double[rowAmount][columnAmount];
        this.rows = rowAmount;
        this.columns = columnAmount;
        this.epsilon = DEFAULT_EPSILON;
    }
    
    Matrix(final double[][] m) {
        this.rows = m.length;
        int cols = 0;
        
        for (double[] row : m) {
            cols = Math.max(cols, row.length);
        }
        
        this.m = new double[rows][cols];
        this.columns = cols;
        
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                this.m[r][c] = m[r][c]; 
            }
        }
    }
    
    double get(final int x, final int y) {
        return m[y][x];
    }
    
    void set(final int x, final int y, final double value) {
        m[y][x] = value;
    }
    
    boolean hasSolution() {
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
    
    int reduceToReducedRowEchelonForm() {
        int rowsProcessed = 0;
        
        for (int k = 0; k != columns - 1; ++k) {
            int ur = findUpmostRowWithAPivotAtColumn(k, rowsProcessed);
            
            if (ur == ROW_NOT_FOUND) {
                continue;
            }
            
            swapRows(ur, rowsProcessed);
            scaleRow(rowsProcessed, 1.0 / m[rowsProcessed][k]);
            
            for (int r = 0; r < rows; ++r) {
                if (r != rowsProcessed) {
                    addToRowMultipleOfAnotherRow(
                            r,             
                            rowsProcessed,
                            -m[r][k] / m[rowsProcessed][k]);
                    
                }
            }
            
            ++rowsProcessed;
        }
        
        return rowsProcessed;
    }
    
    void addToRowMultipleOfAnotherRow(final int targetRow, 
                                      final int sourceRow,
                                      final double factor) {
        checkFactor(factor);
        for (int k = 0; k != columns; ++k) {
            m[targetRow][k] += m[sourceRow][k] * factor;
        }
    }
    
    void swapRows(final int r1, final int r2) {
        double[] tmp = m[r1];
        m[r1] = m[r2];
        m[r2] = tmp;
    }
    
    void scaleRow(final int rowNumber, final double factor) {
        checkFactor(factor);
        double[] row = m[rowNumber];
        for (int i = 0; i != row.length; ++i) {
            row[i] *= factor;
        }
    }
    
    int findUpmostRowWithAPivotAtColumn(final int columnIndex,
                                        final int after) {
        for (int i = after; i < rows; ++i) {
            if (!epsilonEquals(m[i][columnIndex], 0.0, epsilon)) {
                return i;
            }
        }
        
        return ROW_NOT_FOUND;
    }
    
    void setEpsilon(final double epsilon) {
        checkNotNaN(epsilon, "'epsilon' is NaN.");
        checkNotInfinite(epsilon, "'epsilon' is infinite in absolute value.");
        checkPositive(epsilon, "'epsilon' is at most +0.0.");
        this.epsilon = epsilon;
    }
    
    private final void checkFactor(final double factor) {
        checkNotNaN(factor, "'factor' is NaN.");
        checkNotInfinite(factor, "'factor' is infinite.");
    }
    
    public void debugPrint() {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < columns; ++c) {
                System.out.printf("% 3.2f ", m[r][c]);
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
