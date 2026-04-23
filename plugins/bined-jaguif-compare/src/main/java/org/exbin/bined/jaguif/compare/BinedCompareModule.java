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
package org.exbin.bined.jaguif.compare;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.PluginModule;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.bined.jaguif.compare.action.CompareFilesAction;
import org.exbin.bined.jaguif.compare.contribution.CompareFilesContribution;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.ui.api.UiModuleApi;

/**
 * Binary editor compare module.
 */
@ParametersAreNonnullByDefault
public class BinedCompareModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedCompareModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedCompareModule() {
    }

    @Override
    public void register() {
        UiModuleApi uiModule = App.getModule(UiModuleApi.class);
        uiModule.addPostInitAction(() -> {
            registerToolsOptionsMenuActions();
        });
    }

    public void registerToolsOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = new CompareFilesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
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
        CompareFilesAction compareFilesAction = new CompareFilesAction();
        compareFilesAction.init(getResourceBundle());
        return compareFilesAction;
    }
}
