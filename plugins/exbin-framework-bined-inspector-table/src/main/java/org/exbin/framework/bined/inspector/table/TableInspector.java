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
package org.exbin.framework.bined.inspector.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.CodeAreaCaretListener;
import org.exbin.bined.DataChangedListener;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.BinEdInspector;
import org.exbin.framework.bined.inspector.table.api.ValueRowItem;
import org.exbin.framework.bined.inspector.table.api.ValueRowType;
import org.exbin.framework.bined.inspector.table.gui.TableInspectorPanel;
import org.exbin.framework.bined.inspector.table.gui.ValuesTablePanel;
import org.exbin.framework.bined.inspector.table.settings.gui.TableInspectorSettingsPanel;
import org.exbin.framework.bined.inspector.table.settings.gui.ValueRowTypePanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Table inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TableInspector implements BinEdInspector {

    protected TableInspectorPanel component;
    protected CodeAreaCore codeArea;

    protected DataChangedListener dataChangedListener;
    protected CodeAreaCaretListener caretMovedListener;

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (component == null) {
            component = new TableInspectorPanel();
            component.setTableController(new ValuesTablePanel.Controller() {
                @Override
                public void performSettings() {
                    BinedInspectorTableModule binedInspectorTableModule = App.getModule(BinedInspectorTableModule.class);
                    Map<String, ValueRowType> valueRowTypes = binedInspectorTableModule.getValueRowTypes();

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
            dataChangedListener = component::requestUpdate;
            caretMovedListener = (caretPosition) -> {
                component.requestUpdate();
            };
        }
        return component;
    }

    @Override
    public void setCodeArea(CodeAreaCore codeArea, BinaryDataUndoRedo undoRedo) {
        this.codeArea = codeArea;
        component.setCodeArea(codeArea);
    }

    @Override
    public void activateSync() {
        codeArea.addDataChangedListener(dataChangedListener);
        ((CaretCapable) codeArea).addCaretMovedListener(caretMovedListener);
    }

    @Override
    public void deactivateSync() {
        codeArea.removeDataChangedListener(dataChangedListener);
        ((CaretCapable) codeArea).removeCaretMovedListener(caretMovedListener);
    }
}
