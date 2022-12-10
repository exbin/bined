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
package org.exbin.framework.bined.action;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.PagedData;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.CompareFilesPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.CloseControlPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.AllFileTypes;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Compare files action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompareFilesAction extends AbstractAction {

    public static final String ACTION_ID = "compareFilesAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public CompareFilesAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CompareFilesPanel compareFilesPanel = new CompareFilesPanel();
        ResourceBundle panelResourceBundle = compareFilesPanel.getResourceBundle();
        CloseControlPanel controlPanel = new CloseControlPanel(panelResourceBundle);

        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(editorProvider.getEditorComponent(), Dialog.ModalityType.APPLICATION_MODAL, compareFilesPanel, controlPanel);
        frameModule.setDialogTitle(dialog, panelResourceBundle);
        Dimension preferredSize = dialog.getWindow().getPreferredSize();
        dialog.getWindow().setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 450));
        controlPanel.setHandler(dialog::close);
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            compareFilesPanel.setLeftFile(((BinEdFileHandler) activeFile.get()).getCodeArea().getContentData());
        }

        List<FileHandler> fileHandlers;
        if (editorProvider instanceof MultiEditorProvider) {
            fileHandlers = ((MultiEditorProvider) editorProvider).getFileHandlers();
            List<String> availableFiles = new ArrayList<>();
            for (FileHandler fileHandler : fileHandlers) {
                Optional<URI> fileUri = fileHandler.getFileUri();
                availableFiles.add(fileUri.isPresent() ? fileUri.get().toString() : panelResourceBundle.getString("unsavedFile"));
            }
            compareFilesPanel.setAvailableFiles(availableFiles);
        } else {
            fileHandlers = new ArrayList<>();
            Optional<URI> fileUri = editorProvider.getActiveFile().get().getFileUri();
            List<String> availableFiles = new ArrayList<>();
            availableFiles.add(fileUri.isPresent() ? fileUri.get().toString() : panelResourceBundle.getString("unsavedFile"));
            compareFilesPanel.setAvailableFiles(availableFiles);
        }

        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
        compareFilesPanel.setCodeAreaPopupMenu(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC));
        compareFilesPanel.setController(new CompareFilesPanel.Controller() {
            @Nullable
            @Override
            public CompareFilesPanel.FileRecord openFile() {
                final File[] result = new File[1];
                FileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(FileModuleApi.class);
                fileModule.getFileActions().openFile((URI fileUri, FileType fileType) -> {
                    result[0] = new File(fileUri);
                }, new AllFileTypes(), editorProvider);

                if (result[0] == null) {
                    return null;
                }

                try (FileInputStream stream = new FileInputStream(result[0])) {
                    PagedData pagedData = new PagedData();
                    pagedData.loadFromStream(stream);
                    return new CompareFilesPanel.FileRecord(result[0].getAbsolutePath(), pagedData);
                } catch (IOException ex) {
                    Logger.getLogger(CompareFilesAction.class.getName()).log(Level.SEVERE, null, ex);

                }
                return null;
            }

            @Nonnull
            @Override
            public BinaryData getFileData(int index) {
                return ((BinEdFileHandler) fileHandlers.get(index)).getCodeArea().getContentData();
            }
        });
        dialog.showCentered(editorProvider.getEditorComponent());
    }
}
