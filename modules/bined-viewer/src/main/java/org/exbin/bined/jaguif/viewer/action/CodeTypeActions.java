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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import org.exbin.bined.CodeType;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.component.CodeTypeState;
import org.exbin.jaguif.utils.UiUtils;

/**
 * Code type actions.
 */
@ParametersAreNonnullByDefault
public class CodeTypeActions {

    public static final String BINARY_ACTION_ID = "binaryCodeType";
    public static final String OCTAL_ACTION_ID = "octalCodeType";
    public static final String DECIMAL_ACTION_ID = "decimalCodeType";
    public static final String HEXADECIMAL_ACTION_ID = "hexadecimalCodeType";
    public static final String CODE_TYPE_RADIO_GROUP_ID = "codeTypeRadioGroup";

    private ResourceBundle resourceBundle;

    public CodeTypeActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public CodeTypeAction createBinaryCodeTypeAction() {
        CodeTypeAction binaryCodeTypeAction = new CodeTypeAction(BINARY_ACTION_ID, CodeType.BINARY);
        binaryCodeTypeAction.init(resourceBundle);
        return binaryCodeTypeAction;
    }

    @Nonnull
    public CodeTypeAction createOctalCodeTypeAction() {
        CodeTypeAction octalCodeTypeAction = new CodeTypeAction(OCTAL_ACTION_ID, CodeType.OCTAL);
        octalCodeTypeAction.init(resourceBundle);
        return octalCodeTypeAction;
    }

    @Nonnull
    public CodeTypeAction createDecimalCodeTypeAction() {
        CodeTypeAction decimalCodeTypeAction = new CodeTypeAction(DECIMAL_ACTION_ID, CodeType.DECIMAL);
        decimalCodeTypeAction.init(resourceBundle);
        return decimalCodeTypeAction;
    }

    @Nonnull
    public CodeTypeAction createHexadecimalCodeTypeAction() {
        CodeTypeAction hexadecimalCodeTypeAction = new CodeTypeAction(HEXADECIMAL_ACTION_ID, CodeType.HEXADECIMAL);
        hexadecimalCodeTypeAction.init(resourceBundle);
        return hexadecimalCodeTypeAction;
    }

    @Nonnull
    public CycleCodeTypesAction createCycleCodeTypesAction() {
        CycleCodeTypesAction cycleCodeTypesAction = new CycleCodeTypesAction();
        cycleCodeTypesAction.init(resourceBundle);
        return cycleCodeTypesAction;
    }

    @Nonnull
    public CodeTypeContribution createBinaryCodeTypeContribution() {
        CodeTypeContribution binaryCodeTypeContribution = new CodeTypeContribution(BINARY_ACTION_ID, CodeType.BINARY);
        return binaryCodeTypeContribution;
    }

    @Nonnull
    public CodeTypeContribution createOctalCodeTypeContribution() {
        CodeTypeContribution octalCodeTypeContribution = new CodeTypeContribution(OCTAL_ACTION_ID, CodeType.OCTAL);
        return octalCodeTypeContribution;
    }

    @Nonnull
    public CodeTypeContribution createDecimalCodeTypeContribution() {
        CodeTypeContribution decimalCodeTypeContribution = new CodeTypeContribution(DECIMAL_ACTION_ID, CodeType.DECIMAL);
        return decimalCodeTypeContribution;
    }

    @Nonnull
    public CodeTypeContribution createHexadecimalCodeTypeContribution() {
        CodeTypeContribution hexadecimalCodeTypeContribution = new CodeTypeContribution(HEXADECIMAL_ACTION_ID, CodeType.HEXADECIMAL);
        return hexadecimalCodeTypeContribution;
    }

    @Nonnull
    public CycleCodeTypesContribution createCycleCodeTypesContribution() {
        CycleCodeTypesContribution cycleCodeTypesContribution = new CycleCodeTypesContribution();
        return cycleCodeTypesContribution;
    }

    @ParametersAreNonnullByDefault
    public static class CodeTypeAction extends AbstractAction implements ActionContextChange {

        private final String actionId;
        private final CodeType codeType;
        private BinaryDataComponent binaryDataComponent;

        public CodeTypeAction(String actionId, CodeType codeType) {
            this.actionId = actionId;
            this.codeType = codeType;
        }

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, actionId);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeTypeActions.setCodeType(binaryDataComponent, codeType);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.CODE_TYPE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeType codeType = binaryDataComponent.getCodeType();
                putValue(Action.SELECTED_KEY, codeType == codeType);
            }
            setEnabled(hasInstance);
        }
    }
    
    @ParametersAreNonnullByDefault
    public class CodeTypeContribution implements ActionSequenceContribution {
        
        private final String contributionId;
        private final CodeType codeType;

        public CodeTypeContribution(String contributionId, CodeType codeType) {
            this.contributionId = contributionId;
            this.codeType = codeType;
        }

        @Nonnull
        @Override
        public Action createAction() {
            CodeTypeAction action = new CodeTypeAction(contributionId, codeType);
            action.init(resourceBundle);
            return action;
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return contributionId;
        }
    }

    @ParametersAreNonnullByDefault
    public class CycleCodeTypesAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "cycleCodeTypes";

        private BinaryDataComponent binaryDataComponent;
        private List<Action> dropDownActions;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.CYCLE);
            ButtonGroup cycleButtonGroup = new ButtonGroup();
            Map<String, ButtonGroup> buttonGroups = new HashMap<>();
            buttonGroups.put(CODE_TYPE_RADIO_GROUP_ID, cycleButtonGroup);
            JPopupMenu cycleCodeTypesPopupMenu = UiUtils.createPopupMenu();
            dropDownActions = new ArrayList<>();
            dropDownActions.add(createBinaryCodeTypeAction());
            dropDownActions.add(createOctalCodeTypeAction());
            dropDownActions.add(createDecimalCodeTypeAction());
            dropDownActions.add(createHexadecimalCodeTypeAction());
            for (Action dropDownAction : dropDownActions) {
                cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(dropDownAction, buttonGroups));
            }
            setDropDownActions(dropDownActions);
            putValue(ActionConsts.CYCLE_POPUP_MENU, cycleCodeTypesPopupMenu);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeType codeType = binaryDataComponent.getCodeType();
            int codeTypePos = codeType.ordinal();
            CodeType[] values = CodeType.values();
            CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
            CodeTypeActions.setCodeType(binaryDataComponent, next);
        }

        public void setDropDownActions(List<Action> dropDownActions) {
            this.dropDownActions = dropDownActions;
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.CODE_TYPE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
            if (dropDownActions != null) {
                for (Action dropDownAction : dropDownActions) {
                    ActionContextChange ActionContextChange = (ActionContextChange) dropDownAction;
                    ActionContextChange.register(registrar);
                }
            }
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeType codeType = binaryDataComponent.getCodeType();
                putValue(Action.NAME, resourceBundle.getString(ACTION_ID + "Action.codeType." + codeType.name().toLowerCase()));
            }
            setEnabled(hasInstance);
        }
    }
    
    @ParametersAreNonnullByDefault
    public class CycleCodeTypesContribution implements ActionSequenceContribution {
        
        public CycleCodeTypesContribution() {
        }

        @Nonnull
        @Override
        public Action createAction() {
            CycleCodeTypesAction action = new CycleCodeTypesAction();
            action.init(resourceBundle);
            return action;
        }

        @Nonnull
        @Override
        public String getContributionId() {
            return CycleCodeTypesAction.ACTION_ID;
        }
    }

    public static void setCodeType(BinaryDataComponent binaryDataComponent, CodeType codeType) {
        binaryDataComponent.setCodeType(codeType);
    }
}
