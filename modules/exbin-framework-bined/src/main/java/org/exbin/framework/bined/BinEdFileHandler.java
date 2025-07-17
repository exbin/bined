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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.EmptyBinaryData;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.auxiliary.binary_data.delta.file.FileDataSource;
import org.exbin.auxiliary.binary_data.delta.SegmentsRepository;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.auxiliary.binary_data.array.paged.ByteArrayPagedData;
import org.exbin.bined.operation.swing.CodeAreaUndoRedo;
import org.exbin.bined.operation.undo.BinaryDataUndoRedo;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.operation.undo.api.UndoRedoFileHandler;
import org.exbin.framework.editor.api.EditorFileHandler;
import org.exbin.framework.operation.undo.api.UndoRedo;
import org.exbin.framework.operation.undo.api.UndoRedoState;
import org.exbin.framework.text.encoding.TextEncodingController;
import org.exbin.framework.text.font.TextFontController;

/**
 * File handler for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileHandler implements EditableFileHandler, EditorFileHandler, UndoRedoFileHandler {

    private SegmentsRepository segmentsRepository;

    @Nonnull
    private final BinEdDocumentView editorComponent;
    private int id = 0;
    private URI fileUri = null;
    private FileType fileType;
    private String title;
    private SectionCodeAreaColorProfile defaultColors;
    private long documentOriginalSize;
    private ComponentActivationListener componentActivationListener;
    private BinEdDataComponent binaryDataComponent;
    private UndoRedo undoRedo = null;

    public BinEdFileHandler() {
        editorComponent = createEditorComponent();
        init();
    }

    public BinEdFileHandler(int id) {
        this();
        this.id = id;
    }

    @Nonnull
    protected BinEdDocumentView createEditorComponent() {
        return new BinEdDocumentView();
    }

    private void init() {
        final SectCodeArea codeArea = getCodeArea();
        defaultColors = (SectionCodeAreaColorProfile) codeArea.getColorsProfile();
        binaryDataComponent = new BinEdDataComponent(codeArea);
        codeArea.addSelectionChangedListener(() -> {
            if (componentActivationListener != null) {
                componentActivationListener.updated(ActiveComponent.class, binaryDataComponent);
            }
        });
    }

    public void registerUndoHandler() {
        setUndoHandler(new CodeAreaUndoRedo(editorComponent.getCodeArea()));
    }

    public void setUndoHandler(BinaryDataUndoRedo undoRedo) {
        editorComponent.setUndoHandler(undoRedo);
        this.undoRedo = new UndoRedoWrapper();
        ((UndoRedoWrapper) this.undoRedo).setUndoRedo(undoRedo);
        this.undoRedo.addChangeListener(this::notifyUndoChanged);
        notifyUndoChanged();
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        loadFromFile(fileUri, fileType, getFileHandlingMode());
    }

    private void loadFromFile(URI fileUri, FileType fileType, FileHandlingMode fileHandlingMode) {
        this.fileType = fileType;
        File file = new File(fileUri);
        if (!file.isFile()) {
            JOptionPane.showOptionDialog(editorComponent.getComponent(),
                    "File not found",
                    "Unable to load file",
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null, null, null);
            return;
        }

        try {
            BinaryData oldData = editorComponent.getContentData();
            if (fileHandlingMode == FileHandlingMode.DELTA) {
                FileDataSource openFileSource = new FileDataSource(file);
                segmentsRepository.addDataSource(openFileSource);
                DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                editorComponent.setContentData(document);
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
                    editorComponent.setContentData(data);
                    this.fileUri = fileUri;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (undoRedo != null) {
            undoRedo.clear();
        }
        fileSync();
    }

    @Override
    public boolean canSave() {
        return getClipboardActionsController().isEditable();
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try {
            BinaryData contentData = editorComponent.getContentData();
            if (contentData instanceof EmptyBinaryData) {
                clearFile();
                contentData = editorComponent.getContentData();
            }
            if (contentData instanceof DeltaDocument) {
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
            Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        fileSync();
    }

    public void fileSync() {
        documentOriginalSize = getCodeArea().getDataSize();
        if (undoRedo != null) {
            undoRedo.setSyncPosition();
        }
    }

    public void loadFromStream(InputStream stream) throws IOException {
        BinaryData contentData = editorComponent.getContentData();
        if (!(contentData instanceof EditableBinaryData)) {
            contentData = new ByteArrayEditableData();
            // TODO: stream to binary data
        }

        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) contentData);
        data.loadFromStream(stream);
        editorComponent.setContentData(contentData);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        BinaryData contentData = editorComponent.getContentData();
        if (!(contentData instanceof EditableBinaryData)) {
            contentData = new ByteArrayEditableData();
        }

        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) contentData);
        data.clear();
        data.insert(0, stream, dataSize);
        editorComponent.setContentData(contentData);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        BinaryData data = Objects.requireNonNull((BinaryData) editorComponent.getContentData());
        data.saveToStream(stream);
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.ofNullable(fileUri);
    }

    @Override
    public void clearFile() {
        FileHandlingMode fileHandlingMode = getFileHandlingMode();
        closeData();
        SectCodeArea codeArea = editorComponent.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            segmentsRepository.dropDocument(Objects.requireNonNull((DeltaDocument) codeArea.getContentData()));
        }
        setNewData(fileHandlingMode);
        fileUri = null;
        if (undoRedo != null) {
            undoRedo.clear();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Nonnull
    @Override
    public String getTitle() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            String fileName = lastSegment < 0 ? path : path.substring(lastSegment + 1);
            return fileName == null ? "" : fileName;
        }

        return title == null ? "" : title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nonnull
    public String getWindowTitle(String windowTitle) {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastIndexOf = path.lastIndexOf("/");
            if (lastIndexOf < 0) {
                return path + " - " + windowTitle;
            }
            return path.substring(lastIndexOf + 1) + " - " + windowTitle;
        }

        return windowTitle;
    }

    public long getDocumentOriginalSize() {
        return documentOriginalSize;
    }

    @Override
    public void saveFile() {
        SectCodeArea codeArea = editorComponent.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            File file = new File(fileUri);
            try (OutputStream stream = new FileOutputStream(file)) {
                BinaryData contentData = codeArea.getContentData();
                contentData.saveToStream(stream);
                stream.flush();
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void reloadFile() {
        if (fileUri != null) {
            loadFromFile(fileUri, fileType, getFileHandlingMode());
        }
    }

    public void closeData() {
        SectCodeArea codeArea = editorComponent.getCodeArea();
        BinaryData data = codeArea.getContentData();
        editorComponent.setContentData(EmptyBinaryData.getInstance());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = (FileDataSource) ((DeltaDocument) data).getDataSource();
            data.dispose();
            if (fileSource != null) {
                segmentsRepository.detachFileSource(fileSource);
                try {
                    fileSource.close();
                } catch (IOException ex) {
                    Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            data.dispose();
        }
    }

    public void switchFileHandlingMode(FileHandlingMode handlingMode) {
        FileHandlingMode oldFileHandlingMode = getFileHandlingMode();
        SectCodeArea codeArea = editorComponent.getCodeArea();
        if (handlingMode != oldFileHandlingMode) {
            if (fileUri != null) {
                loadFromFile(fileUri, null, handlingMode);
            } else {
                BinaryData oldData = codeArea.getContentData();
                if (oldData instanceof DeltaDocument) {
                    PagedData data = new ByteArrayPagedData();
                    data.insert(0, oldData);
                    editorComponent.setContentData(data);
                } else {
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    editorComponent.setContentData(document);
                }

                if (undoRedo != null) {
                    undoRedo.clear();
                }

                oldData.dispose();
            }
        }
    }

    @Nonnull
    public FileHandlingMode getFileHandlingMode() {
        return getCodeArea().getContentData() instanceof DeltaDocument ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY;
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getComponent() {
        return (BinEdComponentPanel) editorComponent.getComponent();
    }

    @Nonnull
    public SectCodeArea getCodeArea() {
        return editorComponent.getCodeArea();
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        return Optional.empty();
    }

    @Override
    public void setFileType(FileType fileType) {
    }

    @Override
    public boolean isModified() {
        if (undoRedo == null) {
            return false;
        }

        return undoRedo.getCommandPosition() != undoRedo.getSyncPosition();
    }

    public void setNewData(FileHandlingMode fileHandlingMode) {
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            editorComponent.setContentData(segmentsRepository.createDocument());
        } else {
            editorComponent.setContentData(new ByteArrayPagedData());
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void requestFocus() {
        editorComponent.getCodeArea().requestFocus();
    }

    @Nonnull
    public BinEdDataComponent getClipboardActionsController() {
        return binaryDataComponent;
    }

    @Nonnull
    public BinEdDataComponent getBinaryDataComponent() {
        return binaryDataComponent;
    }

    @Nonnull
    @Override
    public Optional<UndoRedoState> getUndoRedo() {
        if (undoRedo == null) {
            undoRedo = new UndoRedoWrapper();
            ((UndoRedoWrapper) undoRedo).setUndoRedo(editorComponent.getUndoHandler().orElse(null));
        }
        return Optional.of(undoRedo);
    }

    @Nonnull
    public Optional<BinaryDataUndoRedo> getCodeAreaUndoHandler() {
        return editorComponent.getUndoHandler();
    }

    @Nonnull
    public SectionCodeAreaColorProfile getDefaultColors() {
        return defaultColors;
    }

    @Override
    public void componentActivated(ComponentActivationListener componentActivationListener) {
        this.componentActivationListener = componentActivationListener;
        componentActivationListener.updated(TextFontController.class, binaryDataComponent);
        componentActivationListener.updated(TextEncodingController.class, binaryDataComponent);
        componentActivationListener.updated(UndoRedoState.class, undoRedo);
        componentActivationListener.updated(ActiveComponent.class, binaryDataComponent);
    }

    @Override
    public void componentDeactivated(ComponentActivationListener componentActivationListener) {
        this.componentActivationListener = null;
        componentActivationListener.updated(TextFontController.class, null);
        componentActivationListener.updated(TextEncodingController.class, null);
        componentActivationListener.updated(UndoRedoState.class, null);
        componentActivationListener.updated(ActiveComponent.class, null);
    }

    private void notifyUndoChanged() {
        if (undoRedo != null) {
            if (componentActivationListener != null) {
                componentActivationListener.updated(UndoRedoState.class, undoRedo);
            }
        }
    }
}
