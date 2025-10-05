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
package org.exbin.framework.bined.search.gui;

import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.auxiliary.dropdownbutton.DropDownButton;
import org.exbin.framework.App;
import org.exbin.framework.bined.search.SearchCondition;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.UiUtils;

/**
 * Search type utility.
 * <p>
 * TODO: Refactor as component instead of utilities class.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SearchTypeUtils {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(SearchTypeUtils.class);

    public SearchTypeUtils() {
    }

    public void setupSearchType(DropDownButton searchTypeButton, SearchModeProvider searchModeProvider, SearchTypeChangeListener listener) {
        Action searchTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                switch (searchModeProvider.getCurrentMode()) {
                    case TEXT:
                        listener.searchTypeChanged(SearchCondition.SearchMode.REGEX);
                        break;
                    case REGEX:
                        listener.searchTypeChanged(SearchCondition.SearchMode.BINARY);
                        break;
                    case BINARY:
                        listener.searchTypeChanged(SearchCondition.SearchMode.TEXT);
                        break;
                }
            }
        };
        searchTypeButton.inheritFromAction(searchTypeAction);
        JPopupMenu searchTypeMenu = UiUtils.createPopupMenu();
        {
            Action textSearchType = new AbstractAction(resourceBundle.getString("searchType.text.name")) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    listener.searchTypeChanged(SearchCondition.SearchMode.TEXT);
                }
            };
            searchTypeMenu.add(new JMenuItem(textSearchType));
            Action regExSearchType = new AbstractAction(resourceBundle.getString("searchType.regex.name")) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    listener.searchTypeChanged(SearchCondition.SearchMode.REGEX);
                }
            };
            searchTypeMenu.add(new JMenuItem(regExSearchType));
            Action binarySearchType = new AbstractAction(resourceBundle.getString("searchType.binary.name")) {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    listener.searchTypeChanged(SearchCondition.SearchMode.BINARY);
                }
            };
            searchTypeMenu.add(new JMenuItem(binarySearchType));
            searchTypeButton.setDropDownMenu(searchTypeMenu);
        }
    }

    public void updateSearchType(DropDownButton searchTypeButton, SearchCondition.SearchMode searchMode) {
        switch (searchMode) {
            case TEXT:
                searchTypeButton.setActionText(resourceBundle.getString("searchType.text.code"));
                searchTypeButton.setActionTooltip(resourceBundle.getString("searchType.text.code") + " - " + resourceBundle.getString("searchType.text.name"));
                break;
            case REGEX:
                searchTypeButton.setActionText(resourceBundle.getString("searchType.regex.code"));
                searchTypeButton.setActionTooltip(resourceBundle.getString("searchType.regex.code") + " - " + resourceBundle.getString("searchType.regex.name"));
                break;
            default:
                searchTypeButton.setActionText(resourceBundle.getString("searchType.binary.code"));
                searchTypeButton.setActionTooltip(resourceBundle.getString("searchType.binary.code") + " - " + resourceBundle.getString("searchType.binary.name"));
                break;
        }
    }

    public interface SearchModeProvider {

        @Nonnull
        SearchCondition.SearchMode getCurrentMode();
    }

    @ParametersAreNonnullByDefault
    public interface SearchTypeChangeListener {

        void searchTypeChanged(SearchCondition.SearchMode searchMode);
    }
}
