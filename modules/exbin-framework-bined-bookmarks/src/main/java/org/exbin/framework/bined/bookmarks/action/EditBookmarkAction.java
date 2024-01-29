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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.bined.CodeAreaSelection;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.bookmarks.gui.BookmarkEditorPanel;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Edit bookmark record action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditBookmarkAction extends AbstractAction {

    public static final String ACTION_ID = "editBookmarkAction";

    private ResourceBundle resourceBundle;
    private BookmarkRecord bookmarkRecord;
    private CodeAreaSelection currentSelection;

    public EditBookmarkAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    public void setCurrentSelection(CodeAreaSelection currentSelection) {
        this.currentSelection = currentSelection;
    }

    @Nonnull
    public Optional<BookmarkRecord> getBookmarkRecord() {
        return Optional.ofNullable(bookmarkRecord);
    }

    public void setBookmarkRecord(@Nullable BookmarkRecord bookmarkRecord) {
        this.bookmarkRecord = bookmarkRecord;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final BookmarkEditorPanel bookmarkEditorPanel = new BookmarkEditorPanel();
        bookmarkEditorPanel.setBookmarkRecord(bookmarkRecord);
        bookmarkEditorPanel.setCurrentSelection(currentSelection);
        ResourceBundle panelResourceBundle = bookmarkEditorPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(windowModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, bookmarkEditorPanel, controlPanel);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        controlPanel.setHandler((actionType) -> {
            switch (actionType) {
                case OK: {
                    bookmarkRecord.setRecord(bookmarkEditorPanel.getBookmarkRecord());
                    break;
                }
                case CANCEL: {
                    bookmarkRecord = null;
                    break;
                }
            }
            dialog.close();
        });

        dialog.showCentered(windowModule.getFrame());
    }
}
