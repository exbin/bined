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
package org.exbin.bined.jaguif.inspector.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.bined.jaguif.inspector.BinEdInspectorComponentExtension;

/**
 * Show parsing panel action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowParsingPanelAction extends AbstractAction {

    public static final String ACTION_ID = "showParsingPanel";

    private BinaryDataComponent binaryComponent;

    public ShowParsingPanelAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                    binaryComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                    setEnabled(binaryComponent != null);
                    boolean showParsingPanel = false;
                    if (binaryComponent != null) {
                        BinEdInspectorComponentExtension componentExtension = binaryComponent.getComponentExtension(BinEdInspectorComponentExtension.class);
                        showParsingPanel = componentExtension.isShowParsingPanel();
                    }
                    putValue(Action.SELECTED_KEY, showParsingPanel);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BinEdInspectorComponentExtension componentExtension = binaryComponent.getComponentExtension(BinEdInspectorComponentExtension.class);
        setShowParsingPanel(!componentExtension.isShowParsingPanel());
    }

    public void setShowParsingPanel(boolean show) {
        BinEdInspectorComponentExtension componentExtension = binaryComponent.getComponentExtension(BinEdInspectorComponentExtension.class);
        componentExtension.setShowParsingPanel(show);
        putValue(Action.SELECTED_KEY, show);
    }
}
