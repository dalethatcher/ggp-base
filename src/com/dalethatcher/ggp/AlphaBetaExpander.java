package com.dalethatcher.ggp;

import com.google.common.base.Throwables;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

import java.util.List;

public class AlphaBetaExpander {
    private final DelayedExpansionTreeNode root;

    public AlphaBetaExpander(MachineState rootState) {
        this.root = new DelayedExpansionTreeNode(rootState);
    }


    public Move findBestAlphaBetaMove(StateMachine stateMachine, Role role, long expiryTime) {
        try {
            List<Move> movesForRole = stateMachine.getLegalMoves(root.getState(), role);

            if (movesForRole.size() == 1) {
                return movesForRole.get(0);
            }

            Move bestMove = null;
            int bestGoal = 0;
            int roleIndex = stateMachine.getRoleIndices().get(role);

            for (List<Move> candidate : root.getPossibleMoves(stateMachine)) {
                DelayedExpansionTreeNode child = root.expandMove(stateMachine, candidate);
                int goal = calculateGoalForMove(stateMachine, role, expiryTime, child, NodeType.MIN);

                if (goal > bestGoal) {
                    if (goal == 100) {
                        return candidate.get(roleIndex);
                    } else {
                        bestGoal = goal;
                        bestMove = candidate.get(roleIndex);
                    }
                }

                if (System.currentTimeMillis() > expiryTime) {
                    if (bestMove != null) {
                        System.out.println("Timed out, returning best found move: " + bestMove);
                        return bestMove;
                    }
                    else {
                        System.out.println("Timed out, returning random move");
                        return stateMachine.getRandomMove(root.getState(), role);
                    }
                }
            }

            return bestMove;
        } catch (MoveDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    private int calculateGoalForMove(StateMachine stateMachine, Role role, long expiryTime,
                                     DelayedExpansionTreeNode node, NodeType nodeType) {
        if (node.isTerminal(stateMachine)) {
            return node.getGoal(stateMachine, role);
        } else {
            int bestGoal = (nodeType == NodeType.MAX) ? 0 : 100;

            for (List<Move> childMove : node.getPossibleMoves(stateMachine)) {
                DelayedExpansionTreeNode child = node.expandMove(stateMachine, childMove);
                int goal = calculateGoalForMove(stateMachine, role, expiryTime, child, nodeType.opposite());

                if (nodeType == NodeType.MAX) {
                    if (goal == 100) {
                        return goal;
                    } else if (goal > bestGoal) {
                        bestGoal = goal;
                    }
                } else if (nodeType == NodeType.MIN) {
                    if (goal == 0) {
                        return goal;
                    } else if (goal < bestGoal) {
                        bestGoal = goal;
                    }
                }

                if (System.currentTimeMillis() > expiryTime) {
                    return bestGoal;
                }
            }

            return bestGoal;
        }
    }

    private static enum NodeType {
        MAX, MIN;

        public NodeType opposite() {
            return (this == MAX) ? MIN : MAX;
        }
    }
}
