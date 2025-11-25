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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Binary status.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryStatus {
    
    protected BinaryStatusPanel binaryStatusPanel;

    public void setBinaryStatusPanel(BinaryStatusPanel binaryStatusPanel) {
        this.binaryStatusPanel = binaryStatusPanel;
    }

    public void attachCodeArea(BinEdDataComponent binaryComponent) {
        SectCodeArea codeArea = (SectCodeArea) binaryComponent.getCodeArea();
        codeArea.addDataChangedListener(() -> {
            if (binaryComponent == getActiveComponent()) {
                // ((BinEdFileHandler) activeFile).getComponent().notifyDataChanged();
                updateCurrentDocumentSize();
            }
        });

        codeArea.addSelectionChangedListener(() -> {
            if (binaryComponent == getActiveComponent()) {
                updateCurrentSelectionRange();
                // updateClipboardActionsStatus();
            }
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            if (binaryComponent == getActiveComponent()) {
                updateCurrentCaretPosition();
            }
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            if (binaryComponent == getActiveComponent() && binaryStatusPanel != null) {
                binaryStatusPanel.setEditMode(mode, operation);
            }
        });
    }

    public void setBinaryStatusController(BinaryStatusPanel.Controller binaryStatusController) {
        binaryStatusPanel.setController(binaryStatusController);
    }

    // TODO
    public void updateStatus() {
        updateCurrentDocumentSize();
        updateCurrentCaretPosition();
        updateCurrentSelectionRange();
        updateCurrentMemoryMode();
        updateCurrentEditMode();
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatusPanel == null) {
            return;
        }

        BinEdDataComponent dataComponent = getActiveComponent();
        if (dataComponent != null) {
            CodeAreaCore codeArea = dataComponent.getCodeArea();
            long documentOriginalSize = 0; // TODO ((BinEdFileHandler) activeFile).getDocumentOriginalSize();
            long dataSize = codeArea.getDataSize();
            binaryStatusPanel.setCurrentDocumentSize(dataSize, documentOriginalSize);
        }
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatusPanel == null) {
            return;
        }

        BinEdDataComponent dataComponent = getActiveComponent();
        if (dataComponent != null) {
            CodeAreaCore codeArea = dataComponent.getCodeArea();
            CodeAreaCaretPosition caretPosition = ((CaretCapable) codeArea).getActiveCaretPosition();
            binaryStatusPanel.setCursorPosition(caretPosition);
        }
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatusPanel == null) {
            return;
        }

        BinEdDataComponent dataComponent = getActiveComponent();
        if (dataComponent != null) {
            CodeAreaCore codeArea = dataComponent.getCodeArea();
            SelectionRange selectionRange = ((SelectionCapable) codeArea).getSelection();
            binaryStatusPanel.setSelectionRange(selectionRange);
        }
    }

    private void updateCurrentMemoryMode() {
        if (binaryStatusPanel == null) {
            return;
        }

        BinEdDataComponent dataComponent = getActiveComponent();
        if (dataComponent != null) {
            CodeAreaCore codeArea = dataComponent.getCodeArea();
            BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
            if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
                newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
            } else if (codeArea.getContentData() instanceof DeltaDocument) {
                newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
            }

            binaryStatusPanel.setMemoryMode(newMemoryMode);
        }
    }

    private void updateCurrentEditMode() {
        if (binaryStatusPanel == null) {
            return;
        }

        BinEdDataComponent dataComponent = getActiveComponent();
        if (dataComponent != null) {
            CodeAreaCore codeArea = dataComponent.getCodeArea();
            binaryStatusPanel.setEditMode(((EditModeCapable) codeArea).getEditMode(), ((EditModeCapable) codeArea).getActiveOperation());
        }
    }

    @Nullable
    private BinEdDataComponent getActiveComponent() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement contextManager = frameModule.getFrameHandler().getContextManager();
        ContextComponent component = contextManager.getActiveState(ContextComponent.class);
        return component instanceof BinEdDataComponent ? (BinEdDataComponent) component : null;
    }
}
