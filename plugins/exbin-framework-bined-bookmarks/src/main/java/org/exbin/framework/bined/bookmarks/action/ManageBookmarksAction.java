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
package org.exbin.framework.bined.bookmarks.action;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.bookmarks.BinedBookmarksModule;
import org.exbin.framework.bined.bookmarks.BookmarksManager;
import org.exbin.framework.bined.bookmarks.gui.BookmarksManagerPanel;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Manage bookmarks action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ManageBookmarksAction extends AbstractAction {

    public static final String ACTION_ID = "manageBookmarksAction";
    public static final String HELP_ID = "bookmarks";

    private DialogParentComponent dialogParentComponent;
    private CodeAreaCore codeArea;

    public ManageBookmarksAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    setEnabled(codeArea != null && dialogParentComponent != null);
                });
                manager.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                    dialogParentComponent = instance;
                    setEnabled(codeArea != null && dialogParentComponent != null);
                });
            }
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BinedBookmarksModule bookmarksModule = App.getModule(BinedBookmarksModule.class);
        BookmarksManager bookmarksManager = bookmarksModule.getBookmarksManager();
        final BookmarksManagerPanel bookmarksPanel = bookmarksManager.createBookmarksManagerPanel();
        List<BookmarkRecord> records = new ArrayList<>();
        for (BookmarkRecord record : bookmarksManager.getBookmarkRecords()) {
            records.add(new BookmarkRecord(record));
        }
        bookmarksPanel.setBookmarkRecords(records);
        ResourceBundle panelResourceBundle = bookmarksPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        helpModule.addLinkToControlPanel(controlPanel, new HelpLink(HELP_ID));

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(dialogParentComponent.getComponent(), Dialog.ModalityType.APPLICATION_MODAL, bookmarksPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), bookmarksPanel.getClass(), bookmarksPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        Dimension preferredSize = dialog.getWindow().getPreferredSize();
        dialog.getWindow().setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 450));
        controlPanel.setController((actionType) -> {
            switch (actionType) {
                case OK: {
                    List<BookmarkRecord> bookmarkRecords = bookmarksPanel.getBookmarkRecords();
                    bookmarksManager.setBookmarkRecords(bookmarkRecords);
                    dialog.close();
                    break;
                }
                case CANCEL: {
                    dialog.close();
                    break;
                }
            }
        });

        dialog.showCentered(codeArea);
    }
}
