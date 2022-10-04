/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.bined.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeType;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * Code type handler.
 *
 * @version 0.2.1 2021/10/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeTypeActions implements FileDependentAction {

    public static final String BINARY_CODE_TYPE_ACTION_ID = "binaryCodeTypeAction";
    public static final String OCTAL_CODE_TYPE_ACTION_ID = "octalCodeTypeAction";
    public static final String DECIMAL_CODE_TYPE_ACTION_ID = "decimalCodeTypeAction";
    public static final String HEXADECIMAL_CODE_TYPE_ACTION_ID = "hexadecimalCodeTypeAction";
    public static final String CYCLE_CODE_TYPES_ACTION_ID = "cycleCodeTypesAction";

    public static final String CODE_TYPE_RADIO_GROUP_ID = "codeTypeRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action binaryCodeTypeAction;
    private Action octalCodeTypeAction;
    private Action decimalCodeTypeAction;
    private Action hexadecimalCodeTypeAction;
    private Action cycleCodeTypesAction;

    private CodeType codeType = CodeType.HEXADECIMAL;

    public CodeTypeActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        CodeType activeCodeType = activeFile.isPresent() ? ((BinEdFileHandler) activeFile.get()).getCodeArea().getCodeType() : null;
        if (activeCodeType != null) {
            setCodeType(activeCodeType);
        }

        if (binaryCodeTypeAction != null) {
            binaryCodeTypeAction.setEnabled(activeFile.isPresent());
        }
        if (octalCodeTypeAction != null) {
            octalCodeTypeAction.setEnabled(activeFile.isPresent());
        }
        if (decimalCodeTypeAction != null) {
            decimalCodeTypeAction.setEnabled(activeFile.isPresent());
        }
        if (hexadecimalCodeTypeAction != null) {
            hexadecimalCodeTypeAction.setEnabled(activeFile.isPresent());
        }
        if (cycleCodeTypesAction != null) {
            cycleCodeTypesAction.setEnabled(activeFile.isPresent());
        }
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
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        ((BinEdFileHandler) activeFile.get()).getCodeArea().setCodeType(codeType);
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
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setCodeType(CodeType.BINARY);
                    }
                }
            };
            ActionUtils.setupAction(binaryCodeTypeAction, resourceBundle, BINARY_CODE_TYPE_ACTION_ID);
            binaryCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            binaryCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            binaryCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.BINARY);

        }
        return binaryCodeTypeAction;
    }

    @Nonnull
    public Action getOctalCodeTypeAction() {
        if (octalCodeTypeAction == null) {
            octalCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setCodeType(CodeType.OCTAL);
                    }
                }
            };
            ActionUtils.setupAction(octalCodeTypeAction, resourceBundle, OCTAL_CODE_TYPE_ACTION_ID);
            octalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            octalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            octalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.OCTAL);
        }
        return octalCodeTypeAction;
    }

    @Nonnull
    public Action getDecimalCodeTypeAction() {
        if (decimalCodeTypeAction == null) {
            decimalCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setCodeType(CodeType.DECIMAL);
                    }
                }
            };
            ActionUtils.setupAction(decimalCodeTypeAction, resourceBundle, DECIMAL_CODE_TYPE_ACTION_ID);
            decimalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            decimalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            decimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.DECIMAL);

        }
        return decimalCodeTypeAction;
    }

    @Nonnull
    public Action getHexadecimalCodeTypeAction() {
        if (hexadecimalCodeTypeAction == null) {
            hexadecimalCodeTypeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setCodeType(CodeType.HEXADECIMAL);
                    }
                }
            };
            ActionUtils.setupAction(hexadecimalCodeTypeAction, resourceBundle, HEXADECIMAL_CODE_TYPE_ACTION_ID);
            hexadecimalCodeTypeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            hexadecimalCodeTypeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, CODE_TYPE_RADIO_GROUP_ID);
            hexadecimalCodeTypeAction.putValue(Action.SELECTED_KEY, codeType == CodeType.HEXADECIMAL);

        }
        return hexadecimalCodeTypeAction;
    }

    @Nonnull
    public Action getCycleCodeTypesAction() {
        if (cycleCodeTypesAction == null) {
            cycleCodeTypesAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        int codeTypePos = codeType.ordinal();
                        CodeType[] values = CodeType.values();
                        CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                        setCodeType(next);
                    }
                }
            };
            ActionUtils.setupAction(cycleCodeTypesAction, resourceBundle, CYCLE_CODE_TYPES_ACTION_ID);
            cycleCodeTypesAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CYCLE);
            ButtonGroup cycleButtonGroup = new ButtonGroup();
            Map<String, ButtonGroup> buttonGroups = new HashMap<>();
            buttonGroups.put(CODE_TYPE_RADIO_GROUP_ID, cycleButtonGroup);
            JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
            cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(getBinaryCodeTypeAction(), buttonGroups));
            cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(getOctalCodeTypeAction(), buttonGroups));
            cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(getDecimalCodeTypeAction(), buttonGroups));
            cycleCodeTypesPopupMenu.add(ActionUtils.actionToMenuItem(getHexadecimalCodeTypeAction(), buttonGroups));
            cycleCodeTypesAction.putValue(ActionUtils.CYCLE_POPUP_MENU, cycleCodeTypesPopupMenu);
            updateCycleButtonName();
        }
        return cycleCodeTypesAction;
    }
}
