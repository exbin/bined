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
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.utils.UiUtils;

/**
 * Code type actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeTypeActions {

    public static final String BINARY_CODE_TYPE_ACTION_ID = "binaryCodeTypeAction";
    public static final String OCTAL_CODE_TYPE_ACTION_ID = "octalCodeTypeAction";
    public static final String DECIMAL_CODE_TYPE_ACTION_ID = "decimalCodeTypeAction";
    public static final String HEXADECIMAL_CODE_TYPE_ACTION_ID = "hexadecimalCodeTypeAction";
    public static final String CYCLE_CODE_TYPES_ACTION_ID = "cycleCodeTypesAction";

    public static final String CODE_TYPE_RADIO_GROUP_ID = "codeTypeRadioGroup";

    private ResourceBundle resourceBundle;

    public CodeTypeActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createBinaryCodeTypeAction() {
        BinaryCodeTypeAction binaryCodeTypeAction = new BinaryCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(binaryCodeTypeAction, resourceBundle, BINARY_CODE_TYPE_ACTION_ID);
        binaryCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        binaryCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        binaryCodeTypeAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, binaryCodeTypeAction);
        return binaryCodeTypeAction;
    }

    @Nonnull
    public Action createOctalCodeTypeAction() {
        OctalCodeTypeAction octalCodeTypeAction = new OctalCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(octalCodeTypeAction, resourceBundle, OCTAL_CODE_TYPE_ACTION_ID);
        octalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        octalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        octalCodeTypeAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, octalCodeTypeAction);
        return octalCodeTypeAction;
    }

    @Nonnull
    public Action createDecimalCodeTypeAction() {
        DecimalCodeTypeAction decimalCodeTypeAction = new DecimalCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(decimalCodeTypeAction, resourceBundle, DECIMAL_CODE_TYPE_ACTION_ID);
        decimalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        decimalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        decimalCodeTypeAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, decimalCodeTypeAction);
        return decimalCodeTypeAction;
    }

    @Nonnull
    public Action createHexadecimalCodeTypeAction() {
        HexadecimalCodeTypeAction hexadecimalCodeTypeAction = new HexadecimalCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(hexadecimalCodeTypeAction, resourceBundle, HEXADECIMAL_CODE_TYPE_ACTION_ID);
        hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, hexadecimalCodeTypeAction);
        return hexadecimalCodeTypeAction;
    }

    @Nonnull
    public Action createCycleCodeTypesAction() {
        CycleCodeTypesAction cycleCodeTypesAction = new CycleCodeTypesAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(cycleCodeTypesAction, resourceBundle, CYCLE_CODE_TYPES_ACTION_ID);
        cycleCodeTypesAction.putValue(ActionConsts.ACTION_TYPE, ActionType.CYCLE);
        ButtonGroup cycleButtonGroup = new ButtonGroup();
        Map<String, ButtonGroup> buttonGroups = new HashMap<>();
        buttonGroups.put(CODE_TYPE_RADIO_GROUP_ID, cycleButtonGroup);
        JPopupMenu cycleCodeTypesPopupMenu = UiUtils.createPopupMenu();
        List<Action> dropDownActions = new ArrayList<>();
        dropDownActions.add(createBinaryCodeTypeAction());
        dropDownActions.add(createOctalCodeTypeAction());
        dropDownActions.add(createDecimalCodeTypeAction());
        dropDownActions.add(createHexadecimalCodeTypeAction());
        for (Action dropDownAction : dropDownActions) {
            cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(dropDownAction, buttonGroups));
        }
        cycleCodeTypesAction.setDropDownActions(dropDownActions);
        cycleCodeTypesAction.putValue(ActionConsts.CYCLE_POPUP_MENU, cycleCodeTypesPopupMenu);
        cycleCodeTypesAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, cycleCodeTypesAction);
        return cycleCodeTypesAction;
    }

    @ParametersAreNonnullByDefault
    private static class BinaryCodeTypeAction extends AbstractAction implements ActionContextChange {

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.BINARY);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                    putValue(Action.SELECTED_KEY, codeType == CodeType.BINARY);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private static class OctalCodeTypeAction extends AbstractAction implements ActionContextChange {

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.OCTAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                    putValue(Action.SELECTED_KEY, codeType == CodeType.OCTAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private static class DecimalCodeTypeAction extends AbstractAction implements ActionContextChange {

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.DECIMAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                    putValue(Action.SELECTED_KEY, codeType == CodeType.DECIMAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private static class HexadecimalCodeTypeAction extends AbstractAction implements ActionContextChange {

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.HEXADECIMAL);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                    putValue(Action.SELECTED_KEY, codeType == CodeType.HEXADECIMAL);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private class CycleCodeTypesAction extends AbstractAction implements ActionContextChange {

        private ActionContextChangeManager manager;
        private CodeAreaCore codeArea;
        private List<Action> dropDownActions;

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
            int codeTypePos = codeType.ordinal();
            CodeType[] values = CodeType.values();
            CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
            ((CodeTypeCapable) codeArea).setCodeType(next);
            manager.updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        public void setDropDownActions(List<Action> dropDownActions) {
            this.dropDownActions = dropDownActions;
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            this.manager = manager;
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                    putValue(Action.NAME, resourceBundle.getString(CYCLE_CODE_TYPES_ACTION_ID + ".codeType." + codeType.name().toLowerCase()));
                }
                setEnabled(hasInstance);
            });
            if (dropDownActions != null) {
                for (Action dropDownAction : dropDownActions) {
                    ActionContextChange ActionContextChange = (ActionContextChange) dropDownAction;
                    ActionContextChange.register(manager);
                }
            }
        }
    }
}
