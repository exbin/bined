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
package org.exbin.framework.bined.viewer.options;

import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.capability.ViewModeCapable;
import org.exbin.bined.highlight.swing.NonAsciiCodeAreaColorAssessor;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.bined.swing.CodeAreaPainter;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Code area options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaOptions implements OptionsData {

    public static final String KEY_CODE_TYPE = "codeType";
    public static final String KEY_SHOW_NONPRINTABLES = "showNonpritables";
    public static final String KEY_BYTES_PER_LINE = "bytesPerLine";
    public static final String KEY_LINE_NUMBERS_LENGTH_TYPE = "lineNumbersLengthType";
    public static final String KEY_LINE_NUMBERS_LENGTH = "lineNumbersLength";
    public static final String KEY_VIEW_MODE = "viewMode";
    public static final String KEY_PAINT_LINE_NUMBERS_BACKGROUND = "showLineNumbersBackground";
    public static final String KEY_POSITION_CODE_TYPE = "positionCodeType";
    public static final String KEY_HEX_CHARACTERS_CASE = "hexCharactersCase";
    public static final String KEY_CODE_COLORIZATION = "codeColorization";
    public static final String KEY_ROW_WRAPPING_MODE = "rowWrappingMode";
    public static final String KEY_MAX_BYTES_PER_ROW = "maxBytesPerRow";
    public static final String KEY_MIN_ROW_POSITION_LENGTH = "minRowPositionLength";
    public static final String KEY_MAX_ROW_POSITION_LENGTH = "maxRowPositionLength";

    public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private final OptionsStorage storage;

    public CodeAreaOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public CodeType getCodeType() {
        CodeType defaultCodeType = CodeType.HEXADECIMAL;
        try {
            return CodeType.valueOf(storage.get(KEY_CODE_TYPE, defaultCodeType.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setCodeType(CodeType codeType) {
        storage.put(KEY_CODE_TYPE, codeType.name());
    }

    public boolean isShowNonprintables() {
        return storage.getBoolean(KEY_SHOW_NONPRINTABLES, false);
    }

    public void setShowNonprintables(boolean showNonprintables) {
        storage.putBoolean(KEY_SHOW_NONPRINTABLES, showNonprintables);
    }

    @Nonnull
    public CodeCharactersCase getCodeCharactersCase() {
        CodeCharactersCase defaultCharactersCase = CodeCharactersCase.UPPER;
        try {
            return CodeCharactersCase.valueOf(storage.get(KEY_HEX_CHARACTERS_CASE, defaultCharactersCase.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCharactersCase;
        }
    }

    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        storage.put(KEY_HEX_CHARACTERS_CASE, codeCharactersCase.name());
    }

    @Nonnull
    public PositionCodeType getPositionCodeType() {
        PositionCodeType defaultCodeType = PositionCodeType.HEXADECIMAL;
        try {
            return PositionCodeType.valueOf(storage.get(KEY_POSITION_CODE_TYPE, defaultCodeType.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setPositionCodeType(PositionCodeType positionCodeType) {
        storage.put(KEY_POSITION_CODE_TYPE, positionCodeType.name());
    }

    @Nonnull
    public CodeAreaViewMode getViewMode() {
        CodeAreaViewMode defaultMode = CodeAreaViewMode.DUAL;
        try {
            return CodeAreaViewMode.valueOf(storage.get(KEY_VIEW_MODE, defaultMode.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultMode;
        }
    }

    public void setViewMode(CodeAreaViewMode viewMode) {
        storage.put(KEY_VIEW_MODE, viewMode.name());
    }

    public boolean isPaintRowPosBackground() {
        return storage.getBoolean(KEY_PAINT_LINE_NUMBERS_BACKGROUND, true);
    }

    public void setPaintRowPosBackground(boolean paintRowPosBackground) {
        storage.putBoolean(KEY_PAINT_LINE_NUMBERS_BACKGROUND, paintRowPosBackground);
    }

    public boolean isCodeColorization() {
        return storage.getBoolean(KEY_CODE_COLORIZATION, true);
    }

    public void setCodeColorization(boolean codeColorization) {
        storage.putBoolean(KEY_CODE_COLORIZATION, codeColorization);
    }

    @Nonnull
    public RowWrappingMode getRowWrappingMode() {
        RowWrappingMode defaultMode = RowWrappingMode.NO_WRAPPING;
        try {
            return RowWrappingMode.valueOf(storage.get(KEY_ROW_WRAPPING_MODE, defaultMode.name()));
        } catch (Exception ex) {
            Logger.getLogger(CodeAreaOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultMode;
        }
    }

    public void setRowWrappingMode(RowWrappingMode rowWrappingMode) {
        storage.put(KEY_ROW_WRAPPING_MODE, rowWrappingMode.name());
    }

    public int getMaxBytesPerRow() {
        return storage.getInt(KEY_MAX_BYTES_PER_ROW, 16);
    }

    public void setMaxBytesPerRow(int maxBytesPerRow) {
        storage.putInt(KEY_MAX_BYTES_PER_ROW, maxBytesPerRow);
    }

    public int getMinRowPositionLength() {
        return storage.getInt(KEY_MIN_ROW_POSITION_LENGTH, 0);
    }

    public void setMinRowPositionLength(int minRowPositionLength) {
        storage.putInt(KEY_MIN_ROW_POSITION_LENGTH, minRowPositionLength);
    }

    public int getMaxRowPositionLength() {
        return storage.getInt(KEY_MAX_ROW_POSITION_LENGTH, 0);
    }

    public void setMaxRowPositionLength(int maxRowPositionLength) {
        storage.putInt(KEY_MAX_ROW_POSITION_LENGTH, maxRowPositionLength);
    }

    @Override
    public void copyTo(OptionsData options) {
        CodeAreaOptions with = (CodeAreaOptions) options;
        with.setCodeCharactersCase(getCodeCharactersCase());
        with.setCodeColorization(isCodeColorization());
        with.setCodeType(getCodeType());
        with.setMaxBytesPerRow(getMaxBytesPerRow());
        with.setMaxRowPositionLength(getMaxRowPositionLength());
        with.setMinRowPositionLength(getMinRowPositionLength());
        with.setPaintRowPosBackground(isPaintRowPosBackground());
        with.setPositionCodeType(getPositionCodeType());
        with.setRowWrappingMode(getRowWrappingMode());
        with.setShowNonprintables(isShowNonprintables());
        with.setViewMode(getViewMode());
    }

    public static void applyFromCodeArea(CodeAreaOptions codeAreaOptions, SectCodeArea codeArea) {
        CodeAreaPainter painter = codeArea.getPainter();
        codeAreaOptions.setCodeType(((CodeTypeCapable) codeArea).getCodeType());
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            codeAreaOptions.setShowNonprintables(nonprintablesCodeAreaAssessor.isShowNonprintables());
        }
        codeAreaOptions.setCodeCharactersCase(((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase());
        codeAreaOptions.setPositionCodeType(((PositionCodeTypeCapable) codeArea).getPositionCodeType());
        codeAreaOptions.setViewMode(((ViewModeCapable) codeArea).getViewMode());
        NonAsciiCodeAreaColorAssessor nonAsciiColorAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonAsciiCodeAreaColorAssessor.class);
        if (nonAsciiColorAssessor != null) {
            codeAreaOptions.setCodeColorization(nonAsciiColorAssessor.isNonAsciiHighlightingEnabled());
        }
        codeAreaOptions.setRowWrappingMode(codeArea.getRowWrapping());
        codeAreaOptions.setMaxBytesPerRow(codeArea.getMaxBytesPerRow());
        codeAreaOptions.setMinRowPositionLength(codeArea.getMinRowPositionLength());
        codeAreaOptions.setMaxRowPositionLength(codeArea.getMaxRowPositionLength());
    }

    public static void applyToCodeArea(CodeAreaOptions codeAreaOptions, SectCodeArea codeArea) {
        CodeAreaPainter painter = codeArea.getPainter();
        ((CodeTypeCapable) codeArea).setCodeType(codeAreaOptions.getCodeType());
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            nonprintablesCodeAreaAssessor.setShowNonprintables(codeAreaOptions.isShowNonprintables());
        }
        ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(codeAreaOptions.getCodeCharactersCase());
        ((PositionCodeTypeCapable) codeArea).setPositionCodeType(codeAreaOptions.getPositionCodeType());
        ((ViewModeCapable) codeArea).setViewMode(codeAreaOptions.getViewMode());
        NonAsciiCodeAreaColorAssessor nonAsciiColorAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonAsciiCodeAreaColorAssessor.class);
        if (nonAsciiColorAssessor != null) {
            nonAsciiColorAssessor.setNonAsciiHighlightingEnabled(codeAreaOptions.isCodeColorization());
        }
        codeArea.setRowWrapping(codeAreaOptions.getRowWrappingMode());
        codeArea.setMaxBytesPerRow(codeAreaOptions.getMaxBytesPerRow());
        codeArea.setMinRowPositionLength(codeAreaOptions.getMinRowPositionLength());
        codeArea.setMaxRowPositionLength(codeAreaOptions.getMaxRowPositionLength());
    }
}
