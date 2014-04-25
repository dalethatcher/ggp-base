package com.dalethatcher.ggp;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

@FunctionalInterface
public interface HeuristicGoalFunction {
    public int apply(StateMachine stateMachine, MachineState state, Role role);
}
