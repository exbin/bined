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
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeType;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;

/**
 * Code type handler.
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

    private CodeAreaCore codeArea;
    private ResourceBundle resourceBundle;

    private Action binaryCodeTypeAction;
    private Action octalCodeTypeAction;
    private Action decimalCodeTypeAction;
    private Action hexadecimalCodeTypeAction;
    private Action cycleCodeTypesAction;

    private CodeType codeType = CodeType.HEXADECIMAL;

    public CodeTypeActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
        switch (codeType) {
            case BINARY: {
                binaryCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            case OCTAL: {
                octalCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            case DECIMAL: {
                decimalCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            case HEXADECIMAL: {
                hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, Boolean.TRUE);
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(codeType);
        }
        ((CodeTypeCapable) codeArea).setCodeType(codeType);
        updateCycleButtonName();
    }

    private void updateCycleButtonName() {
        if (cycleCodeTypesAction != null) {
            cycleCodeTypesAction.putValue(Action.NAME, codeType.name().substring(0, 3));
        }
    }

    @Nonnull
    public Action getBinaryCodeTypeAction() {
        if (binaryCodeTypeAction == null) {
            binaryCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCodeType(CodeType.BINARY);
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(binaryCodeTypeAction, resourceBundle, BINARY_CODE_TYPE_ACTION_ID);
            binaryCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            binaryCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            binaryCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.BINARY);
            binaryCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
                @Nonnull
                @Override
                public Set<Class<?>> forClasses() {
                    return Collections.singleton(CodeAreaCore.class);
                }

                @Override
                public void componentActive(Set<Object> affectedClasses) {
                    boolean hasInstance = !affectedClasses.isEmpty();
                    if (hasInstance) {
                        CodeAreaCore codeArea = (CodeAreaCore) affectedClasses.iterator().next();
                        binaryCodeTypeAction.putValue(Action.SELECTED_KEY, ((CodeTypeCapable) codeArea).getCodeType() == CodeType.BINARY);
                    }
                    binaryCodeTypeAction.setEnabled(hasInstance);
                }
            });

        }
        return binaryCodeTypeAction;
    }

    @Nonnull
    public Action getOctalCodeTypeAction() {
        if (octalCodeTypeAction == null) {
            octalCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCodeType(CodeType.OCTAL);
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(octalCodeTypeAction, resourceBundle, OCTAL_CODE_TYPE_ACTION_ID);
            octalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            octalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            octalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.OCTAL);
            octalCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
                @Nonnull
                @Override
                public Set<Class<?>> forClasses() {
                    return Collections.singleton(CodeAreaCore.class);
                }

                @Override
                public void componentActive(Set<Object> affectedClasses) {
                    boolean hasInstance = !affectedClasses.isEmpty();
                    if (hasInstance) {
                        CodeAreaCore codeArea = (CodeAreaCore) affectedClasses.iterator().next();
                        octalCodeTypeAction.putValue(Action.SELECTED_KEY, ((CodeTypeCapable) codeArea).getCodeType() == CodeType.OCTAL);
                    }
                    octalCodeTypeAction.setEnabled(hasInstance);
                }
            });
        }
        return octalCodeTypeAction;
    }

    @Nonnull
    public Action getDecimalCodeTypeAction() {
        if (decimalCodeTypeAction == null) {
            decimalCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCodeType(CodeType.DECIMAL);
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(decimalCodeTypeAction, resourceBundle, DECIMAL_CODE_TYPE_ACTION_ID);
            decimalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            decimalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            decimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.DECIMAL);
            decimalCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
                @Nonnull
                @Override
                public Set<Class<?>> forClasses() {
                    return Collections.singleton(CodeAreaCore.class);
                }

                @Override
                public void componentActive(Set<Object> affectedClasses) {
                    boolean hasInstance = !affectedClasses.isEmpty();
                    if (hasInstance) {
                        CodeAreaCore codeArea = (CodeAreaCore) affectedClasses.iterator().next();
                        decimalCodeTypeAction.putValue(Action.SELECTED_KEY, ((CodeTypeCapable) codeArea).getCodeType() == CodeType.DECIMAL);
                    }
                    decimalCodeTypeAction.setEnabled(hasInstance);
                }
            });
        }
        return decimalCodeTypeAction;
    }

    @Nonnull
    public Action getHexadecimalCodeTypeAction() {
        if (hexadecimalCodeTypeAction == null) {
            hexadecimalCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCodeType(CodeType.HEXADECIMAL);
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(hexadecimalCodeTypeAction, resourceBundle, HEXADECIMAL_CODE_TYPE_ACTION_ID);
            hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.HEXADECIMAL);
            hexadecimalCodeTypeAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
                @Nonnull
                @Override
                public Set<Class<?>> forClasses() {
                    return Collections.singleton(CodeAreaCore.class);
                }

                @Override
                public void componentActive(Set<Object> affectedClasses) {
                    boolean hasInstance = !affectedClasses.isEmpty();
                    if (hasInstance) {
                        CodeAreaCore codeArea = (CodeAreaCore) affectedClasses.iterator().next();
                        hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, ((CodeTypeCapable) codeArea).getCodeType() == CodeType.HEXADECIMAL);
                    }
                    hexadecimalCodeTypeAction.setEnabled(hasInstance);
                }
            });
        }
        return hexadecimalCodeTypeAction;
    }

    @Nonnull
    public Action getCycleCodeTypesAction() {
        if (cycleCodeTypesAction == null) {
            cycleCodeTypesAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int codeTypePos = codeType.ordinal();
                    CodeType[] values = CodeType.values();
                    CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                    setCodeType(next);
                }
            };
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.setupAction(cycleCodeTypesAction, resourceBundle, CYCLE_CODE_TYPES_ACTION_ID);
            cycleCodeTypesAction.putValue(ActionConsts.ACTION_TYPE, ActionType.CYCLE);
            ButtonGroup cycleButtonGroup = new ButtonGroup();
            Map<String, ButtonGroup> buttonGroups = new HashMap<>();
            buttonGroups.put(CODE_TYPE_RADIO_GROUP_ID, cycleButtonGroup);
            JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
            cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(getBinaryCodeTypeAction(), buttonGroups));
            cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(getOctalCodeTypeAction(), buttonGroups));
            cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(getDecimalCodeTypeAction(), buttonGroups));
            cycleCodeTypesPopupMenu.add(actionModule.actionToMenuItem(getHexadecimalCodeTypeAction(), buttonGroups));
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
            updateCycleButtonName();
        }
        return cycleCodeTypesAction;
    }
}
