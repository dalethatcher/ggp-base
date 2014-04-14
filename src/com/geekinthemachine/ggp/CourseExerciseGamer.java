package com.geekinthemachine.ggp;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import java.util.List;
import java.util.Random;

public class CourseExerciseGamer extends StateMachineGamer {

    private final Random random = new Random();

    @Override
    public StateMachine getInitialStateMachine() {
        return new ProverStateMachine();
    }

    @Override
    public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
        // Deliberately left blank.
    }

    @Override
    public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
        long start = System.currentTimeMillis();

        List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
        Move selection = moves.get(random.nextInt(moves.size()));

        long stop = System.currentTimeMillis();

        notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
        return selection;
    }

    @Override
    public void stateMachineStop() {
        // Deliberately left blank.
    }

    @Override
    public void stateMachineAbort() {
        // Deliberately left blank.
    }

    @Override
    public void preview(Game g, long timeout) throws GamePreviewException {
        // Deliberately left blank.
    }

    @Override
    public String getName() {
        return "WINNER";
    }

    @Override
    public DetailPanel getDetailPanel() {
        return new SimpleDetailPanel();
    }
}
