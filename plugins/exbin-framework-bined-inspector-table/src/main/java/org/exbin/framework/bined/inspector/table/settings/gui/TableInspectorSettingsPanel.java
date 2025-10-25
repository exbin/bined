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
package org.exbin.framework.bined.inspector.table.settings.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.table.api.ValueRowType;
import org.exbin.framework.bined.inspector.table.settings.TableInspectorOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Table inspector settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TableInspectorSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private SettingsModifiedListener settingsModifiedListener;
    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(TableInspectorSettingsPanel.class);

    private Controller controller = null;
    private Map<String, ValueRowType> valueRowTypes;

    public TableInspectorSettingsPanel() {
        initComponents();
        init();
    }

    private void init() {
        rowsList.setModel(new DefaultListModel<>());
        rowsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
                }

                ValueRowType valueRowType = valueRowTypes.get(value);
                return super.getListCellRendererComponent(list, valueRowType.getName(), index, isSelected, cellHasFocus);
            }
        });
        rowsList.addListSelectionListener((ListSelectionEvent e) -> updateStates());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider) {
        TableInspectorOptions options = settingsOptionsProvider.getSettingsOptions(TableInspectorOptions.class);
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int rowsCount = options.getRowsCount();
        model.setSize(rowsCount);
        for (int i = 0; i < rowsCount; i++) {
            model.setElementAt(options.getRowType(i), i);
        }
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider) {
        TableInspectorOptions options = settingsOptionsProvider.getSettingsOptions(TableInspectorOptions.class);
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int rowsCount = model.getSize();
        options.setRowsCount(rowsCount);
        for (int i = 0; i < rowsCount; i++) {
            options.setRowType(i, model.getElementAt(i));
        }
    }
    
    public void addItems(List<String> items) {
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int selectedIndex = rowsList.getSelectedIndex();

        for (String item : items) {
            if (selectedIndex >= 0) {
                rowsList.clearSelection();
                model.add(selectedIndex, item);
            } else {
                model.addElement(item);
            }
            notifyModified();
        }
    }

    private void updateStates() {
        int[] selectedIndices = rowsList.getSelectedIndices();
        boolean hasSelection = selectedIndices.length > 0;
        boolean hasAnyItems = rowsList.getModel().getSize() == 0;
        selectAllButton.setEnabled(hasAnyItems);
        removeButton.setEnabled(hasSelection);

        if (hasSelection) {
            upButton.setEnabled(rowsList.getMaxSelectionIndex() >= selectedIndices.length);
            downButton.setEnabled(rowsList.getMinSelectionIndex() + selectedIndices.length < rowsList.getModel().getSize());
        } else {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
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

        rowsListScrollPane = new javax.swing.JScrollPane();
        rowsList = new javax.swing.JList<>();
        rowsControlPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setName("Form"); // NOI18N

        rowsListScrollPane.setName("rowsListScrollPane"); // NOI18N

        rowsList.setName("rowsList"); // NOI18N
        rowsListScrollPane.setViewportView(rowsList);

        rowsControlPanel.setName("rowsControlPanel"); // NOI18N

        addButton.setText(resourceBundle.getString("addButton.text")); // NOI18N
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        upButton.setText(resourceBundle.getString("upButton.text")); // NOI18N
        upButton.setEnabled(false);
        upButton.setName("upButton"); // NOI18N
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText(resourceBundle.getString("downButton.text")); // NOI18N
        downButton.setEnabled(false);
        downButton.setName("downButton"); // NOI18N
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        selectAllButton.setText(resourceBundle.getString("selectAllButton.text")); // NOI18N
        selectAllButton.setEnabled(false);
        selectAllButton.setName("selectAllButton"); // NOI18N
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        removeButton.setText(resourceBundle.getString("removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.setName("removeButton"); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rowsControlPanelLayout = new javax.swing.GroupLayout(rowsControlPanel);
        rowsControlPanel.setLayout(rowsControlPanelLayout);
        rowsControlPanelLayout.setHorizontalGroup(
            rowsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rowsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rowsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectAllButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        rowsControlPanelLayout.setVerticalGroup(
            rowsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rowsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectAllButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rowsListScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowsControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rowsControlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rowsListScrollPane)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        if (controller != null) {
            controller.performAddItem();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();
        int last = 0;
        for (int i = 0; i < indices.length; i++) {
            int next = indices[i];
            if (last != next) {
                String item = model.getElementAt(next);
                model.add(next - 1, item);
                rowsList.getSelectionModel().addSelectionInterval(next - 1, next - 1);
                model.remove(next + 1);
            } else {
                last++;
            }
        }
        notifyModified();
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();
        int last = model.getSize() - 1;
        for (int i = indices.length; i > 0; i--) {
            int next = indices[i - 1];
            if (last != next) {
                String item = model.getElementAt(next);
                model.add(next + 2, item);
                rowsList.getSelectionModel().addSelectionInterval(next + 2, next + 2);
                model.remove(next);
            } else {
                last--;
            }
        }
        notifyModified();
    }//GEN-LAST:event_downButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        if (rowsList.getSelectedIndices().length < rowsList.getModel().getSize()) {
            rowsList.setSelectionInterval(0, rowsList.getModel().getSize() - 1);
        } else {
            rowsList.clearSelection();
        }
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();

        if (indices.length > 0) {
            Arrays.sort(indices);
            for (int i = indices.length - 1; i >= 0; i--) {
                model.remove(indices[i]);
            }
        }

        rowsList.clearSelection();
        notifyModified();
    }//GEN-LAST:event_removeButtonActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new TableInspectorSettingsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel rowsControlPanel;
    private javax.swing.JList<String> rowsList;
    private javax.swing.JScrollPane rowsListScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.wasModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        settingsModifiedListener = listener;
    }

    public void setRowTypes(Map<String, ValueRowType> valueRowTypes) {
        this.valueRowTypes = valueRowTypes;
    }

    @Nonnull
    public List<String> getItems() {
        List<String> typeIds = new ArrayList<>();
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            typeIds.add(model.getElementAt(i));
        }
        return typeIds;
    }

    public void setItems(List<String> typeIds) {
        DefaultListModel<String> model = (DefaultListModel<String>) rowsList.getModel();
        model.removeAllElements();
        for (String typeId : typeIds) {
            model.addElement(typeId);
        }
    }

    public static interface Controller {

        void performAddItem();
    }
}
