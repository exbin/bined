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

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.ByteArrayData;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.FileDataSource;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextCharsetApi;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.utils.ClipboardActionsHandler;
import org.exbin.framework.utils.ClipboardActionsUpdateListener;
import org.exbin.xbup.core.type.XBData;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.operation.undo.api.UndoFileHandler;
import org.exbin.xbup.operation.undo.XBUndoHandler;

/**
 * File handler for binary editor.
 *
 * @version 0.2.2 2021/10/30
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileHandler implements FileHandler, UndoFileHandler, BinEdComponentFileApi, ClipboardActionsHandler, TextFontApi, TextCharsetApi {

    private SegmentsRepository segmentsRepository;

    @Nonnull
    private final BinEdComponentPanel componentPanel;
    private XBUndoHandler undoHandlerWrapper;
    private int id = 0;
    private URI fileUri = null;
    private Font defaultFont;
    private ExtendedCodeAreaColorProfile defaultColors;
    private long documentOriginalSize;

    public BinEdFileHandler() {
        componentPanel = new BinEdComponentPanel();
        init();
    }

    public BinEdFileHandler(int id) {
        this();
        this.id = id;
    }

    private void init() {
        final ExtCodeArea codeArea = getCodeArea();
        CodeAreaUndoHandler undoHandler = new CodeAreaUndoHandler(componentPanel.getCodeArea());
        componentPanel.setUndoHandler(undoHandler);
        defaultFont = codeArea.getCodeFont();
        defaultColors = (ExtendedCodeAreaColorProfile) codeArea.getColorsProfile();
    }

    public void setApplication(XBApplication application) {
        componentPanel.setApplication(application);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        loadFromFile(fileUri, fileType, getFileHandlingMode());
    }

    private void loadFromFile(URI fileUri, FileType fileType, FileHandlingMode fileHandlingMode) {
        File file = new File(fileUri);
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
            if (fileHandlingMode == FileHandlingMode.DELTA) {
                FileDataSource openFileSource = segmentsRepository.openFileSource(file);
                DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                componentPanel.setContentData(document);
                this.fileUri = fileUri;
                if (oldData != null) {
                    oldData.dispose();
                }
            } else {
                try ( FileInputStream fileStream = new FileInputStream(file)) {
                    BinaryData data = componentPanel.getContentData();
                    if (!(data instanceof XBData)) {
                        data = new XBData();
                        if (oldData != null) {
                            oldData.dispose();
                        }
                    }
                    ((EditableBinaryData) data).loadFromStream(fileStream);
                    componentPanel.setContentData(data);
                    this.fileUri = fileUri;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        undoHandlerWrapper.clear();
        fileSync();
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        File file = new File(fileUri);
        try {
            BinaryData contentData = componentPanel.getContentData();
            if (contentData == null) {
                newFile();
                contentData = componentPanel.getContentData();
            }
            if (contentData instanceof DeltaDocument) {
                // TODO freeze window / replace with progress bar
                DeltaDocument document = (DeltaDocument) contentData;
                FileDataSource fileSource = document.getFileSource();
                if (fileSource == null || !file.equals(fileSource.getFile())) {
                    fileSource = segmentsRepository.openFileSource(file);
                    document.setFileSource(fileSource);
                }
                segmentsRepository.saveDocument(document);
                this.fileUri = fileUri;
            } else {
                try ( FileOutputStream outputStream = new FileOutputStream(file)) {
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

    private void fileSync() {
        documentOriginalSize = getCodeArea().getDataSize();
        undoHandlerWrapper.setSyncPoint();
    }

    public void loadFromStream(InputStream stream) throws IOException {
        BinaryData contentData = componentPanel.getContentData();
        if (!(contentData instanceof EditableBinaryData)) {
            contentData = new ByteArrayEditableData();
            // TODO: stream to binary data
        }

        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) contentData);
        data.loadFromStream(stream);
        componentPanel.setContentData(contentData);
    }

    public void loadFromStream(InputStream stream, long dataSize) throws IOException {
        BinaryData contentData = componentPanel.getContentData();
        if (!(contentData instanceof EditableBinaryData)) {
            contentData = new ByteArrayEditableData();
        }

        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) contentData);
        data.clear();
        data.insert(0, stream, dataSize);
        componentPanel.setContentData(contentData);
    }

    public void saveToStream(OutputStream stream) throws IOException {
        BinaryData data = Objects.requireNonNull((BinaryData) componentPanel.getContentData());
        data.saveToStream(stream);
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.ofNullable(fileUri);
    }

    @Override
    public void newFile() {
        FileHandlingMode fileHandlingMode = getFileHandlingMode();
        closeData();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            segmentsRepository.dropDocument(Objects.requireNonNull((DeltaDocument) codeArea.getContentData()));
        }
        setNewData(fileHandlingMode);
        fileUri = null;
        if (undoHandlerWrapper != null) {
            undoHandlerWrapper.clear();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Nonnull
    @Override
    public Optional<String> getFileName() {
        if (fileUri != null) {
            String path = fileUri.getPath();
            int lastSegment = path.lastIndexOf("/");
            return Optional.of(lastSegment < 0 ? path : path.substring(lastSegment + 1));
        }

        return Optional.empty();
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

    public void saveFile() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            File file = new File(fileUri);
            try ( OutputStream stream = new FileOutputStream(file)) {
                BinaryData contentData = codeArea.getContentData();
                if (contentData != null) {
                    contentData.saveToStream(stream);
                }
                stream.flush();
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void closeData() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        componentPanel.setContentData(new ByteArrayData());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = ((DeltaDocument) data).getFileSource();
            data.dispose();
            if (fileSource != null) {
                segmentsRepository.detachFileSource(fileSource);
                segmentsRepository.closeFileSource(fileSource);
            }
        } else {
            if (data != null) {
                data.dispose();
            }
        }
    }

    @Override
    public void saveDocument() {
        if (fileUri == null) {
            return;
        }

        saveFile();
    }

    @Override
    public void switchFileHandlingMode(FileHandlingMode handlingMode) {
        FileHandlingMode oldFileHandlingMode = getFileHandlingMode();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        if (handlingMode != oldFileHandlingMode) {
            if (fileUri != null) {
                loadFromFile(fileUri, null, handlingMode);
            } else {
                BinaryData oldData = codeArea.getContentData();
                if (oldData instanceof DeltaDocument) {
                    XBData data = new XBData();
                    data.insert(0, oldData);
                    componentPanel.setContentData(data);
                } else {
                    DeltaDocument document = segmentsRepository.createDocument();
                    if (oldData != null) {
                        document.insert(0, oldData);
                    }
                    componentPanel.setContentData(document);
                }

                if (undoHandlerWrapper != null) {
                    undoHandlerWrapper.clear();
                }

                if (oldData != null) {
                    oldData.dispose();
                }
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
        return componentPanel;
    }

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return componentPanel.getCodeArea();
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
        return undoHandlerWrapper.getCommandPosition() != undoHandlerWrapper.getSyncPoint();
    }

    public void setNewData(FileHandlingMode fileHandlingMode) {
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            componentPanel.setContentData(segmentsRepository.createDocument());
        } else {
            componentPanel.setContentData(new XBData());
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void requestFocus() {
        componentPanel.getCodeArea().requestFocus();
    }

    @Nonnull
    @Override
    public XBUndoHandler getUndoHandler() {
        if (undoHandlerWrapper == null) {
            undoHandlerWrapper = new UndoHandlerWrapper();
            ((UndoHandlerWrapper) undoHandlerWrapper).setHandler(componentPanel.getUndoHandler());
        }
        return undoHandlerWrapper;
    }

    @Nonnull
    public CodeAreaUndoHandler getCodeAreaUndoHandler() {
        return componentPanel.getUndoHandler();
    }

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    @Override
    public void performCut() {
        getCodeArea().cut();
    }

    @Override
    public void performCopy() {
        getCodeArea().copy();
    }

    @Override
    public void performPaste() {
        getCodeArea().paste();
    }

    @Override
    public void performDelete() {
        getCodeArea().delete();
    }

    @Override
    public void performSelectAll() {
        getCodeArea().selectAll();
    }

    @Override
    public boolean isSelection() {
        return getCodeArea().hasSelection();
    }

    @Override
    public boolean isEditable() {
        return getCodeArea().isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        return getCodeArea().canPaste();
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public void setCurrentFont(Font font) {
        getCodeArea().setCodeFont(font);
    }

    @Nonnull
    @Override
    public Font getCurrentFont() {
        return getCodeArea().getCodeFont();
    }

    @Nonnull
    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    @Nonnull
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return defaultColors;
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return getCodeArea().getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        getCodeArea().setCharset(charset);
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        // componentPanel.setUpdateListener(updateListener);
    }
}
