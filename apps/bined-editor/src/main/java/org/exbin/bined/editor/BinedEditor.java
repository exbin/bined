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
package org.exbin.bined.editor;

import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.exbin.framework.XBBaseApplication;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplicationModuleRepository;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.blockedit.BinedBlockEditModule;
import org.exbin.framework.bined.bookmarks.BinedBookmarksModule;
import org.exbin.framework.bined.compare.BinedCompareModule;
import org.exbin.framework.bined.clipboard.BinedClipboardModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.bined.objectdata.BinedObjectDataModule;
import org.exbin.framework.bined.preferences.BinaryAppearancePreferences;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.help.online.api.HelpOnlineModuleApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.update.api.UpdateModuleApi;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.operation.undo.api.UndoFileHandler;

/**
 * The main class of the BinEd Hexadecimal Editor application.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedEditor {

    private static final String BINARY_PLUGIN_ID = "binary";

    private static final String OPTION_HELP = "h";
    private static final String OPTION_VERBOSE = "v";
    private static final String OPTION_DEV = "dev";
    private static final String OPTION_SINGLE_FILE = "single_file";
    private static final String OPTION_MULTI_FILE = "multi_file";

    private static final ResourceBundle bundle = LanguageUtils.getResourceBundleByClass(BinedEditor.class);

    /**
     * Main method launching the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        try {
            // Parameters processing
            Options opt = new Options();
            opt.addOption(OPTION_HELP, "help", false, bundle.getString("cl_option_help"));
            opt.addOption(OPTION_VERBOSE, false, bundle.getString("cl_option_verbose"));
            opt.addOption(OPTION_DEV, false, bundle.getString("cl_option_dev"));
            OptionGroup editorProviderType = new OptionGroup();
            editorProviderType.addOption(new Option(OPTION_SINGLE_FILE, bundle.getString("cl_option_single_file")));
            editorProviderType.addOption(new Option(OPTION_MULTI_FILE, bundle.getString("cl_option_multi_file")));
            opt.addOptionGroup(editorProviderType);
            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(opt, args);
            if (cl.hasOption(OPTION_HELP)) {
                HelpFormatter f = new HelpFormatter();
                f.printHelp(bundle.getString("cl_syntax"), opt);
            } else {
                boolean verboseMode = cl.hasOption(OPTION_VERBOSE);
                boolean devMode = cl.hasOption(OPTION_DEV);
                String editorProvideType = editorProviderType.getSelected();

                final XBBaseApplication app = new XBBaseApplication();
                Preferences preferences = app.createPreferences(BinedEditor.class);
                app.setAppBundle(bundle, LanguageUtils.getResourceBaseNameBundleByClass(BinedEditor.class));

                XBApplicationModuleRepository moduleRepository = app.getModuleRepository();
                moduleRepository.addClassPathModules();
                moduleRepository.addModulesFromManifest(BinedEditor.class);
                moduleRepository.loadModulesFromPath(new File("plugins").toURI());
                moduleRepository.initModules();
                Thread.currentThread().setContextClassLoader(moduleRepository.getContextClassLoader());
                app.init();

                app.run(() -> {
                    final FrameModuleApi frameModule = moduleRepository.getModuleByInterface(FrameModuleApi.class);
                    EditorModuleApi editorModule = moduleRepository.getModuleByInterface(EditorModuleApi.class);
                    ActionModuleApi actionModule = moduleRepository.getModuleByInterface(ActionModuleApi.class);
                    AboutModuleApi aboutModule = moduleRepository.getModuleByInterface(AboutModuleApi.class);
                    HelpModuleApi helpModule = moduleRepository.getModuleByInterface(HelpModuleApi.class);
                    HelpOnlineModuleApi helpOnlineModule = moduleRepository.getModuleByInterface(HelpOnlineModuleApi.class);
                    OperationUndoModuleApi undoModule = moduleRepository.getModuleByInterface(OperationUndoModuleApi.class);
                    FileModuleApi fileModule = moduleRepository.getModuleByInterface(FileModuleApi.class);
                    OptionsModuleApi optionsModule = moduleRepository.getModuleByInterface(OptionsModuleApi.class);
                    UpdateModuleApi updateModule = moduleRepository.getModuleByInterface(UpdateModuleApi.class);

                    BinedModule binedModule = moduleRepository.getModuleByInterface(BinedModule.class);
                    BinaryAppearancePreferences binaryAppearanceParameters = new BinaryAppearancePreferences(preferences);
                    boolean multiFileMode = binaryAppearanceParameters.isMultiFileMode();
                    EditorProviderVariant editorProviderVariant = editorProvideType != null
                            ? (OPTION_SINGLE_FILE.equals(editorProvideType) ? EditorProviderVariant.SINGLE : EditorProviderVariant.MULTI)
                            : (multiFileMode ? EditorProviderVariant.MULTI : EditorProviderVariant.SINGLE);
                    binedModule.initEditorProvider(editorProviderVariant);
                    EditorProvider editorProvider = binedModule.getEditorProvider();
                    editorModule.registerEditor(BINARY_PLUGIN_ID, editorProvider);

                    BinedSearchModule binedSearchModule = moduleRepository.getModuleByInterface(BinedSearchModule.class);
                    binedSearchModule.setEditorProvider(editorProvider);

                    BinedBlockEditModule binedBlockEditModule = moduleRepository.getModuleByInterface(BinedBlockEditModule.class);
                    binedBlockEditModule.setEditorProvider(editorProvider);

                    BinedCompareModule binedCompareModule = moduleRepository.getModuleByInterface(BinedCompareModule.class);
                    binedCompareModule.setEditorProvider(editorProvider);
                    
                    BinedBookmarksModule binedBookmarksModule = moduleRepository.getModuleByInterface(BinedBookmarksModule.class);
                    binedBookmarksModule.setEditorProvider(editorProvider);

                    BinedInspectorModule binedInspectorModule = moduleRepository.getModuleByInterface(BinedInspectorModule.class);
                    binedInspectorModule.setEditorProvider(editorProvider);

                    BinedObjectDataModule binedObjectDataModule = moduleRepository.getModuleByInterface(BinedObjectDataModule.class);

                    BinedClipboardModule binedClipboardModule = moduleRepository.getModuleByInterface(BinedClipboardModule.class);
                    binedClipboardModule.setEditorProvider(editorProvider);

                    frameModule.createMainMenu();
                    try {
                        updateModule.setUpdateUrl(new URL(bundle.getString("update_url")));
                        updateModule.setUpdateDownloadUrl(new URL(bundle.getString("update_download_url")));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(BinedEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    updateModule.registerDefaultMenuItem();
                    helpModule.registerMainMenu();
                    aboutModule.registerDefaultMenuItem();
                    try {
                        helpOnlineModule.setOnlineHelpUrl(new URL(bundle.getString("online_help_url")));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(BinedEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    helpOnlineModule.registerOnlineHelpMenu();

                    frameModule.registerExitAction();
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
                    undoModule.registerUndoManagerInMainMenu();

                    // Register clipboard editing actions
                    actionModule.registerClipboardTextActions();
                    actionModule.registerMenuClipboardActions();
                    actionModule.registerToolBarClipboardActions();

                    optionsModule.registerMenuAction();

                    binedSearchModule.registerEditFindMenuActions();
                    binedModule.registerCodeTypeToolBarActions();
                    binedModule.registerShowUnprintablesToolBarActions();
//                binedModule.registerEditFindToolBarActions();
                    binedBookmarksModule.registerBookmarksMenuActions();
                    binedModule.registerViewUnprintablesMenuActions();
                    binedInspectorModule.registerViewValuesPanelMenuActions();
                    binedModule.registerToolsOptionsMenuActions();
                    binedCompareModule.registerToolsOptionsMenuActions();
                    binedModule.registerClipboardCodeActions();
                    binedModule.registerOptionsMenuPanels();
                    binedModule.registerGoToPosition();
                    binedBlockEditModule.registerInsertDataAction();
                    binedModule.registerPropertiesMenu();
                    binedClipboardModule.registerClipboardContentMenu();
                    // TODO binedModule.registerPrintMenu();
                    binedModule.registerViewModeMenu();
                    binedModule.registerCodeTypeMenu();
                    binedModule.registerPositionCodeTypeMenu();
                    binedModule.registerHexCharactersCaseHandlerMenu();
                    binedModule.registerLayoutMenu();

                    final ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
//                UndoHandlerWrapper undoHandlerWrapper = new UndoHandlerWrapper();

                    undoModule.setUndoHandler(((UndoFileHandler) editorProvider).getUndoHandler());
                    editorModule.registerUndoHandler();

                    binedModule.registerStatusBar();
                    binedModule.registerOptionsPanels();
                    binedModule.getBinaryStatusPanel();
                    binedInspectorModule.registerOptionsPanels();
                    updateModule.registerOptionsPanels();

                    binedModule.loadFromPreferences(preferences);

                    frameModule.addExitListener((ApplicationFrameHandler afh) -> {
                        frameModule.saveFramePosition();
                        return true;
                    });

                    frameHandler.setMainPanel(editorModule.getEditorComponent());

                    frameHandler.setDefaultSize(new Dimension(600, 400));
                    frameModule.loadFramePosition();
                    optionsModule.initialLoadFromPreferences();
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

                    updateModule.checkOnStart(frameHandler.getFrame());
                });
            }
        } catch (ParseException | RuntimeException ex) {
            Logger.getLogger(BinedEditor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
}
