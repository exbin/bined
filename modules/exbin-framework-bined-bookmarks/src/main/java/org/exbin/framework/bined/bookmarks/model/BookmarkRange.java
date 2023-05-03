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
package org.exbin.framework.bined.bookmarks.model;

import java.util.Comparator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * Bookmark range.
 *
 * @author ExBin Project (https://exbin.org)
 */
@Immutable
@ParametersAreNonnullByDefault
public class BookmarkRange {

    private final long start;
    private final long end;

    public BookmarkRange(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public BookmarkRange(long start) {
        this.start = start;
        this.end = start;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.start ^ (this.start >>> 32));
        hash = 53 * hash + (int) (this.end ^ (this.end >>> 32));
        return hash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BookmarkRange other = (BookmarkRange) obj;
        if (this.start != other.start) {
            return false;
        }
        return this.end == other.end;
    }

    public static Comparator<BookmarkRange> createComparator() {
        return (@Nonnull BookmarkRange range1, @Nonnull BookmarkRange range2) -> {
            if (range1.start < range2.start || (range1.start == range2.start && range1.end < range2.end)) {
                return -1;
            }
            if (range1.start > range2.start || (range1.start == range2.start && range1.end > range2.end)) {
                return 1;
            }

            return 0;
        };
    }
}
