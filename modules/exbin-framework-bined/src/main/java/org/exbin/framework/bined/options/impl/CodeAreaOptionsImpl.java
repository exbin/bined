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
package org.exbin.framework.bined.options.impl;

import org.exbin.framework.bined.options.CodeAreaOptions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.capability.ViewModeCapable;
import org.exbin.bined.highlight.swing.NonAsciiCodeAreaColorAssessor;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.bined.swing.CodeAreaPainter;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.preferences.CodeAreaPreferences;
import org.exbin.framework.options.api.OptionsData;

/**
 * Code area options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaOptionsImpl implements OptionsData, CodeAreaOptions {

    private CodeType codeType = CodeType.HEXADECIMAL;
    private boolean showNonprintables = true;
    private CodeCharactersCase codeCharactersCase = CodeCharactersCase.UPPER;
    private PositionCodeType positionCodeType = PositionCodeType.HEXADECIMAL;
    private CodeAreaViewMode viewMode = CodeAreaViewMode.DUAL;
    private boolean codeColorization = true;
    private RowWrappingMode rowWrappingMode;
    private int maxBytesPerRow;
    private int minRowPositionLength;
    private int maxRowPositionLength;

    @Nonnull
    @Override
    public CodeType getCodeType() {
        return codeType;
    }

    @Override
    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
    }

    @Override
    public boolean isShowNonprintables() {
        return showNonprintables;
    }

    @Override
    public void setShowNonprintables(boolean showNonprintables) {
        this.showNonprintables = showNonprintables;
    }

    @Nonnull
    @Override
    public CodeCharactersCase getCodeCharactersCase() {
        return codeCharactersCase;
    }

    @Override
    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        this.codeCharactersCase = codeCharactersCase;
    }

    @Nonnull
    @Override
    public PositionCodeType getPositionCodeType() {
        return positionCodeType;
    }

    @Override
    public void setPositionCodeType(PositionCodeType positionCodeType) {
        this.positionCodeType = positionCodeType;
    }

    @Nonnull
    @Override
    public CodeAreaViewMode getViewMode() {
        return viewMode;
    }

    @Override
    public void setViewMode(CodeAreaViewMode viewMode) {
        this.viewMode = viewMode;
    }

    @Override
    public boolean isCodeColorization() {
        return codeColorization;
    }

    @Override
    public void setCodeColorization(boolean codeColorization) {
        this.codeColorization = codeColorization;
    }

    @Nonnull
    @Override
    public RowWrappingMode getRowWrappingMode() {
        return rowWrappingMode;
    }

    @Override
    public void setRowWrappingMode(RowWrappingMode rowWrappingMode) {
        this.rowWrappingMode = rowWrappingMode;
    }

    @Override
    public int getMaxBytesPerRow() {
        return maxBytesPerRow;
    }

    @Override
    public void setMaxBytesPerRow(int maxBytesPerRow) {
        this.maxBytesPerRow = maxBytesPerRow;
    }

    @Override
    public int getMinRowPositionLength() {
        return minRowPositionLength;
    }

    @Override
    public void setMinRowPositionLength(int minRowPositionLength) {
        this.minRowPositionLength = minRowPositionLength;
    }

    @Override
    public int getMaxRowPositionLength() {
        return maxRowPositionLength;
    }

    @Override
    public void setMaxRowPositionLength(int maxRowPositionLength) {
        this.maxRowPositionLength = maxRowPositionLength;
    }

    public void loadFromPreferences(CodeAreaPreferences preferences) {
        codeType = preferences.getCodeType();
        showNonprintables = preferences.isShowNonprintables();
        codeCharactersCase = preferences.getCodeCharactersCase();
        positionCodeType = preferences.getPositionCodeType();
        viewMode = preferences.getViewMode();
        codeColorization = preferences.isCodeColorization();
        rowWrappingMode = preferences.getRowWrappingMode();
        maxBytesPerRow = preferences.getMaxBytesPerRow();
        minRowPositionLength = preferences.getMinRowPositionLength();
        maxRowPositionLength = preferences.getMaxRowPositionLength();
    }

    public void saveToPreferences(CodeAreaPreferences preferences) {
        preferences.setCodeType(codeType);
        preferences.setShowNonprintables(showNonprintables);
        preferences.setCodeCharactersCase(codeCharactersCase);
        preferences.setPositionCodeType(positionCodeType);
        preferences.setViewMode(viewMode);
        preferences.setCodeColorization(codeColorization);
        preferences.setRowWrappingMode(rowWrappingMode);
        preferences.setMaxBytesPerRow(maxBytesPerRow);
        preferences.setMinRowPositionLength(minRowPositionLength);
        preferences.setMaxRowPositionLength(maxRowPositionLength);
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

    public void setOptions(CodeAreaOptionsImpl codeAreaOptions) {
        codeType = codeAreaOptions.codeType;
        showNonprintables = codeAreaOptions.showNonprintables;
        codeCharactersCase = codeAreaOptions.codeCharactersCase;
        positionCodeType = codeAreaOptions.positionCodeType;
        viewMode = codeAreaOptions.viewMode;
        codeColorization = codeAreaOptions.codeColorization;
        rowWrappingMode = codeAreaOptions.rowWrappingMode;
        maxBytesPerRow = codeAreaOptions.maxBytesPerRow;
        minRowPositionLength = codeAreaOptions.minRowPositionLength;
        maxRowPositionLength = codeAreaOptions.maxRowPositionLength;
    }
}
