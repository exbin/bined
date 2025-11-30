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
package org.exbin.framework.bined.search;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.BinEdComponentExtension;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.search.gui.BinarySearchPanel;

/**
 * Bined component search.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinEdComponentSearch extends BinEdComponentExtension {

    /**
     * Shows search panel.
     *
     * @param panelMode panel mode
     */
    void showSearchPanel(BinarySearchPanel.PanelMode panelMode);

    /**
     * Hides search panel.
     */
    void hideSearchPanel();

    /**
     * Performs text search.
     *
     * @param text text to search
     */
    void performSearchText(String text);

    /**
     * Performs find again action.
     */
    void performFindAgain();

    /**
     * Sets popup menu handler for binary component.
     *
     * @param codeAreaPopupMenuHandler popup menu handler
     */
    void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler);
}
