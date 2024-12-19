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
package org.exbin.framework.bined.bookmarks;

import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;

/**
 * Binary data editor bookmarks module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedBookmarksModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedBookmarksModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    private BookmarksManager bookmarksManager;

    public BinedBookmarksModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerBookmarksMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuContribution contribution = actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, getBookmarksMenu());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerBookmarksPopupMenuActions() {
        getBookmarksManager().registerBookmarksPopupMenuActions();
    }

    public void registerBookmarksComponentActions(JComponent component) {
        getBookmarksManager().registerBookmarksComponentActions(component);
    }

    @Nonnull
    public AbstractAction getManageBookmarksAction() {
        return getBookmarksManager().getManageBookmarksAction();
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedBookmarksModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    @Nonnull
    public JMenu getBookmarksMenu() {
        return getBookmarksManager().getBookmarksMenu();
    }

    @Nonnull
    public BookmarksManager getBookmarksManager() {
        if (bookmarksManager == null) {
            ensureSetup();

            bookmarksManager = new BookmarksManager();
            bookmarksManager.setEditorProvider(editorProvider);
            bookmarksManager.init();
        }
        return bookmarksManager;
    }

    private void ensureSetup() {
        if (editorProvider == null) {
            getEditorProvider();
        }

        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
