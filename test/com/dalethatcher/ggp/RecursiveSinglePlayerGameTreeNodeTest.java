package com.dalethatcher.ggp;

import com.google.common.collect.Lists;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecursiveSinglePlayerGameTreeNodeTest {
    @Mock
    private MachineState rootState;
    @Mock
    private StateMachine stateMachine;
    @Mock
    private Role role;
    @Mock
    private Move lhsMove;
    @Mock
    private Move rhsMove;
    @Mock
    private Move lhsChildMove;
    @Mock
    private MachineState lhsState;
    @Mock
    private MachineState rhsState;
    @Mock
    private MachineState lhsChildState;

    @Test
    public void canResolveTree() throws Exception {
        when(stateMachine.getLegalMoves(rootState, role)).thenReturn(Lists.newArrayList(lhsMove, rhsMove));
        when(stateMachine.getNextState(rootState, Lists.newArrayList(lhsMove))).thenReturn(lhsState);
        when(stateMachine.getNextState(rootState, Lists.newArrayList(rhsMove))).thenReturn(rhsState);

        when(stateMachine.getLegalMoves(lhsState, role)).thenReturn(Lists.newArrayList(lhsChildMove));
        when(stateMachine.getNextState(lhsState, Lists.newArrayList(lhsChildMove))).thenReturn(lhsChildState);

        when(stateMachine.getLegalMoves(rhsState, role)).thenThrow(new MoveDefinitionException(rhsState, role));
        when(stateMachine.isTerminal(rhsState)).thenReturn(true);

        when(stateMachine.getLegalMoves(lhsChildState, role)).
                thenThrow(new MoveDefinitionException(lhsChildState, role));
        when(stateMachine.isTerminal(lhsChildState)).thenReturn(true);

        RecursiveSinglePlayerGameTreeNode root = new RecursiveSinglePlayerGameTreeNode(rootState, role, 0);
        root.resolveTree(stateMachine);

        verify(stateMachine).getLegalMoves(lhsState, role);
    }

    @Test
    public void canPickBestMove() throws Exception {
        when(stateMachine.getLegalMoves(rootState, role)).thenReturn(Lists.newArrayList(lhsMove, rhsMove));
        when(stateMachine.getNextState(rootState, Lists.newArrayList(lhsMove))).thenReturn(lhsState);
        when(stateMachine.getNextState(rootState, Lists.newArrayList(rhsMove))).thenReturn(rhsState);

        when(stateMachine.getLegalMoves(lhsState, role)).thenThrow(new MoveDefinitionException(lhsState, role));
        when(stateMachine.isTerminal(lhsState)).thenReturn(true);
        when(stateMachine.getGoal(lhsState, role)).thenReturn(50);

        when(stateMachine.getLegalMoves(rhsState, role)).thenThrow(new MoveDefinitionException(rhsState, role));
        when(stateMachine.isTerminal(rhsState)).thenReturn(true);
        when(stateMachine.getGoal(rhsState, role)).thenReturn(100);

        RecursiveSinglePlayerGameTreeNode root = new RecursiveSinglePlayerGameTreeNode(rootState, role, 0);
        root.resolveTree(stateMachine);
        Move bestMove = root.bestKnownMove();

        assertThat(bestMove, is(rhsMove));
    }
}
