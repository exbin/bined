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
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.operation.BinaryDataUndoableOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.CodeAreaCommandType;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.bined.operation.ReplaceDataOperation;

/**
 * Replace data command.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ReplaceDataCommand extends CodeAreaCommand {
    
    protected final ReplaceDataOperation operation;
    protected BinaryDataUndoableOperation undoOperation;

    public ReplaceDataCommand(CodeAreaCore codeArea, ReplaceDataOperation operation) {
        super(codeArea);
        this.operation = operation;
    }

    @Nonnull
    @Override
    public CodeAreaCommandType getType() {
        return CodeAreaCommandType.DATA_MODIFIED;
    }

    @Override
    public void performExecute() {
        undoOperation = operation.executeWithUndo((EditableBinaryData) codeArea.getContentData());
        ((ScrollingCapable) codeArea).revealCursor();
        codeArea.notifyDataChanged();
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
