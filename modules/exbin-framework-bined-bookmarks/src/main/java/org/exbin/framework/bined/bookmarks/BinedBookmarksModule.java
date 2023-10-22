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
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Binary data editor bookmarks module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedBookmarksModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(BinedBookmarksModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private EditorProvider editorProvider;

    private BookmarksManager bookmarksManager;

    public BinedBookmarksModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    public void initEditorProvider(EditorProviderVariant variant) {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public void registerBookmarksMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, getBookmarksMenu(), new MenuPosition(BinedSearchModule.EDIT_FIND_MENU_GROUP_ID));
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
            resourceBundle = LanguageUtils.getResourceBundleByClass(BinedBookmarksModule.class);
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
            bookmarksManager.setApplication(this.application);
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
