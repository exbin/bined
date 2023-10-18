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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.EditOperation;
import static org.exbin.bined.EditOperation.INSERT;
import static org.exbin.bined.EditOperation.OVERWRITE;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.basic.CodeArea;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.XBFrameworkUtils;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.action.CodeAreaAction;
import org.exbin.framework.bined.blockedit.gui.InsertDataPanel;
import org.exbin.framework.bined.blockedit.gui.ModifyDataPanel;
import org.exbin.framework.bined.blockedit.operation.InsertDataOperation;
import org.exbin.framework.bined.blockedit.operation.ReplaceDataOperation;
import org.exbin.framework.bined.search.SearchCondition;
import org.exbin.framework.bined.search.gui.BinaryMultilinePanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;

/**
 * Modify data action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ModifyDataAction extends AbstractAction implements CodeAreaAction {

    public static final String ACTION_ID = "modifyDataAction";

    private XBApplication application;
    private ResourceBundle resourceBundle;
    private CodeAreaCore codeArea;

    public ModifyDataAction() {

    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, ActionUtils.getMetaMask()));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveCodeArea(@Nullable CodeAreaCore codeArea) {
        this.codeArea = codeArea;
        setEnabled(codeArea != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final ModifyDataPanel modifyDataPanel = new ModifyDataPanel();
        DefaultControlPanel controlPanel = new DefaultControlPanel(modifyDataPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(modifyDataPanel, controlPanel);
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
        WindowUtils.addHeaderPanel(dialog.getWindow(), modifyDataPanel.getClass(), modifyDataPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, modifyDataPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(modifyDataPanel::initFocus);
        dialog.showCentered(codeArea);
    }
}
