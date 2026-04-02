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
package org.exbin.bined.jaguif.viewer;

import java.awt.event.MouseEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.jaguif.BinEdDataComponent;
import org.exbin.bined.jaguif.BinaryFileDocument;
import org.exbin.bined.jaguif.BinaryStatus;
import org.exbin.bined.jaguif.BinaryStatusApi;
import org.exbin.bined.jaguif.FileProcessingMode;
import org.exbin.bined.jaguif.action.GoToPositionAction;
import org.exbin.bined.jaguif.gui.BinaryStatusPanel;
import org.exbin.jaguif.docking.api.DocumentDocking;
import org.exbin.jaguif.text.encoding.EncodingsManager;

/**
 * Binary status controller.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController, BinaryStatusPanel.MemoryModeController {

    protected final BinaryStatus binaryStatus;
    protected final EncodingsManager encodingsManager;

    public BinaryStatusController(BinaryStatus binaryStatus, EncodingsManager encodingsManager) {
        this.binaryStatus = binaryStatus;
        this.encodingsManager = encodingsManager;
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
