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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.AllFileTypes;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.operation.undo.api.UndoFileHandler;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.editor.api.EditorFileHandler;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Binary editor provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorProvider implements EditorProvider, BinEdEditorProvider, UndoFileHandler {

    private BinEdFileHandler activeFile;
    private FileTypes fileTypes;
    @Nullable
    private File lastUsedDirectory;
    private BinaryStatusApi binaryStatus;
    private EditorModificationListener editorModificationListener;
    private ComponentActivationListener componentActivationListener;

    public BinaryEditorProvider(BinEdFileHandler activeFile) {
        init(activeFile);
    }

    private void init(BinEdFileHandler activeFile) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
        this.activeFile = activeFile;
        fileTypes = new AllFileTypes();

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActionsStatus();
        });
        activeFile.getComponent().setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    List<?> droppedFiles = (List) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (Object file : droppedFiles) {
                        openFile(((File) file).toURI(), null);
                        break;
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(BinaryEditorProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        componentActivationListener.updated(EditorProvider.class, this);
        activeFileChanged();
    }

    private void activeFileChanged() {
        CodeAreaCore codeArea = activeFile.getCodeArea();
        componentActivationListener.updated(FileHandler.class, activeFile);
        componentActivationListener.updated(CodeAreaCore.class, codeArea);
        if (activeFile instanceof EditorFileHandler) {
            ((EditorFileHandler) activeFile).componentActivated(componentActivationListener);
        }
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.ofNullable(activeFile);
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getEditorComponent() {
        return activeFile.getComponent();
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        return activeFile.getWindowTitle(parentTitle);
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        this.binaryStatus = binaryStatus;

        ExtCodeArea codeArea = getEditorComponent().getCodeArea();
        codeArea.addDataChangedListener(() -> {
            activeFile.getComponent().notifyDataChanged();
            if (editorModificationListener != null) {
                editorModificationListener.modified();
            }
            updateCurrentDocumentSize();
        });

        codeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(codeArea.getSelection());
            updateClipboardActionsStatus();
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            binaryStatus.setEditMode(mode, operation);
        });

        updateStatus();
    }

    @Override
    public void updateStatus() {
        updateCurrentDocumentSize();
        updateCurrentCaretPosition();
        updateCurrentSelectionRange();
        updateCurrentMemoryMode();
        updateCurrentEditMode();
    }

    @Override
    public void registerUndoHandler() {
        activeFile.registerUndoHandler();
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus == null) {
            return;
        }

        ExtCodeArea codeArea = activeFile.getCodeArea();
        long documentOriginalSize = activeFile.getDocumentOriginalSize();
        long dataSize = codeArea.getDataSize();
        binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus == null) {
            return;
        }

        ExtCodeArea codeArea = activeFile.getCodeArea();
        CodeAreaCaretPosition caretPosition = codeArea.getCaretPosition();
        binaryStatus.setCursorPosition(caretPosition);
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatus == null) {
            return;
        }

        ExtCodeArea codeArea = activeFile.getCodeArea();
        SelectionRange selectionRange = codeArea.getSelection();
        binaryStatus.setSelectionRange(selectionRange);
    }

    private void updateCurrentMemoryMode() {
        if (binaryStatus == null) {
            return;
        }

        ExtCodeArea codeArea = activeFile.getCodeArea();
        BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
            newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        binaryStatus.setMemoryMode(newMemoryMode);
    }

    private void updateCurrentEditMode() {
        if (binaryStatus == null) {
            return;
        }

        ExtCodeArea codeArea = activeFile.getCodeArea();
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        encodingStatus.setEncoding(activeFile.getCharset().name());
    }

    @Override
    public void newFile() {
        if (releaseAllFiles()) {
            activeFile.clearFile();
            BinedModule binedModule = App.getModule(BinedModule.class);
            String title = binedModule.getNewFileTitlePrefix() + " " + activeFile.getId();
            activeFile.setTitle(title);
        }
    }

    @Override
    public void openFile(URI fileUri, @Nullable FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public void openFile() {
        if (releaseAllFiles()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            fileModule.getFileActions().openFile(activeFile, fileTypes, this);
        }
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        URI fileUri = new URI(fileName);
        activeFile.loadFromFile(fileUri, null);
    }

    @Override
    public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public boolean canSave() {
        return activeFile.isSaveSupported() && activeFile.isEditable();
    }

    @Override
    public void saveFile() {
        if (activeFile.getFileUri().isPresent()) {
            activeFile.saveFile();
        } else {
            saveAsFile();
        }
    }

    @Override
    public void saveAsFile() {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.getFileActions().saveAsFile(activeFile, fileTypes, this);
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes, this);
        }

        return true;
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseFile(activeFile);
    }

    @Nonnull
    @Override
    public XBUndoHandler getUndoHandler() {
        return activeFile.getUndoHandler();
    }

    @Nonnull
    @Override
    public Optional<File> getLastUsedDirectory() {
        return Optional.ofNullable(lastUsedDirectory);
    }

    @Override
    public void setLastUsedDirectory(@Nullable File directory) {
        lastUsedDirectory = directory;
    }

    @Override
    public void updateRecentFilesList(URI fileUri, FileType fileType) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }

    private void updateClipboardActionsStatus() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        // TODO ((ClipboardActionsUpdater) actionModule.getClipboardActions()).updateClipboardActions();
    }
}
