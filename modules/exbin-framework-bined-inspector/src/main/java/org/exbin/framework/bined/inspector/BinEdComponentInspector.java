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
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JScrollPane;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.gui.InspectorPanel;
import org.exbin.framework.bined.inspector.options.DataInspectorOptions;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.utils.UiUtils;

/**
 * BinEd component data inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentInspector implements BinEdComponentPanel.BinEdComponentExtension {

    private BinEdComponentPanel componentPanel;
    private InspectorPanel inspectorPanel;
    private boolean parsingPanelVisible = false;

    private JScrollPane valuesPanelScrollPane;
    private BasicValuesPositionColorModifier basicValuesColorModifier;
    private ComponentsProvider componentsProvider = null;

    public BinEdComponentInspector() {
    }

    public BinEdComponentInspector(@Nullable ComponentsProvider componentsProvider) {
        this.componentsProvider = componentsProvider;
    }

    @Override
    public void onCreate(BinEdComponentPanel componentPanel) {
        this.componentPanel = componentPanel;

        UiUtils.runInUiThread(() -> {
            SectCodeArea codeArea = componentPanel.getCodeArea();
            this.inspectorPanel = componentsProvider == null ? new InspectorPanel() : componentsProvider.createValuesPanel();
            inspectorPanel.setCodeArea(codeArea, null);
            if (basicValuesColorModifier != null) {
                // TODO inspectorPanel.registerFocusPainter(basicValuesColorModifier);
            }

            valuesPanelScrollPane = componentsProvider == null ? new JScrollPane() : componentsProvider.createScrollPane();
            valuesPanelScrollPane.setViewportView(inspectorPanel);
            valuesPanelScrollPane.setBorder(null);
        });
        setShowParsingPanel(true);
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
        boolean useDefaultFont = dataInspectorPreferences.isUseDefaultFont();
        if (useDefaultFont) {
            setInputFieldsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        } else {
            Map<TextAttribute, Object> fontAttributes = dataInspectorPreferences.getFontAttributes();
            setInputFieldsFont(new Font(fontAttributes));
        }
    }

    @Override
    public void onClose() {
    }

    public void setShowParsingPanel(boolean show) {
        if (parsingPanelVisible != show) {
            if (show) {
                componentPanel.add(valuesPanelScrollPane, BorderLayout.EAST);
                componentPanel.revalidate();
                parsingPanelVisible = true;
                inspectorPanel.enableUpdate();
            } else {
                inspectorPanel.disableUpdate();
                componentPanel.remove(valuesPanelScrollPane);
                componentPanel.revalidate();
                parsingPanelVisible = false;
            }
        }
    }

    @Nonnull
    public Font getInputFieldsFont() {
        return Font.getFont(Font.DIALOG);
        // TODO return inspectorPanel.getInputFieldsFont();
    }

    public void setInputFieldsFont(Font font) {
        // TODO inspectorPanel.setInputFieldsFont(font);
    }

    public boolean isShowParsingPanel() {
        return parsingPanelVisible;
    }

    public void setBasicValuesColorModifier(BasicValuesPositionColorModifier basicValuesColorModifier) {
        this.basicValuesColorModifier = basicValuesColorModifier;
    }

    public interface ComponentsProvider {

        @Nonnull
        InspectorPanel createValuesPanel();

        @Nonnull
        JScrollPane createScrollPane();
    }
}
