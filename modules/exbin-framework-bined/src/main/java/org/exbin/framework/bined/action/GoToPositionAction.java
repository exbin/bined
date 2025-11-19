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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.gui.GoToPositionPanel;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.controller.DefaultControlController;
import org.exbin.framework.window.api.controller.DefaultControlController.ControlActionType;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Go to position action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToPositionAction extends AbstractAction {

    public static final String ACTION_ID = "goToPositionAction";
    public static final String HELP_ID = "go-to-position";

    private CodeAreaCore codeArea;

    public GoToPositionAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, ActionUtils.getMetaMask()));
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    setEnabled(instance != null);
                });
            }
        });
    }

    public void setCodeArea(CodeAreaCore codeArea) {
        this.codeArea = codeArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GoToPositionPanel goToPanel = new GoToPositionPanel();
        goToPanel.setCursorPosition(((CaretCapable) codeArea).getDataPosition());
        goToPanel.setMaxPosition(codeArea.getDataSize());
        DefaultControlPanel controlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(codeArea, Dialog.ModalityType.APPLICATION_MODAL, goToPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), goToPanel.getClass(), goToPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, goToPanel.getResourceBundle());
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                goToPanel.acceptInput();
                ((CaretCapable) codeArea).setActiveCaretPosition(goToPanel.getTargetPosition());
                ((ScrollingCapable) codeArea).revealCursor();
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(goToPanel::initFocus);
        dialog.showCentered(codeArea);
    }
}
