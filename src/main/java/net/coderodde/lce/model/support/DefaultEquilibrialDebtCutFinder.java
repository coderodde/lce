package net.coderodde.lce.model.support;

import java.util.Map;
import static net.coderodde.lce.Utils.checkTimeMap;
import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;

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
     * Computes the equilibrial debt cuts.
     * 
     * @param graph the graph to work on.
     * @param timeMap a map mapping each node <i>u</i> to the moment at which
     * <i>u</i> is ready to pay its debt cuts.
     * 
     * @return a map mapping each contract to its debt cut leading
     * to the global equilibrium.
     */
    @Override
    public DefaultDebtCutAssignment compute
        (final Graph graph, final TimeAssignment timeAssignment) {
        checkTimeMap(graph, timeAssignment);
        
        return null;
    }
}
