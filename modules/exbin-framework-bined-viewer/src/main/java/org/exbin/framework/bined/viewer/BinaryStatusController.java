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
package org.exbin.framework.bined.viewer;

import java.awt.event.MouseEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.framework.bined.BinEdDataComponent;
import org.exbin.framework.bined.BinaryFileDocument;
import org.exbin.framework.bined.BinaryStatus;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileProcessingMode;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.text.encoding.EncodingsManager;

/**
 * Binary status controller.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController, BinaryStatusPanel.MemoryModeController {

    protected final BinaryStatus binaryStatus;
    protected final EncodingsManager encodingsManager;

    public BinaryStatusController(BinaryStatus binaryStatus, EncodingsManager encodingsHandler) {
        this.binaryStatus = binaryStatus;
        this.encodingsManager = encodingsHandler;
    }

    @Override
    public void changeEditOperation(EditOperation editOperation) {
        BinEdDataComponent activeComponent = binaryStatus.getActiveComponent();
        if (activeComponent != null) {
            ((EditModeCapable) activeComponent.getCodeArea()).setEditOperation(editOperation);
        }
    }

    @Override
    public void changeCursorPosition() {
        BinEdDataComponent activeComponent = binaryStatus.getActiveComponent();
        if (activeComponent != null) {
            GoToPositionAction action = new GoToPositionAction();
            action.setCodeArea(activeComponent.getCodeArea());
            action.actionPerformed(null);
        }
    }

    @Override
    public void cycleNextEncoding() {
        if (encodingsManager != null) {
            encodingsManager.cycleNextEncoding();
        }
    }

    @Override
    public void cyclePreviousEncoding() {
        if (encodingsManager != null) {
            encodingsManager.cyclePreviousEncoding();
        }
    }

    @Override
    public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
        if (encodingsManager != null) {
            encodingsManager.popupEncodingsMenu(mouseEvent);
        }
    }

    @Override
    public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
        // TODO Rename memory mode to processing mode
        BinaryFileDocument activeDocument = binaryStatus.getActiveDocument();
        if (activeDocument != null) {
            FileProcessingMode fileProcessingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileProcessingMode.DELTA : FileProcessingMode.MEMORY;
            FileProcessingMode currentProcessingMode = activeDocument.getFileProcessingMode();
            if (fileProcessingMode != currentProcessingMode) {
                DocumentDocking activeDocking = binaryStatus.getActiveDocking();
                if (activeDocking.releaseDocument(activeDocument)) {
                    activeDocument.loadContent(fileProcessingMode);
                    binaryStatus.updateStatus();
                }
            }
        }
    }
}
