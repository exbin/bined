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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.command.BinaryDataCommand;
import org.exbin.bined.operation.command.BinaryDataCommandType;
import org.exbin.bined.operation.command.BinaryDataUndoableCommand;
import org.exbin.bined.operation.BinaryDataUndoRedoChangeListener;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.framework.operation.api.Command;
import org.exbin.framework.operation.api.CommandType;
import org.exbin.framework.operation.undo.api.UndoRedoChangeListener;
import org.exbin.framework.operation.undo.api.UndoableCommand;
import org.exbin.framework.operation.undo.api.UndoRedo;

/**
 * Undo redo wrapper.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UndoRedoWrapper implements UndoRedo {

    private BinaryDataUndoRedo undoRedo;
    private final Map<UndoRedoChangeListener, BinaryDataUndoRedoChangeListener> listenersMap = new HashMap<>();

    public UndoRedoWrapper() {
    }

    public void setUndoRedo(@Nullable BinaryDataUndoRedo undoRedo) {
        if (this.undoRedo != null) {
            for (BinaryDataUndoRedoChangeListener listener : listenersMap.values()) {
                this.undoRedo.removeChangeListener(listener);
            }
        }

        this.undoRedo = undoRedo;
        for (BinaryDataUndoRedoChangeListener listener : listenersMap.values()) {
            this.undoRedo.addChangeListener(listener);
        }
    }

    @Nullable
    public BinaryDataUndoRedo getUndoRedo() {
        return undoRedo;
    }

    @Override
    public boolean canRedo() {
        return undoRedo != null ? undoRedo.canRedo() : false;
    }

    @Override
    public boolean canUndo() {
        return undoRedo != null ? undoRedo.canUndo() : false;
    }

    @Override
    public void clear() {
        undoRedo.clear();
    }

    @Override
    public void performSync() {
        undoRedo.performSync();
    }

    @Override
    public void execute(Command command) {
        undoRedo.execute(new BinaryCommandWrapper(command));
    }

    @Nonnull
    @Override
    public List<Command> getCommandList() {
        List<Command> result = new ArrayList<>();
        if (undoRedo != null) {
            undoRedo.getCommandList().forEach((command) -> {
                result.add(new CommandWrapper(command));
            });
        }

        return result;
    }

    @Nonnull
    @Override
    public Optional<Command> getTopUndoCommand() {
        Optional<BinaryDataCommand> topUndoCommand = undoRedo.getTopUndoCommand();
        if (topUndoCommand.isPresent()) {
            return Optional.of(new CommandWrapper(topUndoCommand.get()));
        }

        return Optional.empty();
    }

    @Override
    public int getCommandsCount() {
        return undoRedo.getCommandsCount();
    }

    @Override
    public boolean isModified() {
        return undoRedo.isModified();
    }

    @Override
    public int getCommandPosition() {
        return undoRedo != null ? undoRedo.getCommandPosition() : 0;
    }

    @Override
    public int getSyncPosition() {
        return undoRedo != null ? undoRedo.getSyncPosition() : 0;
    }

    @Override
    public void performRedo() {
        undoRedo.performRedo();
    }

    @Override
    public void performRedo(int count) {
        undoRedo.performRedo(count);
    }

    @Override
    public void performUndo() {
        undoRedo.performUndo();
    }

    @Override
    public void performUndo(int i) {
        undoRedo.performUndo(i);
    }

    @Override
    public void setSyncPosition(int position) {
        undoRedo.setSyncPosition(position);
    }

    @Override
    public void setSyncPosition() {
        undoRedo.setSyncPosition();
    }

    @Override
    public void addChangeListener(final UndoRedoChangeListener listener) {
        BinaryDataUndoRedoChangeListener changeListener = listener::undoChanged;
        listenersMap.put(listener, changeListener);
        if (undoRedo != null) {
            undoRedo.addChangeListener(changeListener);
        }
    }

    @Override
    public void removeChangeListener(UndoRedoChangeListener listener) {
        BinaryDataUndoRedoChangeListener changeListener = listenersMap.remove(listener);
        if (undoRedo != null) {
            undoRedo.removeChangeListener(changeListener);
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
        public CommandType getType() {
            return new BinaryCommandWrapperType(command.getType());
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

        private final BinaryDataUndoableCommand command;

        public UndoableCommandWrapper(BinaryDataUndoableCommand command) {
            this.command = command;
        }

        @Nonnull
        @Override
        public CommandType getType() {
            return new BinaryCommandWrapperType(command.getType());
        }

        @Override
        public void execute() {
            command.execute();
        }

        @Override
        public void redo() {
            command.redo();
        }

        @Override
        public void undo() {
            command.undo();
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
        public BinaryDataCommandType getType() {
            return ((BinaryCommandWrapperType) command.getType()).getCommandType();
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
    public static class BinaryCommandWrapperType implements CommandType {

        private final BinaryDataCommandType commandType;

        public BinaryCommandWrapperType(BinaryDataCommandType commandType) {
            this.commandType = commandType;
        }

        @Nonnull
        public BinaryDataCommandType getCommandType() {
            return commandType;
        }
    }
}
