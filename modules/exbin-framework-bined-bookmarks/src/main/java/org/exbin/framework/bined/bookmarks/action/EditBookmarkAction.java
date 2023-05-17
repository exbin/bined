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
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.bookmarks.BinedBookmarksModule;
import org.exbin.framework.bined.bookmarks.gui.BookmarkEditorPanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.DefaultControlPanel;

/**
 * Edit bookmark record action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditBookmarkAction extends AbstractAction {

    public static final String ACTION_ID = "editBookmarkAction";

    private XBApplication application;
    private ResourceBundle resourceBundle;

    public EditBookmarkAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BinedBookmarksModule bookmarksModule = application.getModuleRepository().getModuleByInterface(BinedBookmarksModule.class);
        final BookmarkEditorPanel bookmarksPanel = new BookmarkEditorPanel();
        ResourceBundle panelResourceBundle = bookmarksPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, bookmarksPanel, controlPanel);
        frameModule.setDialogTitle(dialog, panelResourceBundle);
        controlPanel.setHandler((actionType) -> {
            switch (actionType) {
                case OK: {
//                    List<BookmarkRecord> bookmarkRecords = bookmarksPanel.getBookmarkRecords();
//                    bookmarksModule.setBookmarkRecords(bookmarkRecords);
                    break;
                }
                case CANCEL: {
                    dialog.close();
                    break;
                }
            }
        });

        dialog.showCentered(frameModule.getFrame());
    }
}
