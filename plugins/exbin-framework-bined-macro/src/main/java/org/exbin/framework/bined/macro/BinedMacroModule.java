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
package org.exbin.framework.bined.macro;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JMenu;
import org.exbin.bined.operation.undo.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.PluginModule;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.macro.operation.CodeAreaMacroCommandHandler;
import org.exbin.framework.bined.macro.operation.MacroStep;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;

/**
 * Binary editor macro support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedMacroModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedMacroModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    private MacroManager macroManager;

    public BinedMacroModule() {
    }

    @Override
    public void register() {
        UiModuleApi uiModule = App.getModule(UiModuleApi.class);
        uiModule.addPostInitAction(() -> {
            registerMacrosMenuActions();
            registerMacrosPopupMenuActions();

            EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
            editorModule.addEditorProviderChangeListener((editorProvider) -> {
                setEditorProvider(editorProvider);
            });
        });
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
        BinedModule binEdModule = App.getModule(BinedModule.class);
        binEdModule.registerCodeAreaCommandHandlerProvider((codeArea, undoRedo) -> new CodeAreaMacroCommandHandler(codeArea, (BinaryDataUndoRedo) undoRedo));

        BinedSearchModule binedSearchModule = App.getModule(BinedSearchModule.class);
        binedSearchModule.getFindReplaceActions().addFindAgainListener(() -> {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (activeFile.isPresent()) {
                BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                SectCodeArea codeArea = fileHandler.getCodeArea();
                CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                if (commandHandler instanceof CodeAreaMacroCommandHandler && ((CodeAreaMacroCommandHandler) commandHandler).isMacroRecording()) {
                    ((CodeAreaMacroCommandHandler) commandHandler).appendMacroOperationStep(MacroStep.FIND_AGAIN);
                }
            }
        });
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedMacroModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    public void registerMacrosMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> getMacrosMenu());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerMacrosPopupMenuActions() {
        getMacroManager().registerMacrosPopupMenuActions();
    }

    @Nonnull
    public JMenu getMacrosMenu() {
        return getMacroManager().getMacrosMenu();
    }

    @Nonnull
    public MacroManager getMacroManager() {
        if (macroManager == null) {
            ensureSetup();

            macroManager = new MacroManager();
            macroManager.init();
        }
        return macroManager;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
