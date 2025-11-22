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
import org.exbin.framework.bined.BinaryFileDocument;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.document.api.ContextDocument;

/**
 * Reload content of the currently active file.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ReloadFileAction extends AbstractAction {

    public static final String ACTION_ID = "reloadFileAction";

    protected BinaryFileDocument binaryFileDocument;

    public ReloadFileAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextDocument.class, (instance) -> {
                    binaryFileDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                    setEnabled(binaryFileDocument != null);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        binaryFileDocument.reloadFile();
    }
}
