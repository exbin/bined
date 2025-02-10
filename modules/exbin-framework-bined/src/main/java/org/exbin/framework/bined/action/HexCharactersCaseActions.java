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
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeManager;

/**
 * Hex characters case actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HexCharactersCaseActions {

    public static final String UPPER_HEX_CHARACTERS_ACTION_ID = "upperHexCharactersAction";
    public static final String LOWER_HEX_CHARACTERS_ACTION_ID = "lowerHexCharactersAction";
    public static final String HEX_CHARACTERS_CASE_RADIO_GROUP_ID = "hexCharactersCaseRadioGroup";

    private ResourceBundle resourceBundle;

    public HexCharactersCaseActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action createUpperHexCharsAction() {
        UpperHexCharsAction upperHexCharsAction = new UpperHexCharsAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(upperHexCharsAction, resourceBundle, UPPER_HEX_CHARACTERS_ACTION_ID);
        upperHexCharsAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        upperHexCharsAction.putValue(ActionConsts.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
        upperHexCharsAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, upperHexCharsAction);
        return upperHexCharsAction;
    }

    @Nonnull
    public Action createLowerHexCharsAction() {
        LowerHexCharsAction lowerHexCharsAction = new LowerHexCharsAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(lowerHexCharsAction, resourceBundle, LOWER_HEX_CHARACTERS_ACTION_ID);
        lowerHexCharsAction.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        lowerHexCharsAction.putValue(ActionConsts.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
        lowerHexCharsAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, lowerHexCharsAction);
        return lowerHexCharsAction;
    }

    @ParametersAreNonnullByDefault
    private static class UpperHexCharsAction extends AbstractAction implements ActionContextChange {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(CodeCharactersCase.UPPER);
            // TODO App.getModule(ActionModuleApi.class).updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeCharactersCase codeCharactersCase = ((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase();
                    putValue(Action.SELECTED_KEY, codeCharactersCase == CodeCharactersCase.UPPER);
                }
                setEnabled(hasInstance);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private static class LowerHexCharsAction extends AbstractAction implements ActionContextChange {

        private CodeAreaCore codeArea;

        @Override
        public void actionPerformed(ActionEvent e) {
            ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(CodeCharactersCase.LOWER);
            // TODO App.getModule(ActionModuleApi.class).updateActionsForComponent(CodeAreaCore.class, codeArea);
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = instance != null;
                if (hasInstance) {
                    CodeCharactersCase codeCharactersCase = ((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase();
                    putValue(Action.SELECTED_KEY, codeCharactersCase == CodeCharactersCase.LOWER);
                }
                setEnabled(hasInstance);
            });
        }
    }
}
