package com.dalethatcher.ggp;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public class AlphaBetaGamer extends StateMachineGamer {
    @Override
    public StateMachine getInitialStateMachine() {
        return new ProverStateMachine();
    }

    @Override
    public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
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
        return "AlphaBetaGamer";
    }

    @Override
    public DetailPanel getDetailPanel() {
        return new SimpleDetailPanel();
    }

    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
        AlphaBetaExpander expander = new AlphaBetaExpander(getCurrentState());

        return expander.findBestAlphaBetaMove(getStateMachine(), getRole(), timeout - 1000);
    }
}
