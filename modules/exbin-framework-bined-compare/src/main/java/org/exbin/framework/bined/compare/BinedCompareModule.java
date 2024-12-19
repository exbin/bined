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
package org.exbin.framework.bined.compare;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.bined.compare.action.CompareFilesAction;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Binary editor compare module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedCompareModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedCompareModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedCompareModule() {
    }

    public void registerToolsOptionsMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuContribution contribution = actionModule.registerMenuItem(ActionConsts.TOOLS_MENU_ID, MODULE_ID, createCompareFilesAction());
        actionModule.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.TOP));
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedCompareModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public AbstractAction createCompareFilesAction() {
        ensureSetup();
        CompareFilesAction compareFilesAction = new CompareFilesAction();
        compareFilesAction.setup(resourceBundle);
        return compareFilesAction;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
