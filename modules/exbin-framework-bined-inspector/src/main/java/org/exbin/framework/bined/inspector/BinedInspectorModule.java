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
package org.exbin.framework.bined.inspector;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.bined.BinEdComponentExtension;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.action.ShowParsingPanelAction;
import org.exbin.framework.bined.inspector.settings.DataInspectorOptions;
import org.exbin.framework.bined.inspector.settings.DataInspectorSettingsApplier;
import org.exbin.framework.bined.inspector.settings.DataInspectorSettingsComponent;
import org.exbin.framework.bined.inspector.settings.DataInspectorFontOptions;
import org.exbin.framework.bined.viewer.BinedViewerModule;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.document.api.ContextDocument;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;

/**
 * Binary editor data inspector module.
 *
 * @author ExBin Project (https://exbin.org)
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
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addPainterColorModifier(basicValuesColorModifier);
        fileManager.addBinEdComponentExtension(new BinEdInspectorFileExtension());
    }

    @Nonnull
    public ShowParsingPanelAction createShowParsingPanelAction() {
        ensureSetup();
        ShowParsingPanelAction showParsingPanelAction = new ShowParsingPanelAction();
        showParsingPanelAction.setup(resourceBundle);
        showParsingPanelAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                boolean inShowSubmenu = BinedViewerModule.SHOW_POPUP_SUBMENU_ID.equals(subMenuId);
                return popupMenuVariant == BinedModule.PopupMenuVariant.EDITOR && ((inShowSubmenu && popupMenuPositionZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && popupMenuPositionZone != BasicCodeAreaZone.CODE_AREA));
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
                // menuItem.setSelected(Objects.requireNonNull(getActiveCodeArea().getLayoutProfile()).isShowHeader());
            }
        });
        return showParsingPanelAction;
    }

    public void registerShowParsingPanelMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_PARSING_PANEL_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = mgmt.registerMenuItem(createShowParsingPanelAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_PARSING_PANEL_MENU_GROUP_ID));
    }

    public void registerShowParsingPanelPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createShowParsingPanelAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_VIEW_GROUP_ID));

        MenuDefinitionManagement subMgmt = mgmt.getSubMenu(BinedViewerModule.SHOW_POPUP_SUBMENU_ID);
        contribution = subMgmt.registerMenuItem(createShowParsingPanelAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        settingsManagement.registerOptionsSettings(DataInspectorOptions.class, (optionsStorage) -> new DataInspectorOptions(optionsStorage));
        
        settingsManagement.registerOptionsSettings(DataInspectorFontOptions.class, (optionsStorage) -> new DataInspectorFontOptions(optionsStorage));
        settingsManagement.registerApplySetting(ContextDocument.class, new ApplySettingsContribution(DataInspectorSettingsApplier.APPLIER_ID, new DataInspectorSettingsApplier()));

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
