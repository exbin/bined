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
package org.exbin.bined.jaguif.component.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.component.NonprintablesState;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.utils.ActionUtils;

/**
 * Show nonprintables actions.
 */
@ParametersAreNonnullByDefault
public class ShowNonprintablesActions {

    public static final String VIEW_NONPRINTABLES_ACTION_ID = "viewNonprintables";
    public static final String TOGGLE_NONPRINTABLES_ACTION_ID = "toggleNonprintables";

    private ResourceBundle resourceBundle;

    public ShowNonprintablesActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public ViewNonprintablesAction createViewNonprintablesAction() {
        ViewNonprintablesAction viewNonprintablesAction = new ViewNonprintablesAction();
        viewNonprintablesAction.init(resourceBundle);
        return viewNonprintablesAction;
    }

    @Nonnull
    public ViewNonprintablesAction createToggleNonprintablesAction() {
        ViewNonprintablesAction viewNonprintablesAction = new ViewNonprintablesAction() {
            @Override
            public void init(ResourceBundle resourceBundle) {
                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                actionModule.initAction(this, resourceBundle, TOGGLE_NONPRINTABLES_ACTION_ID);
                setEnabled(false);
                putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
                putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
            }
        };
        viewNonprintablesAction.init(resourceBundle);
        return viewNonprintablesAction;
    }

    @ParametersAreNonnullByDefault
    public static class ViewNonprintablesAction extends AbstractAction implements ActionContextChange {

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, VIEW_NONPRINTABLES_ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            binaryDataComponent.setShowNonprintables(!binaryDataComponent.isShowNonprintables());
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (NonprintablesState.UpdateType.NONPRINTABLES.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = binaryDataComponent != null;
            if (hasInstance) {
                putValue(Action.SELECTED_KEY, binaryDataComponent.isShowNonprintables());
            }
            setEnabled(hasInstance);
        }
    }
}
