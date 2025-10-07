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
package org.exbin.framework.bined.operation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.operation.swing.BasicBinaryDataOperationType;
import org.exbin.bined.operation.swing.RemoveDataOperation;
import org.exbin.bined.operation.BinaryDataUndoableOperation;

/**
 * Operation to convert selection or all data into provided data.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ConvertDataOperation implements BinaryDataUndoableOperation {

    protected final long startPosition;
    protected final long length;
    protected final long convertedDataLength;
    protected final ConversionDataProvider conversionDataProvider;

    public ConvertDataOperation(long startPosition, long length, long convertedDataLength, ConversionDataProvider conversionDataProvider) {
        this.startPosition = startPosition;
        this.length = length;
        this.convertedDataLength = convertedDataLength;
        this.conversionDataProvider = conversionDataProvider;
    }

    @Nonnull
    @Override
    public BasicBinaryDataOperationType getType() {
        return BasicBinaryDataOperationType.MODIFY_DATA;
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

        BinaryDataUndoableOperation originalDataUndoOperation = null;

        if (withUndo) {
            originalDataUndoOperation = new org.exbin.bined.operation.swing.InsertDataOperation(startPosition, 0, contentData.copy(startPosition, length));
            undoOperation = new CompoundBinaryDataOperation();
            ((CompoundBinaryDataOperation) undoOperation).addOperation(new RemoveDataOperation(startPosition, 0, convertedDataLength));
            ((CompoundBinaryDataOperation) undoOperation).addOperation(originalDataUndoOperation);
        }

        conversionDataProvider.provideData(contentData, startPosition, length, startPosition + length);
        contentData.remove(startPosition, length);
        return undoOperation;
    }

    @Override
    public void dispose() {
    }
}
