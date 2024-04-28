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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.operation.swing.CodeAreaOperation;
import org.exbin.bined.operation.swing.CodeAreaOperationType;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Compound code area operation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompoundCodeAreaOperation extends CodeAreaOperation {

    private final List<CodeAreaOperation> operations = new ArrayList<>();

    public CompoundCodeAreaOperation(CodeAreaCore codeArea) {
        super(codeArea);
    }

    public CompoundCodeAreaOperation(CodeAreaCore codeArea, CodeAreaCaretPosition backPosition) {
        super(codeArea, backPosition);
    }

    @Nonnull
    @Override
    public CodeAreaOperationType getType() {
        return CodeAreaOperationType.MODIFY_DATA;
    }

    public void appendOperation(CodeAreaOperation operation) {
        operations.add(operation);
    }

    public void appendOperations(Collection<CodeAreaOperation> operations) {
        this.operations.addAll(operations);
    }

    @Nonnull
    public Collection<CodeAreaOperation> getOperations() {
        return operations;
    }

    public boolean isEmpty() {
        return operations.isEmpty();
    }

    /**
     * Main operation execution method.
     *
     * @param executionType if undo should be included
     * @return undo operation if requested
     */
    @Nullable
    @Override
    protected CodeAreaOperation execute(ExecutionType executionType) {
        CompoundCodeAreaOperation compoundUndoOperation = null;
        if (executionType == ExecutionType.WITH_UNDO) {
            compoundUndoOperation = new CompoundCodeAreaOperation(codeArea);
            List<CodeAreaOperation> undoOperations = new ArrayList<>();
            for (CodeAreaOperation operation : operations) {
                CodeAreaOperation undoOperation = operation.executeWithUndo();
                undoOperations.add(0, undoOperation);
            }
            compoundUndoOperation.appendOperations(undoOperations);
        } else {
            for (CodeAreaOperation operation : operations) {
                operation.execute();
            }
        }

        return compoundUndoOperation;
    }

    @Override
    public void dispose() {
        super.dispose();
        for (CodeAreaOperation operation : operations) {
            operation.dispose();
        }
    }
}
