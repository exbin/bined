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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JScrollPane;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.gui.InspectorPanel;
import org.exbin.framework.bined.inspector.settings.DataInspectorOptions;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.utils.UiUtils;

/**
 * BinEd component data inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdInspectorComponentExtension implements BinEdComponentPanel.BinEdComponentExtension {

    private BinEdComponentPanel componentPanel;
    private InspectorPanel inspectorPanel;
    private boolean parsingPanelVisible = false;

    private JScrollPane parsingPanelScrollPane;
    private ComponentsProvider componentsProvider = null;

    public BinEdInspectorComponentExtension() {
    }

    public BinEdInspectorComponentExtension(@Nullable ComponentsProvider componentsProvider) {
        this.componentsProvider = componentsProvider;
    }

    @Override
    public void onCreate(BinEdComponentPanel componentPanel) {
        this.componentPanel = componentPanel;

        UiUtils.runInUiThread(() -> {
            SectCodeArea codeArea = componentPanel.getCodeArea();
            this.inspectorPanel = componentsProvider == null ? new InspectorPanel() : componentsProvider.createInspectorPanel();
            inspectorPanel.setCodeArea(codeArea, null);

            parsingPanelScrollPane = componentsProvider == null ? new JScrollPane() : componentsProvider.createScrollPane();
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
            inspectorPanel.setCodeArea(componentPanel.getCodeArea(), componentPanel.getUndoRedo().orElse(null));
        }
    }

    @Override
    public void onInitFromOptions(OptionsStorage options) {
        DataInspectorOptions dataInspectorPreferences = new DataInspectorOptions(options);
        setShowParsingPanel(dataInspectorPreferences.isShowParsingPanel());
        inspectorPanel.onInitFromOptions(options);
    }

    @Override
    public void onClose() {
    }

    public void setShowParsingPanel(boolean show) {
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

    public interface ComponentsProvider {

        @Nonnull
        InspectorPanel createInspectorPanel();

        @Nonnull
        JScrollPane createScrollPane();
    }
}
