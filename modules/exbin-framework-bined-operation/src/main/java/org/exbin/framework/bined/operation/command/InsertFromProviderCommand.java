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
package org.exbin.framework.bined.operation.command;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.operation.BinaryDataUndoableOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.CodeAreaCommandType;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.bined.operation.InsertFromProviderOperation;

/**
 * Insert data from provider command.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InsertFromProviderCommand extends CodeAreaCommand {

    protected final InsertFromProviderOperation operation;
    protected BinaryDataUndoableOperation undoOperation;

    public InsertFromProviderCommand(CodeAreaCore codeArea, InsertFromProviderOperation operation) {
        super(codeArea);
        this.operation = operation;
    }

    @Nonnull
    @Override
    public CodeAreaCommandType getType() {
        return CodeAreaCommandType.DATA_INSERTED;
    }

    @Override
    public void performExecute() {
        undoOperation = (BinaryDataUndoableOperation) operation.executeWithUndo((EditableBinaryData) codeArea.getContentData());
    }

    @Override
    public void performUndo() {
        undoOperation.execute((EditableBinaryData) codeArea.getContentData());
        undoOperation.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        operation.dispose();
    }
}
