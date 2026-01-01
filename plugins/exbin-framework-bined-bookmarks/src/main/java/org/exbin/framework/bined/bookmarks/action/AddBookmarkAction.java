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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.bookmarks.gui.BookmarkEditorPanel;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Add bookmark record action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddBookmarkAction extends AbstractAction {

    public static final String ACTION_ID = "addBookmarkAction";

    private BookmarkRecord bookmarkRecord = null;
    private DialogParentComponent dialogParentComponent;
    private CodeAreaCore codeArea;

    public AddBookmarkAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    setEnabled(codeArea != null && dialogParentComponent != null);
                });
                registrar.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                    dialogParentComponent = instance;
                    setEnabled(codeArea != null && dialogParentComponent != null);
                });
            }
        });
    }

    @Nonnull
    public Optional<BookmarkRecord> getBookmarkRecord() {
        return Optional.ofNullable(bookmarkRecord);
    }

    public void setCodeArea(CodeAreaCore codeArea) {
        this.codeArea = codeArea;
    }

    public void setDialogParentComponent(DialogParentComponent dialogParentComponent) {
        this.dialogParentComponent = dialogParentComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final BookmarkEditorPanel bookmarkEditorPanel = new BookmarkEditorPanel();
        bookmarkEditorPanel.setBookmarkRecord(new BookmarkRecord());
        bookmarkEditorPanel.setCurrentSelection(((SelectionCapable) codeArea).getSelectionHandler());
        ResourceBundle panelResourceBundle = bookmarkEditorPanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(dialogParentComponent.getComponent(), Dialog.ModalityType.APPLICATION_MODAL, bookmarkEditorPanel, controlPanel);
        windowModule.setWindowTitle(dialog, panelResourceBundle);
        controlPanel.setController((actionType) -> {
            switch (actionType) {
                case OK: {
                    bookmarkRecord = bookmarkEditorPanel.getBookmarkRecord();
                    break;
                }
                case CANCEL: {
                    bookmarkRecord = null;
                    break;
                }
            }
            dialog.close();
        });

        dialog.showCentered(codeArea);
    }
}
