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
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.action.ShowParsingPanelAction;
import org.exbin.framework.bined.inspector.options.gui.DataInspectorOptionsPanel;
import org.exbin.framework.bined.inspector.options.impl.DataInspectorOptionsImpl;
import org.exbin.framework.bined.inspector.preferences.DataInspectorPreferences;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;

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

    private BasicValuesPositionColorModifier basicValuesColorModifier = new BasicValuesPositionColorModifier();
    private ShowParsingPanelAction showParsingPanelAction;

    private DefaultOptionsPage<DataInspectorOptionsImpl> dataInspectorOptionsPage;

    public BinedInspectorModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addPainterColorModifier(basicValuesColorModifier);
        fileManager.addActionStatusUpdateListener(this::updateActionStatus);
        fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
            @Nonnull
            @Override
            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                BinEdComponentInspector binEdComponentInspector = new BinEdComponentInspector();
                binEdComponentInspector.setBasicValuesColorModifier(basicValuesColorModifier);
                return Optional.of(binEdComponentInspector);
            }
        });
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
        if (showParsingPanelAction != null) {
            showParsingPanelAction.updateForActiveFile();
        }
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
    public ShowParsingPanelAction getShowParsingPanelAction() {
        if (showParsingPanelAction == null) {
            ensureSetup();
            showParsingPanelAction = new ShowParsingPanelAction();
            showParsingPanelAction.setup(editorProvider, resourceBundle);
        }

        return showParsingPanelAction;
    }

    public void registerViewValuesPanelMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuGroup(ActionConsts.VIEW_MENU_ID, new MenuGroup(VIEW_PARSING_PANEL_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.NONE));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, getShowParsingPanelAction(), new MenuPosition(VIEW_PARSING_PANEL_MENU_GROUP_ID));
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        dataInspectorOptionsPage = new DefaultOptionsPage<DataInspectorOptionsImpl>() {

            private DataInspectorOptionsPanel panel;

            @Nonnull
            @Override
            public OptionsComponent<DataInspectorOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new DataInspectorOptionsPanel();
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(DataInspectorOptionsPanel.class);
            }

            @Nonnull
            @Override
            public DataInspectorOptionsImpl createOptions() {
                return new DataInspectorOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, DataInspectorOptionsImpl options) {
                options.loadFromPreferences(new DataInspectorPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, DataInspectorOptionsImpl options) {
                options.saveToPreferences(new DataInspectorPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(DataInspectorOptionsImpl options) {
                getShowParsingPanelAction().setShowValuesPanel(options.isShowParsingPanel());
            }
        };
        optionsModule.addOptionsPage(dataInspectorOptionsPage);
    }
}
