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
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ComponentActivationManager;

/**
 * Position code type actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PositionCodeTypeActions {

    public static final String OCTAL_POSITION_CODE_TYPE_ACTION_ID = "octalPositionCodeTypeAction";
    public static final String DECIMAL_POSITION_CODE_TYPE_ACTION_ID = "decimalPositionCodeTypeAction";
    public static final String HEXADECIMAL_POSITION_CODE_TYPE_ACTION_ID = "hexadecimalPositionCodeTypeAction";

    public static final String POSITION_CODE_TYPE_RADIO_GROUP_ID = "positionCodeTypeRadioGroup";

    private ResourceBundle resourceBundle;

    public PositionCodeTypeActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createOctalCodeTypeAction() {
        OctalPositionCodeTypeAction octalPositionCodeTypeAction = new OctalPositionCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(octalPositionCodeTypeAction, resourceBundle, OCTAL_POSITION_CODE_TYPE_ACTION_ID);
        octalPositionCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        octalPositionCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
        octalPositionCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, octalPositionCodeTypeAction);
        return octalPositionCodeTypeAction;
    }

    @Nonnull
    public Action createDecimalCodeTypeAction() {
        DecimalPositionCodeTypeAction decimalPositionCodeTypeAction = new DecimalPositionCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(decimalPositionCodeTypeAction, resourceBundle, DECIMAL_POSITION_CODE_TYPE_ACTION_ID);
        decimalPositionCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        decimalPositionCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
        decimalPositionCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, decimalPositionCodeTypeAction);
        return decimalPositionCodeTypeAction;
    }

    @Nonnull
    public Action createHexadecimalCodeTypeAction() {
        HexadecimalPositionCodeTypeAction hexadecimalPositionCodeTypeAction = new HexadecimalPositionCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(hexadecimalPositionCodeTypeAction, resourceBundle, HEXADECIMAL_POSITION_CODE_TYPE_ACTION_ID);
        hexadecimalPositionCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        hexadecimalPositionCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, POSITION_CODE_TYPE_RADIO_GROUP_ID);
        hexadecimalPositionCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, hexadecimalPositionCodeTypeAction);
        return hexadecimalPositionCodeTypeAction;
    }

    @ParametersAreNonnullByDefault
    public static class OctalPositionCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private ComponentActivationManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((PositionCodeTypeCapable) codeArea).setPositionCodeType(PositionCodeType.OCTAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ComponentActivationManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    PositionCodeType positionCodeType = ((PositionCodeTypeCapable) codeArea).getPositionCodeType();
                    putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.OCTAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class DecimalPositionCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private ComponentActivationManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((PositionCodeTypeCapable) codeArea).setPositionCodeType(PositionCodeType.DECIMAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ComponentActivationManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    PositionCodeType positionCodeType = ((PositionCodeTypeCapable) codeArea).getPositionCodeType();
                    putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.DECIMAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class HexadecimalPositionCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private ComponentActivationManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((PositionCodeTypeCapable) codeArea).setPositionCodeType(PositionCodeType.HEXADECIMAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ComponentActivationManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    PositionCodeType positionCodeType = ((PositionCodeTypeCapable) codeArea).getPositionCodeType();
                    putValue(Action.SELECTED_KEY, positionCodeType == PositionCodeType.HEXADECIMAL);
                }
                setEnabled(hasInstance);
            });
        }
    }
}
