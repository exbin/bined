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
package org.exbin.framework.bined.editor.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileHandler;

/**
 * Reload content of the currently active file.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ReloadFileAction extends AbstractAction {

    public static final String ACTION_ID = "reloadFileAction";

    private EditorProvider editorProvider;
    private FileHandler fileHandler;

    public ReloadFileAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(FileHandler.class, (instance) -> {
                    fileHandler = instance;
                    setEnabled(fileHandler instanceof BinEdFileHandler && (editorProvider instanceof MultiEditorProvider));
                });
                registrar.registerUpdateListener(EditorProvider.class, (instance) -> {
                    editorProvider = instance;
                    setEnabled(fileHandler instanceof BinEdFileHandler && (editorProvider instanceof MultiEditorProvider));
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileHandler instanceof BinEdFileHandler && (editorProvider instanceof MultiEditorProvider)) {
            if (editorProvider.releaseFile(fileHandler)) {
                if (fileHandler.getFileUri().isPresent()) {
                    ((BinEdFileHandler) fileHandler).reloadFile();
                }
            }
        }
    }
}
