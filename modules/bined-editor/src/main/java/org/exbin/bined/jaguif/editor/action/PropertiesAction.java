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
package org.exbin.bined.jaguif.editor.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.bined.jaguif.component.BinaryFileDocument;
import org.exbin.bined.jaguif.editor.gui.BinEdFilePropertiesPanel;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.window.api.gui.CloseControlPanel;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.window.api.WindowModuleApi;

/**
 * Properties action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PropertiesAction extends AbstractAction {

    public static final String ACTION_ID = "propertiesAction";

    private DialogParentComponent dialogParentComponent;
    private BinaryFileDocument binaryDocument;

    public PropertiesAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                    binaryDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                    setEnabled(binaryDocument != null);
                });
                registrar.registerChangeListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                    dialogParentComponent = instance;
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        BinEdFilePropertiesPanel propertiesPanel = new BinEdFilePropertiesPanel();
        propertiesPanel.setBinaryDocument(binaryDocument);
        CloseControlPanel controlPanel = new CloseControlPanel();
        final WindowHandler dialog = windowModule.createDialog(propertiesPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, propertiesPanel.getResourceBundle());
        controlPanel.setController(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(dialogParentComponent.getComponent());
    }
}
