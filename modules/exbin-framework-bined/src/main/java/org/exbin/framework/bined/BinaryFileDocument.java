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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.EmptyBinaryData;
import org.exbin.auxiliary.binary_data.array.paged.ByteArrayPagedData;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.auxiliary.binary_data.delta.SegmentsRepository;
import org.exbin.auxiliary.binary_data.delta.file.FileDataSource;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.context.api.ContextComponentProvider;
import org.exbin.framework.document.api.ComponentDocument;
import org.exbin.framework.document.api.DocumentSource;
import org.exbin.framework.document.api.EditableDocument;
import org.exbin.framework.file.api.FileDocument;
import org.exbin.framework.file.api.FileDocumentSource;
import org.exbin.framework.operation.undo.api.UndoRedoState;
import org.exbin.framework.text.encoding.ContextEncoding;
import org.exbin.framework.text.font.ContextFont;

/**
 * BinEd binary document.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryFileDocument implements BinaryDocument, ComponentDocument, FileDocument, EditableDocument, ContextComponentProvider {

    protected BinEdDataComponent dataComponent = new BinEdDataComponent(new BinEdComponentPanel());
    protected URI fileUri = null;
    protected BinaryData binaryData;

    public BinaryFileDocument() {
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.ofNullable(fileUri);
    }

    @Nonnull
    @Override
    public BinaryData getBinaryData() {
        return binaryData;
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getComponent() {
        return (BinEdComponentPanel) dataComponent.getComponent();
    }

    @Nonnull
    public CodeAreaCore getCodeArea() {
        return dataComponent.getCodeArea();
    }

    @Nonnull
    public Optional<BinaryDataUndoRedo> getUndoHandler() {
        return getComponent().getUndoRedo();
    }

    public void setUndoHandler(BinaryDataUndoRedo undoHandler) {
        getComponent().setUndoRedo(undoHandler);
    }

    @Nonnull
    public BinaryData getContentData() {
        CodeAreaCore codeArea = dataComponent.getCodeArea();
        return codeArea.getContentData();
    }

    public void setContentData(BinaryData data) {
        CodeAreaCore codeArea = dataComponent.getCodeArea();
        codeArea.setContentData(data);
    }

    public void reloadFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFrom(DocumentSource documentSource) {
        if (!(documentSource instanceof FileDocumentSource)) {
            throw new UnsupportedOperationException();
        }

        BinEdComponentPanel componentPanel = getComponent();
        File file = ((FileDocumentSource) documentSource).getFile();
        if (!file.isFile()) {
            JOptionPane.showOptionDialog(componentPanel,
                    "File not found",
                    "Unable to load file",
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, null, null);
            return;
        }

        try {
            BinaryData oldData = componentPanel.getContentData();
            FileProcessingMode fileProcessingMode = FileProcessingMode.MEMORY;
            if (fileProcessingMode == FileProcessingMode.DELTA) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                SegmentsRepository segmentsRepository = binedModule.getFileManager().getSegmentsRepository();
                FileDataSource openFileSource = new FileDataSource(file);
                segmentsRepository.addDataSource(openFileSource);
                DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                componentPanel.setContentData(document);
                this.fileUri = fileUri;
                oldData.dispose();
            } else {
                try (FileInputStream fileStream = new FileInputStream(file)) {
                    BinaryData data = oldData;
                    if (!(data instanceof PagedData)) {
                        data = new ByteArrayPagedData();
                        oldData.dispose();
                    }
                    ((EditableBinaryData) data).loadFromStream(fileStream);
                    componentPanel.setContentData(data);
                    this.fileUri = fileUri;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BinaryFileDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

//        if (undoRedo != null) {
//            undoRedo.clear();
//        }
        fileSync();
    }

    @Override
    public boolean isModified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveTo(DocumentSource documentSource) {
        if (!(documentSource instanceof FileDocumentSource)) {
            throw new UnsupportedOperationException();
        }

        File file = ((FileDocumentSource) documentSource).getFile();
        try {
            BinEdComponentPanel componentPanel = getComponent();
            BinaryData contentData = componentPanel.getContentData();
            if (contentData instanceof EmptyBinaryData) {
                clearFile();
                contentData = componentPanel.getContentData();
            }
            if (contentData instanceof DeltaDocument) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                SegmentsRepository segmentsRepository = binedModule.getFileManager().getSegmentsRepository();
                // TODO freezes window / replace with progress bar
                DeltaDocument document = (DeltaDocument) contentData;
                FileDataSource fileSource = (FileDataSource) document.getDataSource();
                if (fileSource == null || !file.equals(fileSource.getFile())) {
                    fileSource = new FileDataSource(file);
                    segmentsRepository.addDataSource(fileSource);
                    document.setDataSource(fileSource);
                }
                segmentsRepository.saveDocument(document);
                this.fileUri = fileUri;
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    Objects.requireNonNull(contentData).saveToStream(outputStream);
                    this.fileUri = fileUri;
                }
            }
            // TODO
//            documentOriginalSize = codeArea.getDataSize();
//            updateCurrentDocumentSize();
//            updateCurrentMemoryMode();
        } catch (IOException ex) {
            Logger.getLogger(BinaryFileDocument.class.getName()).log(Level.SEVERE, null, ex);
        }

        fileSync();
    }

    public void fileSync() {
        /* documentOriginalSize = getCodeArea().getDataSize();
        if (undoRedo != null) {
            undoRedo.setSyncPosition();
        } */
    }

    @Override
    public void notifyActivated(ActiveContextManagement contextManager) {
        contextManager.changeActiveState(ContextFont.class, dataComponent);
        contextManager.changeActiveState(ContextEncoding.class, dataComponent);
        // TODO contextManager.changeActiveState(UndoRedoState.class, );
        contextManager.changeActiveState(ContextComponent.class, dataComponent);
        contextManager.changeActiveState(DialogParentComponent.class, new DialogParentComponent() {
            @Nonnull
            @Override
            public Component getComponent() {
                return dataComponent.getCodeArea();
            }
        });
    }

    @Override
    public void notifyDeactivated(ActiveContextManagement contextManager) {
        contextManager.changeActiveState(ContextFont.class, null);
        contextManager.changeActiveState(ContextEncoding.class, null);
        contextManager.changeActiveState(UndoRedoState.class, null);
        contextManager.changeActiveState(ContextComponent.class, null);
        contextManager.changeActiveState(DialogParentComponent.class, new DialogParentComponent() {
            @Nonnull
            @Override
            public Component getComponent() {
                return dataComponent.getCodeArea();
            }
        });
    }
}
