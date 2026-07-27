/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.search;

import java.util.List;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.jaguif.App;
import org.exbin.jaguif.search.api.FindSearchController;
import org.exbin.jaguif.search.api.ReplaceSearchController;
import org.jspecify.annotations.NullMarked;

/**
 * Binary search controller.
 */
@NullMarked
public class BinarySearchController implements FindSearchController, ReplaceSearchController {

    protected final BinaryFileDocument binaryDocument;

    public BinarySearchController(BinaryFileDocument binaryDocument) {
        this.binaryDocument = binaryDocument;
    }

    @Override
    public void performFind() {
        BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
        componentExtension.showSearchFindPanel();
    }

    @Override
    public void performFindNext() {
        BinedSearchModule searchModule = App.getModule(BinedSearchModule.class);
        List<FindAgainListener> findAgainListeners = searchModule.getFindAgainListeners();
        BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
        componentExtension.performFindAgain();

        for (FindAgainListener findAgainListener : findAgainListeners) {
            findAgainListener.findNext();
        }
    }

    @Override
    public void performFindPrevious() {
        BinedSearchModule searchModule = App.getModule(BinedSearchModule.class);
        List<FindAgainListener> findAgainListeners = searchModule.getFindAgainListeners();
        BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
        // TODO find previous
        componentExtension.performFindAgain();

        for (FindAgainListener findAgainListener : findAgainListeners) {
            findAgainListener.findPrevious();
        }
    }

    @Override
    public boolean isFindNextAvailable() {
        return true;
    }

    @Override
    public boolean isFindPreviousAvailable() {
        // TODO Is editable
        return true;
    }

    @Override
    public void performReplace() {
        BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
        componentExtension.showSearchReplacePanel();
    }
}
