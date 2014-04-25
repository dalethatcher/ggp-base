package com.dalethatcher.ggp;

import com.google.common.base.Throwables;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

import java.util.List;

public class DrtLimitedDepthExpander {
    private final DelayedExpansionTreeNode root;

    public DrtLimitedDepthExpander(MachineState currentState) {
        root = new DelayedExpansionTreeNode(currentState);
    }

    public Move findBestDepthLimitedMove(StateMachine stateMachine, Role role, long expiryTime,
                                         HeuristicGoalFunction heuristicGoalFunction, int maxDepth) {
        try {
            List<Move> movesForRole = stateMachine.getLegalMoves(root.getState(), role);

            if (movesForRole.size() == 1) {
                return movesForRole.get(0);
            }

            Move bestMove = null;
            int bestGoal = -1;
            int roleIndex = stateMachine.getRoleIndices().get(role);

            for (List<Move> candidate : root.getPossibleMoves(stateMachine)) {
                DelayedExpansionTreeNode child = root.expandMove(stateMachine, candidate);
                int goal = calculateGoalForMove(stateMachine, role, expiryTime, child, heuristicGoalFunction, maxDepth);

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
                                     DelayedExpansionTreeNode node,
                                     HeuristicGoalFunction heuristicGoalFunction, int maxDepth) {
        try {
            if (node.isTerminal(stateMachine) || maxDepth == 0) {
                return getGoal(stateMachine, role, heuristicGoalFunction, node);
            } else {
                List<Move> possibleMoves = stateMachine.getLegalMoves(node.getState(), role);
                NodeType nodeType = (possibleMoves.size() == 1) ? NodeType.MIN : NodeType.MAX;
                int bestGoal = (nodeType == NodeType.MAX) ? 0 : 100;

                for (List<Move> childMove : node.getPossibleMoves(stateMachine)) {
                    DelayedExpansionTreeNode child = node.expandMove(stateMachine, childMove);
                    int goal = calculateGoalForMove(stateMachine, role, expiryTime, child, heuristicGoalFunction,
                            maxDepth - 1);

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
        } catch (MoveDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    private int getGoal(StateMachine stateMachine, Role role,
                        HeuristicGoalFunction heuristicGoalFunction, DelayedExpansionTreeNode node) {
        try {
            return node.getGoal(stateMachine, role);
        }
        catch (Exception e) {
            return heuristicGoalFunction.apply(stateMachine, node.getState(), role);
        }
    }
}
