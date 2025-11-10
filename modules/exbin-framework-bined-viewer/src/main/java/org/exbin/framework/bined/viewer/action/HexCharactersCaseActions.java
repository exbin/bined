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
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.viewer.BinedViewerChangeMessage;

/**
 * Hex characters case actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HexCharactersCaseActions {

    public static final String HEX_CHARACTERS_CASE_RADIO_GROUP_ID = "hexCharactersCaseRadioGroup";

    private ResourceBundle resourceBundle;

    public HexCharactersCaseActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public UpperHexCharsAction createUpperHexCharsAction() {
        UpperHexCharsAction upperHexCharsAction = new UpperHexCharsAction();
        upperHexCharsAction.setup(resourceBundle);
        return upperHexCharsAction;
    }

    @Nonnull
    public LowerHexCharsAction createLowerHexCharsAction() {
        LowerHexCharsAction lowerHexCharsAction = new LowerHexCharsAction();
        lowerHexCharsAction.setup(resourceBundle);
        return lowerHexCharsAction;
    }

    @ParametersAreNonnullByDefault
    public static class UpperHexCharsAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "upperHexCharactersAction";

        private BinaryDataComponent binaryDataComponent;

        public void setup(ResourceBundle resourceBundle) {
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
        public void register(ActionContextChangeRegistration registrar) {
            registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerContextMessageListener(ContextComponent.class, (instance, changeMessage) -> {
                if (BinedViewerChangeMessage.CODE_TYPE.equals(changeMessage)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeCharactersCase codeCharactersCase = ((CodeCharactersCaseCapable) binaryDataComponent.getCodeArea()).getCodeCharactersCase();
                putValue(Action.SELECTED_KEY, codeCharactersCase == CodeCharactersCase.UPPER);
            }
            setEnabled(hasInstance);
        }
    }

    @ParametersAreNonnullByDefault
    public static class LowerHexCharsAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "lowerHexCharactersAction";

        private BinaryDataComponent binaryDataComponent;

        public void setup(ResourceBundle resourceBundle) {
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
        public void register(ActionContextChangeRegistration registrar) {
            registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerContextMessageListener(ContextComponent.class, (instance, changeMessage) -> {
                if (BinedViewerChangeMessage.CODE_TYPE.equals(changeMessage)) {
                    updateByContext(instance);
                }
            });
        }

        public void updateByContext(ContextComponent context) {
            binaryDataComponent = context instanceof BinaryDataComponent ? (BinaryDataComponent) context : null;
            boolean hasInstance = context != null;
            if (hasInstance) {
                CodeCharactersCase codeCharactersCase = ((CodeCharactersCaseCapable) binaryDataComponent.getCodeArea()).getCodeCharactersCase();
                putValue(Action.SELECTED_KEY, codeCharactersCase == CodeCharactersCase.LOWER);
            }
            setEnabled(hasInstance);
        }
    }

    public static void setCodeCharactersCase(BinaryDataComponent binaryDataComponent, CodeCharactersCase codeCharactersCase) {
        ((CodeCharactersCaseCapable) binaryDataComponent.getCodeArea()).setCodeCharactersCase(codeCharactersCase);
        // TODO invoke change notification
    }
}
