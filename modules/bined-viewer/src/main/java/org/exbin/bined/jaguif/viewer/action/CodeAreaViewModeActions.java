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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.capability.ViewModeCapable;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.viewer.BinedViewerUpdateType;

/**
 * View mode actions.
 */
@ParametersAreNonnullByDefault
public class CodeAreaViewModeActions {

    public static final String VIEW_MODE_RADIO_GROUP_ID = "viewModeRadioGroup";

    private ResourceBundle resourceBundle;

    public CodeAreaViewModeActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public DualModeAction createDualModeAction() {
        DualModeAction dualModeAction = new DualModeAction();
        dualModeAction.init(resourceBundle);
        return dualModeAction;
    }

    @Nonnull
    public CodeMatrixModeAction createCodeMatrixModeAction() {
        CodeMatrixModeAction codeMatrixModeAction = new CodeMatrixModeAction();
        codeMatrixModeAction.init(resourceBundle);
        return codeMatrixModeAction;
    }

    @Nonnull
    public TextPreviewModeAction createTextPreviewModeAction() {
        TextPreviewModeAction textPreviewModeAction = new TextPreviewModeAction();
        textPreviewModeAction.init(resourceBundle);
        return textPreviewModeAction;
    }

    @Nonnull
    public ViewModeContribution createDualViewModeContribution() {
        ViewModeContribution viewModeContribution = new ViewModeContribution(DualModeAction.ACTION_ID, CodeAreaViewMode.DUAL);
        return viewModeContribution;
    }

    @Nonnull
    public ViewModeContribution createMatrixModeViewModeContribution() {
        ViewModeContribution viewModeContribution = new ViewModeContribution(DualModeAction.ACTION_ID, CodeAreaViewMode.CODE_MATRIX);
        return viewModeContribution;
    }

    @Nonnull
    public ViewModeContribution createTextPreviewViewModeContribution() {
        ViewModeContribution viewModeContribution = new ViewModeContribution(DualModeAction.ACTION_ID, CodeAreaViewMode.TEXT_PREVIEW);
        return viewModeContribution;
    }

    @ParametersAreNonnullByDefault
    public static class DualModeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "dualViewMode";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ViewModeCapable) binaryDataComponent.getCodeArea()).setViewMode(CodeAreaViewMode.DUAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (BinedViewerUpdateType.VIEW_MODE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeAreaViewMode viewMode = ((ViewModeCapable) binaryDataComponent.getCodeArea()).getViewMode();
                putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.DUAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public static class CodeMatrixModeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "codeMatrixViewMode";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ViewModeCapable) binaryDataComponent.getCodeArea()).setViewMode(CodeAreaViewMode.CODE_MATRIX);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (BinedViewerUpdateType.VIEW_MODE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeAreaViewMode viewMode = ((ViewModeCapable) binaryDataComponent.getCodeArea()).getViewMode();
                putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.CODE_MATRIX);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public static class TextPreviewModeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "textPreviewViewMode";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((ViewModeCapable) binaryDataComponent.getCodeArea()).setViewMode(CodeAreaViewMode.TEXT_PREVIEW);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (BinedViewerUpdateType.VIEW_MODE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeAreaViewMode viewMode = ((ViewModeCapable) binaryDataComponent.getCodeArea()).getViewMode();
                putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.TEXT_PREVIEW);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public class ViewModeContribution implements ActionSequenceContribution {

        private final String contributionId;
        private final CodeAreaViewMode codeAreaViewMode;

        public ViewModeContribution(String contributionId, CodeAreaViewMode codeAreaViewMode) {
            this.contributionId = contributionId;
            this.codeAreaViewMode = codeAreaViewMode;
        }

        @Nonnull
        @Override
        public Action createAction() {
            switch (codeAreaViewMode) {
                case DUAL: {
                    DualModeAction action = new DualModeAction();
                    action.init(resourceBundle);
                    return action;
                }
                case CODE_MATRIX: {
                    CodeMatrixModeAction action = new CodeMatrixModeAction();
                    action.init(resourceBundle);
                    return action;
                }
                case TEXT_PREVIEW: {
                    TextPreviewModeAction action = new TextPreviewModeAction();
                    action.init(resourceBundle);
                    return action;
                }
            }

            throw new IllegalStateException();
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return contributionId;
        }
    }

    public static void setViewMode(BinaryDataComponent binaryDataComponent, CodeAreaViewMode viewMode) {
        ((ViewModeCapable) binaryDataComponent.getCodeArea()).setViewMode(viewMode);
        // TODO invoke change notification
    }
}
