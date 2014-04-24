package com.dalethatcher.ggp;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DelayedExpansionTreeNode {
    private final MachineState state;
    private final Map<List<Move>, DelayedExpansionTreeNode> children = Maps.newHashMap();
    private List<List<Move>> possibleMoves = null;
    private List<List<Move>> unexploredMoves = null;

    public DelayedExpansionTreeNode(MachineState state) {
        this.state = state;
    }

    private void calculatePossibleMoves(StateMachine stateMachine) {
        try {
            if (possibleMoves == null) {
                if (stateMachine.isTerminal(state)) {
                    possibleMoves = Collections.emptyList();
                }
                else {
                    possibleMoves = stateMachine.getLegalJointMoves(state);
                }
                unexploredMoves = Lists.newLinkedList(possibleMoves);
            }
        }
        catch (MoveDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    private List<List<Move>> getUnexploredMoves(StateMachine stateMachine) {
        if (possibleMoves == null) {
            calculatePossibleMoves(stateMachine);
        }

        return unexploredMoves;
    }

    public List<List<Move>> getPossibleMoves(StateMachine stateMachine) {
        if (possibleMoves == null) {
            calculatePossibleMoves(stateMachine);
        }

        return possibleMoves;
    }

    public DelayedExpansionTreeNode expandMove(StateMachine stateMachine, List<Move> possibleMove) {
        try {
            MachineState nextState = stateMachine.getNextState(state, possibleMove);
            DelayedExpansionTreeNode childNode = new DelayedExpansionTreeNode(nextState);

            children.put(possibleMove, childNode);

            return childNode;
        } catch (TransitionDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    public void expandDepthFirst(StateMachine stateMachine) {
        LinkedList<DelayedExpansionTreeNode> fringe = Lists.newLinkedList();
        fringe.add(this);

        while (!fringe.isEmpty()) {
            DelayedExpansionTreeNode nodeToExpand = fringe.get(0);

            if (nodeToExpand.isFullyExpanded(stateMachine)) {
                fringe.remove(0);
            } else {
                List<Move> nextMove = nodeToExpand.popNextUnexploredMove(stateMachine);
                DelayedExpansionTreeNode newNode = nodeToExpand.expandMove(stateMachine, nextMove);

                fringe.push(newNode);
            }
        }
    }

    private List<Move> popNextUnexploredMove(StateMachine stateMachine) {
        return getUnexploredMoves(stateMachine).remove(0);
    }

    public boolean isFullyExpanded(StateMachine stateMachine) {
        return getUnexploredMoves(stateMachine).isEmpty();
    }

    public Move getBestMoveByMinMax(StateMachine stateMachine, Role role) {
        try {
            List<Move> possibleMovesForRole = stateMachine.getLegalMoves(state, role);

            if (possibleMovesForRole.size() == 1) {
                return possibleMovesForRole.get(0);
            }

            expandDepthFirst(stateMachine);

            int roleIndex = stateMachine.getRoleIndices().get(role);
            int bestScore = 0;
            Move bestMove = null;
            for (List<Move> move : possibleMoves) {
                int score = children.get(move).getMinMaxScore(stateMachine, role, false);

                if (bestMove == null || score > bestScore) {
                    bestScore = score;
                    bestMove = move.get(roleIndex);
                }
            }

            return bestMove;
        } catch (MoveDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    private int getMinMaxScore(StateMachine stateMachine, Role role, boolean maximise) {
        try {
            if (stateMachine.isTerminal(state)) {
                return stateMachine.getGoal(state, role);
            }
            else {
                int bestScore = maximise ? 0 : 100;

                for (List<Move> move : possibleMoves) {
                    int score = children.get(move).getMinMaxScore(stateMachine, role, !maximise);

                    if ((maximise && score > bestScore) || (!maximise && score < bestScore)) {
                       bestScore = score;
                    }
                }

                return bestScore;
            }
        } catch (GoalDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    public MachineState getState() {
        return state;
    }

    public boolean isTerminal(StateMachine stateMachine) {
        return stateMachine.isTerminal(state);
    }

    public int getGoal(StateMachine stateMachine, Role role) {
        try {
            return stateMachine.getGoal(state, role);
        }
        catch (GoalDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public String toString() {
        return "DelayedExpansionTreeNode{" +
                "state=" + state +
                ", children=" + children +
                ", possibleMoves=" + possibleMoves +
                ", unexploredMoves=" + unexploredMoves +
                '}';
    }
}
