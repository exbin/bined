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
package org.exbin.bined.jaguif.tool.content.action;

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
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.bined.jaguif.component.BinaryDocument;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.tool.content.StreamUtils;
import org.exbin.bined.jaguif.tool.content.gui.DragDropContentPanel;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.docking.api.DocumentDocking;
import org.exbin.jaguif.document.api.Document;
import org.exbin.jaguif.help.api.HelpLink;
import org.exbin.jaguif.help.api.HelpModuleApi;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.window.api.gui.CloseControlPanel;

/**
 * Drag and drop content action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DragDropContentAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "dragDropContent";
    public static final String HELP_ID = "drag-and-drop-content";

    private DialogParentComponent dialogParentComponent;
    private DocumentDocking documentDocking;

    public DragDropContentAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DragDropContentPanel dragDropContentPanel = new DragDropContentPanel();
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        CloseControlPanel controlPanel = new CloseControlPanel();
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));
        final WindowHandler dialog = windowModule.createDialog(dragDropContentPanel, controlPanel);
        dragDropContentPanel.setController(new DragDropContentPanel.Controller() {
            @Override
            public void openAsTab() {
                Optional<BinaryData> optContentBinaryData = dragDropContentPanel.getContentBinaryData();
                if (optContentBinaryData.isPresent()) {
                    BinaryData contentBinaryData = optContentBinaryData.get();
                    // TODO Force binary document
                    documentDocking.openNewDocument();
                    Optional<Document> document = documentDocking.getActiveDocument();
                    if (document.isPresent()) {
                        BinaryDocument binaryDocument = (BinaryDocument) document.get();
                        ((EditableBinaryData) binaryDocument.getBinaryData()).insert(0, contentBinaryData);
                    }
                }
            }

            @Override
            public void saveAsFile() {
                JFileChooser exportFileChooser = new JFileChooser();
                exportFileChooser.setAcceptAllFileFilterUsed(true);
                if (exportFileChooser.showSaveDialog(dialog.getWindow()) == JFileChooser.APPROVE_OPTION) {
                    BinaryData binaryData = dragDropContentPanel.getContentBinaryData().orElse(null);
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
                        Logger.getLogger(DragDropContentAction.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DragDropContentAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        dragDropContentPanel.setOpenAsTabEnabled(true);
        dragDropContentPanel.setSaveAsFileEnabled(true);
        BinedComponentModule binedModule = App.getModule(BinedComponentModule.class);
        dragDropContentPanel.setCodeAreaPopupMenuHandler(binedModule.createCodeAreaPopupMenuHandler(BinedComponentModule.PopupMenuVariant.BASIC));

        windowModule.setWindowTitle(dialog, dragDropContentPanel.getResourceBundle());
        controlPanel.setController(() -> {
            dialog.close();
            dialog.dispose();
        });
        windowModule.addHeaderPanel(dialog.getWindow(), dragDropContentPanel.getClass(), dragDropContentPanel.getResourceBundle());
        dialog.showCentered(dialogParentComponent.getComponent());
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerChangeListener(ContextDocking.class, (instance) -> {
            documentDocking = instance instanceof DocumentDocking ? (DocumentDocking) instance : null;
            setEnabled(documentDocking != null && dialogParentComponent != null);
        });
        registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
            dialogParentComponent = instance;
            setEnabled(documentDocking != null && dialogParentComponent != null);
        });
    }
}
