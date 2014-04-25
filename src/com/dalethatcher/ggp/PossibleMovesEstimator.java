package com.dalethatcher.ggp;

import com.google.common.collect.Lists;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import java.util.LinkedList;
import java.util.List;

public class PossibleMovesEstimator {
    public int estimateMaxPossibleMoves(StateMachine stateMachine, long expiry) {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(stateMachine.getInitialState());
        int maxFound = countMaxNumberOfMoves(stateMachine, root);

        LinkedList<DelayedExpansionTreeNode> fringe = Lists.newLinkedList();
        fringe.add(root);

        while (System.currentTimeMillis() > expiry && fringe.size() > 0) {
            DelayedExpansionTreeNode node = fringe.remove(0);

            if (!node.isFullyExpanded(stateMachine)) {
                for (List<Move> possibleMove : node.getPossibleMoves(stateMachine)) {
                    if (System.currentTimeMillis() > expiry) {
                        break;
                    }

                    DelayedExpansionTreeNode child = node.expandMove(stateMachine, possibleMove);
                    maxFound = Math.max(maxFound, countMaxNumberOfMoves(stateMachine, child));

                    fringe.push(child);
                }
            }
        }

        return maxFound;
    }

    private int countMaxNumberOfMoves(StateMachine stateMachine, DelayedExpansionTreeNode node) {
        int maxMoves = 0;

        if (!node.isTerminal(stateMachine)) {
            for (Role role : stateMachine.getRoles()) {
                int numberOfMovesForRole = node.getPossibleMovesForRole(stateMachine, role).size();

                if (numberOfMovesForRole > maxMoves) {
                    maxMoves = numberOfMovesForRole;
                }
            }
        }

        return maxMoves;
    }
}
