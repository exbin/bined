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
import org.jspecify.annotations.NullMarked;
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
import org.exbin.bined.jaguif.viewer.status.gui.BinaryCursorPositionComponent;

/**
 * Cursor position code type actions.
 */
@NullMarked
public class CursorPositionCodeTypeActions {

    public static final String POSITION_CODE_TYPE_RADIO_GROUP_ID = "positionCodeTypeRadioGroup";

    private ResourceBundle resourceBundle;

    public CursorPositionCodeTypeActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public OctalPositionCodeTypeAction createOctalCodeTypeAction() {
        OctalPositionCodeTypeAction octalPositionCodeTypeAction = new OctalPositionCodeTypeAction();
        octalPositionCodeTypeAction.init(resourceBundle);
        return octalPositionCodeTypeAction;
    }

    public DecimalPositionCodeTypeAction createDecimalCodeTypeAction() {
        DecimalPositionCodeTypeAction decimalPositionCodeTypeAction = new DecimalPositionCodeTypeAction();
        decimalPositionCodeTypeAction.init(resourceBundle);
        return decimalPositionCodeTypeAction;
    }

    public HexadecimalPositionCodeTypeAction createHexadecimalCodeTypeAction() {
        HexadecimalPositionCodeTypeAction hexadecimalPositionCodeTypeAction = new HexadecimalPositionCodeTypeAction();
        hexadecimalPositionCodeTypeAction.init(resourceBundle);
        return hexadecimalPositionCodeTypeAction;
    }

    public CursorPositionCodeTypeContribution createPositionCodeTypeContribution(PositionCodeType positionCodeType) {
        switch (positionCodeType) {
            case OCTAL:
                return new CursorPositionCodeTypeContribution(OctalPositionCodeTypeAction.ACTION_ID, PositionCodeType.OCTAL);
            case DECIMAL:
                return new CursorPositionCodeTypeContribution(DecimalPositionCodeTypeAction.ACTION_ID, PositionCodeType.DECIMAL);
            case HEXADECIMAL:
                return new CursorPositionCodeTypeContribution(HexadecimalPositionCodeTypeAction.ACTION_ID, PositionCodeType.HEXADECIMAL);
        }

        throw new IllegalStateException();
    }

    @NullMarked
    public static class OctalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "octalPositionCodeType";

        private BinaryCursorPositionComponent statusComponent;

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
            CursorPositionCodeTypeActions.setPositionCodeType(statusComponent, PositionCodeType.OCTAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(BinaryCursorPositionComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(BinaryCursorPositionComponent.class, (instance, updateType) -> {
                if (BinaryCursorPositionComponent.UpdateType.CURSOR_POSITION_FORMAT.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(BinaryCursorPositionComponent context) {
            statusComponent = context;
            boolean hasInstance = context != null;
            if (hasInstance) {
                PositionCodeType positionCodeType = statusComponent.getCursorPositionFormat().getCodeType();
                putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.OCTAL);
            }
            setEnabled(hasInstance);
        }
    }

    @NullMarked
    public static class DecimalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "decimalPositionCodeType";

        private BinaryCursorPositionComponent statusComponent;

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
            CursorPositionCodeTypeActions.setPositionCodeType(statusComponent, PositionCodeType.DECIMAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(BinaryCursorPositionComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(BinaryCursorPositionComponent.class, (instance, updateType) -> {
                if (BinaryCursorPositionComponent.UpdateType.CURSOR_POSITION_FORMAT.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(BinaryCursorPositionComponent context) {
            statusComponent = context;
            boolean hasInstance = context != null;
            if (hasInstance) {
                PositionCodeType positionCodeType = statusComponent.getCursorPositionFormat().getCodeType();
                putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.DECIMAL);
            }
            setEnabled(hasInstance);
        }
    }

    @NullMarked
    public static class HexadecimalPositionCodeTypeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "hexadecimalPositionCodeType";

        private BinaryCursorPositionComponent statusComponent;

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
            CursorPositionCodeTypeActions.setPositionCodeType(statusComponent, PositionCodeType.HEXADECIMAL);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(BinaryCursorPositionComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(BinaryCursorPositionComponent.class, (instance, updateType) -> {
                if (BinaryCursorPositionComponent.UpdateType.CURSOR_POSITION_FORMAT.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(BinaryCursorPositionComponent context) {
            statusComponent = context;
            boolean hasInstance = context != null;
            if (hasInstance) {
                PositionCodeType positionCodeType = statusComponent.getCursorPositionFormat().getCodeType();
                putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.HEXADECIMAL);
            }
            setEnabled(hasInstance);
        }
    }

    @NullMarked
    public class CursorPositionCodeTypeContribution implements ActionSequenceContribution {

        private final String contributionId;
        private final PositionCodeType positionCodeType;

        public CursorPositionCodeTypeContribution(String contributionId, PositionCodeType positionCodeType) {
            this.contributionId = contributionId;
            this.positionCodeType = positionCodeType;
        }

        @Override
        public Action createAction() {
            switch (positionCodeType) {
                case OCTAL: {
                    OctalPositionCodeTypeAction action = new OctalPositionCodeTypeAction();
                    action.init(resourceBundle);
                    return action;
                }
                case DECIMAL: {
                    DecimalPositionCodeTypeAction action = new DecimalPositionCodeTypeAction();
                    action.init(resourceBundle);
                    return action;
                }
                case HEXADECIMAL: {
                    HexadecimalPositionCodeTypeAction action = new HexadecimalPositionCodeTypeAction();
                    action.init(resourceBundle);
                    return action;
                }
            }

            throw new IllegalStateException();
        }

        @Override
        public String getContributionId() {
            return contributionId;
        }
    }

    public static void setPositionCodeType(BinaryCursorPositionComponent statusComponent, PositionCodeType positionCodeType) {
        statusComponent.setCursorPositionCodeType(positionCodeType);
    }
}
