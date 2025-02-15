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
package org.exbin.framework.bined.options;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.section.theme.SectionBackgroundPaintMode;
import org.exbin.framework.editor.text.options.TextFontOptions;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Legacy preferences for version 0.1.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LegacyPreferences {

    public static final String KEY_MEMORY_DELTA_MODE = "deltaMode";
    public static final String KEY_CODE_TYPE = "codeType";
    public static final String KEY_LINE_WRAPPING = "lineWrapping";
    public static final String KEY_SHOW_NONPRINTABLES = "showNonpritables";
    public static final String KEY_ENCODING_SELECTED = "selectedEncoding";
    public static final String KEY_ENCODING_PREFIX = "textEncoding.";
    public static final String KEY_BYTES_PER_LINE = "bytesPerLine";
    public static final String KEY_SHOW_HEADER = "showHeader";
    public static final String KEY_HEADER_SPACE_TYPE = "headerSpaceType";
    public static final String KEY_HEADER_SPACE = "headerSpace";
    public static final String KEY_SHOW_LINE_NUMBERS = "showLineNumbers";
    public static final String KEY_LINE_NUMBERS_LENGTH_TYPE = "lineNumbersLengthType";
    public static final String KEY_LINE_NUMBERS_LENGTH = "lineNumbersLength";
    public static final String KEY_LINE_NUMBERS_SPACE_TYPE = "lineNumbersSpaceType";
    public static final String KEY_LINE_NUMBERS_SPACE = "lineNumbersSpace";
    public static final String KEY_VIEW_MODE = "viewMode";
    public static final String KEY_BACKGROUND_MODE = "backgroundMode";
    public static final String KEY_PAINT_LINE_NUMBERS_BACKGROUND = "showLineNumbersBackground";
    public static final String KEY_POSITION_CODE_TYPE = "positionCodeType";
    public static final String KEY_HEX_CHARACTERS_CASE = "hexCharactersCase";
    public static final String KEY_DECORATION_HEADER_LINE = "decorationHeaderLine";
    public static final String KEY_DECORATION_PREVIEW_LINE = "decorationPreviewLine";
    public static final String KEY_DECORATION_BOX = "decorationBox";
    public static final String KEY_DECORATION_LINENUM_LINE = "decorationLineNumLine";
    public static final String KEY_BYTE_GROUP_SIZE = "byteGroupSize";
    public static final String KEY_SPACE_GROUP_SIZE = "spaceGroupSize";
    public static final String KEY_CODE_COLORIZATION = "codeColorization";
    public static final String KEY_SHOW_VALUES_PANEL = "valuesPanel";

    private final OptionsStorage storage;

    public LegacyPreferences(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getSelectedEncoding() {
        return storage.get(KEY_ENCODING_SELECTED, BinaryEditorOptions.ENCODING_UTF8);
    }

    public void setSelectedEncoding(String encodingName) {
        storage.put(KEY_ENCODING_SELECTED, encodingName);
    }

    @Nonnull
    public Collection<String> getEncodings() {
        List<String> encodings = new ArrayList<>();
        String value;
        int i = 0;
        do {
            value = storage.get(KEY_ENCODING_PREFIX + Integer.toString(i)).orElse(null);
            if (value != null) {
                encodings.add(value);
                i++;
            }
        } while (value != null);

        return encodings;
    }

    public void setEncodings(List<String> encodings) {
        // Save encodings
        for (int i = 0; i < encodings.size(); i++) {
            storage.put(KEY_ENCODING_PREFIX + Integer.toString(i), encodings.get(i));
        }
        storage.remove(KEY_ENCODING_PREFIX + Integer.toString(encodings.size()));
    }

    @Nonnull
    private SectionBackgroundPaintMode convertBackgroundPaintMode(String value) {
        if ("STRIPPED".equals(value)) {
            return SectionBackgroundPaintMode.STRIPED;
        }
        return SectionBackgroundPaintMode.valueOf(value);
    }

    @Nonnull
    public CodeType getCodeType() {
        return CodeType.valueOf(storage.get(KEY_CODE_TYPE, CodeType.HEXADECIMAL.name()));
    }

    public void setCodeType(CodeType codeType) {
        storage.put(KEY_CODE_TYPE, codeType.name());
    }

    @Nonnull
    public Font getCodeFont(Font initialFont) {
        String value;
        Map<TextAttribute, Object> attribs = new HashMap<>();
        value = storage.get(TextFontOptions.KEY_TEXT_FONT_FAMILY).orElse(null);
        if (value != null) {
            attribs.put(TextAttribute.FAMILY, value);
        }
        value = storage.get(TextFontOptions.KEY_TEXT_FONT_SIZE).orElse(null);
        if (value != null) {
            attribs.put(TextAttribute.SIZE, Integer.valueOf(value).floatValue());
        }
        if (storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_UNDERLINE, false)) {
            attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_STRIKETHROUGH, false)) {
            attribs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_STRONG, false)) {
            attribs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_ITALIC, false)) {
            attribs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_SUBSCRIPT, false)) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }
        if (storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_SUPERSCRIPT, false)) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        }
        Font font = initialFont.deriveFont(attribs);
        return font;
    }

    public void setCodeFont(Font font) {
        Map<TextAttribute, ?> attribs = font.getAttributes();
        String value = (String) attribs.get(TextAttribute.FAMILY);
        if (value != null) {
            storage.put(TextFontOptions.KEY_TEXT_FONT_FAMILY, value);
        } else {
            storage.remove(TextFontOptions.KEY_TEXT_FONT_FAMILY);
        }
        Float fontSize = (Float) attribs.get(TextAttribute.SIZE);
        if (fontSize != null) {
            storage.put(TextFontOptions.KEY_TEXT_FONT_SIZE, Integer.toString((int) (float) fontSize));
        } else {
            storage.remove(TextFontOptions.KEY_TEXT_FONT_SIZE);
        }
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL.equals(attribs.get(TextAttribute.UNDERLINE)));
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON.equals(attribs.get(TextAttribute.STRIKETHROUGH)));
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_STRONG, TextAttribute.WEIGHT_BOLD.equals(attribs.get(TextAttribute.WEIGHT)));
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_ITALIC, TextAttribute.POSTURE_OBLIQUE.equals(attribs.get(TextAttribute.POSTURE)));
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_SUBSCRIPT, TextAttribute.SUPERSCRIPT_SUB.equals(attribs.get(TextAttribute.SUPERSCRIPT)));
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER.equals(attribs.get(TextAttribute.SUPERSCRIPT)));
    }

    public boolean isDeltaMemoryMode() {
        return storage.getBoolean(KEY_MEMORY_DELTA_MODE, true);
    }

    public void setDeltaMemoryMode(boolean deltaMemoryMode) {
        storage.putBoolean(KEY_MEMORY_DELTA_MODE, deltaMemoryMode);
    }

    public boolean isLineWrapping() {
        return storage.getBoolean(KEY_LINE_WRAPPING, false);
    }

    public void setLineWrapping(boolean lineWrapping) {
        storage.putBoolean(KEY_LINE_WRAPPING, lineWrapping);
    }

    public boolean isShowNonprintables() {
        return storage.getBoolean(KEY_SHOW_NONPRINTABLES, false);
    }

    public void setShowNonprintables(boolean showNonprintables) {
        storage.putBoolean(KEY_SHOW_NONPRINTABLES, showNonprintables);
    }

    public boolean isShowValuesPanel() {
        return storage.getBoolean(KEY_SHOW_VALUES_PANEL, true);
    }

    public void setShowValuesPanel(boolean showValuesPanel) {
        storage.putBoolean(KEY_SHOW_VALUES_PANEL, showValuesPanel);
    }

    @Nonnull
    public CodeCharactersCase getCodeCharactersCase() {
        return CodeCharactersCase.valueOf(storage.get(KEY_HEX_CHARACTERS_CASE, CodeCharactersCase.UPPER.name()));
    }

    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        storage.put(KEY_HEX_CHARACTERS_CASE, codeCharactersCase.name());
    }

    @Nonnull
    public PositionCodeType getPositionCodeType() {
        return PositionCodeType.valueOf(storage.get(KEY_POSITION_CODE_TYPE, PositionCodeType.HEXADECIMAL.name()));
    }

    public void setPositionCodeType(PositionCodeType positionCodeType) {
        storage.put(KEY_POSITION_CODE_TYPE, positionCodeType.name());
    }

    @Nonnull
    public SectionBackgroundPaintMode getBackgroundPaintMode() {
        return convertBackgroundPaintMode(storage.get(KEY_BACKGROUND_MODE, SectionBackgroundPaintMode.STRIPED.name()));
    }

    @Nonnull
    public CodeAreaViewMode getViewMode() {
        String codeType = storage.get(KEY_VIEW_MODE, CodeAreaViewMode.DUAL.name());
        if ("HEXADECIMAL".equals(codeType)) {
            return CodeAreaViewMode.CODE_MATRIX;
        } else if ("PREVIEW".equals(codeType)) {
            return CodeAreaViewMode.TEXT_PREVIEW;
        }
        return CodeAreaViewMode.valueOf(codeType);
    }

    public void setViewMode(CodeAreaViewMode viewMode) {
        storage.put(KEY_VIEW_MODE, viewMode.name());
    }

    public boolean isPaintRowPosBackground() {
        return storage.getBoolean(KEY_PAINT_LINE_NUMBERS_BACKGROUND, true);
    }

    public boolean isCodeColorization() {
        return storage.getBoolean(KEY_CODE_COLORIZATION, true);
    }

    public void setCodeColorization(boolean codeColorization) {
        storage.putBoolean(KEY_CODE_COLORIZATION, codeColorization);
    }

    public boolean isUseDefaultFont() {
        return storage.getBoolean(TextFontOptions.KEY_TEXT_FONT_DEFAULT, true);
    }

    public void setUseDefaultFont(boolean useDefaultFont) {
        storage.putBoolean(TextFontOptions.KEY_TEXT_FONT_DEFAULT, useDefaultFont);
    }

    public boolean isShowHeader() {
        return storage.getBoolean(KEY_SHOW_HEADER, true);
    }

    public void setShowHeader(boolean showHeader) {
        storage.putBoolean(KEY_SHOW_HEADER, showHeader);
    }

    public boolean isShowLineNumbers() {
        return storage.getBoolean(KEY_SHOW_LINE_NUMBERS, true);
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        storage.putBoolean(KEY_SHOW_LINE_NUMBERS, showLineNumbers);
    }

    public boolean isDecorationHeaderLine() {
        return storage.getBoolean(KEY_DECORATION_HEADER_LINE, true);
    }

    public void setDecorationHeaderLine(boolean decorationHeaderLine) {
        storage.putBoolean(KEY_DECORATION_HEADER_LINE, decorationHeaderLine);
    }

    public boolean isDecorationLineNumLine() {
        return storage.getBoolean(KEY_DECORATION_LINENUM_LINE, true);
    }

    public void setDecorationLineNumLine(boolean decorationLineNumLine) {
        storage.putBoolean(KEY_DECORATION_LINENUM_LINE, decorationLineNumLine);
    }

    public boolean isDecorationPreviewLine() {
        return storage.getBoolean(KEY_DECORATION_PREVIEW_LINE, true);
    }

    public void setDecorationPreviewLine(boolean decorationPreviewLine) {
        storage.putBoolean(KEY_DECORATION_PREVIEW_LINE, decorationPreviewLine);
    }

    public boolean isDecorationBox() {
        return storage.getBoolean(KEY_DECORATION_BOX, false);
    }

    public void setDecorationBox(boolean decorationBox) {
        storage.putBoolean(KEY_DECORATION_BOX, decorationBox);
    }

    public int getByteGroupSize() {
        return storage.getInt(KEY_BYTE_GROUP_SIZE, 1);
    }

    public void setByteGroupSize(int byteGroupSize) {
        storage.putInt(KEY_BYTE_GROUP_SIZE, byteGroupSize);
    }

    public int getSpaceGroupSize() {
        return storage.getInt(KEY_SPACE_GROUP_SIZE, 0);
    }

    public void setSpaceGroupSize(int spaceGroupSize) {
        storage.putInt(KEY_SPACE_GROUP_SIZE, spaceGroupSize);
    }
}
