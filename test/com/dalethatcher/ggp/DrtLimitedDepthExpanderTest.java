package com.dalethatcher.ggp;

import com.google.common.collect.Sets;
import org.ggp.base.util.game.TestGameRepository;
import org.ggp.base.util.gdl.factory.GdlFactory;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.ggp.base.util.gdl.grammar.GdlPool.getConstant;
import static org.ggp.base.util.gdl.grammar.GdlPool.getFunction;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DrtLimitedDepthExpanderTest {
    @Spy
    private StateMachine stateMachine = new ProverStateMachine();

    @Test
    public void searchDepthIsLimited() throws Exception {
        stateMachine.initialize(new TestGameRepository().getGame("min_max_simple_game").getRules());
        MachineState rootState = stateMachine.getInitialState();
        Role role = stateMachine.getRoleFromConstant(getConstant("white"));

        DrtLimitedDepthExpander expander = new DrtLimitedDepthExpander(rootState);
        Move bestMove = expander.findBestDepthLimitedMove(stateMachine, role, System.currentTimeMillis() + 1000, 0);

        Move expectedMove = new Move(
                getFunction(getConstant("move"), newArrayList(getConstant("A"), getConstant("B"))));
        assertThat(bestMove, is(expectedMove));

        GdlSentence unexploredState = (GdlSentence) GdlFactory.create("(true (square D))");
        verify(stateMachine, never()).isTerminal(new MachineState(Sets.newHashSet(unexploredState)));
    }
}
