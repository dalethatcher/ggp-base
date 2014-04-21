package com.dalethatcher.ggp;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import java.util.List;
import java.util.Map;

public class DelayedExpansionTreeNode {
    private final MachineState state;
    private final Map<List<Move>, DelayedExpansionTreeNode> children = Maps.newHashMap();
    private List<List<Move>> possibleMoves = null;

    public DelayedExpansionTreeNode(MachineState state) {
        this.state = state;
    }

    public void calculatePossibleMoves(StateMachine stateMachine) {
        try {
            if (possibleMoves == null) {
                possibleMoves = stateMachine.getLegalJointMoves(state);
            }
        }
        catch (MoveDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }

    public List<List<Move>> getPossibleMoves() {
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
}
