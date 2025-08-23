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
package org.exbin.framework.bined.compare.action;

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
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.paged.ByteArrayPagedData;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.compare.gui.CompareFilesPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.AllFileTypes;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.CloseControlPanel;

/**
 * Compare files action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompareFilesAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "compareFilesAction";
    public static final String HELP_ID = "compare-files-action";

    private EditorProvider editorProvider;
    private DialogParentComponent dialogParentComponent;

    public CompareFilesAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final CompareFilesPanel compareFilesPanel = new CompareFilesPanel();
        ResourceBundle panelResourceBundle = compareFilesPanel.getResourceBundle();
        CloseControlPanel controlPanel = new CloseControlPanel(panelResourceBundle);
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(dialogParentComponent.getComponent(), Dialog.ModalityType.APPLICATION_MODAL, compareFilesPanel, controlPanel);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        Dimension preferredSize = dialog.getWindow().getPreferredSize();
        dialog.getWindow().setSize(new Dimension(preferredSize.width, preferredSize.height + 450));
        controlPanel.setController(dialog::close);
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

        BinedModule binedModule = App.getModule(BinedModule.class);
        compareFilesPanel.setCodeAreaPopupMenu(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC));
        compareFilesPanel.setController(new CompareFilesPanel.Controller() {
            @Nullable
            @Override
            public CompareFilesPanel.FileRecord openFile() {
                final File[] result = new File[1];
                FileModuleApi fileModule = App.getModule(FileModuleApi.class);
                fileModule.getFileActions().openFile((URI fileUri, FileType fileType) -> {
                    result[0] = new File(fileUri);
                }, new AllFileTypes(), editorProvider);

                if (result[0] == null) {
                    return null;
                }

                try (FileInputStream stream = new FileInputStream(result[0])) {
                    PagedData pagedData = new ByteArrayPagedData();
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
        dialog.showCentered(dialogParentComponent.getComponent());
    }

    @Override
    public void register(ActionContextChangeManager manager) {
        manager.registerUpdateListener(EditorProvider.class, (instance) -> {
            editorProvider = instance;
            setEnabled(editorProvider != null && dialogParentComponent != null);
        });
        manager.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
            dialogParentComponent = instance;
            setEnabled(editorProvider != null && dialogParentComponent != null);
        });
    }
}
