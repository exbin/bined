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
package org.exbin.framework.bined.operation.action;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.BinaryDocument;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.bined.operation.api.DataOperationMethod;
import org.exbin.framework.bined.operation.gui.ConvertDataControlPanel;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.bined.operation.gui.ConvertDataControlController;
import org.exbin.framework.bined.operation.gui.DataOperationPanel;
import org.exbin.framework.document.api.DocumentModuleApi;

/**
 * Convert data action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ConvertDataAction extends AbstractAction {

    public static final String ACTION_ID = "convertDataAction";
    public static final String HELP_ID = "convert-data-action";

    private static final int PREVIEW_LENGTH_LIMIT = 4096;

    private CodeAreaCore codeArea;
    private ConvertDataMethod lastMethod = null;

    public ConvertDataAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    boolean hasInstance = instance != null;
                    setEnabled(hasInstance && codeArea.isEditable());
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DataOperationPanel dataOperationPanel = new DataOperationPanel();
        dataOperationPanel.setController(() -> {
            Optional<DataOperationMethod> optionalActiveMethod = dataOperationPanel.getActiveMethod();
            if (optionalActiveMethod.isPresent()) {
                ConvertDataMethod activeMethod = (ConvertDataMethod) optionalActiveMethod.get();
                Component activeComponent = dataOperationPanel.getActiveComponent().get();
                activeMethod.requestPreview((component) -> {
                    dataOperationPanel.setPreviewComponent(component);
                }, activeComponent, codeArea, PREVIEW_LENGTH_LIMIT);
            }
        });
        ConvertDataControlPanel controlPanel = new ConvertDataControlPanel();
        ResourceBundle panelResourceBundle = controlPanel.getResourceBundle();
        controlPanel.setHelpLink(new HelpLink(HELP_ID));
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        JPanel dialogPanel = windowModule.createDialogPanel(dataOperationPanel, controlPanel);
        BinedOperationModule binedBlockEditModule = App.getModule(BinedOperationModule.class);
        dataOperationPanel.setDataMethods(binedBlockEditModule.getConvertDataMethods());
        dataOperationPanel.selectActiveMethod(lastMethod);
        final WindowHandler dialog = windowModule.createWindow(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
        windowModule.addHeaderPanel(dialog.getWindow(), dataOperationPanel.getClass(), panelResourceBundle);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        controlPanel.setController((ConvertDataControlController.ControlActionType actionType) -> {
            if (actionType != ConvertDataControlController.ControlActionType.CANCEL) {
                Optional<DataOperationMethod> optionalActiveMethod = dataOperationPanel.getActiveMethod();
                if (optionalActiveMethod.isPresent()) {
                    Component activeComponent = dataOperationPanel.getActiveComponent().get();
                    ConvertDataMethod activeMethod = (ConvertDataMethod) optionalActiveMethod.get();

                    switch (actionType) {
                        case CONVERT: {
                            CodeAreaCommand command = activeMethod.createConvertCommand(activeComponent, codeArea);

                            CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                            if (commandHandler instanceof CodeAreaOperationCommandHandler) {
                                ((CodeAreaOperationCommandHandler) commandHandler).getUndoRedo().execute(command);
                            } else {
                                command.execute();
                            }
                            break;
                        }
                        case CONVERT_TO_NEW_FILE: {
                            BinaryData outputData = activeMethod.performDirectConvert(activeComponent, codeArea);
                            DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
                            BinaryDocument document = (BinaryDocument) documentModule.getMainDocumentManager().createDefaultDocument();
                            ((EditableBinaryData) document.getBinaryData()).insert(0, outputData);
                            documentModule.getMainDocumentManager().receiveDocument(document);
                            break;
                        }
                        case CONVERT_TO_CLIPBOARD: {
                            try {
                                BinaryData outputData = activeMethod.performDirectConvert(activeComponent, codeArea);
                                DataFlavor binedDataFlavor = new DataFlavor(CodeAreaUtils.BINED_CLIPBOARD_MIME_FULL);
                                DataFlavor binaryDataFlavor = new DataFlavor(CodeAreaUtils.MIME_CLIPBOARD_BINARY);
                                Clipboard clipboard = CodeAreaSwingUtils.getClipboard();
                                CodeAreaSwingUtils.BinaryDataClipboardData binaryData = new CodeAreaSwingUtils.BinaryDataClipboardData(outputData, binedDataFlavor, binaryDataFlavor, null);
                                clipboard.setContents(binaryData, binaryData);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(ConvertDataAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                    }
                }
                lastMethod = (ConvertDataMethod) optionalActiveMethod.orElse(null);
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(dataOperationPanel::initFocus);
        dialog.showCentered(codeArea);
    }
}
