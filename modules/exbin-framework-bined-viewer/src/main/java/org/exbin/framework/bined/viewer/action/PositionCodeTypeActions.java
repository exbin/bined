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
package org.exbin.framework.bined.viewer.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeRegistrar;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.bined.BinaryDataComponent;

/**
 * Position code type actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PositionCodeTypeActions {

    public static final String POSITION_CODE_TYPE_RADIO_GROUP_ID = "positionCodeTypeRadioGroup";

    private ResourceBundle resourceBundle;

    public PositionCodeTypeActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public OctalPositionCodeTypeAction createOctalCodeTypeAction() {
        OctalPositionCodeTypeAction octalPositionCodeTypeAction = new OctalPositionCodeTypeAction();
        octalPositionCodeTypeAction.setup(resourceBundle);
        return octalPositionCodeTypeAction;
    }

    @Nonnull
    public DecimalPositionCodeTypeAction createDecimalCodeTypeAction() {
        DecimalPositionCodeTypeAction decimalPositionCodeTypeAction = new DecimalPositionCodeTypeAction();
        decimalPositionCodeTypeAction.setup(resourceBundle);
        return decimalPositionCodeTypeAction;
    }

    @Nonnull
    public HexadecimalPositionCodeTypeAction createHexadecimalCodeTypeAction() {
        HexadecimalPositionCodeTypeAction hexadecimalPositionCodeTypeAction = new HexadecimalPositionCodeTypeAction();
        hexadecimalPositionCodeTypeAction.setup(resourceBundle);
        return hexadecimalPositionCodeTypeAction;
    }

    @ParametersAreNonnullByDefault
    public static class OctalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "octalPositionCodeTypeAction";

        private ActionContextChangeRegistrar registrar;
        private BinaryDataComponent binaryDataComponent;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((PositionCodeTypeCapable) binaryDataComponent.getCodeArea()).setPositionCodeType(PositionCodeType.OCTAL);
            registrar.updateActionsForComponent(ActiveComponent.class, (ActiveComponent) binaryDataComponent);
        }

        @Override
        public void register(ActionContextChangeRegistrar manager) {
            this.registrar = manager;
            manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    PositionCodeType positionCodeType = ((PositionCodeTypeCapable) binaryDataComponent.getCodeArea()).getPositionCodeType();
                    putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.OCTAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class DecimalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "decimalPositionCodeTypeAction";

        private ActionContextChangeRegistrar registrar;
        private BinaryDataComponent binaryDataComponent;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((PositionCodeTypeCapable) binaryDataComponent.getCodeArea()).setPositionCodeType(PositionCodeType.DECIMAL);
            registrar.updateActionsForComponent(ActiveComponent.class, (ActiveComponent) binaryDataComponent);
        }

        @Override
        public void register(ActionContextChangeRegistrar manager) {
            this.registrar = manager;
            manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    PositionCodeType positionCodeType = ((PositionCodeTypeCapable) binaryDataComponent.getCodeArea()).getPositionCodeType();
                    putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.DECIMAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class HexadecimalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "hexadecimalPositionCodeTypeAction";

        private ActionContextChangeRegistrar registrar;
        private BinaryDataComponent binaryDataComponent;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ((PositionCodeTypeCapable) binaryDataComponent.getCodeArea()).setPositionCodeType(PositionCodeType.HEXADECIMAL);
            registrar.updateActionsForComponent(ActiveComponent.class, (ActiveComponent) binaryDataComponent);
        }

        @Override
        public void register(ActionContextChangeRegistrar manager) {
            this.registrar = manager;
            manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    PositionCodeType positionCodeType = ((PositionCodeTypeCapable) binaryDataComponent.getCodeArea()).getPositionCodeType();
                    putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.HEXADECIMAL);
                }
                setEnabled(hasInstance);
            });
        }
    }
}
