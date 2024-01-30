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
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.extended.capability.ShowUnprintablesCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.utils.ActionUtils;

/**
 * Show unprintables actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowUnprintablesActions {

    public static final String VIEW_UNPRINTABLES_ACTION_ID = "viewUnprintablesAction";
    public static final String VIEW_UNPRINTABLES_TOOLBAR_ACTION_ID = "viewUnprintablesToolbarAction";

    private ResourceBundle resourceBundle;

    public ShowUnprintablesActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createViewUnprintablesAction() {
        ViewUnprintablesAction viewUnprintablesAction = new ViewUnprintablesAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(viewUnprintablesAction, resourceBundle, VIEW_UNPRINTABLES_ACTION_ID);
        viewUnprintablesAction.putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        viewUnprintablesAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, ActionUtils.getMetaMask()));
        return viewUnprintablesAction;
    }

    @Nonnull
    public Action createViewUnprintablesToolbarAction() {
        ViewUnprintablesAction viewUnprintablesAction = new ViewUnprintablesAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(viewUnprintablesAction, resourceBundle, VIEW_UNPRINTABLES_TOOLBAR_ACTION_ID);
        viewUnprintablesAction.putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        return viewUnprintablesAction;
    }

    @ParametersAreNonnullByDefault
    public static class ViewUnprintablesAction extends AbstractAction implements ActionActiveComponent {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean showUnprintables = ((ShowUnprintablesCapable) codeArea).isShowUnprintables();
            ((ShowUnprintablesCapable) codeArea).setShowUnprintables(!showUnprintables);
            App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
        }

        @Nonnull
        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(CodeAreaCore.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            boolean hasInstance = !affectedClasses.isEmpty();
            codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
            if (hasInstance) {
                boolean showUnprintables = ((ShowUnprintablesCapable) codeArea).isShowUnprintables();
                putValue(Action.SELECTED_KEY, showUnprintables);
            }
            setEnabled(hasInstance);
        }
    }
}
