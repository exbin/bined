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
package org.exbin.framework.bined.blockedit.action;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.basic.CodeArea;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.blockedit.gui.InsertDataPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.bined.action.CodeAreaAction;
import org.exbin.framework.bined.blockedit.BinedBlockEditModule;
import org.exbin.framework.bined.blockedit.api.InsertDataMethod;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Insert data action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InsertDataAction extends AbstractAction implements CodeAreaAction {

    public static final String ACTION_ID = "insertDataAction";

    private XBApplication application;
    private ResourceBundle resourceBundle;
    private CodeAreaCore codeArea;

    public InsertDataAction() {

    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, ActionUtils.getMetaMask()));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveCodeArea(@Nullable CodeAreaCore codeArea) {
        this.codeArea = codeArea;
        setEnabled(codeArea != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final InsertDataPanel insertDataPanel = new InsertDataPanel();
        ResourceBundle panelResourceBundle = insertDataPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(insertDataPanel, controlPanel);
        BinedBlockEditModule binedBlockEditModule = application.getModuleRepository().getModuleByInterface(BinedBlockEditModule.class);
        insertDataPanel.setComponents(binedBlockEditModule.getInsertDataComponents());
        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
        insertDataPanel.setCodeAreaPopupMenuHandler(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.NORMAL));
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
        WindowUtils.addHeaderPanel(dialog.getWindow(), insertDataPanel.getClass(), panelResourceBundle);
        frameModule.setDialogTitle(dialog, panelResourceBundle);
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                Optional<InsertDataMethod> optionalActiveMethod = insertDataPanel.getActiveMethod();
                if (optionalActiveMethod.isPresent()) {
                    Component activeComponent = insertDataPanel.getActiveComponent().get();
                    InsertDataMethod activeMethod = optionalActiveMethod.get();
                    long dataPosition = ((CaretCapable) codeArea).getDataPosition();
                    EditOperation activeOperation = codeArea instanceof CodeArea ? ((CodeArea) codeArea).getActiveOperation() : ((ExtCodeArea) codeArea).getActiveOperation();
                    CodeAreaCommand command = activeMethod.createInsertCommand(activeComponent, codeArea, dataPosition, activeOperation);

                    try {
                        ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).getUndoHandler().execute(command);
                    } catch (BinaryDataOperationException ex) {
                        Logger.getLogger(InsertDataAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(insertDataPanel::initFocus);
        dialog.showCentered(codeArea);
        insertDataPanel.detachMenu();
    }
}
