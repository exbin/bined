/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.viewer.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.bined.jaguif.viewer.status.gui.BinaryDataSizeComponent;

/**
 * Show relative data size action.
 */
@NullMarked
public class ShowRelativeDataSizeAction extends AbstractAction {

    public static final String ACTION_ID = "showRelativeDataSize";

    private BinaryDataSizeComponent statusComponent;

    public ShowRelativeDataSizeAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(BinaryDataSizeComponent.class, (instance) -> {
                    statusComponent = instance;
                    update();
                });
                registrar.registerStateUpdateListener(BinaryDataSizeComponent.class, (instance, updateType) -> {
                    if (BinaryDataSizeComponent.UpdateType.DATA_SIZE_FORMAT.equals(updateType)) {
                        update();
                    }
                });
            }

            private void update() {
                setEnabled(statusComponent != null);
                putValue(Action.SELECTED_KEY, statusComponent.getDataSizeFormat().isShowRelative());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        statusComponent.setDataSizeShowRelative(!statusComponent.getDataSizeFormat().isShowRelative());
    }
}
