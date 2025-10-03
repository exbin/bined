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
package org.exbin.framework.bined.launcher;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.exbin.framework.App;
import org.exbin.framework.LauncherModule;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.manager.ActionManagerModule;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.update.api.AddonUpdateModuleApi;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.BinaryMultiEditorProvider;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.editor.BinedEditorModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.launcher.options.StartupOptions;
import org.exbin.framework.bined.launcher.options.StartupOptions.StartupBehavior;
import org.exbin.framework.bined.launcher.options.page.StartupOptionsPage;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.viewer.options.BinaryAppearanceOptions;
import org.exbin.framework.bined.editor.options.BinaryEditorOptions;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.bined.theme.BinedThemeModule;
import org.exbin.framework.bined.viewer.BinedViewerModule;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.help.online.api.HelpOnlineModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.theme.api.UiThemeModuleApi;

/**
 * Binary editor launcher module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedLauncherModule implements LauncherModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedLauncherModule.class);

    private static final String BINARY_PLUGIN_ID = "binary";

    private static final String OPTION_HELP = "h";
    private static final String OPTION_VERBOSE = "v";
    private static final String OPTION_DEV = "dev";
    private static final String OPTION_DEMO = "demo";
    private static final String OPTION_SINGLE_FILE = "single_file";
    private static final String OPTION_MULTI_FILE = "multi_file";
    private static final String OPTION_FULLSCREEN = "fullscreen";

    public BinedLauncherModule() {
    }

    @Override
    public void launch(String[] args) {
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        OptionsStorage preferences = preferencesModule.getAppPreferences();
        ResourceBundle bundle = App.getModule(LanguageModuleApi.class).getBundle(BinedLauncherModule.class);

        try {
            // Parameters processing
            Options opt = new Options();
            opt.addOption(OPTION_HELP, "help", false, bundle.getString("cl_option_help"));
            opt.addOption(OPTION_VERBOSE, false, bundle.getString("cl_option_verbose"));
            opt.addOption(OPTION_DEV, false, bundle.getString("cl_option_dev"));
            opt.addOption(OPTION_DEMO, false, bundle.getString("cl_option_demo"));
            opt.addOption(OPTION_FULLSCREEN, false, bundle.getString("cl_option_fullscreen"));
            OptionGroup editorProviderType = new OptionGroup();
            editorProviderType.addOption(new Option(OPTION_SINGLE_FILE, bundle.getString("cl_option_single_file")));
            editorProviderType.addOption(new Option(OPTION_MULTI_FILE, bundle.getString("cl_option_multi_file")));
            opt.addOptionGroup(editorProviderType);
            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(opt, args);
            if (cl.hasOption(OPTION_HELP)) {
                HelpFormatter f = new HelpFormatter();
                f.printHelp(bundle.getString("cl_syntax"), opt);
                return;
            }
            boolean verboseMode = cl.hasOption(OPTION_VERBOSE);
            boolean devMode = cl.hasOption(OPTION_DEV);
            boolean demoMode = cl.hasOption(OPTION_DEMO);
            boolean fullScreenMode = cl.hasOption(OPTION_FULLSCREEN);
            String editorProvideType = editorProviderType.getSelected();

            if (demoMode) {
                // Don't use delta mode
                preferences.put(BinaryEditorOptions.KEY_FILE_HANDLING_MODE, FileHandlingMode.MEMORY.name());
            }

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            languageModule.setAppBundle(bundle);

            final UiModuleApi uiModule = App.getModule(UiModuleApi.class);
            final UiThemeModuleApi themeModule = App.getModule(UiThemeModuleApi.class);
            themeModule.registerThemeInit();

            uiModule.initSwingUi();

            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            if (demoMode) {
                frameModule.switchFrameToUndecorated();
            }

            EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
            MenuPopupModuleApi menuPopupModule = App.getModule(MenuPopupModuleApi.class);
            ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
            AboutModuleApi aboutModule = App.getModule(AboutModuleApi.class);
            HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
            HelpOnlineModuleApi helpOnlineModule = App.getModule(HelpOnlineModuleApi.class);
            OperationUndoModuleApi undoModule = App.getModule(OperationUndoModuleApi.class);
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
            AddonUpdateModuleApi updateModule = App.getModule(AddonUpdateModuleApi.class);

            BinedModule binedModule = App.getModule(BinedModule.class);
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            BinedEditorModule binedEditorModule = App.getModule(BinedEditorModule.class);
            BinedThemeModule binedThemeModule = App.getModule(BinedThemeModule.class);
            BinaryAppearanceOptions binaryAppearanceParameters = new BinaryAppearanceOptions(preferences);
            boolean multiFileMode = binaryAppearanceParameters.isMultiFileMode();
            EditorProviderVariant editorProviderVariant = editorProvideType != null
                    ? (OPTION_SINGLE_FILE.equals(editorProvideType) ? EditorProviderVariant.SINGLE : EditorProviderVariant.MULTI)
                    : (multiFileMode ? EditorProviderVariant.MULTI : EditorProviderVariant.SINGLE);
            binedModule.initEditorProvider(editorProviderVariant);
            EditorProvider editorProvider = binedModule.getEditorProvider();
            editorModule.registerEditor(BINARY_PLUGIN_ID, editorProvider);

            BinedSearchModule binedSearchModule = App.getModule(BinedSearchModule.class);
            binedSearchModule.setEditorProvider(editorProvider);
            binedSearchModule.registerSearchComponent();

            BinedOperationModule binedOperationModule = App.getModule(BinedOperationModule.class);
            binedOperationModule.addBasicMethods();

            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            binedInspectorModule.setEditorProvider(editorProvider);

            AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
            addonManagerModule.setDevMode(devMode);
            addonManagerModule.setAddonServiceCoreUrl("https://bined.exbin.org/");
            addonManagerModule.setManualLegacyGitHubUrl("https://github.com/exbin/bined/releases/tag/");
            ActionManagerModule actionManagerModule = App.getModule(ActionManagerModule.class);

            frameModule.init();
            if (!demoMode) {
                try {
                    updateModule.setUpdateUrl(new URI(bundle.getString("update_url")).toURL());
                    updateModule.setUpdateDownloadUrl(new URI(bundle.getString("update_download_url")).toURL());
                } catch (MalformedURLException | URISyntaxException ex) {
                    Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateModule.registerDefaultMenuItem();
            }

            // helpModule.registerMainMenu();
            aboutModule.registerDefaultMenuItem();
            try {
                helpOnlineModule.setOnlineHelpUrl(new URI(bundle.getString("online_help_url")).toURL());
            } catch (MalformedURLException | URISyntaxException ex) {
                Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
            }
            helpOnlineModule.registerOpeningHandler();
            helpOnlineModule.registerOnlineHelpMenu();

            if (!demoMode) {
                frameModule.registerExitAction();
            }
            frameModule.registerBarsVisibilityActions();

            fileModule.registerMenuFileHandlingActions();
            if (editorProviderVariant == EditorProviderVariant.MULTI) {
                editorModule.registerMenuFileCloseActions();
            }

            fileModule.registerToolBarFileHandlingActions();
            fileModule.registerRecenFilesMenuActions();
            fileModule.registerCloseListener();

            undoModule.registerMainMenu();
            undoModule.registerMainToolBar();

            // Register clipboard editing actions
            menuPopupModule.registerDefaultClipboardPopupMenuWithIcons();
            menuModule.registerMenuClipboardActions();
            toolBarModule.registerToolBarClipboardActions();

            optionsModule.registerMenuAction();

            binedViewerModule.registerCodeTypeToolBarActions();
            binedModule.registerShowNonprintablesToolBarActions();
//                binedModule.registerEditFindToolBarActions();
            binedModule.registerViewNonprintablesMenuActions();
            binedInspectorModule.registerShowParsingPanelMenuActions();
            binedInspectorModule.registerShowParsingPanelPopupMenuActions();
            binedViewerModule.registerToolsOptionsMenuActions();
            binedEditorModule.registerEditSelectionAction();
            binedModule.registerClipboardCodeActions();
            binedViewerModule.registerEncodings();
            binedModule.registerGoToPosition();
            binedSearchModule.registerEditFindMenuActions();
            binedOperationModule.registerBlockEditActions();

            binedModule.registerCodeAreaPopupMenu();
            binedViewerModule.registerCodeAreaPopupMenu();
            binedEditorModule.registerCodeAreaPopupMenu();
            binedSearchModule.registerEditFindPopupMenuActions();
            binedOperationModule.registerBlockEditPopupMenuActions();

            binedEditorModule.registerPropertiesMenu();
            binedEditorModule.registerReloadFileMenu();
            // TODO binedModule.registerPrintMenu();
            binedViewerModule.registerViewModeMenu();
            binedViewerModule.registerCodeTypeMenu();
            binedViewerModule.registerPositionCodeTypeMenu();
            binedViewerModule.registerHexCharactersCaseHandlerMenu();
            binedViewerModule.registerLayoutMenu();

            final ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
//                UndoHandlerWrapper undoHandlerWrapper = new UndoHandlerWrapper();

//                undoModule.setUndoHandler(((UndoFileHandler) editorProvider).getUndoHandler());
            uiModule.registerOptionsPanels();
            themeModule.registerOptionsPanels();
            actionManagerModule.registerOptionsPanels();
            fileModule.registerOptionsPanels();
            editorModule.registerOptionsPanels();
            binedViewerModule.registerOptionsPanels();
            binedEditorModule.registerOptionsPanels();
            binedThemeModule.registerOptionsPanels();
            binedInspectorModule.registerOptionsPanels();
            registerOptionsPanels(); // Register startup options
            if (!demoMode) {
                updateModule.registerOptionsPanels();
            }

            binedViewerModule.registerStatusBar();
            binedModule.registerUndoHandler();

            binedModule.loadFromOptions(preferences);

            if (demoMode) {
                frameModule.addExitListener((ApplicationFrameHandler afh) -> {
                    return false;
                });
            } else {
                frameModule.addExitListener((ApplicationFrameHandler afh) -> {
                    // Save frame position
                    frameModule.saveFramePosition();

                    // Save session files if in multi-file mode
                    EditorProvider currentProvider = binedModule.getEditorProvider();
                    if (currentProvider instanceof BinaryMultiEditorProvider) {
                        BinaryMultiEditorProvider multiProvider = (BinaryMultiEditorProvider) currentProvider;
                        List<URI> openFiles = multiProvider.getOpenFileUris();
                        StartupOptions startupOptions = new StartupOptions(preferences);
                        startupOptions.setLastSessionFiles(openFiles);
                    }

                    return true;
                });
            }

            JComponent editorComponent = editorModule.getEditorComponent();
            frameHandler.setMainPanel(editorComponent);
            addonManagerModule.registerAddonManagerMenuItem();

            frameHandler.setDefaultSize(new Dimension(600, 400));
            frameModule.loadFramePosition();
            optionsModule.initialLoadFromPreferences();
            if (fullScreenMode || demoMode) {
                frameModule.switchFrameToFullscreen();
            }
            frameHandler.loadMainMenu();
            frameHandler.loadMainToolBar();

            frameHandler.showFrame();

            String filePath = null;
            List fileArgs = cl.getArgList();
            if (!fileArgs.isEmpty()) {
                filePath = (String) fileArgs.get(0);
            }

            if (filePath == null) {
                // Apply startup behavior from options
                StartupOptions startupOptions = new StartupOptions(preferences);
                StartupBehavior startupBehavior = startupOptions.getStartupBehavior();

                switch (startupBehavior) {
                    case START_EMPTY:
                        // Do nothing - start empty
                        break;
                    case NEW_FILE:
                        // Start with a single new file
                        binedModule.start();
                        break;
                    case REOPEN_SESSION:
                        // Reopen last session files (only in multi-file mode)
                        if (editorProviderVariant == EditorProviderVariant.MULTI) {
                            List<URI> sessionFiles = startupOptions.getLastSessionFiles();
                            if (sessionFiles.isEmpty()) {
                                // No session files, fallback to new file
                                binedModule.start();
                            } else {
                                // Load session files
                                for (URI fileUri : sessionFiles) {
                                    try {
                                        fileModule.loadFromFile(fileUri);
                                    } catch (Exception ex) {
                                        Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.WARNING,
                                            "Failed to load session file: " + fileUri, ex);
                                    }
                                }
                            }
                        } else {
                            // Single file mode, fallback to new file
                            binedModule.start();
                        }
                        break;
                }
            } else {
                binedModule.startWithFile(filePath);
            }

            if (!demoMode) {
                updateModule.checkOnStart(frameHandler.getFrame());
            }
        } catch (ParseException | RuntimeException ex) {
            Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
//                System.exit(1);
        }
    }

    /**
     * Registers startup options panels.
     */
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);

        StartupOptionsPage startupOptionsPage = new StartupOptionsPage();
        optionsPageManagement.registerPage(startupOptionsPage);
    }
}
