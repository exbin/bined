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
package org.exbin.framework.bined.inspector;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JScrollPane;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdComponentExtension;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.gui.InspectorPanel;
import org.exbin.framework.bined.inspector.settings.gui.InspectorRecord;
import org.exbin.framework.bined.inspector.settings.gui.InspectorsSettingsPanel;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * BinEd component data inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdInspectorComponentExtension implements BinEdComponentExtension {

    private BinaryDataComponent dataComponent;
    private InspectorPanel inspectorPanel;
    private boolean parsingPanelVisible = false;

    private JScrollPane parsingPanelScrollPane;

    public BinEdInspectorComponentExtension() {
    }

    @Override
    public void onCreate(BinaryDataComponent dataComponent) {
        this.dataComponent = dataComponent;

        UiUtils.runInUiThread(() -> {
            SectCodeArea codeArea = (SectCodeArea) dataComponent.getCodeArea();
            this.inspectorPanel = new InspectorPanel();
            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            BinEdInspectorManager inspectorManager = binedInspectorModule.getBinEdInspectorManager();
            List<BinEdInspectorProvider> inspectorProviders = inspectorManager.getInspectorProviders();
            inspectorPanel.setInspectorProviders(inspectorProviders);
            
            inspectorPanel.setController(new InspectorPanel.Controller() {
                @Override
                public void invokeSettings() {
                    WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    InspectorsSettingsPanel settingsPanel = new InspectorsSettingsPanel();
                    
                    BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
                    BinEdInspectorManager inspectorManager = binedInspectorModule.getBinEdInspectorManager();
                    List<BinEdInspectorProvider> inspectorProviders = inspectorManager.getInspectorProviders();
                    List<InspectorRecord> inspectorRecords = new ArrayList<>();
                    for (BinEdInspectorProvider inspectorProvider : inspectorProviders) {
                        inspectorRecords.add(new InspectorRecord(inspectorProvider.getId(), inspectorProvider.getName()));
                    }
                    settingsPanel.setItems(inspectorRecords);

                    WindowHandler dialog = windowModule.createDialog(settingsPanel, controlPanel);
                    controlPanel.setController((actionType) -> {
                        switch (actionType) {
                            case OK:
                                inspectorPanel.setInspectorRecords(settingsPanel.getItems());
                                // TODO
                                break;

                            case CANCEL:
                                break;
                            default:
                                throw new AssertionError();
                        }
                        dialog.close();
                    });
                    windowModule.setWindowTitle(dialog, settingsPanel.getResourceBundle());
                    dialog.showCentered(inspectorPanel);
                }
            });
            inspectorPanel.setCodeArea(codeArea, null);

            parsingPanelScrollPane = new JScrollPane();
            parsingPanelScrollPane.setViewportView(inspectorPanel);
            parsingPanelScrollPane.setBorder(null);
            setShowParsingPanel(true);
        });
    }
    
    @Override
    public void onDataChange() {
    }

    @Override
    public void onUndoHandlerChange() {
        if (inspectorPanel != null) {
            inspectorPanel.setCodeArea((SectCodeArea) dataComponent.getCodeArea(), dataComponent.getUndoRedo().orElse(null));
        }
    }

    public void setShowParsingPanel(boolean show) {
        BinEdComponentPanel componentPanel = (BinEdComponentPanel) dataComponent.getComponent();
        if (parsingPanelVisible != show) {
            if (show) {
                componentPanel.add(parsingPanelScrollPane, BorderLayout.EAST);
                componentPanel.revalidate();
                parsingPanelVisible = true;
                inspectorPanel.activateSync();
            } else {
                inspectorPanel.deactivateSync();
                componentPanel.remove(parsingPanelScrollPane);
                componentPanel.revalidate();
                parsingPanelVisible = false;
            }
        }
    }

    public boolean isShowParsingPanel() {
        return parsingPanelVisible;
    }

    @Nullable
    public <T extends BinEdInspector> T getInspector(Class<T> clazz) {
        return inspectorPanel.getInspector(clazz);
    }
}
