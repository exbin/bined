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
import java.net.URL;
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
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.bookmarks.BinedBookmarksModule;
import org.exbin.framework.bined.compare.BinedCompareModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.macro.BinedMacroModule;
import org.exbin.framework.bined.objectdata.BinedObjectDataModule;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.operation.bouncycastle.BinedOperationBouncycastleModule;
import org.exbin.framework.bined.preferences.BinaryAppearancePreferences;
import org.exbin.framework.bined.preferences.EditorPreferences;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.bined.tool.content.BinedToolContentModule;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.help.online.api.HelpOnlineModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;

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
        try {
            preferencesModule.setupAppPreferences(Class.forName("org.exbin.bined.editor.BinedEditor"));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        Preferences preferences = preferencesModule.getAppPreferences();
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
                preferences.put(EditorPreferences.PREFERENCES_FILE_HANDLING_MODE, FileHandlingMode.MEMORY.name());
            }

            final UiModuleApi uiModule = App.getModule(UiModuleApi.class);
            final WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            AboutModuleApi aboutModule = App.getModule(AboutModuleApi.class);
            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
            HelpOnlineModuleApi helpOnlineModule = App.getModule(HelpOnlineModuleApi.class);
            OperationUndoModuleApi undoModule = App.getModule(OperationUndoModuleApi.class);
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
            AddonUpdateModuleApi updateModule = App.getModule(AddonUpdateModuleApi.class);

            languageModule.setAppBundle(bundle);
            uiModule.initSwingUi();
            BinedModule binedModule = App.getModule(BinedModule.class);
            BinaryAppearancePreferences binaryAppearanceParameters = new BinaryAppearancePreferences(preferences);
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
            binedOperationModule.setEditorProvider(editorProvider);

            BinedOperationBouncycastleModule binedOperationBouncycastleModule = App.getModule(BinedOperationBouncycastleModule.class);
            binedOperationBouncycastleModule.setEditorProvider(editorProvider);

            BinedCompareModule binedCompareModule = App.getModule(BinedCompareModule.class);
            BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);
            binedBookmarksModule.setEditorProvider(editorProvider);

            BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
            binedMacroModule.setEditorProvider(editorProvider);

            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            binedInspectorModule.setEditorProvider(editorProvider);

            BinedObjectDataModule binedObjectDataModule = App.getModule(BinedObjectDataModule.class);
            BinedToolContentModule binedToolContentModule = App.getModule(BinedToolContentModule.class);

            AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
            ActionManagerModule actionManagerModule = App.getModule(ActionManagerModule.class);
            actionManagerModule.registerOptionsPanels();

            frameModule.createMainMenu();
            if (!demoMode) {
                try {
                    updateModule.setUpdateUrl(new URL(bundle.getString("update_url")));
                    updateModule.setUpdateDownloadUrl(new URL(bundle.getString("update_download_url")));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateModule.registerDefaultMenuItem();
            }

            helpModule.registerMainMenu();
            aboutModule.registerDefaultMenuItem();
            try {
                helpOnlineModule.setOnlineHelpUrl(new URL(bundle.getString("online_help_url")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            actionModule.registerClipboardTextActions();
            actionModule.registerMenuClipboardActions();
            actionModule.registerToolBarClipboardActions();

            optionsModule.registerMenuAction();

            binedModule.registerCodeTypeToolBarActions();
            binedModule.registerShowNonprintablesToolBarActions();
//                binedModule.registerEditFindToolBarActions();
            binedModule.registerViewNonprintablesMenuActions();
            binedInspectorModule.registerViewValuesPanelMenuActions();
            binedModule.registerToolsOptionsMenuActions();
            binedCompareModule.registerToolsOptionsMenuActions();
            binedModule.registerEditSelectionAction();
            binedModule.registerClipboardCodeActions();
            binedModule.registerEncodings();
            binedModule.registerGoToPosition();
            binedSearchModule.registerEditFindMenuActions();
            binedBookmarksModule.registerBookmarksMenuActions();
            binedMacroModule.registerMacrosMenuActions();
            binedOperationModule.registerBlockEditActions();

            binedModule.registerCodeAreaPopupMenu();
            binedSearchModule.registerEditFindPopupMenuActions();
            binedBookmarksModule.registerBookmarksPopupMenuActions();
            binedMacroModule.registerMacrosPopupMenuActions();
            binedOperationModule.registerBlockEditPopupMenuActions();

            binedModule.registerPropertiesMenu();
            binedModule.registerReloadFileMenu();
            binedToolContentModule.registerClipboardContentMenu();
            binedToolContentModule.registerDragDropContentMenu();
            // TODO binedModule.registerPrintMenu();
            binedModule.registerViewModeMenu();
            binedModule.registerCodeTypeMenu();
            binedModule.registerPositionCodeTypeMenu();
            binedModule.registerHexCharactersCaseHandlerMenu();
            binedModule.registerLayoutMenu();

            final ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
//                UndoHandlerWrapper undoHandlerWrapper = new UndoHandlerWrapper();

//                undoModule.setUndoHandler(((UndoFileHandler) editorProvider).getUndoHandler());
            if (!demoMode) {
                uiModule.registerOptionsPanels();
            }
            binedModule.registerStatusBar();
            binedModule.registerOptionsPanels();
            binedModule.getBinaryStatusPanel();
            binedInspectorModule.registerOptionsPanels();
            if (!demoMode) {
                updateModule.registerOptionsPanels();
            }
            binedModule.registerUndoHandler();

            binedModule.loadFromPreferences(preferences);

            if (demoMode) {
                frameModule.addExitListener((ApplicationFrameHandler afh) -> {
                    return false;
                });
            } else {
                frameModule.addExitListener((ApplicationFrameHandler afh) -> {
                    frameModule.saveFramePosition();
                    return true;
                });
            }

            JComponent editorComponent = editorModule.getEditorComponent();
            frameHandler.setMainPanel(editorComponent);
            binedBookmarksModule.registerBookmarksComponentActions(editorComponent);
            binedMacroModule.registerMacrosComponentActions(editorComponent);
            if (!demoMode) {
                addonManagerModule.registerAddonManagerMenuItem();
            }

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
                binedModule.start();
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
}
