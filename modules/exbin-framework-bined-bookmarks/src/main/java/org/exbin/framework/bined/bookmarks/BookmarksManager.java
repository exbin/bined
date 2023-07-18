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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.bookmarks.action.AddBookmarkAction;
import org.exbin.framework.bined.bookmarks.action.EditBookmarkAction;
import org.exbin.framework.bined.bookmarks.action.ManageBookmarksAction;
import org.exbin.framework.bined.bookmarks.gui.BookmarksManagerPanel;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;
import org.exbin.framework.bined.bookmarks.preferences.BookmarkPreferences;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Bookmarks manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BookmarksManager {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BookmarksManager.class);

    private final List<BookmarkRecord> bookmarkRecords = new ArrayList<>();
    private BookmarkPreferences bookmarkPreferences;
    private BookmarksPositionColorModifier bookmarksPositionColorModifier;

    private final BookmarksManagerPanel bookmarksManagerPanel;
    private XBApplication application;
    private EditorProvider editorProvider;

    private ManageBookmarksAction manageBookmarksAction = new ManageBookmarksAction();
    private AddBookmarkAction addBookmarkAction = new AddBookmarkAction();
    private EditBookmarkAction editBookmarkAction = new EditBookmarkAction();
    private JMenu bookmarksMenu = new JMenu(resourceBundle.getString("bookmarksMenu.text"));

    public BookmarksManager() {
        bookmarksManagerPanel = new BookmarksManagerPanel();
        bookmarksManagerPanel.setControl(new BookmarksManagerPanel.Control() {
            @Override
            public void addRecord() {
                addBookmarkAction.actionPerformed(null);
                Optional<BookmarkRecord> bookmarkRecord = addBookmarkAction.getBookmarkRecord();
                if (bookmarkRecord.isPresent()) {
                    List<BookmarkRecord> records = bookmarksManagerPanel.getBookmarkRecords();
                    records.add(bookmarkRecord.get());
                    bookmarksManagerPanel.setBookmarkRecords(records);
                }
            }

            @Override
            public void editRecord() {
                BookmarkRecord selectedRecord = bookmarksManagerPanel.getSelectedRecord();
                int selectedRow = bookmarksManagerPanel.getTable().getSelectedRow();
                editBookmarkAction.setBookmarkRecord(new BookmarkRecord(selectedRecord));
                editBookmarkAction.actionPerformed(null);
                Optional<BookmarkRecord> bookmarkRecord = editBookmarkAction.getBookmarkRecord();
                if (bookmarkRecord.isPresent()) {
                    bookmarksManagerPanel.updateRecord(bookmarkRecord.get(), selectedRow);
                }
            }

            @Override
            public void removeRecord() {
                int[] selectedRows = bookmarksManagerPanel.getTable().getSelectedRows();
                Arrays.sort(selectedRows);
                List<BookmarkRecord> records = bookmarksManagerPanel.getBookmarkRecords();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    records.remove(selectedRows[i]);
                }
                bookmarksManagerPanel.setBookmarkRecords(records);
            }

            @Override
            public void selectAll() {
                bookmarksManagerPanel.getTable().selectAll();
            }

            @Override
            public void moveUp() {
                JTable table = bookmarksManagerPanel.getTable();
                int[] selectedRows = table.getSelectedRows();
                Arrays.sort(selectedRows);
                List<BookmarkRecord> records = bookmarksManagerPanel.getBookmarkRecords();
                ListSelectionModel selectionModel = table.getSelectionModel();
                for (int i = 0; i < selectedRows.length; i++) {
                    int index = selectedRows[i];
                    selectionModel.removeSelectionInterval(index, index);
                    BookmarkRecord movedRecord = records.remove(index - 1);
                    records.add(index, movedRecord);
                    table.addRowSelectionInterval(index - 1, index - 1);
                }
                bookmarksManagerPanel.updateBookmarkRecords(records);
            }

            @Override
            public void moveDown() {
                JTable table = bookmarksManagerPanel.getTable();
                int[] selectedRows = table.getSelectedRows();
                Arrays.sort(selectedRows);
                List<BookmarkRecord> records = bookmarksManagerPanel.getBookmarkRecords();
                ListSelectionModel selectionModel = table.getSelectionModel();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int index = selectedRows[i];
                    selectionModel.removeSelectionInterval(index, index);
                    BookmarkRecord movedRecord = records.remove(index);
                    records.add(index + 1, movedRecord);
                    table.addRowSelectionInterval(index + 1, index + 1);
                }
                bookmarksManagerPanel.updateBookmarkRecords(records);
            }
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;

        addBookmarkAction.setup(application, resourceBundle);
        editBookmarkAction.setup(application, resourceBundle);
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        manageBookmarksAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
    }

    public void init() {
        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);

        Preferences preferences = application.getAppPreferences();
        bookmarkPreferences = new BookmarkPreferences(preferences);
        loadBookmarkRecords();
        updateBookmarksMenu();
        bookmarksPositionColorModifier = new BookmarksPositionColorModifier(bookmarkRecords);
        binedModule.addPainterColorModifier(bookmarksPositionColorModifier);
    }

    private void loadBookmarkRecords() {
        int bookmarksCount = bookmarkPreferences.getBookmarksCount();
        for (int i = 0; i < bookmarksCount; i++) {
            BookmarkRecord bookmarkRecord = bookmarkPreferences.getBookmarkRecord(i);
            bookmarkRecords.add(bookmarkRecord);
        }
    }

    private void saveBookmarkRecords() {
        int bookmarksCount = bookmarkRecords.size();
        bookmarkPreferences.setBookmarksCount(bookmarksCount);
        for (int i = 0; i < bookmarksCount; i++) {
            bookmarkPreferences.setBookmarkRecord(i, bookmarkRecords.get(i));
        }
    }

    @Nonnull
    public List<BookmarkRecord> getBookmarkRecords() {
        return bookmarkRecords;
    }

    @Nonnull
    public AbstractAction getManageBookmarksAction() {
        return manageBookmarksAction;
    }

    public void setBookmarkRecords(List<BookmarkRecord> records) {
        bookmarkRecords.clear();
        bookmarkRecords.addAll(records);
        saveBookmarkRecords();
        bookmarksPositionColorModifier.notifyBookmarksChanged();
        updateBookmarksMenu();
    }

    @Nonnull
    public BookmarksManagerPanel getBookmarksManagerPanel() {
        return bookmarksManagerPanel;
    }

    @Nonnull
    public JMenu getBookmarksMenu() {
        return bookmarksMenu;
    }

    public void setBookmarksMenu(JMenu bookmarksMenu) {
        this.bookmarksMenu = bookmarksMenu;
        updateBookmarksMenu();
    }
    
    public void updateBookmarksMenu() {
        bookmarksMenu.removeAll();
        int recordsLimit = Math.min(bookmarkRecords.size(), 10);
        for (int i = 0; i < recordsLimit; i++) {
            BookmarkRecord bookmarkRecord = bookmarkRecords.get(i);
            Action bookmarkAction = new AbstractAction("Bookmark " + (i+1)) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long startPosition = bookmarkRecord.getStartPosition();
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                        ExtCodeArea codeArea = fileHandler.getCodeArea();
                        codeArea.centerOnPosition(startPosition, 0, codeArea.getActiveSection());
                    }
                }
            };
            bookmarkAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
            bookmarksMenu.add(bookmarkAction);
        }
        
        if (!bookmarkRecords.isEmpty()) {
            bookmarksMenu.addSeparator();
        }
        bookmarksMenu.add(manageBookmarksAction);
    }
}
