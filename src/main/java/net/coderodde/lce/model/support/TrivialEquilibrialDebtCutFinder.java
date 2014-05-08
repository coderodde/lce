package net.coderodde.lce.model.support;

import net.coderodde.lce.model.Contract;
import net.coderodde.lce.model.DebtCutAssignment;
import net.coderodde.lce.model.EquilibrialDebtCutFinder;
import net.coderodde.lce.model.Graph;
import net.coderodde.lce.model.Node;
import net.coderodde.lce.model.TimeAssignment;

/**
 *
 * @author rodionefremov
 */
public class TrivialEquilibrialDebtCutFinder 
implements EquilibrialDebtCutFinder {

    @Override
    public DebtCutAssignment compute(Graph graph, TimeAssignment timeAssignment, double equilibriumTime) {
        final DebtCutAssignment dca = 
                new DefaultDebtCutAssignment(equilibriumTime);
        
        for (final Node node : graph.getNodes()) {
            for (final Node debtor : node.getDebtors()) {
                for (final Contract contract : node.getContractsTo(debtor)) {
                    dca.put(contract, contract.evaluate(timeAssignment.get(debtor) - contract.getTimestamp()));
                }
            }
        }
        
        return dca;
    }

    @Override
    public long getMatrixReductionTime() {
        return -1L;
    }

    @Override
    public long getMinimizationTime() {
        return -1L;
    }
}
