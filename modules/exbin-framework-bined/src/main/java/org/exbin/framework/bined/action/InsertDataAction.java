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
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.basic.CodeArea;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.XBFrameworkUtils;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.SearchCondition;
import org.exbin.framework.bined.gui.BinaryMultilinePanel;
import org.exbin.framework.bined.gui.InsertDataPanel;
import org.exbin.framework.bined.operation.InsertDataOperation;
import org.exbin.framework.bined.operation.ReplaceDataOperation;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.bined.BinedModule;
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
        final EditableBinaryData sampleBinaryData = new ByteArrayEditableData();
        final InsertDataPanel insertDataPanel = new InsertDataPanel();
        DefaultControlPanel controlPanel = new DefaultControlPanel(insertDataPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(insertDataPanel, controlPanel);
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
        insertDataPanel.setController(() -> {
            BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
            final BinaryMultilinePanel multilinePanel = new BinaryMultilinePanel();
            SearchCondition searchCondition = new SearchCondition();
            EditableBinaryData conditionData = new ByteArrayEditableData();
            conditionData.insert(0, sampleBinaryData);
            searchCondition.setBinaryData(conditionData);
            searchCondition.setSearchMode(SearchCondition.SearchMode.BINARY);
            multilinePanel.setCondition(searchCondition);
            multilinePanel.setCodeAreaPopupMenuHandler(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC));
            DefaultControlPanel controlPanel1 = new DefaultControlPanel();
            JPanel dialogPanel1 = WindowUtils.createDialogPanel(multilinePanel, controlPanel1);
            final DialogWrapper multilineDialog = frameModule.createDialog(dialog.getWindow(), Dialog.ModalityType.APPLICATION_MODAL, dialogPanel1);
            frameModule.setDialogTitle(multilineDialog, multilinePanel.getResourceBundle());
            controlPanel1.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                    SearchCondition condition = multilinePanel.getCondition();
                    sampleBinaryData.clear();
                    sampleBinaryData.insert(0, condition.getBinaryData());
                    insertDataPanel.setFillWith(InsertDataOperation.FillWithType.SAMPLE);
                    long dataLength = insertDataPanel.getDataLength();
                    if (dataLength < sampleBinaryData.getDataSize()) {
                        insertDataPanel.setDataLength(sampleBinaryData.getDataSize());
                    }
                }

                multilineDialog.close();
                multilineDialog.dispose();
            });
            multilineDialog.showCentered(dialog.getWindow());
//                    multilinePanel.detachMenu();
        });
        WindowUtils.addHeaderPanel(dialog.getWindow(), insertDataPanel.getClass(), insertDataPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, insertDataPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                insertDataPanel.acceptInput();
                long dataLength = insertDataPanel.getDataLength();
                InsertDataOperation.FillWithType fillWithType = insertDataPanel.getFillWithType();
                EditOperation activeOperation = codeArea instanceof CodeArea ? ((CodeArea) codeArea).getActiveOperation() : ((ExtCodeArea) codeArea).getActiveOperation();
                CodeAreaCommand command;
                switch (activeOperation) {
                    case INSERT: {
                        InsertDataOperation operation = new InsertDataOperation(codeArea, ((CaretCapable) codeArea).getDataPosition(), dataLength, fillWithType, sampleBinaryData);
                        command = new InsertDataOperation.InsertDataCommand(operation);
                        break;
                    }
                    case OVERWRITE: {
                        ReplaceDataOperation operation = new ReplaceDataOperation(codeArea, ((CaretCapable) codeArea).getDataPosition(), dataLength, fillWithType, sampleBinaryData);
                        command = new ReplaceDataOperation.ReplaceDataCommand(operation);
                        break;
                    }
                    default:
                        throw XBFrameworkUtils.getInvalidTypeException(activeOperation);
                }
                try {
                    ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).getUndoHandler().execute(command);
                } catch (BinaryDataOperationException ex) {
                    Logger.getLogger(InsertDataAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(insertDataPanel::initFocus);
        dialog.showCentered(codeArea);
    }
}
