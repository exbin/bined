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

import org.exbin.framework.bined.search.ReplaceParameters;
import org.exbin.framework.bined.search.SearchCondition;
import org.exbin.framework.bined.search.SearchParameters;
import org.exbin.framework.bined.search.SearchHistoryModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.exbin.bined.ScrollBarVisibility;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.extended.theme.ExtendedBackgroundPaintMode;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.framework.bined.search.service.BinarySearchService;
import org.exbin.framework.bined.search.service.BinarySearchService.FoundMatches;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Binary editor search panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinarySearchPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinarySearchPanel.class);

    private Thread searchStartThread;
    private Thread searchThread;
    private final SearchParameters searchParameters = new SearchParameters();
    private final ReplaceParameters replaceParameters = new ReplaceParameters();

    private BinarySearchService binarySearchService;
    private final BinarySearchService.SearchStatusListener searchStatusListener;

    private FoundMatches foundMatches = new FoundMatches();
    private final ExtCodeArea searchCodeArea = new ExtCodeArea();

    private boolean replaceMode = true;
    private ComboBoxEditor findComboBoxEditor;
    private BinarySearchComboBoxPanel findComboBoxEditorComponent;
    private ComboBoxEditor replaceComboBoxEditor;
    private BinarySearchComboBoxPanel replaceComboBoxEditorComponent;

    private final List<SearchCondition> searchHistory = new ArrayList<>();
    private final List<SearchCondition> replaceHistory = new ArrayList<>();

    private ClosePanelListener closePanelListener = null;
    private CodeAreaPopupMenuHandler codeAreaPopupMenuHandler;
    private XBApplication application;

    public BinarySearchPanel() {
        initComponents();
        searchStatusListener = new BinarySearchService.SearchStatusListener() {
            @Override
            public void setStatus(@Nonnull FoundMatches foundMatches) {
                BinarySearchPanel.this.foundMatches = foundMatches;
                switch (foundMatches.getMatchesCount()) {
                    case 0:
                        infoLabel.setText("No matches found");
                        break;
                    case 1:
                        infoLabel.setText("Single match found");
                        break;
                    default:
                        infoLabel.setText("Match " + (foundMatches.getMatchPosition() + 1) + " of " + foundMatches.getMatchesCount());
                        break;
                }
                updateMatchStatus();
            }

            @Override
            public void clearStatus() {
                infoLabel.setText("");
                BinarySearchPanel.this.foundMatches = new FoundMatches();
                updateMatchStatus();
            }

            private void updateMatchStatus() {
                int matchesCount = foundMatches.getMatchesCount();
                int matchPosition = foundMatches.getMatchPosition();
                prevButton.setEnabled(matchesCount > 1 && matchPosition > 0);
                nextButton.setEnabled(matchPosition < matchesCount - 1);
                replaceButton.setEnabled(matchesCount > 0);
                replaceAllButton.setEnabled(matchesCount > 0);
            }
        };
        init();
    }

    private void init() {
        ExtendedCodeAreaLayoutProfile layoutProfile = Objects.requireNonNull(searchCodeArea.getLayoutProfile());
        layoutProfile.setShowHeader(false);
        layoutProfile.setShowRowPosition(false);

        searchCodeArea.setLayoutProfile(layoutProfile);
        ExtendedCodeAreaThemeProfile themeProfile = searchCodeArea.getThemeProfile();
        themeProfile.setBackgroundPaintMode(ExtendedBackgroundPaintMode.PLAIN);

        searchCodeArea.setLayoutProfile(layoutProfile);
        searchCodeArea.setRowWrapping(RowWrappingMode.WRAPPING);
        searchCodeArea.setWrappingBytesGroupSize(0);
        searchCodeArea.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        searchCodeArea.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        searchCodeArea.setContentData(new ByteArrayEditableData(new byte[]{1, 2, 3}));

        final KeyAdapter editorKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(@Nonnull KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    SearchCondition condition = searchParameters.getCondition();
                    if (!condition.isEmpty()) {
                        clearSearch();
                    } else {
                        cancelSearch();
                        closePanel();
                    }
                }
            }
        };

        findComboBoxEditorComponent = new BinarySearchComboBoxPanel();
        findComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final JPanel panel = new JPanel();
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, @Nullable SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return panel;
                }

                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    searchCodeArea.setContentData(value.getBinaryData());
                    searchCodeArea.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
// TODO                    ColorsGroup mainColors = new ColorsGroup(hexadecimalRenderer.getMainColors());
//                    mainColors.setBothBackgroundColors(backgroundColor);
//                    hexadecimalRenderer.setMainColors(mainColors);
                    return searchCodeArea;
                }
            }
        });
        findComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return findComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                SearchCondition condition;
                if (item == null || item instanceof String) {
                    condition = new SearchCondition();
                    condition.setSearchMode(SearchCondition.SearchMode.TEXT);
                    if (item != null) {
                        condition.setSearchText((String) item);
                    }
                } else {
                    condition = (SearchCondition) item;
                }
                searchParameters.setCondition(new SearchCondition(condition));
                SearchCondition currentItem = findComboBoxEditorComponent.getItem();
                if (item != currentItem) {
                    findComboBoxEditorComponent.setItem(condition);
                    updateFindStatus();
                }
            }

            @Nonnull
            @Override
            public Object getItem() {
                return findComboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                findComboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        findComboBox.setEditor(findComboBoxEditor);

        findComboBoxEditorComponent.setValueChangedListener(this::comboBoxValueChanged);
        findComboBoxEditorComponent.addValueKeyListener(editorKeyListener);
        findComboBox.setModel(new SearchHistoryModel(searchHistory));

        replaceComboBoxEditorComponent = new BinarySearchComboBoxPanel();
        replaceComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final JPanel panel = new JPanel();
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return panel;
                }

                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    searchCodeArea.setContentData(value.getBinaryData());
                    searchCodeArea.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
// TODO                    ColorsGroup mainColors = new ColorsGroup(hexadecimalRenderer.getMainColors());
//                    mainColors.setBothBackgroundColors(backgroundColor);
//                    hexadecimalRenderer.setMainColors(mainColors);
                    return searchCodeArea;
                }
            }
        });
        replaceComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return replaceComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                SearchCondition condition;
                if (item == null || item instanceof String) {
                    condition = new SearchCondition();
                    condition.setSearchMode(SearchCondition.SearchMode.TEXT);
                    if (item != null) {
                        condition.setSearchText((String) item);
                    }
                } else {
                    condition = (SearchCondition) item;
                }
                replaceParameters.setCondition(new SearchCondition(condition));
                SearchCondition currentItem = replaceComboBoxEditorComponent.getItem();
                if (item != currentItem) {
                    replaceComboBoxEditorComponent.setItem(condition);
                    updateReplaceStatus();
                }
            }

            @Nonnull
            @Override
            public Object getItem() {
                return replaceComboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                replaceComboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        replaceComboBox.setEditor(replaceComboBoxEditor);

        replaceComboBoxEditorComponent.addValueKeyListener(editorKeyListener);
        replaceComboBox.setModel(new SearchHistoryModel(replaceHistory));
    }

    public void setBinarySearchService(BinarySearchService binarySearchService) {
        this.binarySearchService = binarySearchService;
    }

    public void switchReplaceMode(boolean replaceMode) {
        if (this.replaceMode != replaceMode) {
            this.replaceMode = replaceMode;
            if (replaceMode) {
                add(replacePanel, BorderLayout.SOUTH);
            } else {
                remove(replacePanel);
            }
            revalidate();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topSeparator = new javax.swing.JSeparator();
        findPanel = new javax.swing.JPanel();
        findLabel = new javax.swing.JLabel();
        findTypeToolBar = new javax.swing.JToolBar();
        findTypeButton = new javax.swing.JButton();
        findComboBox = new javax.swing.JComboBox<>();
        findToolBar = new javax.swing.JToolBar();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        matchCaseToggleButton = new javax.swing.JToggleButton();
        multipleMatchesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();
        optionsButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        closeToolBar = new javax.swing.JToolBar();
        closeButton = new javax.swing.JButton();
        replacePanel = new javax.swing.JPanel();
        replaceLabel = new javax.swing.JLabel();
        replaceTypeToolBar = new javax.swing.JToolBar();
        replaceTypeButton = new javax.swing.JButton();
        replaceComboBox = new javax.swing.JComboBox<>();
        replaceToolBar = new javax.swing.JToolBar();
        replaceButton = new javax.swing.JButton();
        replaceAllButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        topSeparator.setName("topSeparator"); // NOI18N
        add(topSeparator, java.awt.BorderLayout.NORTH);

        findPanel.setName("findPanel"); // NOI18N

        findLabel.setText(resourceBundle.getString("findLabel.text")); // NOI18N
        findLabel.setName("findLabel"); // NOI18N

        findTypeToolBar.setBorder(null);
        findTypeToolBar.setRollover(true);
        findTypeToolBar.setFocusable(false);
        findTypeToolBar.setName("findTypeToolBar"); // NOI18N

        findTypeButton.setText("T");
        findTypeButton.setToolTipText(resourceBundle.getString("findTypeButton.toolTipText")); // NOI18N
        findTypeButton.setFocusable(false);
        findTypeButton.setMaximumSize(new java.awt.Dimension(27, 27));
        findTypeButton.setMinimumSize(new java.awt.Dimension(27, 27));
        findTypeButton.setName("findTypeButton"); // NOI18N
        findTypeButton.setPreferredSize(new java.awt.Dimension(27, 27));
        findTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findTypeButtonActionPerformed(evt);
            }
        });
        findTypeToolBar.add(findTypeButton);

        findComboBox.setEditable(true);
        findComboBox.setSelectedItem("");
        findComboBox.setName("findComboBox"); // NOI18N

        findToolBar.setBorder(null);
        findToolBar.setRollover(true);
        findToolBar.setFocusable(false);
        findToolBar.setName("findToolBar"); // NOI18N

        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/search/resources/icons/open_icon_library/icons/png/16x16/actions/arrow-left.png"))); // NOI18N
        prevButton.setEnabled(false);
        prevButton.setFocusable(false);
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevButton.setName("prevButton"); // NOI18N
        prevButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });
        findToolBar.add(prevButton);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/search/resources/icons/open_icon_library/icons/png/16x16/actions/arrow-right.png"))); // NOI18N
        nextButton.setEnabled(false);
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setName("nextButton"); // NOI18N
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        findToolBar.add(nextButton);

        matchCaseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/search/resources/icons/case_sensitive.gif"))); // NOI18N
        matchCaseToggleButton.setSelected(true);
        matchCaseToggleButton.setToolTipText(resourceBundle.getString("matchCaseToggleButton.toolTipText")); // NOI18N
        matchCaseToggleButton.setFocusable(false);
        matchCaseToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        matchCaseToggleButton.setName("matchCaseToggleButton"); // NOI18N
        matchCaseToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        matchCaseToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchCaseToggleButtonActionPerformed(evt);
            }
        });
        findToolBar.add(matchCaseToggleButton);

        multipleMatchesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/search/resources/icons/mark_occurrences.png"))); // NOI18N
        multipleMatchesToggleButton.setSelected(true);
        multipleMatchesToggleButton.setToolTipText(resourceBundle.getString("multipleMatchesToggleButton.toolTipText")); // NOI18N
        multipleMatchesToggleButton.setFocusable(false);
        multipleMatchesToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        multipleMatchesToggleButton.setName("multipleMatchesToggleButton"); // NOI18N
        multipleMatchesToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        multipleMatchesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multipleMatchesToggleButtonActionPerformed(evt);
            }
        });
        findToolBar.add(multipleMatchesToggleButton);

        separator1.setName("separator1"); // NOI18N
        findToolBar.add(separator1);

        optionsButton.setText(resourceBundle.getString("optionsButton.text")); // NOI18N
        optionsButton.setFocusable(false);
        optionsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        optionsButton.setName("optionsButton"); // NOI18N
        optionsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });
        findToolBar.add(optionsButton);

        infoLabel.setEnabled(false);
        infoLabel.setName("infoLabel"); // NOI18N

        closeToolBar.setBorder(null);
        closeToolBar.setRollover(true);
        closeToolBar.setName("closeToolBar"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/search/resources/icons/open_icon_library/icons/png/16x16/actions/dialog-cancel-3.png"))); // NOI18N
        closeButton.setToolTipText(resourceBundle.getString("closeButton.toolTipText")); // NOI18N
        closeButton.setFocusable(false);
        closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closeToolBar.add(closeButton);

        javax.swing.GroupLayout findPanelLayout = new javax.swing.GroupLayout(findPanel);
        findPanel.setLayout(findPanelLayout);
        findPanelLayout.setHorizontalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findTypeToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findComboBox, 0, 519, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        findPanelLayout.setVerticalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(closeToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findTypeToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findComboBox)
        );

        add(findPanel, java.awt.BorderLayout.CENTER);

        replacePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 0, 0));
        replacePanel.setName("replacePanel"); // NOI18N
        replacePanel.setPreferredSize(new java.awt.Dimension(1015, 28));

        replaceLabel.setText(resourceBundle.getString("replaceLabel.text")); // NOI18N
        replaceLabel.setName("replaceLabel"); // NOI18N

        replaceTypeToolBar.setBorder(null);
        replaceTypeToolBar.setRollover(true);
        replaceTypeToolBar.setFocusable(false);
        replaceTypeToolBar.setName("replaceTypeToolBar"); // NOI18N

        replaceTypeButton.setText(resourceBundle.getString("BinarySearchPanel.replaceTypeButton.text")); // NOI18N
        replaceTypeButton.setToolTipText(resourceBundle.getString("replaceTypeButton.toolTipText")); // NOI18N
        replaceTypeButton.setDefaultCapable(false);
        replaceTypeButton.setFocusable(false);
        replaceTypeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        replaceTypeButton.setMaximumSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setMinimumSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setName("replaceTypeButton"); // NOI18N
        replaceTypeButton.setPreferredSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        replaceTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceTypeButtonActionPerformed(evt);
            }
        });
        replaceTypeToolBar.add(replaceTypeButton);

        replaceComboBox.setEditable(true);
        replaceComboBox.setSelectedItem("");
        replaceComboBox.setName("replaceComboBox"); // NOI18N

        replaceToolBar.setBorder(null);
        replaceToolBar.setRollover(true);
        replaceToolBar.setFocusable(false);
        replaceToolBar.setName("replaceToolBar"); // NOI18N

        replaceButton.setText(resourceBundle.getString("replaceButton.text")); // NOI18N
        replaceButton.setEnabled(false);
        replaceButton.setFocusable(false);
        replaceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        replaceButton.setName("replaceButton"); // NOI18N
        replaceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        replaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceButtonActionPerformed(evt);
            }
        });
        replaceToolBar.add(replaceButton);

        replaceAllButton.setText(resourceBundle.getString("replaceAllButton.text")); // NOI18N
        replaceAllButton.setEnabled(false);
        replaceAllButton.setFocusable(false);
        replaceAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        replaceAllButton.setName("replaceAllButton"); // NOI18N
        replaceAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllButtonActionPerformed(evt);
            }
        });
        replaceToolBar.add(replaceAllButton);

        javax.swing.GroupLayout replacePanelLayout = new javax.swing.GroupLayout(replacePanel);
        replacePanel.setLayout(replacePanelLayout);
        replacePanelLayout.setHorizontalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(replaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceTypeToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceComboBox, 0, 734, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(replaceToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        replacePanelLayout.setVerticalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(replaceTypeToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(replaceComboBox)
            .addComponent(replaceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(replaceToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(replacePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
        cancelSearch();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final FindBinaryPanel findBinaryPanel = new FindBinaryPanel();
        findBinaryPanel.setSelected();
        findBinaryPanel.setSearchHistory(searchHistory);
        findBinaryPanel.setSearchParameters(searchParameters);
        replaceParameters.setPerformReplace(replaceMode);
        findBinaryPanel.setReplaceParameters(replaceParameters);
        findBinaryPanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
        DefaultControlPanel controlPanel = new DefaultControlPanel(findBinaryPanel.getResourceBundle());
        final DialogWrapper dialog = frameModule.createDialog(findBinaryPanel, controlPanel);
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
                final DialogWrapper multilineDialog = frameModule.createDialog(dialog.getWindow(), Dialog.ModalityType.APPLICATION_MODAL, dialogPanel);
                WindowUtils.addHeaderPanel(multilineDialog.getWindow(), multilinePanel.getClass(), multilinePanel.getResourceBundle());
                frameModule.setDialogTitle(multilineDialog, multilinePanel.getResourceBundle());
                final SearchConditionResult result = new SearchConditionResult();
                controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                        result.searchCondition = multilinePanel.getCondition();
                        updateFindStatus();
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
                ((SearchHistoryModel) findComboBox.getModel()).addSearchCondition(dialogSearchParameters.getCondition());
                dialogSearchParameters.setFromParameters(dialogSearchParameters);
                findComboBoxEditorComponent.setItem(dialogSearchParameters.getCondition());
                updateFindStatus();

                ReplaceParameters dialogReplaceParameters = findBinaryPanel.getReplaceParameters();
                switchReplaceMode(dialogReplaceParameters.isPerformReplace());
                binarySearchService.performFind(dialogSearchParameters, searchStatusListener);
            }
            findBinaryPanel.detachMenu();
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(WindowUtils.getWindow(this));
    }//GEN-LAST:event_optionsButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        foundMatches.prev();
        binarySearchService.setMatchPosition(foundMatches.getMatchPosition());
        searchStatusListener.setStatus(foundMatches);
    }//GEN-LAST:event_prevButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        foundMatches.next();
        binarySearchService.setMatchPosition(foundMatches.getMatchPosition());
        searchStatusListener.setStatus(foundMatches);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void multipleMatchesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multipleMatchesToggleButtonActionPerformed
        searchParameters.setMultipleMatches(multipleMatchesToggleButton.isSelected());
        performSearch();
    }//GEN-LAST:event_multipleMatchesToggleButtonActionPerformed

    private void matchCaseToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchCaseToggleButtonActionPerformed
        searchParameters.setMatchCase(matchCaseToggleButton.isSelected());
        performSearch();
    }//GEN-LAST:event_matchCaseToggleButtonActionPerformed

    private void findTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTypeButtonActionPerformed
        SearchCondition condition = searchParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }

        findComboBoxEditor.setItem(condition);
        findComboBox.setEditor(findComboBoxEditor);
        findComboBox.repaint();
        performSearch();
    }//GEN-LAST:event_findTypeButtonActionPerformed

    private void updateFindStatus() {
        SearchCondition condition = searchParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            findTypeButton.setText("T");
            matchCaseToggleButton.setEnabled(true);
        } else {
            findTypeButton.setText("B");
            matchCaseToggleButton.setEnabled(false);
        }
    }

    private void updateReplaceStatus() {
        SearchCondition condition = replaceParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            replaceTypeButton.setText("T");
        } else {
            replaceTypeButton.setText("B");
        }
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        closePanel();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void replaceTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceTypeButtonActionPerformed
        SearchCondition condition = replaceParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }

        replaceComboBoxEditor.setItem(condition);
        replaceComboBox.setEditor(replaceComboBoxEditor);
        replaceComboBox.repaint();
    }//GEN-LAST:event_replaceTypeButtonActionPerformed

    private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceButtonActionPerformed
        performReplace();
    }//GEN-LAST:event_replaceButtonActionPerformed

    private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllButtonActionPerformed
        performReplaceAll();
    }//GEN-LAST:event_replaceAllButtonActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new BinarySearchPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JToolBar closeToolBar;
    private javax.swing.JComboBox<SearchCondition> findComboBox;
    private javax.swing.JLabel findLabel;
    private javax.swing.JPanel findPanel;
    private javax.swing.JToolBar findToolBar;
    private javax.swing.JButton findTypeButton;
    private javax.swing.JToolBar findTypeToolBar;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JToggleButton matchCaseToggleButton;
    private javax.swing.JToggleButton multipleMatchesToggleButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton optionsButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton replaceAllButton;
    private javax.swing.JButton replaceButton;
    private javax.swing.JComboBox<SearchCondition> replaceComboBox;
    private javax.swing.JLabel replaceLabel;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JToolBar replaceToolBar;
    private javax.swing.JButton replaceTypeButton;
    private javax.swing.JToolBar replaceTypeToolBar;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JSeparator topSeparator;
    // End of variables declaration//GEN-END:variables

    private void comboBoxValueChanged() {
        SearchCondition condition = searchParameters.getCondition();
        SearchCondition searchCondition = (SearchCondition) findComboBox.getEditor().getItem();

        switch (searchCondition.getSearchMode()) {
            case TEXT: {
                String searchText = searchCondition.getSearchText();
                if (searchText.isEmpty()) {
                    condition.setSearchText(searchText);
                    performFind();
                    return;
                }

                if (searchText.equals(condition.getSearchText())) {
                    return;
                }

                condition.setSearchText(searchText);
                break;
            }
            case BINARY: {
                EditableBinaryData searchData = (EditableBinaryData) searchCondition.getBinaryData();
                if (searchData == null || searchData.isEmpty()) {
                    condition.setBinaryData(null);
                    performFind();
                    return;
                }

                if (searchData.equals(condition.getBinaryData())) {
                    return;
                }

                ByteArrayEditableData data = new ByteArrayEditableData();
                data.insert(0, searchData);
                condition.setBinaryData(data);
                break;
            }
        }
        updatePosition(searchCodeArea.getCaretPosition().getDataPosition(), searchCodeArea.getDataSize());
        performSearch(500);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
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
                searchThread = new Thread(this::performFind);
                searchThread.start();
            } catch (InterruptedException ex) {
                // don't search
            }
        });
        searchStartThread.start();
    }

    public void clearSearch() {
        SearchCondition condition = searchParameters.getCondition();
        if (!condition.isEmpty()) {
            condition.clear();
            findComboBox.getEditor().setItem(new SearchCondition());
            performSearch();
        }
    }

    public void requestSearchFocus() {
        findComboBox.requestFocus();
        findComboBoxEditorComponent.requestFocus();
    }

    public void cancelSearch() {
        if (searchThread != null) {
            searchThread.interrupt();
        }
    }

    public void performFind() {
        binarySearchService.performFind(searchParameters, searchStatusListener);
        findComboBoxEditorComponent.setRunningUpdate(true);
        ((SearchHistoryModel) findComboBox.getModel()).addSearchCondition(searchParameters.getCondition());
        findComboBoxEditorComponent.setRunningUpdate(false);
    }

    public void performReplace() {
        replaceParameters.setCondition(replaceComboBoxEditorComponent.getItem());
        binarySearchService.performReplace(searchParameters, replaceParameters);
    }

    public void performReplaceAll() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void dataChanged() {
        binarySearchService.clearMatches();
        performSearch(500);
    }

    public void closePanel() {
        if (closePanelListener != null) {
            clearSearch();
            closePanelListener.panelClosed();
        }
    }

    @Nullable
    public ClosePanelListener getClosePanelListener() {
        return closePanelListener;
    }

    public void setClosePanelListener(ClosePanelListener closePanelListener) {
        this.closePanelListener = closePanelListener;
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        this.codeAreaPopupMenuHandler = codeAreaPopupMenuHandler;
        findComboBoxEditorComponent.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler, "");
    }

    /**
     * Listener for panel closing.
     */
    public interface ClosePanelListener {

        void panelClosed();
    }
}
