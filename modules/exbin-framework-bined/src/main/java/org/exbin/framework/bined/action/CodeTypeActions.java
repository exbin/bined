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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
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
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;

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
        binaryCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, binaryCodeTypeAction);
        return binaryCodeTypeAction;
    }

    @Nonnull
    public Action createOctalCodeTypeAction() {
        OctalCodeTypeAction octalCodeTypeAction = new OctalCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(octalCodeTypeAction, resourceBundle, OCTAL_CODE_TYPE_ACTION_ID);
        octalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        octalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        octalCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, octalCodeTypeAction);
        return octalCodeTypeAction;
    }

    @Nonnull
    public Action createDecimalCodeTypeAction() {
        DecimalCodeTypeAction decimalCodeTypeAction = new DecimalCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(decimalCodeTypeAction, resourceBundle, DECIMAL_CODE_TYPE_ACTION_ID);
        decimalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        decimalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        decimalCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, decimalCodeTypeAction);
        return decimalCodeTypeAction;
    }

    @Nonnull
    public Action createHexadecimalCodeTypeAction() {
        HexadecimalCodeTypeAction hexadecimalCodeTypeAction = new HexadecimalCodeTypeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(hexadecimalCodeTypeAction, resourceBundle, HEXADECIMAL_CODE_TYPE_ACTION_ID);
        hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
        hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, hexadecimalCodeTypeAction);
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
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(createBinaryCodeTypeAction(), buttonGroups));
        cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(createOctalCodeTypeAction(), buttonGroups));
        cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(createDecimalCodeTypeAction(), buttonGroups));
        cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(createHexadecimalCodeTypeAction(), buttonGroups));
        cycleCodeTypesAction.putValue(ActionConsts.CYCLE_POPUP_MENU, cycleCodeTypesPopupMenu);
        cycleCodeTypesAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Nonnull
            @Override
            public Set<Class<?>> forClasses() {
                return Collections.singleton(CodeAreaCore.class);
            }

            @Override
            public void componentActive(Set<Object> affectedClasses) {
                cycleCodeTypesAction.setEnabled(!affectedClasses.isEmpty());
            }
        });
        return cycleCodeTypesAction;
    }

    @ParametersAreNonnullByDefault
    private static class BinaryCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.BINARY);
            App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
        }

        @Nonnull
        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(CodeAreaCore.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            boolean hasInstance = !affectedClasses.isEmpty();
            codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
            if (hasInstance) {
                CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                putValue(Action.SELECTED_KEY, codeType == CodeType.BINARY);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    private static class OctalCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.OCTAL);
            App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
        }

        @Nonnull
        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(CodeAreaCore.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            boolean hasInstance = !affectedClasses.isEmpty();
            codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
            if (hasInstance) {
                CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                putValue(Action.SELECTED_KEY, codeType == CodeType.OCTAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    private static class DecimalCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.DECIMAL);
            App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
        }

        @Nonnull
        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(CodeAreaCore.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            boolean hasInstance = !affectedClasses.isEmpty();
            codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
            if (hasInstance) {
                CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                putValue(Action.SELECTED_KEY, codeType == CodeType.DECIMAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    private static class HexadecimalCodeTypeAction extends AbstractAction implements ActionActiveComponent {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeTypeCapable) codeArea).setCodeType(CodeType.HEXADECIMAL);
            App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
        }

        @Nonnull
        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(CodeAreaCore.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            boolean hasInstance = !affectedClasses.isEmpty();
            codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
            if (hasInstance) {
                CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                putValue(Action.SELECTED_KEY, codeType == CodeType.HEXADECIMAL);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    private static class CycleCodeTypesAction extends AbstractAction implements ActionActiveComponent {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
            int codeTypePos = codeType.ordinal();
            CodeType[] values = CodeType.values();
            CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
            ((CodeTypeCapable) codeArea).setCodeType(next);
            App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
        }

        @Nonnull
        @Override
        public Set<Class<?>> forClasses() {
            return Collections.singleton(CodeAreaCore.class);
        }

        @Override
        public void componentActive(Set<Object> affectedClasses) {
            boolean hasInstance = !affectedClasses.isEmpty();
            codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
            if (hasInstance) {
                CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
                // TODO language support
                putValue(Action.NAME, codeType.name().substring(0, 3));
            }
            setEnabled(hasInstance);
        }
    }
}
