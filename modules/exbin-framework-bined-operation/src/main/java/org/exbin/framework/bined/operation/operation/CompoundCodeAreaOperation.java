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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.operation.swing.BasicBinaryDataOperationType;
import org.exbin.bined.operation.undo.BinaryDataUndoableOperation;

/**
 * Compound code area operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompoundCodeAreaOperation implements BinaryDataUndoableOperation {

    protected final List<BinaryDataUndoableOperation> operations = new ArrayList<>();

    public CompoundCodeAreaOperation() {
        super();
    }

    @Nonnull
    @Override
    public BasicBinaryDataOperationType getType() {
        return BasicBinaryDataOperationType.MODIFY_DATA;
    }

    public void addOperation(BinaryDataUndoableOperation operation) {
        operations.add(operation);
    }

    public void addOperations(Collection<BinaryDataUndoableOperation> operations) {
        this.operations.addAll(operations);
    }

    @Nonnull
    public Collection<BinaryDataUndoableOperation> getOperations() {
        return operations;
    }

    public boolean isEmpty() {
        return operations.isEmpty();
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
        CompoundCodeAreaOperation compoundUndoOperation = null;
        if (withUndo) {
            compoundUndoOperation = new CompoundCodeAreaOperation();
            List<BinaryDataUndoableOperation> undoOperations = new ArrayList<>();
            for (BinaryDataUndoableOperation operation : operations) {
                BinaryDataUndoableOperation undoOperation = operation.executeWithUndo(contentData);
                undoOperations.add(0, undoOperation);
            }
            compoundUndoOperation.addOperations(undoOperations);
        } else {
            for (BinaryDataUndoableOperation operation : operations) {
                operation.execute(contentData);
            }
        }

        return compoundUndoOperation;
    }

    @Override
    public void dispose() {
        for (BinaryDataUndoableOperation operation : operations) {
            operation.dispose();
        }
    }
}
