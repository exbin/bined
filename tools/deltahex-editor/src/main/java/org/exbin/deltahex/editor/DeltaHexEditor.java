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
package org.exbin.deltahex.editor;

import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.exbin.framework.XBBaseApplication;
import org.exbin.framework.api.XBApplicationModuleRepository;
import org.exbin.framework.deltahex.DeltaHexModule;
import org.exbin.framework.deltahex.HexEditorProvider;
import org.exbin.framework.deltahex.UndoHandlerWrapper;
import org.exbin.framework.deltahex.panel.HexAppearanceOptionsPanel;
import org.exbin.framework.gui.about.api.GuiAboutModuleApi;
import org.exbin.framework.gui.docking.api.GuiDockingModuleApi;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.ApplicationExitListener;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.framework.gui.update.api.GuiUpdateModuleApi;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * The main class of the Delta Hexadecimal Editor application.
 *
 * @version 0.1.2 2017/01/06
 * @author ExBin Project (http://exbin.org)
 */
public class DeltaHexEditor {

    private static boolean verboseMode = false;
    private static boolean devMode = false;
    private static ResourceBundle bundle;

    /**
     * Main method launching the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        try {
            bundle = LanguageUtils.getResourceBundleByClass(DeltaHexEditor.class);
            // Parameters processing
            Options opt = new Options();
            opt.addOption("h", "help", false, bundle.getString("cl_option_help"));
            opt.addOption("v", false, bundle.getString("cl_option_verbose"));
            opt.addOption("dev", false, bundle.getString("cl_option_dev"));
            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(opt, args);
            if (cl.hasOption('h')) {
                HelpFormatter f = new HelpFormatter();
                f.printHelp(bundle.getString("cl_syntax"), opt);
            } else {
                verboseMode = cl.hasOption("v");
                devMode = cl.hasOption("dev");

                XBBaseApplication app = new XBBaseApplication();
                Preferences preferences = app.createPreferences(DeltaHexEditor.class);
                app.setAppBundle(bundle, LanguageUtils.getResourceBaseNameBundleByClass(DeltaHexEditor.class));
                boolean multiTabMode = preferences.getBoolean(HexAppearanceOptionsPanel.PREFERENCES_MULTITAB_MODE, false);

                XBApplicationModuleRepository moduleRepository = app.getModuleRepository();
                moduleRepository.addClassPathModules();
                moduleRepository.addModulesFromManifest(DeltaHexEditor.class);
                moduleRepository.loadModulesFromPath(new File("plugins").toURI());
                moduleRepository.initModules();
                app.init();

                final GuiFrameModuleApi frameModule = moduleRepository.getModuleByInterface(GuiFrameModuleApi.class);
                GuiEditorModuleApi editorModule = moduleRepository.getModuleByInterface(GuiEditorModuleApi.class);
                GuiMenuModuleApi menuModule = moduleRepository.getModuleByInterface(GuiMenuModuleApi.class);
                GuiAboutModuleApi aboutModule = moduleRepository.getModuleByInterface(GuiAboutModuleApi.class);
                GuiUndoModuleApi undoModule = moduleRepository.getModuleByInterface(GuiUndoModuleApi.class);
                GuiFileModuleApi fileModule = moduleRepository.getModuleByInterface(GuiFileModuleApi.class);
                GuiOptionsModuleApi optionsModule = moduleRepository.getModuleByInterface(GuiOptionsModuleApi.class);
                GuiDockingModuleApi dockingModule = moduleRepository.getModuleByInterface(GuiDockingModuleApi.class);
                GuiUpdateModuleApi updateModule = moduleRepository.getModuleByInterface(GuiUpdateModuleApi.class);

                DeltaHexModule deltaHexModule = moduleRepository.getModuleByInterface(DeltaHexModule.class);

                frameModule.createMainMenu();
                try {
                    updateModule.setUpdateUrl(new URL(bundle.getString("update_url")));
                    updateModule.setUpdateDownloadUrl(new URL(bundle.getString("update_download_url")));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(DeltaHexEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
                updateModule.registerDefaultMenuItem();
                aboutModule.registerDefaultMenuItem();

                frameModule.registerExitAction();
                frameModule.registerBarsVisibilityActions();

                // Register clipboard editing actions
                fileModule.registerMenuFileHandlingActions();
                fileModule.registerToolBarFileHandlingActions();
                fileModule.registerLastOpenedMenuActions();
                fileModule.registerCloseListener();

                undoModule.registerMainMenu();
                undoModule.registerMainToolBar();
                undoModule.registerUndoManagerInMainMenu();

                // Register clipboard editing actions
                menuModule.registerMenuClipboardActions();
                menuModule.registerToolBarClipboardActions();

                optionsModule.registerMenuAction();

                HexEditorProvider editorProvider;
                if (multiTabMode) {
                    editorProvider = deltaHexModule.getMultiEditorProvider();
                } else {
                    editorProvider = deltaHexModule.getEditorProvider();
                }

                deltaHexModule.registerEditFindMenuActions();
                deltaHexModule.registerEditFindToolBarActions();
                deltaHexModule.registerViewNonprintablesMenuActions();
                deltaHexModule.registerToolsOptionsMenuActions();
                deltaHexModule.registerClipboardCodeActions();
                deltaHexModule.registerOptionsMenuPanels();
                deltaHexModule.registerGoToLine();
                deltaHexModule.registerPropertiesMenu();
                // TODO deltaHexModule.registerPrintMenu();
                deltaHexModule.registerViewModeMenu();
                deltaHexModule.registerCodeTypeMenu();
                deltaHexModule.registerPositionCodeTypeMenu();
                deltaHexModule.registerHexCharactersCaseHandlerMenu();
                deltaHexModule.registerWordWrapping();

                final ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
                if (multiTabMode) {
                    editorModule.registerMultiEditor("hex", (MultiEditorProvider) editorProvider);
                } else {
                    editorModule.registerEditor("hex", editorProvider);
                }

                editorModule.registerUndoHandler();
                undoModule.setUndoHandler(new UndoHandlerWrapper(editorProvider.getHexUndoHandler()));

                deltaHexModule.registerStatusBar();
                deltaHexModule.registerOptionsPanels();
                deltaHexModule.getTextStatusPanel();
                updateModule.registerOptionsPanels();

                deltaHexModule.loadFromPreferences(preferences);

                frameModule.addExitListener(new ApplicationExitListener() {
                    @Override
                    public boolean processExit(ApplicationFrameHandler afh) {
                        frameModule.saveFramePosition();
                        return true;
                    }
                });

                if (multiTabMode) {
                    frameHandler.setMainPanel(dockingModule.getDockingPanel());
                } else {
                    frameHandler.setMainPanel(editorModule.getEditorPanel());
                }

                frameHandler.setDefaultSize(new Dimension(600, 400));
                frameModule.loadFramePosition();
                frameHandler.show();
                updateModule.checkOnStart(frameHandler.getFrame());

                List fileArgs = cl.getArgList();
                if (fileArgs.size() > 0) {
                    fileModule.loadFromFile((String) fileArgs.get(0));
                }
            }
        } catch (ParseException | RuntimeException ex) {
            Logger.getLogger(DeltaHexEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
