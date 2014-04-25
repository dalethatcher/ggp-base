package com.dalethatcher.ggp;

import org.ggp.base.util.game.TestGameRepository;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PossibleMovesEstimatorTest {
    @Test
    public void estimatesExpectedNumberOfPossibleMoves() throws Exception {
        ProverStateMachine stateMachine = new ProverStateMachine();
        stateMachine.initialize(new TestGameRepository().getGame("ticTacToe").getRules());

        PossibleMovesEstimator estimator = new PossibleMovesEstimator();
        int maxMoveEstimate = estimator.estimateMaxPossibleMoves(stateMachine, System.currentTimeMillis() + 500);

        assertThat(maxMoveEstimate, is(9));
    }
}