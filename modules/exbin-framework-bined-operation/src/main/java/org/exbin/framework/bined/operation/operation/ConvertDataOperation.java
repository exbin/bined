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
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.operation.swing.CodeAreaOperation;
import org.exbin.bined.operation.swing.CodeAreaOperationType;
import org.exbin.bined.operation.swing.RemoveDataOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.CodeAreaCommandType;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Operation to convert selection or all data into provided data.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ConvertDataOperation extends CodeAreaOperation {

    private final long startPosition;
    private final long length;
    private final long convertedDataLength;
    private final ConversionDataProvider conversionDataProvider;

    public ConvertDataOperation(CodeAreaCore codeArea, long startPosition, long length, long convertedDataLength, ConversionDataProvider conversionDataProvider) {
        super(codeArea);
        this.startPosition = startPosition;
        this.length = length;
        this.convertedDataLength = convertedDataLength;
        this.conversionDataProvider = conversionDataProvider;
    }

    @Nonnull
    @Override
    public CodeAreaOperationType getType() {
        return CodeAreaOperationType.MODIFY_DATA;
    }

    @Nullable
    @Override
    protected CodeAreaOperation execute(ExecutionType executionType) {
        CodeAreaOperation undoOperation = null;
        EditableBinaryData contentData = (EditableBinaryData) codeArea.getContentData();

        CodeAreaOperation originalDataUndoOperation = null;

        if (executionType == ExecutionType.WITH_UNDO) {
            originalDataUndoOperation = new org.exbin.bined.operation.swing.InsertDataOperation(codeArea, startPosition, 0, contentData.copy(startPosition, length));
            undoOperation = new CompoundCodeAreaOperation(codeArea);
            ((CompoundCodeAreaOperation) undoOperation).addOperation(new RemoveDataOperation(codeArea, startPosition, 0, convertedDataLength));
            ((CompoundCodeAreaOperation) undoOperation).addOperation(originalDataUndoOperation);
        }

        conversionDataProvider.provideData(contentData, startPosition, length, startPosition + length);
        contentData.remove(startPosition, length);

        ((CaretCapable) codeArea).getCaret().setCaretPosition(startPosition + convertedDataLength, 0);

        ((SelectionCapable) codeArea).setSelection(startPosition, convertedDataLength);
        return undoOperation;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @ParametersAreNonnullByDefault
    public static class ConvertDataCommand extends CodeAreaCommand {

        private final ConvertDataOperation operation;
        private CodeAreaOperation undoOperation;

        public ConvertDataCommand(ConvertDataOperation operation) {
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
