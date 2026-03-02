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

import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.exbin.framework.App;
import org.exbin.framework.PluginModule;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.bined.BinEdComponentExtension;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.bookmarks.settings.BookmarkOptions;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;

/**
 * Binary data editor bookmarks module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedBookmarksModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedBookmarksModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private BookmarksManager bookmarksManager;

    public BinedBookmarksModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedBookmarksModule.class);
        }

        return resourceBundle;
    }

    @Override
    public void register() {
        UiModuleApi uiModule = App.getModule(UiModuleApi.class);
        uiModule.addPostInitAction(() -> {
            registerBookmarksMenuActions();
            registerBookmarksPopupMenuActions();
            
            registerSettings();

            BinedModule binedModule = App.getModule(BinedModule.class);
            BinEdFileManager fileManager = binedModule.getFileManager();
            // TODO Rework to use different approach than extension
            fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
                @Nonnull
                @Override
                public Optional<BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                    getBookmarksManager();
                    bookmarksManager.registerBookmarksComponentActions(component);
                    return Optional.empty();
                }
            });
        });
    }

    public void registerBookmarksMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> getBookmarksMenu());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerBookmarksPopupMenuActions() {
        getBookmarksManager().registerBookmarksPopupMenuActions();
    }

    @Nonnull
    public AbstractAction getManageBookmarksAction() {
        return getBookmarksManager().getManageBookmarksAction();
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        settingsManagement.registerSettingsOptions(BookmarkOptions.class, (optionsStorage) -> new BookmarkOptions(optionsStorage));
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
            bookmarksManager.init();
        }
        return bookmarksManager;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
