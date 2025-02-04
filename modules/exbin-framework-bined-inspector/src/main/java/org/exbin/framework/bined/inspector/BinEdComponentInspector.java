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
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.bined.inspector.preferences.DataInspectorPreferences;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;

/**
 * BinEd component data inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentInspector implements BinEdComponentPanel.BinEdComponentExtension {

    private BinEdComponentPanel componentPanel;
    private BasicValuesPanel valuesPanel;
    private boolean parsingPanelVisible = false;

    private JScrollPane valuesPanelScrollPane;
    private BasicValuesPositionColorModifier basicValuesColorModifier;
    private ComponentsProvider componentsProvider = null;

    public BinEdComponentInspector() {
    }

    public BinEdComponentInspector(ComponentsProvider componentsProvider) {
        this.componentsProvider = componentsProvider;
    }

    @Override
    public void onCreate(BinEdComponentPanel componentPanel) {
        this.componentPanel = componentPanel;

        if (SwingUtilities.isEventDispatchThread()) {
            onCreateInt();
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    onCreateInt();
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(BinEdComponentInspector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setShowParsingPanel(true);
    }

    private void onCreateInt() {
        SectCodeArea codeArea = componentPanel.getCodeArea();
        this.valuesPanel = componentsProvider == null ? new BasicValuesPanel() : componentsProvider.createValuesPanel();
        valuesPanel.setCodeArea(codeArea, null);
        if (basicValuesColorModifier != null) {
            valuesPanel.registerFocusPainter(basicValuesColorModifier);
        }

        valuesPanelScrollPane = componentsProvider == null ? new JScrollPane() : componentsProvider.createScrollPane();
        valuesPanelScrollPane.setViewportView(valuesPanel);
        valuesPanelScrollPane.setBorder(null);
    }

    @Override
    public void onDataChange() {
    }

    @Override
    public void onUndoHandlerChange() {
        if (valuesPanel != null) {
            valuesPanel.setCodeArea(componentPanel.getCodeArea(), componentPanel.getUndoRedo().orElse(null));
        }
    }

    @Override
    public void onInitFromPreferences(BinaryEditorPreferences preferences) {
        DataInspectorPreferences dataInspectorPreferences = new DataInspectorPreferences(preferences.getPreferences());
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
                valuesPanel.enableUpdate();
            } else {
                valuesPanel.disableUpdate();
                componentPanel.remove(valuesPanelScrollPane);
                componentPanel.revalidate();
                parsingPanelVisible = false;
            }
        }
    }

    @Nonnull
    public Font getInputFieldsFont() {
        return valuesPanel.getInputFieldsFont();
    }

    public void setInputFieldsFont(Font font) {
        valuesPanel.setInputFieldsFont(font);
    }

    public boolean isShowParsingPanel() {
        return parsingPanelVisible;
    }

    public void setBasicValuesColorModifier(BasicValuesPositionColorModifier basicValuesColorModifier) {
        this.basicValuesColorModifier = basicValuesColorModifier;
    }

    public interface ComponentsProvider {

        @Nonnull
        BasicValuesPanel createValuesPanel();

        @Nonnull
        JScrollPane createScrollPane();
    }
}
