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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.NonprintablesState;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.utils.ActionUtils;

/**
 * Show nonprintables actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowNonprintablesActions {

    public static final String VIEW_NONPRINTABLES_ACTION_ID = "viewNonprintablesAction";
    public static final String VIEW_NONPRINTABLES_TOOLBAR_ACTION_ID = "viewNonprintablesToolbarAction";

    private ResourceBundle resourceBundle;

    public ShowNonprintablesActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public ViewNonprintablesAction createViewNonprintablesAction() {
        ViewNonprintablesAction viewNonprintablesAction = new ViewNonprintablesAction();
        viewNonprintablesAction.setup(resourceBundle);
        return viewNonprintablesAction;
    }

    @Nonnull
    public ViewNonprintablesAction createViewNonprintablesToolbarAction() {
        ViewNonprintablesAction viewNonprintablesAction = new ViewNonprintablesAction() {
            @Override
            public void setup(ResourceBundle resourceBundle) {
                ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
                actionModule.initAction(this, resourceBundle, VIEW_NONPRINTABLES_TOOLBAR_ACTION_ID);
                setEnabled(false);
                putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
                putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
            }
        };
        viewNonprintablesAction.setup(resourceBundle);
        return viewNonprintablesAction;
    }

    @ParametersAreNonnullByDefault
    public static class ViewNonprintablesAction extends AbstractAction implements ActionContextChange {

        private BinaryDataComponent binaryDataComponent;

        public void setup(ResourceBundle resourceBundle) {
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
            registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateChangeListener(ContextComponent.class, (instance, changeType) -> {
                if (NonprintablesState.ChangeType.NONPRINTABLES.equals(changeType)) {
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
