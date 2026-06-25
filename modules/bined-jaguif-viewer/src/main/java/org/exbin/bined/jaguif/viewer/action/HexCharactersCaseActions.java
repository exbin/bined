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
import org.exbin.bined.CodeCharactersCase;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.component.CodeTypeState;

/**
 * Hex characters case actions.
 */
@NullMarked
public class HexCharactersCaseActions {

    public static final String HEX_CHARACTERS_CASE_RADIO_GROUP_ID = "hexCharactersCaseRadioGroup";

    private ResourceBundle resourceBundle;

    public HexCharactersCaseActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public UpperHexCharsAction createUpperHexCharsAction() {
        UpperHexCharsAction upperHexCharsAction = new UpperHexCharsAction();
        upperHexCharsAction.init(resourceBundle);
        return upperHexCharsAction;
    }

    public LowerHexCharsAction createLowerHexCharsAction() {
        LowerHexCharsAction lowerHexCharsAction = new LowerHexCharsAction();
        lowerHexCharsAction.init(resourceBundle);
        return lowerHexCharsAction;
    }

    public UpperHexCharsContribution createUpperHexCharsContribution() {
        UpperHexCharsContribution upperHexCharsContribution = new UpperHexCharsContribution();
        return upperHexCharsContribution;
    }

    public LowerHexCharsContribution createLowerHexCharsContribution() {
        LowerHexCharsContribution lowerHexCharsContribution = new LowerHexCharsContribution();
        return lowerHexCharsContribution;
    }

    @NullMarked
    public static class UpperHexCharsAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "upperHexCharacters";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            HexCharactersCaseActions.setCodeCharactersCase(binaryDataComponent, CodeCharactersCase.UPPER);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.HEX_CHARACTERS_CASE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeCharactersCase codeCharactersCase = binaryDataComponent.getCodeCharactersCase();
                putValue(Action.SELECTED_KEY, codeCharactersCase == CodeCharactersCase.UPPER);
            }
            setEnabled(hasInstance);
        }
    }

    @NullMarked
    public static class LowerHexCharsAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "lowerHexCharacters";

        private BinaryDataComponent binaryDataComponent;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
            putValue(ActionConsts.ACTION_RADIO_GROUP, HEX_CHARACTERS_CASE_RADIO_GROUP_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            HexCharactersCaseActions.setCodeCharactersCase(binaryDataComponent, CodeCharactersCase.LOWER);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextComponent.class, (instance, updateType) -> {
                if (CodeTypeState.UpdateType.HEX_CHARACTERS_CASE.equals(updateType)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeCharactersCase codeCharactersCase = binaryDataComponent.getCodeCharactersCase();
                putValue(Action.SELECTED_KEY, codeCharactersCase == CodeCharactersCase.LOWER);
            }
            setEnabled(hasInstance);
        }
    }

    @NullMarked
    public class UpperHexCharsContribution implements ActionSequenceContribution {

        public UpperHexCharsContribution() {
        }

        @Override
        public Action createAction() {
            UpperHexCharsAction action = new UpperHexCharsAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return UpperHexCharsAction.ACTION_ID;
        }
    }

    @NullMarked
    public class LowerHexCharsContribution implements ActionSequenceContribution {

        public LowerHexCharsContribution() {
        }

        @Override
        public Action createAction() {
            LowerHexCharsAction action = new LowerHexCharsAction();
            action.init(resourceBundle);
            return action;
        }

        @Override
        public String getContributionId() {
            return LowerHexCharsAction.ACTION_ID;
        }
    }

    public static void setCodeCharactersCase(BinaryDataComponent binaryDataComponent, CodeCharactersCase codeCharactersCase) {
        binaryDataComponent.setCodeCharactersCase(codeCharactersCase);
    }
}
