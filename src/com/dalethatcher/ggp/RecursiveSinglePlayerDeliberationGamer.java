package com.dalethatcher.ggp;

import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public class RecursiveSinglePlayerDeliberationGamer extends StateMachineGamer {
    @Override
    public StateMachine getInitialStateMachine() {
        return new ProverStateMachine();
    }

    @Override
    public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
    }

    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
        StateMachine stateMachine = getStateMachine();
        MachineState currentState = getCurrentState();
        Role role = getRole();

        int goal = RecursiveSinglePlayerGameTreeNode.goalOrZero(stateMachine, currentState, role);
        RecursiveSinglePlayerGameTreeNode root = new RecursiveSinglePlayerGameTreeNode(currentState, role, goal);

        root.resolveTree(stateMachine);

        return root.bestKnownMove();
    }

    @Override
    public void stateMachineStop() {

    }

    @Override
    public void stateMachineAbort() {

    }

    @Override
    public void preview(Game g, long timeout) throws GamePreviewException {

    }

    @Override
    public String getName() {
        return "RecursiveSinglePlayerDeliberationGamer";
    }
}
