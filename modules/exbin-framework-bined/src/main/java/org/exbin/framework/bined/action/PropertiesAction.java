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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.bined.gui.PropertiesPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.window.api.gui.CloseControlPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Properties action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PropertiesAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "propertiesAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;

    public PropertiesAction() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        setEnabled(activeFile.isPresent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        PropertiesPanel propertiesPanel = new PropertiesPanel();
        propertiesPanel.setEditorProvider(editorProvider);
        CloseControlPanel controlPanel = new CloseControlPanel();
        final WindowHandler dialog = windowModule.createDialog(propertiesPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), propertiesPanel.getClass(), propertiesPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, propertiesPanel.getResourceBundle());
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered((Component) e.getSource());
    }
}
