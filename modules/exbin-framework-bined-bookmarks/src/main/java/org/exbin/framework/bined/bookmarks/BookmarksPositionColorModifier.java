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
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaSection;
import org.exbin.framework.bined.BinEdCodeAreaPainter;
import org.exbin.framework.bined.bookmarks.model.BookmarkRange;
import org.exbin.framework.bined.bookmarks.model.BookmarkRecord;

/**
 * Bookmarks position color modifier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BookmarksPositionColorModifier implements BinEdCodeAreaPainter.PositionColorModifier {

    private final TreeMap<BookmarkRange, BookmarkRecord> records;

    public BookmarksPositionColorModifier(TreeMap<BookmarkRange, BookmarkRecord> records) {
        this.records = records;
    }

    @Nullable
    @Override
    public Color getPositionBackgroundColor(long rowDataPosition, int byteOnRow, int charOnRow, CodeAreaSection section, boolean unprintables) {
        long dataPosition = rowDataPosition + byteOnRow;
        // TODO
        // Map.Entry<BookmarkRange, BookmarkRecord> entry = records.floorEntry(new BookmarkRange(dataPosition));
        // if (entry != null && entry.getKey().getEnd())
        return null;
    }

    @Nullable
    @Override
    public Color getPositionTextColor(long rowDataPosition, int byteOnRow, int charOnRow, CodeAreaSection section, boolean unprintables) {
        return null;
    }
}
