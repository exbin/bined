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
package org.exbin.framework.bined.inspector.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.BinEdComponentInspector;
import org.exbin.framework.file.api.FileHandler;

/**
 * Show parsing panel action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowParsingPanelAction extends AbstractAction {

    public static final String ACTION_ID = "showParsingPanelAction";

    private FileHandler fileHandler;

    public ShowParsingPanelAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(FileHandler.class, (instance) -> {
                    fileHandler = instance;
                    setEnabled(fileHandler instanceof BinEdFileHandler);
                    boolean showParsingPanel = false;
                    if (fileHandler instanceof BinEdFileHandler) {
                        BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
                        BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
                        showParsingPanel = componentExtension.isShowParsingPanel();
                    }
                    putValue(Action.SELECTED_KEY, showParsingPanel);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(fileHandler instanceof BinEdFileHandler)) {
            return;
        }

        BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
        BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
        setShowValuesPanel(!componentExtension.isShowParsingPanel());
    }

    public void setShowValuesPanel(boolean show) {
        if (!(fileHandler instanceof BinEdFileHandler)) {
            return;
        }

        BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
        BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
        componentExtension.setShowParsingPanel(show);
        putValue(Action.SELECTED_KEY, show);
    }
}
