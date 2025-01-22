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

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.bined.BinEdFileHandler;
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
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.editor.text.options.gui.TextFontOptionsPanel;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.handler.DefaultControlHandler;

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

    private BasicValuesPositionColorModifier basicValuesColorModifier;

    private DefaultOptionsPage<DataInspectorOptionsImpl> dataInspectorOptionsPage;

    public BinedInspectorModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        basicValuesColorModifier = new BasicValuesPositionColorModifier();
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addPainterColorModifier(basicValuesColorModifier);
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
        return showParsingPanelAction;
    }

    public void registerViewValuesPanelMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(ActionConsts.VIEW_MENU_ID, VIEW_PARSING_PANEL_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.BOTTOM));
        contribution = mgmt.registerMenuItem(ActionConsts.VIEW_MENU_ID, createShowParsingPanelAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(VIEW_PARSING_PANEL_MENU_GROUP_ID));
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        dataInspectorOptionsPage = new DefaultOptionsPage<DataInspectorOptionsImpl>() {

            private DataInspectorOptionsPanel panel;
            private Font defaultFont;

            @Nonnull
            @Override
            public OptionsComponent<DataInspectorOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new DataInspectorOptionsPanel();
                    defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
                    panel.setDefaultFont(defaultFont);
                    panel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
                        @Override
                        public Font changeFont(Font currentFont) {
                            final Result result = new Result();
                            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                            final TextFontPanel fontPanel = new TextFontPanel();
                            fontPanel.setStoredFont(currentFont);
                            DefaultControlPanel controlPanel = new DefaultControlPanel();
                            final WindowHandler dialog = windowModule.createDialog(fontPanel, controlPanel);
                            windowModule.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
                            windowModule.setWindowTitle(dialog, fontPanel.getResourceBundle());
                            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                                        TextFontPreferences textFontParameters = new TextFontPreferences(preferencesModule.getAppPreferences());
                                        textFontParameters.setUseDefaultFont(true);
                                        textFontParameters.setFont(fontPanel.getStoredFont());
                                    }
                                    result.font = fontPanel.getStoredFont();
                                }

                                dialog.close();
                                dialog.dispose();
                            });
                            dialog.showCentered(frameModule.getFrame());

                            return result.font;
                        }

                        class Result {

                            Font font;
                        }
                    });
                }

                Font currentFont = defaultFont;
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    FileHandler fileHandler = activeFile.get();
                    if (fileHandler instanceof BinEdFileHandler) {
                        BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
                        BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
                        currentFont = componentExtension.getInputFieldsFont();
                    }
                }

                panel.setCurrentFont(currentFont);
                
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
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    return;
                }
                FileHandler fileHandler = activeFile.get();
                if (!(fileHandler instanceof BinEdFileHandler)) {
                    return;
                } 

                BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
                BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
                componentExtension.setShowParsingPanel(options.isShowParsingPanel());
                boolean useDefaultFont = options.isUseDefaultFont();
                Map<TextAttribute, ?> fontAttributes = options.getFontAttributes();
                componentExtension.setInputFieldsFont(useDefaultFont || fontAttributes == null ? defaultFont : new Font(fontAttributes));
            }
        };
        optionsModule.addOptionsPage(dataInspectorOptionsPage);
    }
}
