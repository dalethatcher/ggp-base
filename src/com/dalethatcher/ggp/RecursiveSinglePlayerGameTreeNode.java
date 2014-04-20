package com.dalethatcher.ggp;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import java.util.Map;

public class RecursiveSinglePlayerGameTreeNode {
    private final MachineState state;
    private final Role role;
    private final int goal;
    private final Map<Move, RecursiveSinglePlayerGameTreeNode> children;

    public RecursiveSinglePlayerGameTreeNode(MachineState state, Role role, int goal) {
        this.state = state;
        this.role = role;
        this.goal = goal;
        this.children = Maps.newHashMap();
    }

    public void resolveTree(StateMachine stateMachine) {
        try {
            if (stateMachine.isTerminal(state)) {
                return;
            }

            for (Move move : stateMachine.getLegalMoves(state, role)) {
                MachineState newState = stateMachine.getNextState(state, Lists.newArrayList(move));
                int goal = goalOrZero(stateMachine, newState, role);
                RecursiveSinglePlayerGameTreeNode child = new RecursiveSinglePlayerGameTreeNode(newState, role, goal);
                child.resolveTree(stateMachine);

                children.put(move, child);
            }
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public Move bestKnownMove() {
        Move bestMove = null;
        int bestGoal = 0;

        for (Map.Entry<Move, RecursiveSinglePlayerGameTreeNode> entry : children.entrySet()) {
            int childGoal = entry.getValue().getMaxGoal();

            if (bestMove == null || childGoal > bestGoal) {
                bestMove = entry.getKey();
                bestGoal = childGoal;
            }
        }

        return bestMove;
    }

    private int getMaxGoal() {
        int maxFound = goal;

        for (RecursiveSinglePlayerGameTreeNode child : children.values()) {
            int childGoal = child.getMaxGoal();

            if (childGoal > maxFound) {
                maxFound = childGoal;
            }
        }

        return maxFound;
    }

    public static int goalOrZero(StateMachine stateMachine, MachineState state, Role role) {
        try {
            return stateMachine.getGoal(state, role);
        } catch (GoalDefinitionException e) {
            return 0;
        }
    }
}
