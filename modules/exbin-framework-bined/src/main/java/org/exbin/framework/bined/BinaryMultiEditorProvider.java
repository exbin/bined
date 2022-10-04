/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.bined;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.MultiEditorUndoHandler;
import org.exbin.framework.editor.action.CloseAllFileAction;
import org.exbin.framework.editor.action.CloseFileAction;
import org.exbin.framework.editor.action.CloseOtherFileAction;
import org.exbin.framework.editor.action.EditorActions;
import org.exbin.framework.editor.api.MultiEditorPopupMenu;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.editor.gui.MultiEditorPanel;
import org.exbin.framework.file.api.AllFileTypes;
import org.exbin.framework.file.api.FileActionsApi;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileTypes;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.operation.undo.api.UndoFileHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.utils.ClipboardActionsUpdater;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.file.api.FileModuleApi;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/10/31
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryMultiEditorProvider implements MultiEditorProvider, BinEdEditorProvider, UndoFileHandler {

    private final XBApplication application;
    private SegmentsRepository segmentsRepository;
    private FileTypes fileTypes;
    private final MultiEditorPanel multiEditorPanel = new MultiEditorPanel();
    private int lastIndex = 0;
    private int lastNewFileIndex = 0;
    private final Map<Integer, Integer> newFilesMap = new HashMap<>();
    private FileHandlingMode defaultFileHandlingMode = FileHandlingMode.MEMORY;
    private final List<ActiveFileChangeListener> activeFileChangeListeners = new ArrayList<>();

    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;
    private JPopupMenu codeAreaPopupMenu;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private EditorModificationListener editorModificationListener;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi textEncodingStatusApi;
    private MultiEditorUndoHandler undoHandler = new MultiEditorUndoHandler();
    private Optional<FileHandler> activeFileCache = Optional.empty();
    @Nullable
    private File lastUsedDirectory;

    public BinaryMultiEditorProvider(XBApplication application) {
        this.application = application;
        init();
    }

    private void init() {
        multiEditorPanel.setController(new MultiEditorPanel.Controller() {
            @Override
            public void activeIndexChanged(int index) {
                activeFileChanged();
            }

            @Override
            public void showPopupMenu(int index, Component component, int positionX, int positionY) {
                if (index < 0) {
                    return;
                }

                FileHandler fileHandler = multiEditorPanel.getFileHandler(index);
                EditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(EditorModuleApi.class);
                JPopupMenu fileTabPopupMenu = new EditorPopupMenu(fileHandler);
                CloseFileAction closeFileAction = (CloseFileAction) editorModule.getCloseFileAction();
                JMenuItem closeMenuItem = new JMenuItem(closeFileAction);
                fileTabPopupMenu.add(closeMenuItem);
                CloseAllFileAction closeAllFileAction = (CloseAllFileAction) editorModule.getCloseAllFileAction();
                JMenuItem closeAllMenuItem = new JMenuItem(closeAllFileAction);
                fileTabPopupMenu.add(closeAllMenuItem);
                CloseOtherFileAction closeOtherFileAction = (CloseOtherFileAction) editorModule.getCloseOtherFileAction();
                JMenuItem closeOtherMenuItem = new JMenuItem(closeOtherFileAction);
                fileTabPopupMenu.add(closeOtherMenuItem);
                fileTabPopupMenu.show(component, positionX, positionY);
            }
        });
        fileTypes = new AllFileTypes();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActionsStatus();
        });
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return activeFileCache;
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        this.editorModificationListener = editorModificationListener;
    }

    public void setDefaultFileHandlingMode(FileHandlingMode defaultFileHandlingMode) {
        this.defaultFileHandlingMode = defaultFileHandlingMode;
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        return activeFile == null ? "" : ((BinEdFileHandler) activeFile).getWindowTitle(parentTitle);
    }

    @Override
    public void newFile() {
        int fileIndex = ++lastIndex;
        newFilesMap.put(fileIndex, ++lastNewFileIndex);
        BinEdFileHandler newFile = createFileHandler(fileIndex);
        newFile.newFile();
        multiEditorPanel.addFileHandler(newFile, getName(newFile));
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        BinEdFileHandler file = createFileHandler(++lastIndex);
        file.loadFromFile(fileUri, fileType);
        multiEditorPanel.addFileHandler(file, file.getFileName().orElse(""));
    }

    @Nonnull
    private BinEdFileHandler createFileHandler(int id) {
        BinEdFileHandler fileHandler = new BinEdFileHandler(id);
        fileHandler.setApplication(application);
        fileHandler.setSegmentsRepository(segmentsRepository);
        fileHandler.setNewData(defaultFileHandlingMode);
        fileHandler.getUndoHandler().addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                undoHandler.notifyUndoUpdate();
                updateCurrentDocumentSize();
                // notifyModified();
            }

            @Override
            public void undoCommandAdded(Command cmnd) {
                undoHandler.notifyUndoCommandAdded(cmnd);
                updateCurrentDocumentSize();
                // notifyModified();
            }
        });
        ExtCodeArea codeArea = fileHandler.getCodeArea();
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, fileHandler.getCodeAreaUndoHandler());
        codeArea.setCommandHandler(commandHandler);

        codeArea.addDataChangedListener(() -> {
            if (fileHandler == activeFileCache.orElse(null)) {
                ((BinEdFileHandler) activeFileCache.get()).getComponent().notifyDataChanged();
                if (editorModificationListener != null) {
                    editorModificationListener.modified();
                }
                updateCurrentDocumentSize();
            }
        });

        codeArea.addSelectionChangedListener(() -> {
            if (fileHandler == activeFileCache.orElse(null)) {
                updateCurrentSelectionRange();
                updateClipboardActionsStatus();
            }
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            if (fileHandler == activeFileCache.orElse(null)) {
                updateCurrentCaretPosition();
            }
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            if (fileHandler == activeFileCache.orElse(null) && binaryStatus != null) {
                binaryStatus.setEditMode(mode, operation);
            }
        });

        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
        CodeAreaPopupMenuHandler normalCodeAreaPopupMenuHandler = binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.NORMAL);
        binedModule.initFileHandler(fileHandler);
        attachFilePopupMenu(fileHandler);
        fileHandler.getComponent().setCodeAreaPopupMenuHandler(normalCodeAreaPopupMenuHandler);

        return fileHandler;
    }

    @Override
    public void openFile() {
        FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
        FileActionsApi fileActions = fileModule.getFileActions();
        FileActionsApi.OpenFileResult openFileResult = fileActions.showOpenFileDialog(fileTypes, this);
        if (openFileResult.dialogResult == JFileChooser.APPROVE_OPTION) {
            openFile(CodeAreaUtils.requireNonNull(openFileResult.selectedFile).toURI(), openFileResult.fileType);
        }
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        URI fileUri = new URI(fileName);
        openFile(fileUri, null);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        openFile(fileUri, fileType);
    }

    @Override
    public void saveFile() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        saveFile(activeFile);
    }

    @Override
    public void saveFile(FileHandler fileHandler) {
        if (fileHandler.getFileUri().isPresent()) {
            ((BinEdFileHandler) fileHandler).saveFile();
        } else {
            saveAsFile(fileHandler);
        }
    }

    @Override
    public void saveAsFile() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            throw new IllegalStateException();
        }

        saveAsFile(activeFile);
    }

    @Override
    public void saveAsFile(FileHandler fileHandler) {
        FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
        fileModule.getFileActions().saveAsFile(fileHandler, fileTypes, this);
    }

    @Override
    public boolean canSave() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        if (activeFile == null) {
            return false;
        }

        return ((BinEdFileHandler) activeFile).isSaveSupported() && ((BinEdFileHandler) activeFile).isEditable();
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
        FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }

    private void activeFileChanged() {
        FileHandler activeFile = multiEditorPanel.getActiveFile();
        activeFileCache = Optional.ofNullable(activeFile);
        undoHandler.setActiveFile(activeFile);

        for (ActiveFileChangeListener listener : activeFileChangeListeners) {
            listener.activeFileChanged(activeFile);
        }

        if (clipboardActionsUpdateListener != null) {
            updateClipboardActionsStatus();
        }

        if (binaryStatus != null) {
            updateStatus();
        }

        if (textEncodingStatusApi != null) {
            updateCurrentEncoding();
        }
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseOtherFiles(null);
    }

    private boolean releaseOtherFiles(@Nullable FileHandler excludedFile) {
        int fileHandlersCount = multiEditorPanel.getFileHandlersCount();
        if (fileHandlersCount == 0) {
            return true;
        }

        if (fileHandlersCount == 1) {
            FileHandler activeFile = getActiveFile().get();
            return (activeFile == excludedFile) || releaseFile(activeFile);
        }

        List<FileHandler> modifiedFiles = new ArrayList<>();
        for (int i = 0; i < fileHandlersCount; i++) {
            FileHandler fileHandler = multiEditorPanel.getFileHandler(i);
            if (fileHandler.isModified() && fileHandler != excludedFile) {
                modifiedFiles.add(fileHandler);
            }
        }

        if (modifiedFiles.isEmpty()) {
            return true;
        }

        EditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(EditorModuleApi.class);
        EditorActions editorActions = (EditorActions) editorModule.getEditorActions();
        return editorActions.showAskForSaveDialog(modifiedFiles);
    }

    @Override
    public List<FileHandler> getFileHandlers() {
        List<FileHandler> fileHandlers = new ArrayList<>();
        for (int i = 0; i < multiEditorPanel.getFileHandlersCount(); i++) {
            fileHandlers.add(multiEditorPanel.getFileHandler(i));
        }
        return fileHandlers;
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler.isModified()) {
            FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, fileTypes, this);
        }

        return true;
    }

    @Nonnull
    @Override
    public String getName(FileHandler fileHandler) {
        Optional<String> fileName = fileHandler.getFileName();
        if (fileName.isPresent()) {
            return fileName.get();
        }

        return "New File " + newFilesMap.get(fileHandler.getId());
    }

    @Override
    public void closeFile() {
        if (!activeFileCache.isPresent()) {
            throw new IllegalStateException();
        }

        closeFile(activeFileCache.get());
    }

    @Override
    public void closeFile(FileHandler file) {
        if (releaseFile(file)) {
            multiEditorPanel.removeFileHandler(file);
            newFilesMap.remove(file.getId());
        }
    }

    @Override
    public void closeOtherFiles(FileHandler exceptHandler) {
        if (releaseOtherFiles(exceptHandler)) {
            multiEditorPanel.removeAllFileHandlersExceptFile(exceptHandler);
            int exceptionFileId = exceptHandler.getId();
            // I miss List.of()
            List<Integer> list = new ArrayList<>();
            list.add(exceptionFileId);
            newFilesMap.keySet().retainAll(list);
        }
    }

    @Override
    public void closeAllFiles() {
        if (releaseAllFiles()) {
            multiEditorPanel.removeAllFileHandlers();
            newFilesMap.clear();
        }
    }

    @Override
    public void saveAllFiles() {
        int fileHandlersCount = multiEditorPanel.getFileHandlersCount();
        if (fileHandlersCount == 0) {
            return;
        }

        List<FileHandler> modifiedFiles = new ArrayList<>();
        for (int i = 0; i < fileHandlersCount; i++) {
            FileHandler fileHandler = multiEditorPanel.getFileHandler(i);
            if (fileHandler.isModified()) {
                modifiedFiles.add(fileHandler);
            }
        }

        if (modifiedFiles.isEmpty()) {
            return;
        }

        EditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(EditorModuleApi.class);
        EditorActions editorActions = (EditorActions) editorModule.getEditorActions();
        editorActions.showAskForSaveDialog(modifiedFiles);
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        this.binaryStatus = binaryStatus;
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
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        this.textEncodingStatusApi = encodingStatus;
        updateCurrentEncoding();
    }

    public void setClipboardActionsUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        updateClipboardActionsStatus();
    }

    @Override
    public void addActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.add(listener);
    }

    @Override
    public void removeActiveFileChangeListener(ActiveFileChangeListener listener) {
        activeFileChangeListeners.remove(listener);
    }

    @Nonnull
    @Override
    public XBUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        this.codeAreaPopupMenuHandler = codeAreaPopupMenuHandler;
    }

    private void attachFilePopupMenu(BinEdFileHandler newFile) {
        if (codeAreaPopupMenu == null) {
            String popupMenuId = BinedModule.BINARY_POPUP_MENU_ID + ".multi";

            codeAreaPopupMenu = new JPopupMenu() {
                @Override
                public void show(Component invoker, int x, int y) {
                    if (codeAreaPopupMenuHandler == null || invoker == null) {
                        return;
                    }

                    int clickedX = x;
                    int clickedY = y;
                    if (invoker instanceof JViewport) {
                        clickedX += ((JViewport) invoker).getParent().getX();
                        clickedY += ((JViewport) invoker).getParent().getY();
                    }

                    ExtCodeArea codeArea = invoker instanceof ExtCodeArea ? (ExtCodeArea) invoker
                            : (ExtCodeArea) ((JViewport) invoker).getParent().getParent();

                    JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, clickedX, clickedY);
                    popupMenu.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                            codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                        }

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {
                        }
                    });
                    popupMenu.show(invoker, x, y);
                }
            };
        }
        newFile.getComponent().getCodeArea().setComponentPopupMenu(codeAreaPopupMenu);
    }

    private void updateClipboardActionsStatus() {
        if (clipboardActionsUpdateListener != null) {
            clipboardActionsUpdateListener.stateChanged();
        }
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        ((ClipboardActionsUpdater) actionModule.getClipboardActions()).updateClipboardActions();
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            long documentOriginalSize = ((BinEdFileHandler) activeFile.get()).getDocumentOriginalSize();
            long dataSize = codeArea.getDataSize();
            binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
        }
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            CodeAreaCaretPosition caretPosition = codeArea.getCaretPosition();
            binaryStatus.setCursorPosition(caretPosition);
        }
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            SelectionRange selectionRange = codeArea.getSelection();
            binaryStatus.setSelectionRange(selectionRange);
        }
    }

    private void updateCurrentMemoryMode() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
            if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
                newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
            } else if (codeArea.getContentData() instanceof DeltaDocument) {
                newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
            }

            binaryStatus.setMemoryMode(newMemoryMode);
        }
    }

    private void updateCurrentEditMode() {
        if (binaryStatus == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
        }
    }

    private void updateCurrentEncoding() {
        if (textEncodingStatusApi == null) {
            return;
        }

        Optional<FileHandler> activeFile = getActiveFile();
        if (activeFile.isPresent()) {
            textEncodingStatusApi.setEncoding(((BinEdFileHandler) activeFile.get()).getCharset().name());
        }
    }

    private class EditorPopupMenu extends JPopupMenu implements MultiEditorPopupMenu {

        @Nullable
        private final FileHandler selectedFile;

        public EditorPopupMenu(@Nullable FileHandler selectedFile) {
            super();
            this.selectedFile = selectedFile;
        }

        @Nonnull
        @Override
        public Optional<FileHandler> getSelectedFile() {
            return Optional.ofNullable(selectedFile);
        }
    }
}
