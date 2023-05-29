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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.bookmarks.action.AddBookmarkAction;
import org.exbin.framework.bined.bookmarks.action.EditBookmarkAction;
import org.exbin.framework.bined.bookmarks.gui.BookmarksManagerPanel;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;
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
    private BookmarksPositionColorModifier bookmarksPositionColorModifier;

    private final BookmarksManagerPanel bookmarksManagerPanel;
    private XBApplication application;

    private AddBookmarkAction addBookmarkAction = new AddBookmarkAction();
    private EditBookmarkAction editBookmarkAction = new EditBookmarkAction();
//    private DeleteBookmarkAction deleteBookmarkAction = new DeleteBookmarkAction();

    public BookmarksManager() {
        bookmarkRecords.add(new BookmarkRecord(3, 5, Color.RED));
        bookmarkRecords.add(new BookmarkRecord(10, 20, Color.BLUE));
        bookmarkRecords.add(new BookmarkRecord(15, 3, Color.GREEN));

        bookmarksManagerPanel = new BookmarksManagerPanel();
        bookmarksManagerPanel.setControl(new BookmarksManagerPanel.Control() {
            @Override
            public void addRecord() {
                addBookmarkAction.actionPerformed(null);
            }

            @Override
            public void editRecord() {
                BookmarkRecord selectedRecord = bookmarksManagerPanel.getSelectedRecord();
                editBookmarkAction.setBookmarkRecord(selectedRecord);
                editBookmarkAction.actionPerformed(null);
            }

            @Override
            public void removeRecord() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void selectAll() {
                bookmarksManagerPanel.getTable().selectAll();
            }

            @Override
            public void moveUp() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void moveDown() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;

        addBookmarkAction.setup(application, resourceBundle);
        editBookmarkAction.setup(application, resourceBundle);
    }
    
    public void init() {
        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
        bookmarksPositionColorModifier = new BookmarksPositionColorModifier(bookmarkRecords);
        binedModule.addPainterColorModifier(bookmarksPositionColorModifier);
    }

    @Nonnull
    public List<BookmarkRecord> getBookmarkRecords() {
        return bookmarkRecords;
    }

    public void setBookmarkRecords(List<BookmarkRecord> records) {
        bookmarkRecords.clear();
        bookmarkRecords.addAll(records);
        bookmarksPositionColorModifier.notifyBookmarksChanged();
    }

    @Nonnull
    public BookmarksManagerPanel getBookmarksManagerPanel() {
        return bookmarksManagerPanel;
    }
}
