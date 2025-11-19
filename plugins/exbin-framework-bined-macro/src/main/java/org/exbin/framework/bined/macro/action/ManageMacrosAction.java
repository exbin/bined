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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.macro.BinedMacroModule;
import org.exbin.framework.bined.macro.MacroManager;
import org.exbin.framework.bined.macro.gui.MacrosManagerPanel;
import org.exbin.framework.bined.macro.model.MacroRecord;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Manage macros action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ManageMacrosAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "manageMacrosAction";

    private DialogParentComponent dialogParentComponent;

    public ManageMacrosAction() {
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
        BinedMacroModule macroModule = App.getModule(BinedMacroModule.class);
        MacroManager macroManager = macroModule.getMacroManager();
        final MacrosManagerPanel macrosPanel = macroManager.createMacrosManagerPanel();
        List<MacroRecord> records = new ArrayList<>();
        for (MacroRecord record : macroManager.getMacroRecords()) {
            records.add(new MacroRecord(record));
        }
        macrosPanel.setMacroRecords(records);
        ResourceBundle panelResourceBundle = macrosPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(dialogParentComponent.getComponent(), Dialog.ModalityType.APPLICATION_MODAL, macrosPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), macrosPanel.getClass(), macrosPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        Dimension preferredSize = dialog.getWindow().getPreferredSize();
        dialog.getWindow().setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 450));
        controlPanel.setController((actionType) -> {
            switch (actionType) {
                case OK: {
                    List<MacroRecord> bookmarkRecords = macrosPanel.getMacroRecords();
                    macroManager.setMacroRecords(bookmarkRecords);
                    dialog.close();
                    break;
                }
                case CANCEL: {
                    dialog.close();
                    break;
                }
            }
        });

        dialog.showCentered(dialogParentComponent.getComponent());
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
            dialogParentComponent = instance;
            setEnabled(dialogParentComponent != null);
        });
    }
}
