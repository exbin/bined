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

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.editor.text.EditorTextModule;
import org.exbin.framework.editor.text.action.TextFontAction;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;

/**
 * Code area change font action.
 * 
 * TODO: Merge with TextFontAction action
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaFontAction extends AbstractAction {

    public static final String ACTION_ID = "codeAreaFontAction";

    private EditorProvider editorProvider;
    private ResourceBundle resourceBundle;
    private TextFontAction textFontAction;

    public CodeAreaFontAction() {
    }

    public void setup(EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Override
            public void register(ComponentActivationManager manager) {
                manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                    setEnabled(instance != null);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (textFontAction == null) {
            textFontAction = new TextFontAction();
            textFontAction.setup(App.getModule(LanguageModuleApi.class).getBundle(EditorTextModule.class));
        }
        textFontAction.actionPerformed(e);
        // App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
    }
}
