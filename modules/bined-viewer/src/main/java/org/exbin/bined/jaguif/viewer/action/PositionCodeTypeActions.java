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
package org.exbin.bined.jaguif.viewer.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.PositionCodeType;
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
import org.exbin.jaguif.menu.api.ActionMenuCreation;

/**
 * Position code type actions.
 */
@ParametersAreNonnullByDefault
public class PositionCodeTypeActions {

    public static final String POSITION_CODE_TYPE_RADIO_GROUP_ID = "positionCodeTypeRadioGroup";

    private ResourceBundle resourceBundle;

    public PositionCodeTypeActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public OctalPositionCodeTypeAction createOctalCodeTypeAction() {
        OctalPositionCodeTypeAction octalPositionCodeTypeAction = new OctalPositionCodeTypeAction();
        octalPositionCodeTypeAction.init(resourceBundle);
        return octalPositionCodeTypeAction;
    }

    @Nonnull
    public DecimalPositionCodeTypeAction createDecimalCodeTypeAction() {
        DecimalPositionCodeTypeAction decimalPositionCodeTypeAction = new DecimalPositionCodeTypeAction();
        decimalPositionCodeTypeAction.init(resourceBundle);
        return decimalPositionCodeTypeAction;
    }

    @Nonnull
    public HexadecimalPositionCodeTypeAction createHexadecimalCodeTypeAction() {
        HexadecimalPositionCodeTypeAction hexadecimalPositionCodeTypeAction = new HexadecimalPositionCodeTypeAction();
        hexadecimalPositionCodeTypeAction.init(resourceBundle);
        return hexadecimalPositionCodeTypeAction;
    }

    @Nonnull
    public CodeTypeContribution createPositionCodeTypeContribution(PositionCodeType positionCodeType, @Nullable ActionMenuCreation positionCodeTypeCreating) {
        switch (positionCodeType) {
            case OCTAL:
                return new CodeTypeContribution(OctalPositionCodeTypeAction.ACTION_ID, PositionCodeType.OCTAL, positionCodeTypeCreating);
            case DECIMAL:
                return new CodeTypeContribution(DecimalPositionCodeTypeAction.ACTION_ID, PositionCodeType.DECIMAL, positionCodeTypeCreating);
            case HEXADECIMAL:
                return new CodeTypeContribution(HexadecimalPositionCodeTypeAction.ACTION_ID, PositionCodeType.HEXADECIMAL, positionCodeTypeCreating);
        }

        throw new IllegalStateException();
    }

    @ParametersAreNonnullByDefault
    public static class OctalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "octalPositionCodeType";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PositionCodeTypeActions.setPositionCodeType(binaryDataComponent, PositionCodeType.OCTAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.POSITION_CODE_TYPE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                PositionCodeType positionCodeType = binaryDataComponent.getPositionCodeType();
                putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.OCTAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public static class DecimalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "decimalPositionCodeType";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PositionCodeTypeActions.setPositionCodeType(binaryDataComponent, PositionCodeType.DECIMAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.POSITION_CODE_TYPE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                PositionCodeType positionCodeType = binaryDataComponent.getPositionCodeType();
                putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.DECIMAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public static class HexadecimalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "hexadecimalPositionCodeType";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PositionCodeTypeActions.setPositionCodeType(binaryDataComponent, PositionCodeType.HEXADECIMAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.POSITION_CODE_TYPE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                PositionCodeType positionCodeType = binaryDataComponent.getPositionCodeType();
                putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.HEXADECIMAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public class CodeTypeContribution implements ActionSequenceContribution {

        private final String contributionId;
        private final PositionCodeType positionCodeType;
        private final ActionMenuCreation positionCodeTypeCreating;

        public CodeTypeContribution(String contributionId, PositionCodeType positionCodeType, @Nullable ActionMenuCreation positionCodeTypeCreating) {
            this.contributionId = contributionId;
            this.positionCodeType = positionCodeType;
            this.positionCodeTypeCreating = positionCodeTypeCreating;
        }

        @Nonnull
        @Override
        public Action createAction() {
            switch (positionCodeType) {
                case OCTAL: {
                    OctalPositionCodeTypeAction action = new OctalPositionCodeTypeAction();
                    action.init(resourceBundle);
                    if (positionCodeTypeCreating != null) {
                        action.putValue(ActionConsts.ACTION_MENU_CREATION, positionCodeTypeCreating);
                    }
                    return action;
                }
                case DECIMAL: {
                    DecimalPositionCodeTypeAction action = new DecimalPositionCodeTypeAction();
                    action.init(resourceBundle);
                    if (positionCodeTypeCreating != null) {
                        action.putValue(ActionConsts.ACTION_MENU_CREATION, positionCodeTypeCreating);
                    }
                    return action;
                }
                case HEXADECIMAL: {
                    HexadecimalPositionCodeTypeAction action = new HexadecimalPositionCodeTypeAction();
                    action.init(resourceBundle);
                    if (positionCodeTypeCreating != null) {
                        action.putValue(ActionConsts.ACTION_MENU_CREATION, positionCodeTypeCreating);
                    }
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

    public static void setPositionCodeType(BinaryDataComponent binaryDataComponent, PositionCodeType positionCodeType) {
        binaryDataComponent.setPositionCodeType(positionCodeType);
    }
}
