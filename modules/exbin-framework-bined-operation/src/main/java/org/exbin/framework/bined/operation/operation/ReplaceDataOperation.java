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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.operation.swing.CodeAreaOperation;
import org.exbin.bined.operation.swing.CodeAreaOperationType;
import org.exbin.bined.operation.swing.ModifyDataOperation;
import org.exbin.bined.operation.swing.RemoveDataOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.CodeAreaCommandType;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Replace data operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ReplaceDataOperation extends CodeAreaOperation {

    private final long position;
    private final long length;
    private final InsertionDataProvider dataOperationDataProvider;

    public ReplaceDataOperation(CodeAreaCore codeArea, long position, long length, InsertionDataProvider dataOperationDataProvider) {
        super(codeArea);
        this.position = position;
        this.length = length;
        this.dataOperationDataProvider = dataOperationDataProvider;
    }

    @Nonnull
    @Override
    public CodeAreaOperationType getType() {
        return CodeAreaOperationType.MODIFY_DATA;
    }

    @Nullable
    @Override
    protected CodeAreaOperation execute(ExecutionType executionType) {
        long dataSize = codeArea.getDataSize();
        if (position > dataSize) {
            throw new IllegalStateException("Unable to replace data outside of document");
        }

        CodeAreaOperation undoOperation = null;
        EditableBinaryData contentData = (EditableBinaryData) codeArea.getContentData();

        if (position == dataSize) {
            if (executionType == ExecutionType.WITH_UNDO) {
                undoOperation = new RemoveDataOperation(codeArea, position, 0, length);
            } 
            contentData.insertUninitialized(dataSize, length);
        } else if (position + length > dataSize) {
            long diff = position + length - dataSize;
            if (executionType == ExecutionType.WITH_UNDO) {
                // TODO use copy directly once delta is fixed
                PagedData origData = new PagedData();
                origData.insert(0, contentData.copy(position, length - diff));
                undoOperation = new CompoundCodeAreaOperation(codeArea);
                ((CompoundCodeAreaOperation) undoOperation).addOperation(new ModifyDataOperation(codeArea, position, origData));
                ((CompoundCodeAreaOperation) undoOperation).addOperation(new RemoveDataOperation(codeArea, dataSize, 0, diff));
            }

            contentData.insertUninitialized(dataSize, diff);
        } else if (executionType == ExecutionType.WITH_UNDO) {
            // TODO use copy directly once delta is fixed
            PagedData origData = new PagedData();
            origData.insert(0, contentData.copy(position, length));
            undoOperation = new ModifyDataOperation(codeArea, position, origData);
        }

        dataOperationDataProvider.provideData(contentData, position);

        ((CaretCapable) codeArea).getCaret().setCaretPosition(position + length, 0);
        return undoOperation;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @ParametersAreNonnullByDefault
    public static class ReplaceDataCommand extends CodeAreaCommand {

        private final ReplaceDataOperation operation;
        private CodeAreaOperation undoOperation;

        public ReplaceDataCommand(ReplaceDataOperation operation) {
            super(operation.getCodeArea());
            this.operation = operation;
        }

        @Nonnull
        @Override
        public CodeAreaCommandType getType() {
            return CodeAreaCommandType.DATA_MODIFIED;
        }

        @Override
        public void execute() {
            undoOperation = operation.executeWithUndo();
            ((ScrollingCapable) codeArea).revealCursor();
            codeArea.notifyDataChanged();
        }

        @Override
        public void undo() {
            undoOperation.execute();
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
