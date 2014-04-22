package com.dalethatcher.ggp;

import com.google.common.collect.Lists;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DelayedExpansionTreeNodeTest {
    @Mock
    private StateMachine stateMachine;
    @Mock
    private MachineState rootState;
    @Mock
    private MachineState childState;
    @Mock
    private MachineState lhsGrandChildState;
    @Mock
    private MachineState lhsGrandGrandChildState;
    @Mock
    private MachineState rhsGrandChildState;
    @Mock
    private Role roleA;
    @Mock
    private Role roleB;
    @Mock
    private Move firstMove;
    @Mock
    private Move lhsChildMove;
    @Mock
    private Move rhsChildMove;
    @Mock
    private Move lhsChildChildMove;
    @Mock
    private Move noopMove;

    @Before
    public void setUpMocks() throws Exception {
        when(stateMachine.getRoles()).thenReturn(Lists.newArrayList(roleA, roleB));
        when(stateMachine.isTerminal(rootState)).thenReturn(false);
        when(stateMachine.getLegalJointMoves(rootState)).thenReturn(toMovePairList(firstMove, noopMove));
        when(stateMachine.getLegalMoves(rootState, roleA)).thenReturn(Lists.newArrayList(firstMove));
        when(stateMachine.getLegalMoves(rootState, roleB)).thenReturn(Lists.newArrayList(noopMove));

        when(stateMachine.getNextState(rootState, toMovePairList(firstMove, noopMove).get(0))).thenReturn(childState);
        when(stateMachine.isTerminal(childState)).thenReturn(false);
        when(stateMachine.getLegalJointMoves(childState)).thenReturn(toMovePairList(noopMove, lhsChildMove,
                noopMove, rhsChildMove));

        when(stateMachine.getNextState(childState, toMovePair(noopMove, lhsChildMove))).thenReturn(lhsGrandChildState);
        when(stateMachine.getNextState(childState, toMovePair(noopMove, rhsChildMove))).thenReturn(rhsGrandChildState);

        when(stateMachine.isTerminal(lhsGrandChildState)).thenReturn(false);
        when(stateMachine.getLegalJointMoves(lhsGrandChildState)).thenReturn(toMovePairList(lhsChildChildMove, noopMove));
        when(stateMachine.getNextState(lhsGrandChildState, toMovePair(lhsChildChildMove, noopMove))).thenReturn(lhsGrandGrandChildState);

        when(stateMachine.isTerminal(rhsGrandChildState)).thenReturn(true);

        when(stateMachine.isTerminal(lhsGrandGrandChildState)).thenReturn(true);
    }

    @Test
    public void onlyExpandsNodesWhenRequested() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        root.getPossibleMoves(stateMachine);

        verify(stateMachine).getLegalJointMoves(rootState);
        verify(stateMachine, never()).getLegalJointMoves(childState);
    }

    @Test
    public void onlyExpandsNodeOnce() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        root.getPossibleMoves(stateMachine);
        root.getPossibleMoves(stateMachine);

        verify(stateMachine).getLegalJointMoves(rootState);
        verify(stateMachine, never()).getLegalJointMoves(childState);
    }

    @Test
    public void canExpandMove() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);

        List<Move> possibleMove = root.getPossibleMoves(stateMachine).get(0);
        DelayedExpansionTreeNode childNode = root.expandMove(stateMachine, possibleMove);

        assertThat(childNode, notNullValue());
        verify(stateMachine).getLegalJointMoves(rootState);
        verify(stateMachine).getNextState(rootState, possibleMove);
    }

    @Test
    public void canExpandDepthFirst() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        root.expandDepthFirst(stateMachine);

        InOrder inOrder = inOrder(stateMachine);
        inOrder.verify(stateMachine).getLegalJointMoves(rootState);
        inOrder.verify(stateMachine).getNextState(rootState, toMovePair(firstMove, noopMove));
        inOrder.verify(stateMachine).getLegalJointMoves(childState);
        inOrder.verify(stateMachine).getNextState(childState, toMovePair(noopMove, lhsChildMove));
        inOrder.verify(stateMachine).getLegalJointMoves(lhsGrandChildState);
        inOrder.verify(stateMachine).getNextState(lhsGrandChildState, toMovePair(lhsChildChildMove, noopMove));
        inOrder.verify(stateMachine).getNextState(childState, toMovePair(noopMove, rhsChildMove));
    }

    @Test
    public void ifOnlyOnePossibleMoveReturnsImmediately() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        Move bestMove = root.getBestMoveByMinMax(stateMachine, roleB);

        assertThat(bestMove, is(noopMove));
        verify(stateMachine, never()).getNextState(any(MachineState.class), anyListOf(Move.class));
    }

    private List<Move> toMovePair(Move one, Move two) {
        return Lists.newArrayList(one, two);
    }

    private List<List<Move>> toMovePairList(Move... moves) {
        List<List<Move>> result = Lists.newArrayList();

        for (int i = 0; i < moves.length; i += 2) {
            result.add(toMovePair(moves[i], moves[i + 1]));
        }

        return result;
    }
}
