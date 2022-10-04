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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * Hex characters case handler.
 *
 * @version 0.2.1 2021/10/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HexCharactersCaseActions implements FileDependentAction {

    public static final String UPPER_HEX_CHARACTERS_ACTION_ID = "upperHexCharactersAction";
    public static final String LOWER_HEX_CHARACTERS_ACTION_ID = "lowerHexCharactersAction";
    public static final String HEX_CHARACTERS_CASE_RADIO_GROUP_ID = "hexCharactersCaseRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action upperHexCharsAction;
    private Action lowerHexCharsAction;

    private CodeCharactersCase hexCharactersCase = CodeCharactersCase.UPPER;

    public HexCharactersCaseActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        CodeCharactersCase codeCharactersCase = activeFile.isPresent() ? ((BinEdFileHandler) activeFile.get()).getCodeArea().getCodeCharactersCase() : null;

        if (upperHexCharsAction != null) {
            upperHexCharsAction.setEnabled(activeFile.isPresent());
            if (codeCharactersCase == CodeCharactersCase.UPPER) {
                upperHexCharsAction.putValue(Action.SELECTED_KEY, true);
            }
        }
        if (lowerHexCharsAction != null) {
            lowerHexCharsAction.setEnabled(activeFile.isPresent());
            if (codeCharactersCase == CodeCharactersCase.LOWER) {
                lowerHexCharsAction.putValue(Action.SELECTED_KEY, true);
            }
        }
    }

    public void setHexCharactersCase(CodeCharactersCase hexCharactersCase) {
        this.hexCharactersCase = hexCharactersCase;

        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        ((BinEdFileHandler) activeFile.get()).getCodeArea().setCodeCharactersCase(hexCharactersCase);
    }

    @Nonnull
    public Action getUpperHexCharsAction() {
        if (upperHexCharsAction == null) {
            upperHexCharsAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setHexCharactersCase(CodeCharactersCase.UPPER);
                    }
                }
            };
            ActionUtils.setupAction(upperHexCharsAction, resourceBundle, UPPER_HEX_CHARACTERS_ACTION_ID);
            upperHexCharsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            upperHexCharsAction.putValue(ActionUtils.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
            upperHexCharsAction.putValue(Action.SELECTED_KEY, hexCharactersCase == CodeCharactersCase.UPPER);
        }
        return upperHexCharsAction;
    }

    @Nonnull
    public Action getLowerHexCharsAction() {
        if (lowerHexCharsAction == null) {
            lowerHexCharsAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setHexCharactersCase(CodeCharactersCase.LOWER);
                    }
                }
            };
            ActionUtils.setupAction(lowerHexCharsAction, resourceBundle, LOWER_HEX_CHARACTERS_ACTION_ID);
            lowerHexCharsAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            lowerHexCharsAction.putValue(ActionUtils.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
            lowerHexCharsAction.putValue(Action.SELECTED_KEY, hexCharactersCase == CodeCharactersCase.LOWER);
        }
        return lowerHexCharsAction;
    }
}
