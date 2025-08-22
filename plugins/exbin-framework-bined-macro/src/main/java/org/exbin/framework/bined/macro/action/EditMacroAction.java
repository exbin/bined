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
package org.exbin.framework.bined.macro.action;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.macro.gui.MacroEditorPanel;
import org.exbin.framework.bined.macro.model.MacroRecord;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Edit macro record action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditMacroAction extends AbstractAction {

    public static final String ACTION_ID = "editMacroAction";

    private MacroRecord macroRecord;
    private DialogParentComponent dialogParentComponent;

    public EditMacroAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
            manager.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                dialogParentComponent = instance;
            });
        });
    }

    @Nonnull
    public Optional<MacroRecord> getMacroRecord() {
        return Optional.ofNullable(macroRecord);
    }

    public void setMacroRecord(@Nullable MacroRecord macroRecord) {
        this.macroRecord = macroRecord;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MacroEditorPanel macroEditorPanel = new MacroEditorPanel();
        macroEditorPanel.setMacroRecord(macroRecord);
        ResourceBundle panelResourceBundle = macroEditorPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(dialogParentComponent.getComponent(), Dialog.ModalityType.APPLICATION_MODAL, macroEditorPanel, controlPanel);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        controlPanel.setController((actionType) -> {
            switch (actionType) {
                case OK: {
                    macroRecord.setRecord(macroEditorPanel.getMacroRecord());
                    break;
                }
                case CANCEL: {
                    macroRecord = null;
                    break;
                }
            }
            dialog.close();
        });

        dialog.showCentered(dialogParentComponent.getComponent());
    }
}
