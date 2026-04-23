/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.theme.settings.gui;

import org.exbin.bined.jaguif.theme.model.ColorProfileTableModel;
import java.awt.Color;
import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table model for color profile panel.
 */
public class ColorCellTableRenderer implements TableCellRenderer {

    public ColorCellTableRenderer() {
    }

    @Nonnull
    @Override
    public Component getTableCellRendererComponent(@Nullable JTable table, @Nullable Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table == null) {
            return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        return new ColorCellPanel(new ColorCellPanel.ColorHandler() {
            @Nullable
            @Override
            public Color getColor() {
                ColorProfileTableModel model = (ColorProfileTableModel) table.getModel();
                return (Color) model.getValueAt(row, column);
            }

            @Override
            public void setColor(@Nullable Color color) {
                ColorProfileTableModel model = (ColorProfileTableModel) table.getModel();
                model.setValueAt(color, row, column);
            }
        });
    }
}
