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
package org.exbin.framework.bined.bookmarks.options;

import java.awt.Color;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Code area bookmarks options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BookmarkOptions implements OptionsData {

    public static final String KEY_BOOKMARK_COUNT = "bookmarksCount";
    public static final String KEY_BOOKMARK_VALUE_PREFIX = "bookmark.";

    public static final String BOOKMARK_START_POSITION = "startPosition";
    public static final String BOOKMARK_LENGTH = "length";
    public static final String BOOKMARK_COLOR = "bookmarkColor";

    private final OptionsStorage storage;

    public BookmarkOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public int getBookmarksCount() {
        return storage.getInt(KEY_BOOKMARK_COUNT, 0);
    }

    public BookmarkRecord getBookmarkRecord(int index) {
        String prefix = KEY_BOOKMARK_VALUE_PREFIX + index + ".";
        long startPosition = storage.getLong(prefix + BOOKMARK_START_POSITION, 0);
        long length = storage.getLong(prefix + BOOKMARK_LENGTH, 0);
        Color color = textAsColor(storage.get(prefix + BOOKMARK_COLOR));
        return new BookmarkRecord(startPosition, length, color);
    }

    public void setBookmarksCount(int count) {
        storage.putInt(KEY_BOOKMARK_COUNT, count);
    }

    public void setBookmarkRecord(int index, BookmarkRecord record) {
        String prefix = KEY_BOOKMARK_VALUE_PREFIX + index + ".";
        storage.putLong(prefix + BOOKMARK_START_POSITION, record.getStartPosition());
        storage.putLong(prefix + BOOKMARK_LENGTH, record.getLength());
        storage.put(prefix + BOOKMARK_COLOR, colorAsText(record.getColor()));
    }

    /**
     * Converts color to text.
     *
     * @param color color
     * @return color string in hex format, e.g. "#FFFFFF"
     */
    @Nullable
    private static String colorAsText(@Nullable Color color) {
        if (color == null) {
            return null;
        }
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return String.format("#%02x%02x%02x", red, green, blue);
    }

    /**
     * Converts text to color.
     *
     * @param colorStr e.g. "#FFFFFF"
     * @return color or null
     */
    @Nullable
    private static Color textAsColor(Optional<String> colorStr) {
        if (!colorStr.isPresent()) {
            return null;
        }
        return Color.decode(colorStr.get());
    }

    @Override
    public void copyTo(OptionsData options) {
        BookmarkOptions with = (BookmarkOptions) options;
        int bookmarksCount = getBookmarksCount();
        with.setBookmarksCount(bookmarksCount);
        for (int i = 0; i < bookmarksCount; i++) {
            with.setBookmarkRecord(i, getBookmarkRecord(i));
        }
    }
}
