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
package org.exbin.bined.jaguif.document.action;

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
 * Binary processing mode actions.
 */
@NullMarked
public class ProcessingModeActions {

    public static final String PROCESSING_MODE_RADIO_GROUP_ID = "processingModeRadioGroup";
    private ResourceBundle resourceBundle;

    public ProcessingModeActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public MemoryProcessingModeAction createMemoryProcessingModeAction() {
        MemoryProcessingModeAction insertEditModeOperationAction = new MemoryProcessingModeAction();
        insertEditModeOperationAction.init(resourceBundle);
        return insertEditModeOperationAction;
    }

    public DeltaProcessingModeAction createDeltaProcessingModeAction() {
        DeltaProcessingModeAction overwriteEditModeOperationAction = new DeltaProcessingModeAction();
        overwriteEditModeOperationAction.init(resourceBundle);
        return overwriteEditModeOperationAction;
    }

    public MemoryProcessingModeContribution createMemoryProcessingModeContribution() {
        MemoryProcessingModeContribution insertEditModeOperationContribution = new MemoryProcessingModeContribution(MemoryProcessingModeAction.ACTION_ID);
        return insertEditModeOperationContribution;
    }

    public DeltaProcessingModeContribution createDeltaProcessingModeContribution() {
        DeltaProcessingModeContribution overwriteEditModeOperationContribution = new DeltaProcessingModeContribution(DeltaProcessingModeAction.ACTION_ID);
        return overwriteEditModeOperationContribution;
    }

    @NullMarked
    public class MemoryProcessingModeAction extends AbstractAction {

        public static final String ACTION_ID = "memoryProcessingMode";

        private BinaryDataComponent binaryDataComponent;

        public MemoryProcessingModeAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, PROCESSING_MODE_RADIO_GROUP_ID);
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
    public class DeltaProcessingModeAction extends AbstractAction {

        public static final String ACTION_ID = "deltaProcessingMode";

        private BinaryDataComponent binaryDataComponent;

        public DeltaProcessingModeAction() {
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, PROCESSING_MODE_RADIO_GROUP_ID);
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
    public class MemoryProcessingModeContribution implements ActionSequenceContribution {

        private final String contributionId;

        public MemoryProcessingModeContribution(String contributionId) {
            this.contributionId = contributionId;
        }

        @Override
        public Action createAction() {
            MemoryProcessingModeAction action = new MemoryProcessingModeAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return contributionId;
        }
    }

    @NullMarked
    public class DeltaProcessingModeContribution implements ActionSequenceContribution {

        private final String contributionId;

        public DeltaProcessingModeContribution(String contributionId) {
            this.contributionId = contributionId;
        }

        @Override
        public Action createAction() {
            DeltaProcessingModeAction action = new DeltaProcessingModeAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return contributionId;
        }
    }
}
