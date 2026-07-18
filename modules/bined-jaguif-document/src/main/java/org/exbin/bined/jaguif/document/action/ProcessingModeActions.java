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
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.document.FileProcessingMode;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.docking.api.DocumentDocking;
import org.exbin.jaguif.document.api.ContextDocument;

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

        private BinaryFileDocument binaryFileDocument;
        private DocumentDocking documentDocking;

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
                    registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                        binaryFileDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                        update();
                    });
                    registrar.registerChangeListener(ContextDocking.class, (instance) -> {
                        documentDocking = instance instanceof DocumentDocking ? (DocumentDocking) instance : null;
                        update();
                    });
                    registrar.registerStateUpdateListener(ContextDocument.class, (instance, updateType) -> {
                        if (BinaryFileDocument.UpdateType.PROCESSING_MODE.equals(updateType)) {
                            update();
                        }
                    });
                }

                void update() {
                    setEnabled(binaryFileDocument != null && documentDocking != null);
                    putValue(Action.SELECTED_KEY, binaryFileDocument != null && binaryFileDocument.getFileProcessingMode() == FileProcessingMode.MEMORY);
                }
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            changeFileProcessingMode(documentDocking, binaryFileDocument, FileProcessingMode.MEMORY);
        }
    }

    @NullMarked
    public class DeltaProcessingModeAction extends AbstractAction {

        public static final String ACTION_ID = "deltaProcessingMode";

        private BinaryFileDocument binaryFileDocument;
        private DocumentDocking documentDocking;

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
                    registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                        binaryFileDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                        update();
                    });
                    registrar.registerChangeListener(ContextDocking.class, (instance) -> {
                        documentDocking = instance instanceof DocumentDocking ? (DocumentDocking) instance : null;
                        update();
                    });
                    registrar.registerStateUpdateListener(ContextDocument.class, (instance, updateType) -> {
                        if (BinaryFileDocument.UpdateType.PROCESSING_MODE.equals(updateType)) {
                            update();
                        }
                    });
                }

                void update() {
                    setEnabled(binaryFileDocument != null && documentDocking != null);
                    putValue(Action.SELECTED_KEY, binaryFileDocument != null && binaryFileDocument.getFileProcessingMode() == FileProcessingMode.DELTA);
                }
            });
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            changeFileProcessingMode(documentDocking, binaryFileDocument, FileProcessingMode.DELTA);
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

    public void changeFileProcessingMode(DocumentDocking docking, BinaryFileDocument binaryFileDocument, FileProcessingMode fileProcessingMode) {
        if (binaryFileDocument.getFileProcessingMode() == fileProcessingMode) {
            return;
        }

        if (docking.releaseDocument(binaryFileDocument)) {
            binaryFileDocument.loadContent(fileProcessingMode);

            ActiveContextManagement context = binaryFileDocument.getDataComponent().getContextManagement().orElse(null);
            if (context != null) {
                context.updateActiveState(ContextDocument.class, binaryFileDocument, BinaryFileDocument.UpdateType.PROCESSING_MODE);
            }
        }
    }
}
