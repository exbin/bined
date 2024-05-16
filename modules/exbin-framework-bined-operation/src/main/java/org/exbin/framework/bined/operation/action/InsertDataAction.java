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
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.operation.gui.InsertDataPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.operation.api.InsertDataMethod;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Insert data action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InsertDataAction extends AbstractAction {

    public static final String ACTION_ID = "insertDataAction";

    private static final int PREVIEW_LENGTH_LIMIT = 4096;

    private ResourceBundle resourceBundle;
    private CodeAreaCore codeArea;
    private InsertDataMethod lastMethod = null;

    public InsertDataAction() {

    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Override
            public void register(ComponentActivationManager manager) {
                manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                    codeArea = instance;
                    boolean hasInstance = instance != null;
                    setEnabled(hasInstance);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final InsertDataPanel insertDataPanel = new InsertDataPanel();
        insertDataPanel.setController((previewCodeArea) -> {
            Optional<InsertDataMethod> optionalActiveMethod = insertDataPanel.getActiveMethod();
            if (optionalActiveMethod.isPresent()) {
                Component activeComponent = insertDataPanel.getActiveComponent().get();
                optionalActiveMethod.get().registerPreviewDataHandler((binaryData) -> {
                    previewCodeArea.setContentData(binaryData);
                }, activeComponent, PREVIEW_LENGTH_LIMIT);
            }
        });
        ResourceBundle panelResourceBundle = insertDataPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        JPanel dialogPanel = windowModule.createDialogPanel(insertDataPanel, controlPanel);
        BinedOperationModule binedBlockEditModule = App.getModule(BinedOperationModule.class);
        insertDataPanel.setComponents(binedBlockEditModule.getInsertDataComponents());
        insertDataPanel.selectActiveMethod(lastMethod);
        BinedModule binedModule = App.getModule(BinedModule.class);
        insertDataPanel.setCodeAreaPopupMenuHandler(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.NORMAL));
        final WindowHandler dialog = windowModule.createWindow(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
        windowModule.addHeaderPanel(dialog.getWindow(), insertDataPanel.getClass(), panelResourceBundle);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                Optional<InsertDataMethod> optionalActiveMethod = insertDataPanel.getActiveMethod();
                if (optionalActiveMethod.isPresent()) {
                    Component activeComponent = insertDataPanel.getActiveComponent().get();
                    InsertDataMethod activeMethod = optionalActiveMethod.get();
                    long dataPosition = ((CaretCapable) codeArea).getDataPosition();
                    EditOperation activeOperation = ((EditModeCapable) codeArea).getActiveOperation();
                    CodeAreaCommand command = activeMethod.createInsertCommand(activeComponent, codeArea, dataPosition, activeOperation);

                    ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).getUndoRedo().execute(command);
                }
                lastMethod = optionalActiveMethod.orElse(null);
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(insertDataPanel::initFocus);
        dialog.showCentered(codeArea);
        insertDataPanel.detachMenu();
    }
}
