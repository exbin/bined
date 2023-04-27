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
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxEditor;
import org.exbin.bined.ScrollBarVisibility;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.extended.theme.ExtendedBackgroundPaintMode;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;

/**
 * Find text/hexadecimal data panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindBinaryPanel1 extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(FindBinaryPanel1.class);

    private final ExtCodeArea findCodeArea = new ExtCodeArea();
    private BinarySearchComboBoxPanel findComboBoxEditorComponent;
    private ComboBoxEditor findComboBoxEditor;

    private final ExtCodeArea replaceCodeArea = new ExtCodeArea();
    private BinarySearchComboBoxPanel replaceComboBoxEditorComponent;
    private ComboBoxEditor replaceComboBoxEditor;

    private MultilineEditorListener multilineEditorListener = null;

    public FindBinaryPanel1() {
        initComponents();
        init();
    }

    private void init() {
        {
            ExtendedCodeAreaLayoutProfile layoutProfile = Objects.requireNonNull(findCodeArea.getLayoutProfile());
            layoutProfile.setShowHeader(false);
            layoutProfile.setShowRowPosition(false);
            findCodeArea.setLayoutProfile(layoutProfile);
        }
        findCodeArea.setRowWrapping(RowWrappingMode.WRAPPING);
        findCodeArea.setWrappingBytesGroupSize(0);
        {
            ExtendedCodeAreaThemeProfile themeProfile = findCodeArea.getThemeProfile();
            themeProfile.setBackgroundPaintMode(ExtendedBackgroundPaintMode.PLAIN);
            findCodeArea.setThemeProfile(themeProfile);
        }
        findCodeArea.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        findCodeArea.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        findCodeArea.setContentData(new ByteArrayEditableData());

        findComboBoxEditorComponent = new BinarySearchComboBoxPanel();
//        findComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
//            private final JPanel emptyPanel = new JPanel();
//            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
//
//            @Override
//            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
//                if (value == null) {
//                    return emptyPanel;
//                }
//
//                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
//                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
//                } else {
//                    findCodeArea.setContentData(value.getBinaryData());
//                    findCodeArea.setPreferredSize(new Dimension(200, 20));
//                    Color backgroundColor;
//                    if (isSelected) {
//                        backgroundColor = list.getSelectionBackground();
//                    } else {
//                        backgroundColor = list.getBackground();
//                    }
//// TODO                    ColorsGroup mainColors = new ColorsGroup(findHexadecimalRenderer.getMainColors());
////                    mainColors.setBothBackgroundColors(backgroundColor);
////                    findHexadecimalRenderer.setMainColors(mainColors);
//                    return findCodeArea;
//                }
//            }
//        });
        findComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return findComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                findComboBoxEditorComponent.setItem((SearchCondition) item);
                updateFindStatus();
            }

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
//        findComboBox.setEditor(findComboBoxEditor);
//        findComboBox.setModel(new SearchHistoryModel(searchHistory));

        {
            ExtendedCodeAreaLayoutProfile layoutProfile = Objects.requireNonNull(replaceCodeArea.getLayoutProfile());
            layoutProfile.setShowHeader(false);
            layoutProfile.setShowRowPosition(false);
            replaceCodeArea.setLayoutProfile(layoutProfile);
        }
        replaceCodeArea.setRowWrapping(RowWrappingMode.WRAPPING);
        replaceCodeArea.setWrappingBytesGroupSize(0);
        {
            ExtendedCodeAreaThemeProfile themeProfile = replaceCodeArea.getThemeProfile();
            themeProfile.setBackgroundPaintMode(ExtendedBackgroundPaintMode.PLAIN);
            replaceCodeArea.setThemeProfile(themeProfile);
        }
        replaceCodeArea.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        replaceCodeArea.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        replaceCodeArea.setContentData(new ByteArrayEditableData());

        replaceComboBoxEditorComponent = new BinarySearchComboBoxPanel();
//        replaceComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
//            private final JPanel emptyPanel = new JPanel();
//            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
//
//            @Override
//            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
//                if (value == null) {
//                    return emptyPanel;
//                }
//
//                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
//                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
//                } else {
//                    replaceCodeArea.setContentData(value.getBinaryData());
//                    replaceCodeArea.setPreferredSize(new Dimension(200, 20));
//                    Color backgroundColor;
//                    if (isSelected) {
//                        backgroundColor = list.getSelectionBackground();
//                    } else {
//                        backgroundColor = list.getBackground();
//                    }
//// TODO                    ColorsGroup mainColors = new ColorsGroup(replaceHexadecimalRenderer.getMainColors());
////                    mainColors.setBothBackgroundColors(backgroundColor);
////                    replaceHexadecimalRenderer.setMainColors(mainColors);
//                    return replaceCodeArea;
//                }
//            }
//        });
        replaceComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return replaceComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                replaceComboBoxEditorComponent.setItem((SearchCondition) item);
                updateReplaceStatus();
            }

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
//        replaceComboBox.setEditor(replaceComboBoxEditor);
//        replaceComboBox.setModel(new SearchHistoryModel(replaceHistory));
    }

    public void setSelected() {
//        findComboBox.requestFocusInWindow();
//        findComboBox.getEditor().selectAll();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findButtonGroup = new javax.swing.ButtonGroup();
        replaceButtonGroup = new javax.swing.ButtonGroup();
        findPanel = new javax.swing.JPanel();
        findTextRadioButton = new javax.swing.JRadioButton();
        findTextTextField = new javax.swing.JTextField();
        findBinaryRadioButton = new javax.swing.JRadioButton();
        findBinaryLabel = new javax.swing.JLabel();
        findBinaryButton = new javax.swing.JButton();
        searchFromCursorCheckBox = new javax.swing.JCheckBox();
        matchCaseCheckBox = new javax.swing.JCheckBox();
        multipleMatchesCheckBox = new javax.swing.JCheckBox();
        replacePanel = new javax.swing.JPanel();
        performReplaceCheckBox = new javax.swing.JCheckBox();
        replaceLabel = new javax.swing.JLabel();
        replaceMultilineButton = new javax.swing.JButton();
        replaceAllMatchesCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        findPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Find"));

        findButtonGroup.add(findTextRadioButton);
        findTextRadioButton.setSelected(true);
        findTextRadioButton.setText("Text");
        findTextRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findTextRadioButtonActionPerformed(evt);
            }
        });

        findButtonGroup.add(findBinaryRadioButton);
        findBinaryRadioButton.setText("Binary");
        findBinaryRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findBinaryRadioButtonActionPerformed(evt);
            }
        });

        findBinaryLabel.setText("(no data)");

        findBinaryButton.setText("Edit...");
        findBinaryButton.setToolTipText("Edit as multiline");
        findBinaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findBinaryButtonActionPerformed(evt);
            }
        });

        searchFromCursorCheckBox.setSelected(true);
        searchFromCursorCheckBox.setText("Search from cursor");

        matchCaseCheckBox.setText("Match case");

        multipleMatchesCheckBox.setSelected(true);
        multipleMatchesCheckBox.setText("Show multiple matches");

        javax.swing.GroupLayout findPanelLayout = new javax.swing.GroupLayout(findPanel);
        findPanel.setLayout(findPanelLayout);
        findPanelLayout.setHorizontalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(matchCaseCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(searchFromCursorCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(multipleMatchesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(findTextTextField)
                    .addGroup(findPanelLayout.createSequentialGroup()
                        .addComponent(findBinaryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(findBinaryButton))
                    .addComponent(findBinaryRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(findTextRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        findPanelLayout.setVerticalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, findPanelLayout.createSequentialGroup()
                .addComponent(findTextRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findTextTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findBinaryRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(findBinaryLabel)
                    .addComponent(findBinaryButton))
                .addGap(18, 18, 18)
                .addComponent(searchFromCursorCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(matchCaseCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multipleMatchesCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(findPanel, java.awt.BorderLayout.PAGE_START);

        replacePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Replace"));

        performReplaceCheckBox.setText("Perform replace on match");
        performReplaceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performReplaceCheckBoxActionPerformed(evt);
            }
        });

        replaceLabel.setText("Text to replace");
        replaceLabel.setEnabled(false);

        replaceMultilineButton.setText("...");
        replaceMultilineButton.setToolTipText("Edit as multiline");
        replaceMultilineButton.setEnabled(false);
        replaceMultilineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceMultilineButtonActionPerformed(evt);
            }
        });

        replaceAllMatchesCheckBox.setText("Replace all matches");
        replaceAllMatchesCheckBox.setEnabled(false);

        javax.swing.GroupLayout replacePanelLayout = new javax.swing.GroupLayout(replacePanel);
        replacePanel.setLayout(replacePanelLayout);
        replacePanelLayout.setHorizontalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(performReplaceCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE)
                    .addGroup(replacePanelLayout.createSequentialGroup()
                        .addComponent(replaceLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(replacePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(replaceMultilineButton))
                    .addComponent(replaceAllMatchesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        replacePanelLayout.setVerticalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addComponent(performReplaceCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceMultilineButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceAllMatchesCheckBox)
                .addContainerGap(263, Short.MAX_VALUE))
        );

        add(replacePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void findBinaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findBinaryButtonActionPerformed
        if (multilineEditorListener != null) {
            SearchCondition condition = multilineEditorListener.multilineEdit((SearchCondition) findComboBoxEditor.getItem());
            if (condition != null) {
                findComboBoxEditorComponent.setItem(condition);
            }
        }
    }//GEN-LAST:event_findBinaryButtonActionPerformed

    private void performReplaceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performReplaceCheckBoxActionPerformed
        updateReplaceEnablement();
    }//GEN-LAST:event_performReplaceCheckBoxActionPerformed

    private void replaceMultilineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceMultilineButtonActionPerformed
        if (multilineEditorListener != null) {
            SearchCondition condition = multilineEditorListener.multilineEdit((SearchCondition) replaceComboBoxEditor.getItem());
            if (condition != null) {
                replaceComboBoxEditorComponent.setItem(condition);
            }
        }
    }//GEN-LAST:event_replaceMultilineButtonActionPerformed

    private void findTextRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTextRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_findTextRadioButtonActionPerformed

    private void findBinaryRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findBinaryRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_findBinaryRadioButtonActionPerformed

    private void updateFindStatus() {
//        SearchCondition condition = (SearchCondition) findComboBoxEditor.getItem();
//        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
//            searchTypeButton.setText("T");
//            matchCaseCheckBox.setEnabled(true);
//        } else {
//            searchTypeButton.setText("B");
//            matchCaseCheckBox.setEnabled(false);
//        }
    }

    private void updateReplaceStatus() {
//        SearchCondition condition = (SearchCondition) replaceComboBoxEditor.getItem();
//        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
//            replaceTypeButton.setText("T");
//        } else {
//            replaceTypeButton.setText("B");
//        }
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new FindBinaryPanel1());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton findBinaryButton;
    private javax.swing.JLabel findBinaryLabel;
    private javax.swing.JRadioButton findBinaryRadioButton;
    private javax.swing.ButtonGroup findButtonGroup;
    private javax.swing.JPanel findPanel;
    private javax.swing.JRadioButton findTextRadioButton;
    private javax.swing.JTextField findTextTextField;
    private javax.swing.JCheckBox matchCaseCheckBox;
    private javax.swing.JCheckBox multipleMatchesCheckBox;
    private javax.swing.JCheckBox performReplaceCheckBox;
    private javax.swing.JCheckBox replaceAllMatchesCheckBox;
    private javax.swing.ButtonGroup replaceButtonGroup;
    private javax.swing.JLabel replaceLabel;
    private javax.swing.JButton replaceMultilineButton;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JCheckBox searchFromCursorCheckBox;
    // End of variables declaration//GEN-END:variables

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public String getFindText() {
//        return (String) findComboBox.getEditor().getItem();
        return "";
    }

    public boolean getShallReplace() {
        return performReplaceCheckBox.isSelected();
    }

    public SearchParameters getSearchParameters() {
        SearchParameters result = new SearchParameters();
//        result.setCondition((SearchCondition) findComboBox.getEditor().getItem());
//        result.setSearchFromCursor(searchFromCursorCheckBox.isSelected());
//        result.setMatchCase(matchCaseCheckBox.isSelected());
//        result.setMultipleMatches(multipleMatchesCheckBox.isSelected());
        return result;
    }

    public void setSearchParameters(SearchParameters parameters) {
        searchFromCursorCheckBox.setSelected(parameters.isSearchFromCursor());
        matchCaseCheckBox.setSelected(parameters.isMatchCase());
        multipleMatchesCheckBox.setSelected(parameters.isMultipleMatches());
        findComboBoxEditorComponent.setItem(parameters.getCondition());
//        findComboBox.setEditor(findComboBoxEditor);
//        findComboBox.repaint();
        updateFindStatus();
    }

    public ReplaceParameters getReplaceParameters() {
        ReplaceParameters result = new ReplaceParameters();
//        result.setCondition((SearchCondition) replaceComboBox.getEditor().getItem());
//        result.setPerformReplace(performReplaceCheckBox.isSelected());
//        result.setReplaceAll(replaceAllMatchesCheckBox.isSelected());
        return result;
    }

    public void setReplaceParameters(ReplaceParameters parameters) {
        performReplaceCheckBox.setSelected(parameters.isPerformReplace());
        replaceAllMatchesCheckBox.setSelected(parameters.isReplaceAll());
        replaceComboBoxEditorComponent.setItem(parameters.getCondition());
//        replaceComboBox.setEditor(replaceComboBoxEditor);
//        replaceComboBox.repaint();
        updateReplaceStatus();
        updateReplaceEnablement();
    }

    private void updateReplaceEnablement() {
        boolean replaceEnabled = performReplaceCheckBox.isSelected();
//        replaceTypeButton.setEnabled(replaceEnabled);
//        replaceComboBox.setEnabled(replaceEnabled);
        replaceMultilineButton.setEnabled(replaceEnabled);
        replaceAllMatchesCheckBox.setEnabled(replaceEnabled);
        replaceLabel.setEnabled(replaceEnabled);
    }

    public void setMultilineEditorListener(MultilineEditorListener multilineEditorListener) {
        this.multilineEditorListener = multilineEditorListener;
    }

    @ParametersAreNonnullByDefault
    public static interface MultilineEditorListener {

        @Nullable
        SearchCondition multilineEdit(SearchCondition condition);
    }
}
