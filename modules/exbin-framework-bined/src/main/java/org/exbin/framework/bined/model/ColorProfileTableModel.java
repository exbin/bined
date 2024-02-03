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
package org.exbin.framework.bined.model;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.exbin.bined.color.BasicCodeAreaDecorationColorType;
import org.exbin.bined.color.CodeAreaBasicColors;
import org.exbin.bined.color.CodeAreaColorType;
import org.exbin.bined.extended.color.CodeAreaUnprintablesColorType;
import org.exbin.bined.highlight.swing.color.CodeAreaColorizationColorType;
import org.exbin.bined.highlight.swing.color.CodeAreaMatchColorType;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.bined.preferences.CodeAreaColorPreferences;

/**
 * Table model for Color profile panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ColorProfileTableModel implements TableModel {

    private final List<TableModelListener> listeners = new ArrayList<>();

    private final List<ColorRow> rows = new ArrayList<>();
    private ExtendedCodeAreaColorProfile colorProfile;
    private java.util.ResourceBundle resourceBundle;

    public ColorProfileTableModel(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        init();
    }

    private void init() {
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_TEXT_COLOR), CodeAreaBasicColors.TEXT_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_TEXT_BACKGROUND), CodeAreaBasicColors.TEXT_BACKGROUND));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_SELECTION_COLOR), CodeAreaBasicColors.SELECTION_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_SELECTION_BACKGROUND), CodeAreaBasicColors.SELECTION_BACKGROUND));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_SELECTION_MIRROR_COLOR), CodeAreaBasicColors.SELECTION_MIRROR_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_SELECTION_MIRROR_BACKGROUND), CodeAreaBasicColors.SELECTION_MIRROR_BACKGROUND));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_ALTERNATE_COLOR), CodeAreaBasicColors.ALTERNATE_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_ALTERNATE_BACKGROUND), CodeAreaBasicColors.ALTERNATE_BACKGROUND));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_CURSOR_COLOR), CodeAreaBasicColors.CURSOR_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_CURSOR_NEGATIVE_COLOR), CodeAreaBasicColors.CURSOR_NEGATIVE_COLOR));

        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_DECORATION_LINE), BasicCodeAreaDecorationColorType.LINE));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_CONTROL_CODES_COLOR), CodeAreaColorizationColorType.CONTROL_CODES_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_CONTROL_CODES_BACKGROUND), CodeAreaColorizationColorType.CONTROL_CODES_BACKGROUND));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_UPPER_CODES_COLOR), CodeAreaColorizationColorType.UPPER_CODES_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.COLOR_UPPER_CODES_BACKGROUND), CodeAreaColorizationColorType.UPPER_CODES_BACKGROUND));

        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.MATCH_COLOR), CodeAreaMatchColorType.MATCH_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.MATCH_BACKGROUND), CodeAreaMatchColorType.MATCH_BACKGROUND));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.ACTIVE_MATCH_COLOR), CodeAreaMatchColorType.ACTIVE_MATCH_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.ACTIVE_MATCH_BACKGROUND), CodeAreaMatchColorType.ACTIVE_MATCH_BACKGROUND));

        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.UNPRINTABLES_COLOR), CodeAreaUnprintablesColorType.UNPRINTABLES_COLOR));
        rows.add(new ColorRow(getColorTypeText(CodeAreaColorPreferences.UNPRINTABLES_BACKGROUND), CodeAreaUnprintablesColorType.UNPRINTABLES_BACKGROUND));
    }

    @Nonnull
    private String getColorTypeText(String colorType) {
        return resourceBundle.getString("colorType." + colorType);
    }

    @Nullable
    public ExtendedCodeAreaColorProfile getColorProfile() {
        return colorProfile;
    }

    public void setColorProfile(ExtendedCodeAreaColorProfile colorProfile) {
        this.colorProfile = colorProfile;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Nonnull
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return resourceBundle.getString("table.colorColumn");
            case 1:
                return resourceBundle.getString("table.valueColumn");
        }

        throw createUnexpectedColumnException(columnIndex);
    }

    @Nonnull
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Color.class;
        }

        throw createUnexpectedColumnException(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return true;
        }

        throw createUnexpectedColumnException(columnIndex);
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rows.get(rowIndex).colorName;
            case 1:
                return colorProfile == null ? null : colorProfile.getColor(rows.get(rowIndex).colorType);
        }

        throw createUnexpectedColumnException(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1: {
                if (colorProfile == null) {
                    throw new IllegalStateException("Editing is not allowed when color profile was not set");
                }

                colorProfile.setColor(rows.get(rowIndex).colorType, (Color) aValue);
                notifyAllListeners(rowIndex);
                return;
            }
        }

        throw createUnexpectedColumnException(columnIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyAllListeners(int rowNumber) {
        listeners.forEach((listener) -> listener.tableChanged(new TableModelEvent(this, rowNumber, 1, TableModelEvent.UPDATE)));
    }

    private void notifyAllListeners() {
        listeners.forEach((listener) -> listener.tableChanged(new TableModelEvent(this, 0, rows.size() - 1, 1, TableModelEvent.UPDATE)));
    }

    @Nonnull
    private static InvalidParameterException createUnexpectedColumnException(int columnIndex) {
        return new InvalidParameterException("Unexpected column index " + columnIndex);
    }

    @ParametersAreNonnullByDefault
    private static class ColorRow {

        String colorName;
        CodeAreaColorType colorType;

        public ColorRow(String colorName, CodeAreaColorType colorType) {
            this.colorName = colorName;
            this.colorType = colorType;
        }
    }
}
