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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.operation.api.CopyAsDataMethod;
import org.exbin.framework.bined.operation.api.DataOperationMethod;
import org.exbin.framework.bined.operation.gui.DataOperationPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Copy binary data as specific form action.
 */
@ParametersAreNonnullByDefault
public class CopyAsAction extends AbstractAction {

    public static final String ACTION_ID = "copyAsAction";
    public static final String HELP_ID = "copy-as-action";

    private static final int PREVIEW_LENGTH_LIMIT = 4096;

    private CodeAreaCore codeArea;
    private CopyAsDataMethod lastMethod = null;

    public CopyAsAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    boolean hasInstance = instance != null;
                    setEnabled(hasInstance);
                });
            }
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DataOperationPanel dataOperationPanel = new DataOperationPanel();
        dataOperationPanel.setController(() -> {
            Optional<DataOperationMethod> optionalActiveMethod = dataOperationPanel.getActiveMethod();
            if (optionalActiveMethod.isPresent()) {
                CopyAsDataMethod activeMethod = (CopyAsDataMethod) optionalActiveMethod.get();
                Component activeComponent = dataOperationPanel.getActiveComponent().get();
                activeMethod.requestPreview((component) -> {
                    dataOperationPanel.setPreviewComponent(component);
                }, activeComponent, codeArea, PREVIEW_LENGTH_LIMIT);
            }
        });
        ResourceBundle panelResourceBundle = App.getModule(LanguageModuleApi.class).getResourceBundleByBundleName("org.exbin.framework.bined.operation.gui.resources.CopyAsDataControlPanel");
        DefaultControlPanel controlPanel = new DefaultControlPanel();
//        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
//        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        JPanel dialogPanel = windowModule.createDialogPanel(dataOperationPanel, controlPanel);
        BinedOperationModule binedBlockEditModule = App.getModule(BinedOperationModule.class);
        dataOperationPanel.setDataMethods(binedBlockEditModule.getCopyAsDataMethods());
        dataOperationPanel.selectActiveMethod(lastMethod);
        final WindowHandler dialog = windowModule.createWindow(dialogPanel, codeArea, "", Dialog.ModalityType.APPLICATION_MODAL);
        windowModule.addHeaderPanel(dialog.getWindow(), dataOperationPanel.getClass(), panelResourceBundle);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            if (actionType == DefaultControlController.ControlActionType.OK) {
                Optional<DataOperationMethod> optionalActiveMethod = dataOperationPanel.getActiveMethod();
                if (optionalActiveMethod.isPresent()) {
                    Component activeComponent = dataOperationPanel.getActiveComponent().get();
                    CopyAsDataMethod activeMethod = (CopyAsDataMethod) optionalActiveMethod.get();
                    activeMethod.performCopy(activeComponent, codeArea);
                }
                lastMethod = (CopyAsDataMethod) optionalActiveMethod.orElse(null);
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(dataOperationPanel::initFocus);
        dialog.showCentered(codeArea);
    }
}
