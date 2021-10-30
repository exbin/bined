/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.exbin.framework.bined.preferences.BinaryAppearancePreferences;
import org.exbin.framework.gui.about.api.GuiAboutModuleApi;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.link.api.GuiLinkModuleApi;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.framework.gui.update.api.GuiUpdateModuleApi;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.EditorProviderVariant;
import org.exbin.framework.gui.undo.api.UndoFileHandler;

/**
 * The main class of the BinEd Hexadecimal Editor application.
 *
 * @version 0.2.1 2021/10/29
 * @author ExBin Project (http://exbin.org)
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

                XBBaseApplication app = new XBBaseApplication();
                Preferences preferences = app.createPreferences(BinedEditor.class);
                app.setAppBundle(bundle, LanguageUtils.getResourceBaseNameBundleByClass(BinedEditor.class));
                BinaryAppearancePreferences binaryAppearanceParameters = new BinaryAppearancePreferences(preferences);

                XBApplicationModuleRepository moduleRepository = app.getModuleRepository();
                moduleRepository.addClassPathModules();
                moduleRepository.addModulesFromManifest(BinedEditor.class);
                moduleRepository.loadModulesFromPath(new File("plugins").toURI());
                moduleRepository.initModules();
                app.init();

                final GuiFrameModuleApi frameModule = moduleRepository.getModuleByInterface(GuiFrameModuleApi.class);
                GuiEditorModuleApi editorModule = moduleRepository.getModuleByInterface(GuiEditorModuleApi.class);
                GuiActionModuleApi actionModule = moduleRepository.getModuleByInterface(GuiActionModuleApi.class);
                GuiAboutModuleApi aboutModule = moduleRepository.getModuleByInterface(GuiAboutModuleApi.class);
                GuiLinkModuleApi linkModule = moduleRepository.getModuleByInterface(GuiLinkModuleApi.class);
                GuiUndoModuleApi undoModule = moduleRepository.getModuleByInterface(GuiUndoModuleApi.class);
                GuiFileModuleApi fileModule = moduleRepository.getModuleByInterface(GuiFileModuleApi.class);
                GuiOptionsModuleApi optionsModule = moduleRepository.getModuleByInterface(GuiOptionsModuleApi.class);
                GuiUpdateModuleApi updateModule = moduleRepository.getModuleByInterface(GuiUpdateModuleApi.class);

                BinedModule binedModule = moduleRepository.getModuleByInterface(BinedModule.class);
                boolean multiFileMode = binaryAppearanceParameters.isMultiFileMode();
                EditorProviderVariant editorProviderVariant = editorProvideType != null
                        ? (OPTION_SINGLE_FILE.equals(editorProvideType) ? EditorProviderVariant.SINGLE : EditorProviderVariant.MULTI)
                        : (multiFileMode ? EditorProviderVariant.MULTI : EditorProviderVariant.SINGLE);
                binedModule.initEditorProvider(editorProviderVariant);
                EditorProvider editorProvider = binedModule.getEditorProvider();
                editorModule.registerEditor(BINARY_PLUGIN_ID, editorProvider);

                frameModule.createMainMenu();
                try {
                    updateModule.setUpdateUrl(new URL(bundle.getString("update_url")));
                    updateModule.setUpdateDownloadUrl(new URL(bundle.getString("update_download_url")));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(BinedEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateModule.registerDefaultMenuItem();
                aboutModule.registerDefaultMenuItem();
                try {
                    linkModule.setOnlineHelpUrl(new URL(bundle.getString("online_help_url")));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(BinedEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                linkModule.registerOnlineHelpMenu();

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

                binedModule.registerEditFindMenuActions();
                binedModule.registerCodeTypeToolBarActions();
                binedModule.registerShowUnprintablesToolBarActions();
//                binedModule.registerEditFindToolBarActions();
                binedModule.registerViewUnprintablesMenuActions();
                binedModule.registerViewValuesPanelMenuActions();
                binedModule.registerToolsOptionsMenuActions();
                binedModule.registerClipboardCodeActions();
                binedModule.registerOptionsMenuPanels();
                binedModule.registerGoToPosition();
                binedModule.registerInsertDataAction();
                binedModule.registerPropertiesMenu();
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
                if (fileArgs.size() > 0) {
                    filePath = (String) fileArgs.get(0);
                }

                if (filePath == null) {
                    binedModule.start();
                } else {
                    binedModule.startWithFile(filePath);
                }

                updateModule.checkOnStart(frameHandler.getFrame());
            }
        } catch (ParseException | RuntimeException ex) {
            Logger.getLogger(BinedEditor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
}
