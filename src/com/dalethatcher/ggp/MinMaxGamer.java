package com.dalethatcher.ggp;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public class MinMaxGamer extends StateMachineGamer {
    @Override
    public StateMachine getInitialStateMachine() {
        return new ProverStateMachine();
    }

    @Override
    public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

    }

    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
        MachineState state = getCurrentState();
        StateMachine stateMachine = getStateMachine();

        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(state);
        Move bestMove = root.getBestMoveByMinMax(stateMachine, getRole());

        return bestMove;
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
        return "MinMaxGamer";
    }

    @Override
    public DetailPanel getDetailPanel() {
        return new SimpleDetailPanel();
    }
}
