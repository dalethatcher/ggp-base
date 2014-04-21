package com.dalethatcher.ggp;

import com.google.common.collect.Lists;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

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
    private Role roleA;
    @Mock
    private Role roleB;
    @Mock
    private Move firstMove;
    @Mock
    private Move noopMove;

    @Before
    public void setUpMocks() throws Exception {
        when(stateMachine.getRoles()).thenReturn(Lists.newArrayList(roleA, roleB));
        when(stateMachine.isTerminal(rootState)).thenReturn(false);
        List<List<Move>> firstMovePairList = singleMovePair(firstMove, noopMove);
        when(stateMachine.getLegalJointMoves(rootState)).thenReturn(firstMovePairList);

        when(stateMachine.getNextState(rootState, firstMovePairList.get(0))).thenReturn(childState);
        when(stateMachine.isTerminal(childState)).thenReturn(true);
    }

    @Test
    public void onlyExpandsNodesWhenRequested() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        root.calculatePossibleMoves(stateMachine);

        verify(stateMachine).getLegalJointMoves(rootState);
        verify(stateMachine, never()).getLegalJointMoves(childState);
    }

    @Test
    public void onlyExpandsNodeOnce() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        root.calculatePossibleMoves(stateMachine);
        root.calculatePossibleMoves(stateMachine);

        verify(stateMachine).getLegalJointMoves(rootState);
        verify(stateMachine, never()).getLegalJointMoves(childState);
    }

    @Test
    public void canExpandMove() throws Exception {
        DelayedExpansionTreeNode root = new DelayedExpansionTreeNode(rootState);
        root.calculatePossibleMoves(stateMachine);

        List<Move> possibleMove = root.getPossibleMoves().get(0);
        DelayedExpansionTreeNode childNode = root.expandMove(stateMachine, possibleMove);

        assertThat(childNode, notNullValue());
        verify(stateMachine).getLegalJointMoves(rootState);
        verify(stateMachine).getNextState(rootState, possibleMove);
    }

    private List<List<Move>> singleMovePair(Move... moves) {
        List<List<Move>> result = Lists.newArrayList();
        result.add(Lists.newArrayList(moves));

        return result;
    }
}
