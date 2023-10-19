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
package org.exbin.framework.bined.blockedit.component;

import java.awt.Component;
import java.awt.Dialog;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.blockedit.api.InsertDataComponent;
import org.exbin.framework.bined.blockedit.component.gui.SimpleFillDataPanel;
import org.exbin.framework.bined.search.SearchCondition;
import org.exbin.framework.bined.search.gui.BinaryMultilinePanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;

/**
 * Simple fill data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SimpleFillDataComponent implements InsertDataComponent {

    private XBApplication application;

    @Nonnull
    @Override
    public String getName() {
        return "";
    }

    @Nonnull
    @Override
    public Component getComponent() {
        SimpleFillDataPanel component = new SimpleFillDataPanel();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final EditableBinaryData sampleBinaryData = new ByteArrayEditableData();
        component.setController(() -> {
            BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
            final BinaryMultilinePanel multilinePanel = new BinaryMultilinePanel();
            final DialogWrapper dialog = null; // TODO WindowUtils.createDialog(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
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
/*                    insertDataPanel.setFillWith(InsertDataOperation.FillWithType.SAMPLE);
                    long dataLength = insertDataPanel.getDataLength();
                    if (dataLength < sampleBinaryData.getDataSize()) {
                        insertDataPanel.setDataLength(sampleBinaryData.getDataSize());
                    } */
                }

                multilineDialog.close();
                multilineDialog.dispose();
            });
            multilineDialog.showCentered(dialog.getWindow());
//                    multilinePanel.detachMenu();
        });
        return component;
    }
}
