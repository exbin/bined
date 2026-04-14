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
package org.exbin.bined.jaguif.macro.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.macro.MacroManager;
import org.exbin.bined.jaguif.macro.MacroStateUpdateType;
import org.exbin.bined.jaguif.macro.operation.CodeAreaMacroCommandHandler;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.utils.ActionUtils;

/**
 * Execute last macro record action.
 */
@ParametersAreNonnullByDefault
public class ExecuteLastMacroAction extends AbstractAction {

    public static final String ACTION_ID = "executeLastMacro";

    private CodeAreaCore codeArea;
    private ResourceBundle resourceBundle;
    private MacroManager macroManager;

    public ExecuteLastMacroAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                    updateByContext(instance);
                });
                registrar.registerStateUpdateListener(ContextComponent.class, (ContextComponent instance, StateUpdateType updateType) -> {
                    if (MacroStateUpdateType.LAST_MACRO.equals(updateType)) {
                        updateByContext(instance);
                    }
                });
            }
        });
    }

    private void updateByContext(ContextComponent instance) {
        codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
        boolean hasInstance = instance != null;
        boolean actionEnabled = false;
        if (hasInstance) {
            CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
            actionEnabled = commandHandler instanceof CodeAreaMacroCommandHandler && !((CodeAreaMacroCommandHandler) commandHandler).isMacroRecording() && (macroManager.getLastActiveMacro() >= 0);
        }
        setEnabled(actionEnabled);
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            macroManager.executeMacro(codeArea, macroManager.getLastActiveMacro());
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message == null || message.isEmpty()) {
                message = ex.toString();
            } else if (ex.getCause() != null) {
                message += ex.getCause().getMessage();
            }
            JOptionPane.showMessageDialog((Component) e.getSource(), message, resourceBundle.getString("macroExecutionFailed"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
