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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JScrollPane;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.table.BinedInspectorTableModule;
import org.exbin.framework.bined.inspector.table.api.ValueRowItem;
import org.exbin.framework.bined.inspector.table.api.ValueRowType;
import org.exbin.framework.bined.inspector.table.settings.gui.TableInspectorSettingsPanel;
import org.exbin.framework.bined.inspector.table.settings.gui.ValueRowTypePanel;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Values table side inspector panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TableInspectorPanel extends javax.swing.JPanel {

    protected JScrollPane scrollPane;
    protected ValuesTablePanel component;

    public TableInspectorPanel() {
        initComponents();
        init();
    }

    private void init() {
        component = new ValuesTablePanel();

        BinedInspectorTableModule binedInspectorTableModule = App.getModule(BinedInspectorTableModule.class);
        Map<String, ValueRowType> valueRowTypes = binedInspectorTableModule.getValueRowTypes();

        List<ValueRowItem> rowItems = new ArrayList<>();
        for (ValueRowType valueRowType : valueRowTypes.values()) {
            rowItems.add(valueRowType.createRowItem());
        }

        component.setValueRows(rowItems);
        component.setController(new ValuesTablePanel.Controller() {
            @Override
            public void performSettings() {
                BinedInspectorTableModule binedInspectorTableModule = App.getModule(BinedInspectorTableModule.class);
                Map<String, ValueRowType> valueRowTypes = binedInspectorTableModule.getValueRowTypes();

                OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                TableInspectorSettingsPanel settingsPanel = new TableInspectorSettingsPanel();
                settingsPanel.setRowTypes(valueRowTypes);
                List<ValueRowItem> valueRows = component.getValueRows();
                {
                    List<String> typeIds = new ArrayList<>();
                    for (ValueRowItem valueRow : valueRows) {
                        typeIds.add(valueRow.getTypeId());
                    }
                    settingsPanel.setItems(typeIds);
                }
                settingsPanel.setController(new TableInspectorSettingsPanel.Controller() {
                    @Override
                    public void performAddItem() {
                        ValueRowTypePanel valueRowTypePanel = new ValueRowTypePanel();
                        valueRowTypePanel.setRowTypes(valueRowTypes);
                        List<String> typeIds = new ArrayList<>();
                        for (ValueRowType valueRowType : valueRowTypes.values()) {
                            typeIds.add(valueRowType.getId());
                        }
                        valueRowTypePanel.setItems(typeIds);
                        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                        DefaultControlPanel controlPanel = new DefaultControlPanel();

                        WindowHandler dialog = windowModule.createDialog(valueRowTypePanel, controlPanel);
                        controlPanel.setController((actionType) -> {
                            switch (actionType) {
                                case OK:
                                    List<String> resultTypeIds = valueRowTypePanel.getSelectedItems();
                                    settingsPanel.addItems(resultTypeIds);
                                    break;
                                case CANCEL:
                                    break;
                                default:
                                    throw new AssertionError();
                            }
                            dialog.close();
                        });
                        windowModule.setWindowTitle(dialog, valueRowTypePanel.getResourceBundle());
                        dialog.showCentered(component);
                    }
                });
                WindowHandler dialog = windowModule.createDialog(settingsPanel, controlPanel);
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case OK:
                            List<String> resultTypeIds = settingsPanel.getItems();
                            List<ValueRowItem> rowItems = new ArrayList<>();
                            for (String typeId : resultTypeIds) {
                                ValueRowType valueRowType = valueRowTypes.get(typeId);
                                rowItems.add(valueRowType.createRowItem());
                            }
                            component.setValueRows(rowItems);
                            break;
                        case CANCEL:
                            break;
                        default:
                            throw new AssertionError();
                    }
                    dialog.close();
                });
                windowModule.setWindowTitle(dialog, settingsPanel.getResourceBundle());
                dialog.showCentered(component);
            }
        });
        component.setPreferredSize(new Dimension());
        scrollPane = new JScrollPane(component);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setCodeArea(CodeAreaCore codeArea) {
        component.setCodeArea(codeArea);
    }

    public void requestUpdate() {
        component.notifyChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new TableInspectorPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
