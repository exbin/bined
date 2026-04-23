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
package org.exbin.bined.jaguif.bookmarks.gui;

import java.awt.Color;
import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import org.exbin.bined.jaguif.bookmarks.model.BookmarksTableModel;

/**
 * Table model for color value.
 */
public class BookmarksManagerCellTableRenderer implements TableCellRenderer {

    public BookmarksManagerCellTableRenderer() {
    }

    @Nonnull
    @Override
    public Component getTableCellRendererComponent(@Nullable JTable table, @Nullable Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel renderComponent = new JPanel();
        renderComponent.setBorder(new BevelBorder(1));
        if (table != null) {
            BookmarksTableModel model = (BookmarksTableModel) table.getModel();
            renderComponent.setBackground((Color) model.getValueAt(row, column));
        }
        return renderComponent;
    }
}
