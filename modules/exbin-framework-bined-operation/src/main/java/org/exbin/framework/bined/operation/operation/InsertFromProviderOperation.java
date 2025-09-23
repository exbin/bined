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
package org.exbin.framework.bined.operation.operation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationType;
import org.exbin.bined.operation.swing.RemoveDataOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.CodeAreaCommandType;
import org.exbin.bined.operation.undo.BinaryDataUndoableOperation;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Insert data operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InsertFromProviderOperation implements BinaryDataUndoableOperation {

    protected final long position;
    protected final long length;
    protected final InsertionDataProvider dataOperationDataProvider;

    public InsertFromProviderOperation(long position, long length, InsertionDataProvider dataOperationDataProvider) {
        this.position = position;
        this.length = length;
        this.dataOperationDataProvider = dataOperationDataProvider;
    }

    @Nonnull
    @Override
    public CodeAreaOperationType getType() {
        return CodeAreaOperationType.INSERT_DATA;
    }

    @Override
    public void execute(EditableBinaryData contentData) {
        execute(contentData, false);
    }

    @Nonnull
    @Override
    public BinaryDataUndoableOperation executeWithUndo(EditableBinaryData contentData) {
        return execute(contentData, true);
    }

    private BinaryDataUndoableOperation execute(EditableBinaryData contentData, boolean withUndo) {
        BinaryDataUndoableOperation undoOperation = null;

        contentData.insertUninitialized(position, length);
        dataOperationDataProvider.provideData(contentData, position);

        if (withUndo) {
            undoOperation = new RemoveDataOperation(position, 0, length);
        }
        return undoOperation;
    }

    @Override
    public void dispose() {
    }

    @ParametersAreNonnullByDefault
    public static class InsertDataCommand extends CodeAreaCommand {

        protected final InsertFromProviderOperation operation;
        protected BinaryDataUndoableOperation undoOperation;

        public InsertDataCommand(CodeAreaCore codeArea, InsertFromProviderOperation operation) {
            super(codeArea);
            this.operation = operation;
        }

        @Nonnull
        @Override
        public CodeAreaCommandType getType() {
            return CodeAreaCommandType.DATA_INSERTED;
        }

        @Override
        public void execute() {
            undoOperation = (BinaryDataUndoableOperation) operation.executeWithUndo(((EditableBinaryData) codeArea.getContentData()));
            ((ScrollingCapable) codeArea).revealCursor();
            codeArea.notifyDataChanged();
        }

        @Override
        public void undo() {
            undoOperation.execute(((EditableBinaryData) codeArea.getContentData()));
            undoOperation.dispose();
            ((ScrollingCapable) codeArea).revealCursor();
            codeArea.notifyDataChanged();
        }

        @Override
        public void dispose() {
            super.dispose();
            operation.dispose();
        }
    }
}
