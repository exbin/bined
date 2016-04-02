/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.xbup.tool.xbteditor;

import java.awt.Dimension;
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
import org.exbin.xbup.core.parser.basic.XBHead;
import org.exbin.framework.editor.text.EditorTextModule;
import org.exbin.framework.editor.text.panel.TextPanel;
import org.exbin.framework.gui.about.api.GuiAboutModuleApi;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.undo.api.GuiUndoModuleApi;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.api.XBApplicationModuleRepository;

/**
 * The main class of the XBTEditor application.
 *
 * @version 0.2.0 2016/01/23
 * @author ExBin Project (http://exbin.org)
 */
public class XBTEditor {

    private static Preferences preferences;
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
            preferences = Preferences.userNodeForPackage(XBTEditor.class);
        } catch (SecurityException ex) {
            preferences = null;
        }
        try {
            bundle = ActionUtils.getResourceBundleByClass(XBTEditor.class);
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
                Logger logger = Logger.getLogger("");
                try {
                    logger.setLevel(Level.ALL);
                    logger.addHandler(new XBHead.XBLogHandler(verboseMode));
                } catch (java.security.AccessControlException ex) {
                    // Ignore it in java webstart
                }

                XBBaseApplication app = new XBBaseApplication();
                app.setAppPreferences(preferences);
                app.setAppBundle(bundle, ActionUtils.getResourceBaseNameBundleByClass(XBTEditor.class));
                app.init();

                XBApplicationModuleRepository moduleRepository = app.getModuleRepository();
                moduleRepository.addClassPathModules();
                moduleRepository.addModulesFromManifest(XBTEditor.class);
                moduleRepository.initModules();

                GuiFrameModuleApi frameModule = moduleRepository.getModuleByInterface(GuiFrameModuleApi.class);
                GuiEditorModuleApi editorModule = moduleRepository.getModuleByInterface(GuiEditorModuleApi.class);
                GuiMenuModuleApi menuModule = moduleRepository.getModuleByInterface(GuiMenuModuleApi.class);
                GuiAboutModuleApi aboutModule = moduleRepository.getModuleByInterface(GuiAboutModuleApi.class);
                GuiUndoModuleApi undoModule = moduleRepository.getModuleByInterface(GuiUndoModuleApi.class);
                GuiFileModuleApi fileModule = moduleRepository.getModuleByInterface(GuiFileModuleApi.class);
                GuiOptionsModuleApi optionsModule = moduleRepository.getModuleByInterface(GuiOptionsModuleApi.class);
                EditorTextModule textEditorModule = moduleRepository.getModuleByInterface(EditorTextModule.class);

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

                // Register clipboard editing actions
                menuModule.registerMenuClipboardActions();
                menuModule.registerToolBarClipboardActions();

                optionsModule.registerMenuAction();

                textEditorModule.registerFileTypes();
                textEditorModule.registerEditFindMenuActions();
                textEditorModule.registerEditFindToolBarActions();
                textEditorModule.registerToolsOptionsMenuActions();
                textEditorModule.registerOptionsMenuPanels();
                textEditorModule.registerWordWrapping();
                textEditorModule.registerGoToLine();
                textEditorModule.registerPropertiesMenu();
                textEditorModule.registerPrintMenu();

                ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
                TextPanel textPanel = (TextPanel) textEditorModule.getEditorProvider();
                editorModule.registerEditor("text", textPanel);
                editorModule.registerUndoHandler();
                textEditorModule.registerStatusBar();
                textEditorModule.registerOptionsPanels();

                frameHandler.setMainPanel(editorModule.getEditorPanel());
                frameHandler.setDefaultSize(new Dimension(600, 400));
                frameHandler.show();

                List fileArgs = cl.getArgList();
                if (fileArgs.size() > 0) {
                    fileModule.loadFromFile((String) fileArgs.get(0));
                }
            }
        } catch (ParseException | RuntimeException ex) {
            Logger.getLogger(XBTEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
