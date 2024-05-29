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

import java.awt.Component;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.operation.undo.api.UndoRedoFileHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.editor.DefaultMultiEditorProvider;
import org.exbin.framework.editor.api.EditorFileHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.operation.undo.api.EmptyUndoRedo;
import org.exbin.framework.operation.undo.api.UndoRedo;

/**
 * Binary editor provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryMultiEditorProvider extends DefaultMultiEditorProvider implements BinEdEditorProvider, UndoRedoFileHandler {
    
    private FileHandlingMode defaultFileHandlingMode = FileHandlingMode.MEMORY;

    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;
    private JPopupMenu codeAreaPopupMenu;
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private ComponentActivationListener componentActivationListener;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi textEncodingStatusApi;
    private UndoRedo undoRedo = null;

    public BinaryMultiEditorProvider() {
        init();
    }

    private void init() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener((FlavorEvent e) -> {
            updateClipboardActionsStatus();
        });
        multiEditorPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    Object transferData = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    List<?> droppedFiles = (List) transferData;
                    for (Object file : droppedFiles) {
                        openFile(((File) file).toURI(), null);
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(BinaryMultiEditorProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        componentActivationListener.updated(EditorProvider.class, this);
    }

    @Override
    public void registerUndoHandler() {
        // TODO
        undoRedo = new EmptyUndoRedo();
        if (activeFile != null) {
            ((BinEdFileHandler) activeFile).registerUndoHandler();
        }
    }

    @Override
    public void activeFileChanged() {
        super.activeFileChanged();
        if (componentActivationListener != null) {
            componentActivationListener.updated(FileHandler.class, activeFile);
            componentActivationListener.updated(CodeAreaCore.class, activeFile instanceof BinEdFileHandler ? ((BinEdFileHandler) activeFile).getCodeArea() : null);
            if (activeFile instanceof EditorFileHandler) {
                ((EditorFileHandler) activeFile).componentActivated(componentActivationListener);
            }
        }

        if (undoRedo != null) {
            // TODO
            // undoRedo.setActiveFile(activeFile);
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

    public void setDefaultFileHandlingMode(FileHandlingMode defaultFileHandlingMode) {
        this.defaultFileHandlingMode = defaultFileHandlingMode;
    }

    @Nonnull
    @Override
    public String getNewFileTitlePrefix() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        return binedModule.getNewFileTitlePrefix();
    }
    
    @Nonnull
    @Override
    public BinEdFileHandler createFileHandler(int id) {
        BinEdFileHandler fileHandler = new BinEdFileHandler(id);

        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.initFileHandler(fileHandler);
        if (undoRedo != null) {
            fileHandler.registerUndoHandler();
        }

        fileHandler.setNewData(defaultFileHandlingMode);
        fileHandler.getUndoRedo().addChangeListener(() -> {
            // TODO undoRedo.notifyUndoUpdate();
            updateCurrentDocumentSize();
            // notifyModified();
        });
        fileManager.initCommandHandler(fileHandler.getComponent());

        ExtCodeArea codeArea = fileHandler.getCodeArea();
        codeArea.addDataChangedListener(() -> {
            if (fileHandler == activeFile) {
                ((BinEdFileHandler) activeFile).getComponent().notifyDataChanged();
                if (editorModificationListener != null) {
                    editorModificationListener.modified();
                }
                updateCurrentDocumentSize();
            }
        });

        codeArea.addSelectionChangedListener(() -> {
            if (fileHandler == activeFile) {
                updateCurrentSelectionRange();
                updateClipboardActionsStatus();
            }
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            if (fileHandler == activeFile) {
                updateCurrentCaretPosition();
            }
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            if (fileHandler == activeFile && binaryStatus != null) {
                binaryStatus.setEditMode(mode, operation);
            }
        });

        attachFilePopupMenu(fileHandler);

        return fileHandler;
    }

    @Override
    public boolean canSave() {
        if (activeFile == null) {
            return false;
        }

        return ((BinEdFileHandler) activeFile).isSaveSupported() && ((BinEdFileHandler) activeFile).isEditable();
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

    @Nonnull
    @Override
    public UndoRedo getUndoRedo() {
        return undoRedo;
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
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        // TODO ((ClipboardActionsUpdater) actionModule.getClipboardActions()).updateClipboardActions();
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus == null) {
            return;
        }

        if (activeFile instanceof BinEdFileHandler) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile).getCodeArea();
            long documentOriginalSize = ((BinEdFileHandler) activeFile).getDocumentOriginalSize();
            long dataSize = codeArea.getDataSize();
            binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
        }
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus == null) {
            return;
        }

        if (activeFile instanceof BinEdFileHandler) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile).getCodeArea();
            CodeAreaCaretPosition caretPosition = codeArea.getCaretPosition();
            binaryStatus.setCursorPosition(caretPosition);
        }
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatus == null) {
            return;
        }

        if (activeFile instanceof BinEdFileHandler) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile).getCodeArea();
            SelectionRange selectionRange = codeArea.getSelection();
            binaryStatus.setSelectionRange(selectionRange);
        }
    }

    private void updateCurrentMemoryMode() {
        if (binaryStatus == null) {
            return;
        }

        if (activeFile instanceof BinEdFileHandler) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile).getCodeArea();
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

        if (activeFile instanceof BinEdFileHandler) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile).getCodeArea();
            binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
        }
    }

    private void updateCurrentEncoding() {
        if (textEncodingStatusApi == null) {
            return;
        }

        if (activeFile instanceof BinEdFileHandler) {
            textEncodingStatusApi.setEncoding(((BinEdFileHandler) activeFile).getCharset().name());
        }
    }
}
