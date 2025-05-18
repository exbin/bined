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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.capability.ViewModeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeManager;

/**
 * View mode actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewModeHandlerActions {

    public static final String VIEW_MODE_RADIO_GROUP_ID = "viewModeRadioGroup";

    private ResourceBundle resourceBundle;

    public ViewModeHandlerActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public DualModeAction createDualModeAction() {
        DualModeAction dualModeAction = new DualModeAction();
        dualModeAction.setup(resourceBundle);
        return dualModeAction;
    }

    @Nonnull
    public CodeMatrixModeAction createCodeMatrixModeAction() {
        CodeMatrixModeAction codeMatrixModeAction = new CodeMatrixModeAction();
        codeMatrixModeAction.setup(resourceBundle);
        return codeMatrixModeAction;
    }

    @Nonnull
    public TextPreviewModeAction createTextPreviewModeAction() {
        TextPreviewModeAction textPreviewModeAction = new TextPreviewModeAction();
        textPreviewModeAction.setup(resourceBundle);
        return textPreviewModeAction;
    }

    @ParametersAreNonnullByDefault
    public static class DualModeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "dualViewModeAction";

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ViewModeCapable) codeArea).setViewMode(CodeAreaViewMode.DUAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeAreaViewMode viewMode = ((ViewModeCapable) codeArea).getViewMode();
                    putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.DUAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class CodeMatrixModeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "codeMatrixViewModeAction";

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ViewModeCapable) codeArea).setViewMode(CodeAreaViewMode.CODE_MATRIX);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeAreaViewMode viewMode = ((ViewModeCapable) codeArea).getViewMode();
                    putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.CODE_MATRIX);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class TextPreviewModeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "textPreviewViewModeAction";

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ViewModeCapable) codeArea).setViewMode(CodeAreaViewMode.TEXT_PREVIEW);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeAreaViewMode viewMode = ((ViewModeCapable) codeArea).getViewMode();
                    putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.TEXT_PREVIEW);
                }
                setEnabled(hasInstance);
            });
        }
    }
}
