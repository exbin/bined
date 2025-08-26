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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.macro.MacroManager;
import org.exbin.framework.bined.macro.operation.CodeAreaMacroCommandHandler;
import org.exbin.framework.utils.ActionUtils;

/**
 * Execute last macro record action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ExecuteLastMacroAction extends AbstractAction {

    public static final String ACTION_ID = "executeLastMacroAction";

    private CodeAreaCore codeArea;
    private ResourceBundle resourceBundle;
    private MacroManager macroManager;

    public ExecuteLastMacroAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    boolean hasInstance = instance != null;
                    boolean enabled = false;
                    if (hasInstance) {
                        CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                        enabled = commandHandler instanceof CodeAreaMacroCommandHandler && !((CodeAreaMacroCommandHandler) commandHandler).isMacroRecording() && (macroManager.getLastActiveMacro() >= 0);
                    }
                    setEnabled(enabled);
                });
            }
        });
        setEnabled(false);
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
