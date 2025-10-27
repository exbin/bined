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
package org.exbin.framework.bined.inspector.table.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.table.api.ValueRowItem;
import org.exbin.framework.bined.objectdata.property.gui.PropertyTableCellEditor;
import org.exbin.framework.bined.objectdata.property.gui.PropertyTableCellRenderer;
import org.exbin.framework.bined.objectdata.property.gui.PropertyTableItem;
import org.exbin.framework.bined.objectdata.property.gui.PropertyTableModel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.WindowUtils;

/**
 * Panel for table with values for binary data inspection.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ValuesTablePanel extends javax.swing.JPanel {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ValuesTablePanel.class);

    private static final int DATA_LIMIT = 250;
    private final byte[] values = new byte[DATA_LIMIT];

    private final PropertyTableModel tableModel;
    private final PropertyTableCellRenderer valueCellRenderer;
    private final TableCellRenderer nameCellRenderer;
    private final PropertyTableCellEditor valueCellEditor;
    private Controller controller = null;

    private CodeAreaCore codeArea;

    public ValuesTablePanel() {
        tableModel = new PropertyTableModel();

        initComponents();

        TableColumnModel columns = valuesTable.getColumnModel();
        columns.getColumn(0).setPreferredWidth(80);
        columns.getColumn(1).setPreferredWidth(80);
        columns.getColumn(0).setWidth(80);
        columns.getColumn(1).setWidth(80);
        nameCellRenderer = new DefaultTableCellRenderer() {
            @Nonnull
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JComponent component = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                PropertyTableItem tableItem = ((PropertyTableModel) table.getModel()).getRow(row);
                component.setToolTipText("(" + tableItem.getTypeName() + ") " + tableItem.getValueName());
                return component;
            }
        };
        columns.getColumn(0).setCellRenderer(nameCellRenderer);
        valueCellRenderer = new PropertyTableCellRenderer();
        columns.getColumn(1).setCellRenderer(valueCellRenderer);
        valueCellEditor = new PropertyTableCellEditor();
        columns.getColumn(1).setCellEditor(valueCellEditor);

        valuesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Nonnull
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (component instanceof JComponent) {
                    ((JComponent) component).setBorder(noFocusBorder);
                }

                return component;
            }
        });
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Nonnull
    public List<ValueRowItem> getValueRows() {
        List<ValueRowItem> rowItems = new ArrayList<>();
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            rowItems.add((ValueRowItem) tableModel.getRow(i));
        }
        return rowItems;
    }

    public void setValueRows(List<ValueRowItem> rowItems) {
        tableModel.removeAll();
        for (ValueRowItem rowItem : rowItems) {
            tableModel.addRow(rowItem);
        }
        notifyChanged();
    }

    public void setCodeArea(CodeAreaCore codeArea) {
        this.codeArea = codeArea;
        notifyChanged();
    }

    public void notifyChanged() {
        if (codeArea == null) {
            return;
        }

        BinaryData contentData = codeArea.getContentData();
        long dataSize = codeArea.getDataSize();
        long dataPosition = ((CaretCapable) codeArea).getDataPosition();
        long available = dataSize - dataPosition;

        if (valuesTable.isEditing()) {
            valuesTable.getCellEditor().cancelCellEditing();
        }

        int valuesAvailable = available > DATA_LIMIT ? DATA_LIMIT : (int) available;
        contentData.copyToArray(dataPosition, values, 0, valuesAvailable);
        List<PropertyTableItem> items = tableModel.getItems();
        for (PropertyTableItem item : items) {
            ((ValueRowItem) item).updateRow(values, valuesAvailable);
        }
        tableModel.fireTableRowsUpdated(0, tableModel.getRowCount() - 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainScrollPane = new javax.swing.JScrollPane();
        valuesTable = new javax.swing.JTable();
        configurePanel = new javax.swing.JPanel();
        configureButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        mainScrollPane.setName("mainScrollPane"); // NOI18N

        valuesTable.setModel(tableModel);
        valuesTable.setName("valuesTable"); // NOI18N
        valuesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mainScrollPane.setViewportView(valuesTable);

        add(mainScrollPane, java.awt.BorderLayout.CENTER);

        configurePanel.setName("configurePanel"); // NOI18N

        configureButton.setText(resourceBundle.getString("configureButton.text")); // NOI18N
        configureButton.setName("configureButton"); // NOI18N
        configureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configureButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout configurePanelLayout = new javax.swing.GroupLayout(configurePanel);
        configurePanel.setLayout(configurePanelLayout);
        configurePanelLayout.setHorizontalGroup(
            configurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, configurePanelLayout.createSequentialGroup()
                .addContainerGap(315, Short.MAX_VALUE)
                .addComponent(configureButton)
                .addContainerGap())
        );
        configurePanelLayout.setVerticalGroup(
            configurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(configurePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configureButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(configurePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void configureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configureButtonActionPerformed
        if (controller != null) {
            controller.performSettings();
        }
    }//GEN-LAST:event_configureButtonActionPerformed

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication.run(() -> {
            WindowUtils.invokeWindow(new ValuesTablePanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton configureButton;
    private javax.swing.JPanel configurePanel;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JTable valuesTable;
    // End of variables declaration//GEN-END:variables

    public static interface Controller {

        void performSettings();
    }
}
