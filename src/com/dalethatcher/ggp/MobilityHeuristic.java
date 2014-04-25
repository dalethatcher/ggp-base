package com.dalethatcher.ggp;

import com.google.common.base.Throwables;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

public class MobilityHeuristic implements HeuristicGoalFunction {
    private final double maxNumberOfMoves;

    public MobilityHeuristic(int maxNumberOfMoves) {
        this.maxNumberOfMoves = maxNumberOfMoves;
    }

    @Override
    public int apply(StateMachine stateMachine, MachineState state, Role role) {
        try {
            double numberOfLegalMoves = stateMachine.getLegalMoves(state, role).size();

            return (int)(100.0 * (numberOfLegalMoves/maxNumberOfMoves));
        } catch (MoveDefinitionException e) {
            throw Throwables.propagate(e);
        }
    }
}
