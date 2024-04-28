/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.bined;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.BinaryDataCommandSequenceListener;
import org.exbin.bined.operation.undo.BinaryDataUndoableCommandSequence;
import org.exbin.framework.operation.api.Command;
import org.exbin.framework.operation.api.CommandSequenceListener;
import org.exbin.framework.operation.undo.api.UndoableCommand;
import org.exbin.framework.operation.undo.api.UndoableCommandSequence;

/**
 * Undo handler wrapper.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoHandlerWrapper implements UndoableCommandSequence {

    private BinaryDataUndoableCommandSequence handler;
    private final Map<CommandSequenceListener, BinaryDataCommandSequenceListener> listenersMap = new HashMap<>();

    public UndoHandlerWrapper() {
    }

    public void setHandler(@Nullable BinaryDataUndoableCommandSequence newHandler) {
        if (handler != null) {
            for (BinaryDataCommandSequenceListener listener : listenersMap.values()) {
                handler.removeCommandSequenceListener(listener);
            }
        }

        this.handler = newHandler;
        for (BinaryDataCommandSequenceListener listener : listenersMap.values()) {
            handler.addCommandSequenceListener(listener);
        }
    }

    @Override
    public boolean canRedo() {
        return handler != null ? handler.canRedo() : false;
    }

    @Override
    public boolean canUndo() {
        return handler != null ? handler.canUndo() : false;
    }

    @Override
    public void clear() {
        handler.clear();
    }

    @Override
    public void performSync() {
        handler.performSync();
    }

    @Override
    public void execute(Command command) {
        handler.execute(new BinaryCommandWrapper(command));
    }

    @Override
    public void schedule(Command command) {
        handler.schedule(new BinaryCommandWrapper(command));
    }

    @Override
    public void executeScheduled(int count) {
        handler.executeScheduled(count);
    }

    @Nonnull
    @Override
    public List<Command> getCommandList() {
        List<Command> result = new ArrayList<>();
        if (handler != null) {
            handler.getCommandList().forEach((command) -> {
                result.add(new CommandWrapper(command));
            });
        }

        return result;
    }

    @Override
    public long getCommandPosition() {
        return handler != null ? handler.getCommandPosition() : 0;
    }

    @Override
    public long getSyncPosition() {
        return handler != null ? handler.getSyncPosition() : 0;
    }

    @Override
    public void performRedo() {
        handler.performRedo();
    }

    public void performRedo(int count) {
        handler.performRedo(count);
    }

    @Override
    public void performUndo() {
        handler.performUndo();
    }

    public void performUndo(int i) {
        handler.performUndo(i);
    }

    @Override
    public void setSyncPosition(long position) {
        handler.setSyncPosition(position);
    }

    @Override
    public void setSyncPosition() {
        handler.setSyncPosition();
    }

    @Override
    public void addCommandSequenceListener(final CommandSequenceListener listener) {
        BinaryDataCommandSequenceListener binaryListener = listener::sequenceChanged;
        listenersMap.put(listener, binaryListener);
        if (handler != null) {
            handler.addCommandSequenceListener(binaryListener);
        }
    }

    @Override
    public void removeCommandSequenceListener(CommandSequenceListener listener) {
        BinaryDataCommandSequenceListener binaryListener = listenersMap.remove(listener);
        if (handler != null) {
            handler.removeCommandSequenceListener(binaryListener);
        }
    }

    @ParametersAreNonnullByDefault
    private static class CommandWrapper implements Command {

        private final BinaryDataCommand command;

        public CommandWrapper(BinaryDataCommand command) {
            this.command = command;
        }

        @Nonnull
        @Override
        public String getName() {
            return command.getName();
        }

        @Override
        public void execute() {
            command.execute();
        }

        @Override
        public void dispose() {
            command.dispose();
        }
    }

    @ParametersAreNonnullByDefault
    private static class UndoableCommandWrapper implements UndoableCommand {

        private final BinaryDataCommand command;

        public UndoableCommandWrapper(BinaryDataCommand command) {
            this.command = command;
        }

        @Nonnull
        @Override
        public String getName() {
            return command.getName();
        }

        @Override
        public void execute() {
            command.execute();
        }

        public void redo() {
            command.redo();
        }

        public void undo() {
            command.undo();
        }

        public boolean canUndo() {
            return command.canUndo();
        }

        @Override
        public void dispose() {
            command.dispose();
        }
    }

    @ParametersAreNonnullByDefault
    private static class BinaryCommandWrapper implements BinaryDataCommand {

        private final Command command;

        public BinaryCommandWrapper(Command command) {
            this.command = command;
        }

        @Nonnull
        @Override
        public String getName() {
            return command.getName();
        }

        @Override
        public void execute() {
            command.execute();
        }

        @Override
        public void redo() {
            throw new IllegalStateException();
//            command.redo();
        }

        @Override
        public void undo() {
            throw new IllegalStateException();
//            command.undo();
        }

        @Override
        public boolean canUndo() {
            return false;
//            return command.canUndo();
        }

        @Override
        public void dispose() {
            command.dispose();
        }
    }
}
