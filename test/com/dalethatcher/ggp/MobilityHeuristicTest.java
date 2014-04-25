package com.dalethatcher.ggp;

import org.ggp.base.util.game.TestGameRepository;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MobilityHeuristicTest {
    @Test
    public void givesExpectedScoreForGameState() {
        ProverStateMachine stateMachine = new ProverStateMachine();
        stateMachine.initialize(new TestGameRepository().getGame("min_max_simple_game").getRules());
        Role role = stateMachine.getRoles().get(0);

        int heuristicGoalScore = new MobilityHeuristic(6).apply(stateMachine, stateMachine.getInitialState(), role);

        assertThat(heuristicGoalScore, is((int)(100.0 * 2.0/6.0)));
    }
}