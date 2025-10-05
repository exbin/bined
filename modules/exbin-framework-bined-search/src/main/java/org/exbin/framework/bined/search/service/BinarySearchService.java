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
package org.exbin.framework.bined.search.service;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.search.ReplaceParameters;
import org.exbin.framework.bined.search.SearchParameters;

/**
 * Binary search service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinarySearchService {

    void performFind(SearchParameters dialogSearchParameters, SearchStatusListener searchStatusListener);

    void setMatchIndex(int matchIndex);

    void performFindAgain(SearchStatusListener searchStatusListener);

    void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters);

    @Nonnull
    SearchParameters getLastSearchParameters();

    void clearMatches();

    @ParametersAreNonnullByDefault
    public interface SearchStatusListener {

        void setStatus(FoundMatches foundMatches, SearchParameters.MatchMode matchMode);

        void setProgress(int progress);

        void clearStatus();

        void setCancelled();
    }

    public static class FoundMatches {

        private int matchesCount;
        private int matchIndex;

        public FoundMatches() {
            matchesCount = 0;
            matchIndex = -1;
        }

        public FoundMatches(int matchesCount, int matchIndex) {
            if (matchIndex >= matchesCount) {
                throw new IllegalStateException("Match position is out of range");
            }

            this.matchesCount = matchesCount;
            this.matchIndex = matchIndex;
        }

        public int getMatchesCount() {
            return matchesCount;
        }

        public int getMatchIndex() {
            return matchIndex;
        }

        public void setMatchesCount(int matchesCount) {
            this.matchesCount = matchesCount;
        }

        public void setMatchIndex(int matchIndex) {
            this.matchIndex = matchIndex;
        }

        public void next() {
            if (matchIndex == matchesCount - 1) {
                throw new IllegalStateException("Cannot find next on last match");
            }

            matchIndex++;
        }

        public void prev() {
            if (matchIndex == 0) {
                throw new IllegalStateException("Cannot find previous on first match");
            }

            matchIndex--;
        }
    }
}
