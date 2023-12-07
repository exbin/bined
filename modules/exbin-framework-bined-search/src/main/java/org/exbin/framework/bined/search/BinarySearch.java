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

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.search.gui.BinaryMultilinePanel;
import org.exbin.framework.bined.search.gui.BinarySearchPanel;
import org.exbin.framework.bined.search.gui.FindBinaryPanel;
import org.exbin.framework.bined.search.service.BinarySearchService;
import org.exbin.framework.bined.search.service.BinarySearchService.FoundMatches;
import org.exbin.framework.bined.search.service.impl.BinarySearchServiceImpl;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.utils.handler.DefaultControlHandler;

/**
 * Binary search.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinarySearch {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinarySearch.class);

    private Thread searchStartThread;
    private Thread searchThread;

    private final SearchParameters searchParameters = new SearchParameters();
    private final ReplaceParameters replaceParameters = new ReplaceParameters();
    private FoundMatches foundMatches = new FoundMatches();

    private final List<SearchCondition> searchHistory = new ArrayList<>();
    private final List<SearchCondition> replaceHistory = new ArrayList<>();

    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;
    private PanelClosingListener panelClosingListener = null;
    private BinarySearchService binarySearchService;
    private final BinarySearchService.SearchStatusListener searchStatusListener;
    private final BinarySearchPanel binarySearchPanel = new BinarySearchPanel();

    private XBApplication application;

    public BinarySearch() {
        searchStatusListener = new BinarySearchService.SearchStatusListener() {
            @Override
            public void setStatus(@Nonnull BinarySearchService.FoundMatches foundMatches) {
                BinarySearch.this.foundMatches = foundMatches;
                switch (foundMatches.getMatchesCount()) {
                    case 0:
                        binarySearchPanel.setInfoLabel(resourceBundle.getString("searchStatus.noMatch"));
                        break;
                    case 1:
                        binarySearchPanel.setInfoLabel(resourceBundle.getString("searchStatus.singleMatch"));
                        break;
                    default:
                        binarySearchPanel.setInfoLabel(
                                java.text.MessageFormat.format(resourceBundle.getString("searchStatus.foundMatches"), foundMatches.getMatchPosition() + 1, foundMatches.getMatchesCount())
                        );
                        break;
                }
                updateMatchStatus();
            }

            @Override
            public void clearStatus() {
                binarySearchPanel.setInfoLabel("");
                BinarySearch.this.foundMatches = new BinarySearchService.FoundMatches();
                updateMatchStatus();
            }

            private void updateMatchStatus() {
                int matchesCount = foundMatches.getMatchesCount();
                int matchPosition = foundMatches.getMatchPosition();
                binarySearchPanel.updateMatchStatus(matchesCount > 0,
                        matchesCount > 1 && matchPosition > 0,
                        matchPosition < matchesCount - 1
                );
            }
        };
        binarySearchPanel.setControl(new BinarySearchPanel.Control() {
            @Override
            public void prevMatch() {
                foundMatches.prev();
                binarySearchService.setMatchPosition(foundMatches.getMatchPosition());
                searchStatusListener.setStatus(foundMatches);
            }

            @Override
            public void nextMatch() {
                foundMatches.next();
                binarySearchService.setMatchPosition(foundMatches.getMatchPosition());
                searchStatusListener.setStatus(foundMatches);
            }

            @Override
            public void performEscape() {
                SearchCondition condition = searchParameters.getCondition();
                if (!condition.isEmpty()) {
                    clearSearch();
                } else {
                    cancelSearch();
                    close();
                }
            }

            @Override
            public void performFind() {
                binarySearchService.performFind(searchParameters, searchStatusListener);
                // TODO
//                findComboBoxEditorComponent.setRunningUpdate(true);
//                ((SearchHistoryModel) findComboBox.getModel()).addSearchCondition(searchParameters.getCondition());
//                findComboBoxEditorComponent.setRunningUpdate(false);
            }

            @Override
            public void performReplace() {
                // TODO replaceParameters.setCondition(replaceComboBoxEditorComponent.getItem());
                binarySearchService.performReplace(searchParameters, replaceParameters);
            }

            @Override
            public void performReplaceAll() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void searchOptions() {
                cancelSearch();
                FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
                final FindBinaryPanel findBinaryPanel = new FindBinaryPanel();
                findBinaryPanel.setSelected();
                findBinaryPanel.setSearchHistory(searchHistory);
                findBinaryPanel.setSearchParameters(searchParameters);
                // TODO replaceParameters.setPerformReplace(replaceMode);
                findBinaryPanel.setReplaceParameters(replaceParameters);
                findBinaryPanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
                DefaultControlPanel controlPanel = new DefaultControlPanel(findBinaryPanel.getResourceBundle());
                final WindowUtils.DialogWrapper dialog = frameModule.createDialog(findBinaryPanel, controlPanel);
                frameModule.setDialogTitle(dialog, findBinaryPanel.getResourceBundle());
                WindowUtils.addHeaderPanel(dialog.getWindow(), findBinaryPanel.getClass(), findBinaryPanel.getResourceBundle());
                findBinaryPanel.setMultilineEditorListener(new FindBinaryPanel.MultilineEditorListener() {
                    @Override
                    public SearchCondition multilineEdit(SearchCondition condition) {
                        final BinaryMultilinePanel multilinePanel = new BinaryMultilinePanel();
                        multilinePanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
                        multilinePanel.setCondition(condition);
                        DefaultControlPanel controlPanel = new DefaultControlPanel();
                        JPanel dialogPanel = WindowUtils.createDialogPanel(multilinePanel, controlPanel);
                        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
                        final WindowUtils.DialogWrapper multilineDialog = frameModule.createDialog(dialog.getWindow(), Dialog.ModalityType.APPLICATION_MODAL, dialogPanel);
                        WindowUtils.addHeaderPanel(multilineDialog.getWindow(), multilinePanel.getClass(), multilinePanel.getResourceBundle());
                        frameModule.setDialogTitle(multilineDialog, multilinePanel.getResourceBundle());
                        final SearchConditionResult result = new SearchConditionResult();
                        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                result.searchCondition = multilinePanel.getCondition();
                                binarySearchPanel.updateFindStatus();
                            }

                            multilineDialog.close();
                            multilineDialog.dispose();
                        });
                        multilineDialog.showCentered(dialog.getWindow());
                        multilinePanel.detachMenu();
                        return result.searchCondition;
                    }

                    class SearchConditionResult {

                        SearchCondition searchCondition = null;
                    }
                });
                controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                        SearchParameters dialogSearchParameters = findBinaryPanel.getSearchParameters();
                        // TODO ((SearchHistoryModel) findComboBox.getModel()).addSearchCondition(dialogSearchParameters.getCondition());
                        dialogSearchParameters.setFromParameters(dialogSearchParameters);
                        // TODO findComboBoxEditorComponent.setItem(dialogSearchParameters.getCondition());
                        binarySearchPanel.updateFindStatus();

                        ReplaceParameters dialogReplaceParameters = findBinaryPanel.getReplaceParameters();
                        binarySearchPanel.switchPanelMode(dialogReplaceParameters.isPerformReplace() ? BinarySearchPanel.Mode.REPLACE : BinarySearchPanel.Mode.FIND);
                        binarySearchService.performFind(dialogSearchParameters, searchStatusListener);
                    }
                    findBinaryPanel.detachMenu();
                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(WindowUtils.getWindow(binarySearchPanel));
            }

            @Override
            public void close() {
                if (panelClosingListener != null) {
                    clearSearch();
                    panelClosingListener.closed();
                }
            }
        });
        binarySearchPanel.setSearchHistory(searchHistory);
        binarySearchPanel.setReplaceHistory(replaceHistory);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        binarySearchPanel.setApplication(application);
    }

    public void setBinarySearchService(BinarySearchService binarySearchService) {
        this.binarySearchService = binarySearchService;
    }

    public void setPanelClosingListener(PanelClosingListener panelClosingListener) {
        this.panelClosingListener = panelClosingListener;
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        this.codeAreaPopupMenuHandler = codeAreaPopupMenuHandler;
        binarySearchPanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
    }

    @Nonnull
    public BinarySearchService.SearchStatusListener getSearchStatusListener() {
        return searchStatusListener;
    }

    private void performSearch() {
        performSearch(0);
    }

    private void performSearch(final int delay) {
        if (searchStartThread != null) {
            searchStartThread.interrupt();
        }
        searchStartThread = new Thread(() -> {
            try {
                Thread.sleep(delay);
                if (searchThread != null) {
                    searchThread.interrupt();
                }
                searchThread = new Thread(); // TODO parameter was: this::performFind
                searchThread.start();
            } catch (InterruptedException ex) {
                // don't search
            }
        });
        searchStartThread.start();
    }

    public void cancelSearch() {
        if (searchThread != null) {
            searchThread.interrupt();
        }
    }

    public void clearSearch() {
        SearchCondition condition = searchParameters.getCondition();
        if (!condition.isEmpty()) {
            condition.clear();
            binarySearchPanel.clearSearch();
            performSearch();
        }
    }

    public void updatePosition(long position, long dataSize) {
        long startPosition;
        if (searchParameters.isSearchFromCursor()) {
            startPosition = position;
        } else {
            switch (searchParameters.getSearchDirection()) {
                case FORWARD: {
                    startPosition = 0;
                    break;
                }
                case BACKWARD: {
                    startPosition = dataSize - 1;
                    break;
                }
                default:
                    throw CodeAreaUtils.getInvalidTypeException(searchParameters.getSearchDirection());
            }
        }
        searchParameters.setStartPosition(startPosition);
    }

    @Nonnull
    public BinarySearchPanel getPanel() {
        return binarySearchPanel;
    }

    public void dataChanged() {
        binarySearchService.clearMatches();
        performSearch(500);
    }

    public interface PanelClosingListener {

        void closed();
    }
}
