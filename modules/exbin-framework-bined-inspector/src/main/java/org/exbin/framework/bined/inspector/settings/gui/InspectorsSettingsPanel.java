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
package org.exbin.framework.bined.inspector.settings.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.settings.DataInspectorOptions;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Inspectors settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InspectorsSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    protected SettingsModifiedListener settingsModifiedListener;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(InspectorsSettingsPanel.class);
    protected boolean showMode = true;

    public InspectorsSettingsPanel() {
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

                InspectorRecord record = (InspectorRecord) value;
                DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, record.getName(), index, isSelected, cellHasFocus);
                if (!record.isShown()) {
                    renderer.setForeground(Color.GRAY);
                }
                return renderer;
            }
        });
        rowsList.addListSelectionListener((ListSelectionEvent e) -> updateStates());
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider) {
        DataInspectorOptions options = settingsOptionsProvider.getSettingsOptions(DataInspectorOptions.class);
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        List<InspectorRecord> records = new ArrayList<>();
        List<InspectorRecord> tailRecords = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            InspectorRecord record = model.getElementAt(i);
            record.setShown(!options.isInspectorHidden(record.getId()));
            int position = options.getInspectorPosition(record.getId());
            if (position >= 0) {
                int size = records.size();
                if (position >= size) {
                    for (int pos = size; pos <= position; pos++) {
                        records.add(null);
                    }
                }
                records.set(position, record);
            } else {
                tailRecords.add(record);
            }
        }
        
        int pos = 0;
        for (int i = 0; i < records.size(); i++) {
            InspectorRecord record = records.get(i);
            if (record != null) {
                model.setElementAt(record, pos);
                pos++;
            }
        }
        for (int i = 0; i < tailRecords.size(); i++) {
            model.setElementAt(tailRecords.get(i), pos);
            pos++;
        }
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider) {
        DataInspectorOptions options = settingsOptionsProvider.getSettingsOptions(DataInspectorOptions.class);
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            InspectorRecord record = model.getElementAt(i);
            options.setInspectorHidden(record.getId(), !record.isShown());
            options.setInspectorPosition(record.getId(), i);
        }
    }
    
    private void updateStates() {
        int[] selectedIndices = rowsList.getSelectedIndices();
        boolean hasSelection = selectedIndices.length > 0;
        boolean hasAnyItems = rowsList.getModel().getSize() > 0;
        selectAllButton.setEnabled(hasAnyItems);

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
        showButton = new javax.swing.JButton();
        hideButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();

        setName("Form"); // NOI18N

        rowsListScrollPane.setName("rowsListScrollPane"); // NOI18N

        rowsList.setName("rowsList"); // NOI18N
        rowsListScrollPane.setViewportView(rowsList);

        rowsControlPanel.setName("rowsControlPanel"); // NOI18N

        showButton.setText(resourceBundle.getString("showButton.text")); // NOI18N
        showButton.setName("showButton"); // NOI18N
        showButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonActionPerformed(evt);
            }
        });

        hideButton.setText(resourceBundle.getString("hideButton.text")); // NOI18N
        hideButton.setName("hideButton"); // NOI18N
        hideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideButtonActionPerformed(evt);
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

        javax.swing.GroupLayout rowsControlPanelLayout = new javax.swing.GroupLayout(rowsControlPanel);
        rowsControlPanel.setLayout(rowsControlPanelLayout);
        rowsControlPanelLayout.setHorizontalGroup(
            rowsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rowsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rowsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectAllButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hideButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        rowsControlPanelLayout.setVerticalGroup(
            rowsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rowsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hideButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectAllButton)
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

    private void showButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showButtonActionPerformed
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            int next = indices[i];
            InspectorRecord item = model.getElementAt(next);
            item.setShown(true);
            model.set(next, item);
        }
        notifyModified();
    }//GEN-LAST:event_showButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();
        int last = 0;
        for (int i = 0; i < indices.length; i++) {
            int next = indices[i];
            if (last != next) {
                InspectorRecord item = model.getElementAt(next);
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
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();
        int last = model.getSize() - 1;
        for (int i = indices.length; i > 0; i--) {
            int next = indices[i - 1];
            if (last != next) {
                InspectorRecord item = model.getElementAt(next);
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

    private void hideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideButtonActionPerformed
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        int[] indices = rowsList.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
            int next = indices[i];
            InspectorRecord item = model.getElementAt(next);
            item.setShown(false);
            model.set(next, item);
        }
        notifyModified();
    }//GEN-LAST:event_hideButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downButton;
    private javax.swing.JButton hideButton;
    private javax.swing.JPanel rowsControlPanel;
    private javax.swing.JList<InspectorRecord> rowsList;
    private javax.swing.JScrollPane rowsListScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton showButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.notifyModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        settingsModifiedListener = listener;
    }

    @Nonnull
    public List<InspectorRecord> getItems() {
        List<InspectorRecord> records = new ArrayList<>();
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            records.add(model.getElementAt(i));
        }
        return records;
    }

    public void setItems(List<InspectorRecord> records) {
        DefaultListModel<InspectorRecord> model = (DefaultListModel<InspectorRecord>) rowsList.getModel();
        model.removeAllElements();
        for (InspectorRecord record : records) {
            model.addElement(record);
        }
    }
}
