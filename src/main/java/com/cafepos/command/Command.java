package com.cafepos.command;

public interface Command {
    void execute();

    default void undo() {
        throw new UnsupportedOperationException("Undo not supported for this command.");
    }
}
