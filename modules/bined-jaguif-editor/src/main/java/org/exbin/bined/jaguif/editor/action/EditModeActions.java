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
package org.exbin.bined.jaguif.editor.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;

/**
 * Binary edit mode operation actions.
 */
@NullMarked
public class EditModeActions {

    private ResourceBundle resourceBundle;

    public EditModeActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public SwitchEditOperationAction createSwitchEditOperationAction() {
        SwitchEditOperationAction switchEditOperationAction = new SwitchEditOperationAction();
        switchEditOperationAction.init(resourceBundle);
        return switchEditOperationAction;
    }

    public InsertEditModeOperationAction createInsertEditModeOperationAction() {
        InsertEditModeOperationAction insertEditModeOperationAction = new InsertEditModeOperationAction();
        insertEditModeOperationAction.init(resourceBundle);
        return insertEditModeOperationAction;
    }

    public OverwriteEditModeOperationAction createOverwriteEditModeOperationAction() {
        OverwriteEditModeOperationAction overwriteEditModeOperationAction = new OverwriteEditModeOperationAction();
        overwriteEditModeOperationAction.init(resourceBundle);
        return overwriteEditModeOperationAction;
    }

    public SwitchEditOperationContribution createSwitchEditOperationContribution() {
        SwitchEditOperationContribution switchEditOperationContribution = new SwitchEditOperationContribution(SwitchEditOperationAction.ACTION_ID);
        return switchEditOperationContribution;
    }

    public InsertEditModeOperationContribution createInsertEditModeOperationContribution() {
        InsertEditModeOperationContribution insertEditModeOperationContribution = new InsertEditModeOperationContribution(InsertEditModeOperationAction.ACTION_ID);
        return insertEditModeOperationContribution;
    }

    public OverwriteEditModeOperationContribution createOverwriteEditModeOperationContribution() {
        OverwriteEditModeOperationContribution overwriteEditModeOperationContribution = new OverwriteEditModeOperationContribution(OverwriteEditModeOperationAction.ACTION_ID);
        return overwriteEditModeOperationContribution;
    }

    @NullMarked
    public class SwitchEditOperationAction extends AbstractAction {

        public static final String ACTION_ID = "switchEditOperation";

        private BinaryDataComponent binaryDataComponent;

        public SwitchEditOperationAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.PUSH);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ContextChangeRegistration registrar) {
                    registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                        binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                        setEnabled(instance != null);
                    });
                }
            });
            setEnabled(false);
        }

        public void setBinaryDataComponent(BinaryDataComponent binaryDataComponent) {
            this.binaryDataComponent = binaryDataComponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeAreaCore codeArea = binaryDataComponent.getCodeArea();
            EditMode editMode = ((EditModeCapable) codeArea).getEditMode();
            if (editMode == EditMode.EXPANDING || editMode == EditMode.CAPPED) {
                EditOperation activeOperation = ((EditModeCapable) codeArea).getActiveOperation();
                binaryDataComponent.setEditOperation(activeOperation == EditOperation.INSERT ? EditOperation.OVERWRITE : EditOperation.INSERT);
            }
        }
    }

    @NullMarked
    public class InsertEditModeOperationAction extends AbstractAction {

        public static final String ACTION_ID = "insertEditModeOperation";

        private BinaryDataComponent binaryDataComponent;

        public InsertEditModeOperationAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ContextChangeRegistration registrar) {
                    registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                        binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                        update();
                    });
                    registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                        if (BinEdDataComponent.UpdateType.EDIT_MODE.equals(updateType)) {
                            update();
                        }
                    });
                }

                void update() {
                    setEnabled(binaryDataComponent != null);
                    putValue(Action.SELECTED_KEY, ((EditModeCapable) binaryDataComponent.getCodeArea()).getActiveOperation() == EditOperation.INSERT);
                }
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeAreaCore codeArea = binaryDataComponent.getCodeArea();
            EditMode editMode = ((EditModeCapable) codeArea).getEditMode();
            if (editMode == EditMode.EXPANDING || editMode == EditMode.CAPPED) {
                binaryDataComponent.setEditOperation(EditOperation.INSERT);
            }
        }
    }

    @NullMarked
    public class OverwriteEditModeOperationAction extends AbstractAction {

        public static final String ACTION_ID = "overwriteEditModeOperation";

        private BinaryDataComponent binaryDataComponent;

        public OverwriteEditModeOperationAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
                @Override
                public void register(ContextChangeRegistration registrar) {
                    registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                        binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                        update();
                    });
                    registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                        if (BinEdDataComponent.UpdateType.EDIT_MODE.equals(updateType)) {
                            update();
                        }
                    });
                }

                void update() {
                    setEnabled(binaryDataComponent != null);
                    putValue(Action.SELECTED_KEY, ((EditModeCapable) binaryDataComponent.getCodeArea()).getActiveOperation() == EditOperation.OVERWRITE);
                }
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeAreaCore codeArea = binaryDataComponent.getCodeArea();
            EditMode editMode = ((EditModeCapable) codeArea).getEditMode();
            if (editMode == EditMode.EXPANDING || editMode == EditMode.CAPPED) {
                binaryDataComponent.setEditOperation(EditOperation.OVERWRITE);
            }
        }
    }

    @NullMarked
    public class SwitchEditOperationContribution implements ActionSequenceContribution {

        private final String contributionId;

        public SwitchEditOperationContribution(String contributionId) {
            this.contributionId = contributionId;
        }

        @Override
        public Action createAction() {
            SwitchEditOperationAction action = new SwitchEditOperationAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return contributionId;
        }
    }

    @NullMarked
    public class InsertEditModeOperationContribution implements ActionSequenceContribution {

        private final String contributionId;

        public InsertEditModeOperationContribution(String contributionId) {
            this.contributionId = contributionId;
        }

        @Override
        public Action createAction() {
            InsertEditModeOperationAction action = new InsertEditModeOperationAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return contributionId;
        }
    }

    @NullMarked
    public class OverwriteEditModeOperationContribution implements ActionSequenceContribution {

        private final String contributionId;

        public OverwriteEditModeOperationContribution(String contributionId) {
            this.contributionId = contributionId;
        }

        @Override
        public Action createAction() {
            OverwriteEditModeOperationAction action = new OverwriteEditModeOperationAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return contributionId;
        }
    }
}
