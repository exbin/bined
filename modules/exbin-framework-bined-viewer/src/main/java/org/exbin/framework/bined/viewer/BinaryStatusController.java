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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.EditOperation;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.FileProcessingMode;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.text.encoding.EncodingsManager;

/**
 * Binary status controller.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController, BinaryStatusPanel.MemoryModeController {

    protected final EditorProvider editorProvider;
    protected final EncodingsManager encodingsHandler;

    public BinaryStatusController(EditorProvider editorProvider, EncodingsManager encodingsHandler) {
        this.editorProvider = editorProvider;
        this.encodingsHandler = encodingsHandler;
    }

    @Override
    public void changeEditOperation(EditOperation editOperation) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            ((BinEdFileHandler) activeFile.get()).getCodeArea().setEditOperation(editOperation);
        }
    }

    @Override
    public void changeCursorPosition() {
        GoToPositionAction action = new GoToPositionAction();
        action.setCodeArea(getActiveCodeArea());
        action.actionPerformed(null);
    }

    @Override
    public void cycleNextEncoding() {
        if (encodingsHandler != null) {
            encodingsHandler.cycleNextEncoding();
        }
    }

    @Override
    public void cyclePreviousEncoding() {
        if (encodingsHandler != null) {
            encodingsHandler.cyclePreviousEncoding();
        }
    }

    @Override
    public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
        if (encodingsHandler != null) {
            encodingsHandler.popupEncodingsMenu(mouseEvent);
        }
    }

    @Override
    public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
            FileProcessingMode fileHandlingMode = fileHandler.getFileHandlingMode();
            FileProcessingMode newHandlingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileProcessingMode.DELTA : FileProcessingMode.MEMORY;
            if (newHandlingMode != fileHandlingMode) {
                if (editorProvider.releaseFile(fileHandler)) {
                    fileHandler.switchFileHandlingMode(newHandlingMode);
                    // TODO preferences.getEditorOptions().setFileHandlingMode(newHandlingMode);
                }
                ((BinEdEditorProvider) editorProvider).updateStatus();
            }
        }
    }

    @Nonnull
    public SectCodeArea getActiveCodeArea() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        Optional<FileHandler> activeFile = binedModule.getEditorProvider().getActiveFile();
        if (activeFile.isPresent()) {
            return ((BinEdFileHandler) activeFile.get()).getComponent().getCodeArea();
        }
        throw new IllegalStateException("No active file");
    }
}
