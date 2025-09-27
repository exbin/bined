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
import org.exbin.auxiliary.binary_data.array.paged.ByteArrayPagedData;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.bined.operation.swing.BasicBinaryDataOperationType;
import org.exbin.bined.operation.swing.ModifyDataOperation;
import org.exbin.bined.operation.swing.RemoveDataOperation;
import org.exbin.bined.operation.BinaryDataUndoableOperation;

/**
 * Replace data operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ReplaceDataOperation implements BinaryDataUndoableOperation {

    protected final long position;
    protected final long length;
    protected final InsertionDataProvider dataOperationDataProvider;

    public ReplaceDataOperation(long position, long length, InsertionDataProvider dataOperationDataProvider) {
        this.position = position;
        this.length = length;
        this.dataOperationDataProvider = dataOperationDataProvider;
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
        long dataSize = contentData.getDataSize();
        if (position > dataSize) {
            throw new IllegalStateException("Unable to replace data outside of document");
        }

        BinaryDataUndoableOperation undoOperation = null;

        if (position == dataSize) {
            if (withUndo) {
                undoOperation = new RemoveDataOperation(position, 0, length);
            } 
            contentData.insertUninitialized(dataSize, length);
        } else if (position + length > dataSize) {
            long diff = position + length - dataSize;
            if (withUndo) {
                // TODO use copy directly once delta is fixed
                PagedData origData = new ByteArrayPagedData();
                origData.insert(0, contentData.copy(position, length - diff));
                undoOperation = new CompoundCodeAreaOperation();
                ((CompoundCodeAreaOperation) undoOperation).addOperation(new ModifyDataOperation(position, origData));
                ((CompoundCodeAreaOperation) undoOperation).addOperation(new RemoveDataOperation(dataSize, 0, diff));
            }

            contentData.insertUninitialized(dataSize, diff);
        } else if (withUndo) {
            // TODO use copy directly once delta is fixed
            PagedData origData = new ByteArrayPagedData();
            origData.insert(0, contentData.copy(position, length));
            undoOperation = new ModifyDataOperation(position, origData);
        }

        dataOperationDataProvider.provideData(contentData, position);

        return undoOperation;
    }

    @Override
    public void dispose() {
    }
}
