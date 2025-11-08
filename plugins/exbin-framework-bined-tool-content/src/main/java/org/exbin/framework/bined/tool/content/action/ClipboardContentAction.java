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
package org.exbin.framework.bined.tool.content.action;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.tool.content.StreamUtils;
import org.exbin.framework.bined.tool.content.gui.ClipboardContentControlPanel;
import org.exbin.framework.bined.tool.content.gui.ClipboardContentPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;

/**
 * Clipboard content action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardContentAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "clipboardContentAction";
    public static final String HELP_ID = "clipboard-content";

    private ClipboardContentPanel clipboardContentPanel = new ClipboardContentPanel();
    private DialogParentComponent dialogParentComponent;
    private EditorProvider editorProvider;

    public ClipboardContentAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        clipboardContentPanel.loadFromClipboard();
        ClipboardContentControlPanel controlPanel = new ClipboardContentControlPanel();
        controlPanel.setHelpLink(new HelpLink(HELP_ID));
        final WindowHandler dialog = windowModule.createDialog(clipboardContentPanel, controlPanel);
        clipboardContentPanel.setOpenAsTabAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Optional<BinaryData> optContentBinaryData = clipboardContentPanel.getContentBinaryData();
                if (optContentBinaryData.isPresent()) {
                    BinaryData contentBinaryData = optContentBinaryData.get();
                    editorProvider.newFile();
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                        fileHandler.getCodeArea().setContentData(contentBinaryData);
                    }
                }
            }
        });
        clipboardContentPanel.setSaveAsFileAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser exportFileChooser = new JFileChooser();
                exportFileChooser.setAcceptAllFileFilterUsed(true);
                if (exportFileChooser.showSaveDialog(dialog.getWindow()) == JFileChooser.APPROVE_OPTION) {
                    BinaryData binaryData = clipboardContentPanel.getContentBinaryData().orElse(null);
                    if (binaryData == null) {
                        return;
                    }
                    InputStream dataInputStream = binaryData.getDataInputStream();

                    FileOutputStream fileStream;
                    try {
                        fileStream = new FileOutputStream(exportFileChooser.getSelectedFile().getAbsolutePath());
                        try {
                            StreamUtils.copyInputStreamToOutputStream(dataInputStream, fileStream);
                        } finally {
                            fileStream.close();
                        }
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ClipboardContentAction.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ClipboardContentAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        BinedModule binedModule = App.getModule(BinedModule.class);
        clipboardContentPanel.setCodeAreaPopupMenuHandler(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC));

        windowModule.setWindowTitle(dialog, clipboardContentPanel.getResourceBundle());
        controlPanel.setController((actionType) -> {
            switch (actionType) {
                case CLOSE: {
                    dialog.close();
                    dialog.dispose();

                    break;
                }
                case REFRESH: {
                    clipboardContentPanel.loadFromClipboard();
                    break;
                }
            }
        });
        dialog.showCentered(dialogParentComponent.getComponent());
    }

    @Override
    public void register(ActionContextChangeRegistration registrar) {
        registrar.registerUpdateListener(EditorProvider.class, (instance) -> {
            editorProvider = instance;
            setEnabled(editorProvider != null && dialogParentComponent != null);
        });
        registrar.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
            dialogParentComponent = instance;
            setEnabled(editorProvider != null && dialogParentComponent != null);
        });
    }
}
