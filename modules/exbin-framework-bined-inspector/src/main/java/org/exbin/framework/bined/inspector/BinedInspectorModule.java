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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.action.ShowParsingPanelAction;
import org.exbin.framework.bined.inspector.options.page.DataInspectorOptionsPage;
import org.exbin.framework.bined.viewer.BinedViewerModule;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.ParentOptionsGroupRule;

/**
 * Binary editor data inspector module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedInspectorModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedInspectorModule.class);

    private static final String VIEW_PARSING_PANEL_MENU_GROUP_ID = MODULE_ID + ".viewParsingPanelMenuGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    private BinEdInspectorManager binEdInspectorManager;
    private BasicValuesPositionColorModifier basicValuesColorModifier;

    private DataInspectorOptionsPage dataInspectorOptionsPage;

    public BinedInspectorModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        setEditorProvider(editorProvider, null);
    }

    public void setEditorProvider(EditorProvider editorProvider, @Nullable BinEdInspectorComponentExtension.ComponentsProvider componentsProvider) {
        this.editorProvider = editorProvider;

        BinEdInspectorManager inspectorManager = getBinEdInspectorManager();
        inspectorManager.addInspector(new BasicValuesInspectorProvider());
        basicValuesColorModifier = new BasicValuesPositionColorModifier();
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addPainterColorModifier(basicValuesColorModifier);
        fileManager.addBinEdComponentExtension(new BinEdInspectorFileExtension(componentsProvider));
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedInspectorModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    private void ensureSetup() {
        if (editorProvider == null) {
            getEditorProvider();
        }

        if (resourceBundle == null) {
            getResourceBundle();
        }
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_PARSING_PANEL_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = mgmt.registerMenuItem(createShowParsingPanelAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_PARSING_PANEL_MENU_GROUP_ID));
    }

    public void registerShowParsingPanelPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createShowParsingPanelAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_VIEW_GROUP_ID));

        MenuManagement subMgmt = mgmt.getSubMenu(BinedViewerModule.SHOW_POPUP_SUBMENU_ID);
        contribution = subMgmt.registerMenuItem(createShowParsingPanelAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);

        OptionsGroup inspectorOptionsGroup = optionsModule.createOptionsGroup("inspector", getResourceBundle());
        optionsPageManagement.registerGroup(inspectorOptionsGroup);
        optionsPageManagement.registerGroupRule(inspectorOptionsGroup, new ParentOptionsGroupRule("binaryEditor"));

        dataInspectorOptionsPage = new DataInspectorOptionsPage();
        dataInspectorOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(dataInspectorOptionsPage);
        optionsPageManagement.registerPageRule(dataInspectorOptionsPage, new GroupOptionsPageRule(inspectorOptionsGroup));
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

        private final BinEdInspectorComponentExtension.ComponentsProvider componentsProvider;

        public BinEdInspectorFileExtension(@Nullable BinEdInspectorComponentExtension.ComponentsProvider componentsProvider) {
            this.componentsProvider = componentsProvider;
        }

        @Nonnull
        @Override
        public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
            BinEdInspectorComponentExtension binEdComponentInspector = new BinEdInspectorComponentExtension(componentsProvider);
            return Optional.of(binEdComponentInspector);
        }
    }
}
