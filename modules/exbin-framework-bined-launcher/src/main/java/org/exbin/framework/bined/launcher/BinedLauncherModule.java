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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.framework.App;
import org.exbin.framework.LauncherModule;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.manager.ActionManagerModule;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.update.api.AddonUpdateModuleApi;
import org.exbin.framework.bined.BinaryFileDocument;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.FileProcessingMode;
import org.exbin.framework.bined.editor.BinedEditorModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.document.settings.StartupOptions;
import org.exbin.framework.document.settings.StartupOptions.StartupBehavior;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.viewer.settings.BinaryAppearanceOptions;
import org.exbin.framework.bined.editor.settings.BinaryFileProcessingOptions;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.bined.theme.BinedThemeModule;
import org.exbin.framework.bined.viewer.BinedViewerModule;
import org.exbin.framework.docking.api.DockingModuleApi;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.docking.multi.api.DockingMultiModuleApi;
import org.exbin.framework.document.api.DocumentManagement;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.document.recent.DocumentRecentModule;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.help.online.api.HelpOnlineModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.theme.api.UiThemeModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.sidebar.api.SideBarModuleApi;
import org.exbin.framework.frame.api.ComponentFrame;

/**
 * Binary editor launcher module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedLauncherModule implements LauncherModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedLauncherModule.class);
    private static final int BUFFER_SIZE = 1024;

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
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsStorage optionsStorage = optionsModule.getAppOptions();
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
                optionsStorage.put(BinaryFileProcessingOptions.KEY_FILE_PROCESSING_MODE, FileProcessingMode.MEMORY.name());
            }

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            languageModule.setAppBundle(bundle);

            final UiModuleApi uiModule = App.getModule(UiModuleApi.class);
            final UiThemeModuleApi themeModule = App.getModule(UiThemeModuleApi.class);
            themeModule.registerThemeInit();

            BinedModule binedModule = App.getModule(BinedModule.class);
            binedModule.registerDocument();

            uiModule.initSwingUi();

            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            if (demoMode) {
                frameModule.switchFrameToUndecorated();
            }

            DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
            DocumentRecentModule documentRecentModule = App.getModule(DocumentRecentModule.class);
            DockingModuleApi dockingModule = App.getModule(DockingModuleApi.class);
            DockingMultiModuleApi dockingMultiModule = App.getModule(DockingMultiModuleApi.class);
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            ActionManagerModule actionManagerModule = App.getModule(ActionManagerModule.class);
            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
            MenuPopupModuleApi menuPopupModule = App.getModule(MenuPopupModuleApi.class);
            ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
            SideBarModuleApi sideBarModule = App.getModule(SideBarModuleApi.class);
            AboutModuleApi aboutModule = App.getModule(AboutModuleApi.class);
            HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
            HelpOnlineModuleApi helpOnlineModule = App.getModule(HelpOnlineModuleApi.class);
            OperationUndoModuleApi undoModule = App.getModule(OperationUndoModuleApi.class);
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
            AddonUpdateModuleApi updateModule = App.getModule(AddonUpdateModuleApi.class);

            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            BinedEditorModule binedEditorModule = App.getModule(BinedEditorModule.class);
            BinedThemeModule binedThemeModule = App.getModule(BinedThemeModule.class);
            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);

            uiModule.registerSettings();
            frameModule.registerSettings();
            documentModule.registerSettings();
            themeModule.registerSettings();
            actionManagerModule.registerSettings();
            fileModule.registerSettings();
            binedViewerModule.registerSettings();
            binedEditorModule.registerSettings();
            binedThemeModule.registerSettings();
            binedInspectorModule.registerSettings();
            if (!demoMode) {
                updateModule.registerSettings();
            }

            fileModule.registerFileProviders();
            BinaryAppearanceOptions binaryAppearanceParameters = new BinaryAppearanceOptions(optionsStorage);
            boolean multiFileMode = binaryAppearanceParameters.isMultiFileMode();
            BasicDockingType dockingType = editorProvideType != null
                    ? (OPTION_SINGLE_FILE.equals(editorProvideType) ? BasicDockingType.SINGLE : BasicDockingType.MULTI)
                    : (multiFileMode ? BasicDockingType.MULTI : BasicDockingType.SINGLE);
            // binedModule.initEditorProvider(dockingType);

            BinedSearchModule binedSearchModule = App.getModule(BinedSearchModule.class);
            binedSearchModule.registerSearchComponent();

            BinedOperationModule binedOperationModule = App.getModule(BinedOperationModule.class);
            binedOperationModule.addBasicMethods();

            AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
            addonManagerModule.setDevMode(devMode);
            addonManagerModule.setAddonServiceCoreUrl("https://bined.exbin.org/");
            addonManagerModule.setManualLegacyGitHubUrl("https://github.com/exbin/bined/releases/tag/");

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

            dockingModule.registerMenuFileHandlingActions();
            if (dockingType == BasicDockingType.MULTI) {
                dockingMultiModule.registerMenuFileCloseActions();
            }

            dockingModule.registerToolBarFileHandlingActions();
            documentRecentModule.registerRecenFilesMenuActions();

            undoModule.registerMainMenu();
            undoModule.registerMainToolBar();

            // Register clipboard editing actions
            menuPopupModule.registerDefaultClipboardPopupMenuWithIcons();
            menuModule.registerMenuClipboardActions();
            toolBarModule.registerToolBarClipboardActions();

            optionsSettingsModule.registerMenuAction();

            binedViewerModule.registerCodeTypeToolBarActions();
            binedModule.registerShowNonprintablesToolBarActions();
//                binedModule.registerEditFindToolBarActions();
            binedModule.registerViewNonprintablesMenuActions();
            binedModule.registerViewZoomMenuActions();
            binedInspectorModule.setComponentsProvider(null);
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

            final ComponentFrame frameHandler = frameModule.getFrameHandler();
//                UndoHandlerWrapper undoHandlerWrapper = new UndoHandlerWrapper();

//                undoModule.setUndoHandler(((UndoFileHandler) editorProvider).getUndoHandler());
            binedViewerModule.registerStatusBar();

            if (demoMode) {
                frameModule.addClosingListener(() -> {
                    return false;
                });
            } else {
                frameModule.addClosingListener(() -> {
                    // Save frame position
                    frameModule.saveFramePosition();

                    // Save session files if in multi-file mode
                    if (dockingType == BasicDockingType.MULTI) {
                        // TODO
                        /* BinaryMultiEditorProvider multiProvider = (BinaryMultiEditorProvider) currentProvider;
                        List<URI> openFiles = multiProvider.getOpenFileUris();
                        StartupOptions startupOptions = new StartupOptions(optionsStorage);
                        startupOptions.setLastSessionFiles(openFiles); */
                    }

                    return true;
                });
            }

            DocumentDocking documentDocking = dockingType == BasicDockingType.SINGLE ? dockingModule.createDefaultDocking() : dockingMultiModule.createDefaultDocking();
            frameModule.attachFrameContentComponent(documentDocking);
            sideBarModule.registerDockingSideBar(documentDocking);
            dockingModule.registerDocumentReceiver(documentDocking);
            addonManagerModule.registerAddonManagerMenuItem();

            frameHandler.setDefaultSize(new Dimension(600, 400));
            frameModule.loadFramePosition();
            optionsSettingsModule.initialLoadFromPreferences();
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
                StartupOptions startupOptions = new StartupOptions(optionsStorage);
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
                        if (dockingType == BasicDockingType.MULTI) {
                            List<URI> sessionFiles = startupOptions.getLastSessionFiles();
                            if (sessionFiles.isEmpty()) {
                                // No session files, fallback to new file
                                binedModule.start();
                            } else {
                                binedModule.start();
                                // Load session files
                                for (URI fileUri : sessionFiles) {
                                    try {
                                        fileModule.openFile(fileUri);
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
            
            loadSampleFile();

            if (!demoMode) {
                updateModule.checkOnStart(frameHandler.getFrame());
            }
        } catch (ParseException | RuntimeException ex) {
            Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
//                System.exit(1);
        }
    }

    public void loadSampleFile() {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement mainDocumentManager = documentModule.getMainDocumentManager();
        BinaryFileDocument document = (BinaryFileDocument) mainDocumentManager.createDefaultDocument();
        EditableBinaryData binaryData = (EditableBinaryData) document.getBinaryData();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getClass().getResourceAsStream("/org/exbin/framework/bined/launcher/resources/images/icon.png");
            outputStream = binaryData.getDataOutputStream();
            BinedLauncherModule.copyInputStreamToOutputStream(inputStream, outputStream);
        } catch (IOException ex) {
            Logger.getLogger(BinedLauncherModule.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                // ignore
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                // ignore
            }
        }
        mainDocumentManager.receiveDocument(document);
    }

    /**
     * Copies all data from input stream to output stream using 1k buffer.
     *
     * @param source input stream
     * @param target output stream
     * @throws IOException if read or write fails
     */
    public static void copyInputStreamToOutputStream(InputStream source, OutputStream target) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bufferUsed = 0;

        int bytesRead;
        do {
            bytesRead = source.read(buffer, bufferUsed, BUFFER_SIZE - bufferUsed);
            if (bytesRead > 0) {
                bufferUsed += bytesRead;
                if (bufferUsed == BUFFER_SIZE) {
                    target.write(buffer, 0, BUFFER_SIZE);
                    bufferUsed = 0;
                }
            }
        } while (bytesRead > 0);

        if (bufferUsed > 0) {
            target.write(buffer, 0, bufferUsed);
        }
    }

    public enum BasicDockingType {
        SINGLE, MULTI;
    }
}
