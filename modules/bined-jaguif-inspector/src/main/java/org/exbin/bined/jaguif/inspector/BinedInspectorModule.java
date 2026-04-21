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
package org.exbin.bined.jaguif.inspector;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaZone;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.bined.jaguif.component.BinEdComponentExtension;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.bined.jaguif.component.BinEdFileManager;
import org.exbin.bined.jaguif.component.BinaryFileDocument;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.inspector.action.ShowParsingPanelAction;
import org.exbin.bined.jaguif.inspector.contribution.ShowParsingPanelContribution;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorOptions;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorSettingsApplier;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorSettingsComponent;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorFontOptions;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.jaguif.context.api.ContextStateProvider;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;

/**
 * Binary editor data inspector module.
 */
@ParametersAreNonnullByDefault
public class BinedInspectorModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedInspectorModule.class);
    public static final String SETTINGS_PAGE_ID = "dataInspector";

    private static final String VIEW_PARSING_PANEL_MENU_GROUP_ID = MODULE_ID + ".viewParsingPanelMenuGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private BinEdInspectorManager binEdInspectorManager;
    private BasicValuesPositionColorModifier basicValuesColorModifier;

    public BinedInspectorModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedInspectorModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerBasicInspector() {
        BinEdInspectorManager inspectorManager = getBinEdInspectorManager();
        inspectorManager.addInspector(new BasicValuesInspectorProvider(getResourceBundle()));
        basicValuesColorModifier = new BasicValuesPositionColorModifier();
        BinedComponentModule binedModule = App.getModule(BinedComponentModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addPainterColorModifier(basicValuesColorModifier);
        fileManager.addBinEdComponentExtension(new BinEdInspectorFileExtension());
    }

    @Nonnull
    public ShowParsingPanelAction createShowParsingPanelAction() {
        ensureSetup();
        ShowParsingPanelAction showParsingPanelAction = new ShowParsingPanelAction();
        showParsingPanelAction.init(resourceBundle);
        showParsingPanelAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                CodeAreaZone codeAreaZone = contextState.getActiveState(CodeAreaZone.class);
                ContextDocument contextDocument = contextState.getActiveState(ContextDocument.class);
                boolean inShowSubmenu = BinedViewerModule.SHOW_POPUP_SUBMENU_ID.equals(subMenuId);
                return contextDocument instanceof BinaryFileDocument && ((inShowSubmenu && codeAreaZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && codeAreaZone != BasicCodeAreaZone.CODE_AREA));
            }
        });
        return showParsingPanelAction;
    }

    public void registerShowParsingPanelMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_PARSING_PANEL_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = new ShowParsingPanelContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_PARSING_PANEL_MENU_GROUP_ID));
    }

    public void registerShowParsingPanelPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = new ShowParsingPanelContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_VIEW_GROUP_ID));

        MenuDefinitionManagement subMgmt = mgmt.getSubMenu(BinedViewerModule.SHOW_POPUP_SUBMENU_ID);
        contribution = new ShowParsingPanelContribution();
        subMgmt.registerMenuContribution(contribution);
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        settingsManagement.registerSettingsOptions(DataInspectorOptions.class, (optionsStorage) -> new DataInspectorOptions(optionsStorage));
        
        settingsManagement.registerSettingsOptions(DataInspectorFontOptions.class, (optionsStorage) -> new DataInspectorFontOptions(optionsStorage));
        settingsManagement.registerApplySetting(DataInspectorOptions.class, new ApplySettingsContribution(DataInspectorSettingsApplier.APPLIER_ID, new DataInspectorSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextDocument.class, new ApplySettingsContribution(DataInspectorSettingsApplier.APPLIER_ID, new DataInspectorSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, getResourceBundle());
        settingsManagement.registerPage(pageContribution);
        settingsManagement.registerSettingsRule(pageContribution, new SettingsPageContributionRule("binary"));
        
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(DataInspectorSettingsComponent.COMPONENT_ID, new DataInspectorSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }

    @Nonnull
    public BinEdInspectorManager getBinEdInspectorManager() {
        if (binEdInspectorManager == null) {
            binEdInspectorManager = new BinEdInspectorManager();
        }
        return binEdInspectorManager;
    }

    @Nonnull
    public BasicValuesPositionColorModifier getBasicValuesColorModifier() {
        return Objects.requireNonNull(basicValuesColorModifier);
    }

    @ParametersAreNonnullByDefault
    public static class BinEdInspectorFileExtension implements BinEdFileManager.BinEdFileExtension {

        public BinEdInspectorFileExtension() {
        }

        @Nonnull
        @Override
        public Optional<BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
            BinEdInspectorComponentExtension binEdComponentInspector = new BinEdInspectorComponentExtension();
            return Optional.of(binEdComponentInspector);
        }
    }
}
