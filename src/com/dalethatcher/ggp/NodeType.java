package com.dalethatcher.ggp;

enum NodeType {
    MAX, MIN;

    public NodeType opposite() {
        return (this == MAX) ? MIN : MAX;
    }
}
