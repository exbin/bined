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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.section.layout.SectionCodeAreaDecorations;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.editor.text.options.TextEncodingOptions;
import org.exbin.framework.editor.text.options.TextFontOptions;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Binary editor options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorOptions {

    public static final String ENCODING_UTF8 = "UTF-8";
    private final static String PREFERENCES_VERSION = "version";
    private final static String PREFERENCES_VERSION_VALUE = "0.2.1";
    // TODO nonprintables -> nonprintables, colors:nonprintable, activeMatch -> currentMatch

    private final OptionsStorage storage;

    private final EditorOptions editorOptions;
    private final StatusOptions statusOptions;
    private final CodeAreaOptions codeAreaOptions;
    private final TextEncodingOptions encodingOptions;
    private final TextFontOptions fontOptions;
    private final CodeAreaLayoutOptions layoutOptions;
    private final CodeAreaThemeOptions themeOptions;
    private final CodeAreaColorOptions colorOptions;

    public BinaryEditorOptions(OptionsStorage storage) {
        this.storage = storage;

        editorOptions = new EditorOptions(storage);
        statusOptions = new StatusOptions(storage);
        codeAreaOptions = new CodeAreaOptions(storage);
        encodingOptions = new TextEncodingOptions(storage);
        fontOptions = new TextFontOptions(storage);
        layoutOptions = new CodeAreaLayoutOptions(storage);
        themeOptions = new CodeAreaThemeOptions(storage);
        colorOptions = new CodeAreaColorOptions(storage);

        convertOlderPreferences();
    }

    private void convertOlderPreferences() {
        final String legacyDef = "LEGACY";
        String storedVersion = storage.get(PREFERENCES_VERSION, legacyDef);
        if (PREFERENCES_VERSION_VALUE.equals(storedVersion)) {
            return;
        }

        if (legacyDef.equals(storedVersion)) {
            try {
                importLegacyPreferences();
            } finally {
                storage.put(PREFERENCES_VERSION, PREFERENCES_VERSION_VALUE);
                storage.flush();
            }
        }

        if ("0.2.0".equals(storedVersion)) {
            convertPreferences_0_2_0();
        }
    }

    @Nonnull
    public OptionsStorage getPreferences() {
        return storage;
    }

    @Nonnull
    public EditorOptions getEditorOptions() {
        return editorOptions;
    }

    @Nonnull
    public StatusOptions getStatusOptions() {
        return statusOptions;
    }

    @Nonnull
    public CodeAreaOptions getCodeAreaOptions() {
        return codeAreaOptions;
    }

    @Nonnull
    public TextEncodingOptions getEncodingOptions() {
        return encodingOptions;
    }

    @Nonnull
    public TextFontOptions getFontOptions() {
        return fontOptions;
    }

    @Nonnull
    public CodeAreaLayoutOptions getLayoutOptions() {
        return layoutOptions;
    }

    @Nonnull
    public CodeAreaThemeOptions getThemeOptions() {
        return themeOptions;
    }

    @Nonnull
    public CodeAreaColorOptions getColorOptions() {
        return colorOptions;
    }

    private void importLegacyPreferences() {
        LegacyPreferences legacyPreferences = new LegacyPreferences(storage);
        codeAreaOptions.setCodeType(legacyPreferences.getCodeType());
        codeAreaOptions.setRowWrappingMode(legacyPreferences.isLineWrapping() ? RowWrappingMode.WRAPPING : RowWrappingMode.NO_WRAPPING);
        codeAreaOptions.setShowNonprintables(legacyPreferences.isShowNonprintables());
        codeAreaOptions.setCodeCharactersCase(legacyPreferences.getCodeCharactersCase());
        codeAreaOptions.setPositionCodeType(legacyPreferences.getPositionCodeType());
        codeAreaOptions.setViewMode(legacyPreferences.getViewMode());
        codeAreaOptions.setPaintRowPosBackground(legacyPreferences.isPaintRowPosBackground());
        codeAreaOptions.setCodeColorization(legacyPreferences.isCodeColorization());

        editorOptions.setFileHandlingMode(legacyPreferences.isDeltaMemoryMode() ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY);
        // editorOptions.setShowValuesPanel(legacyPreferences.isShowValuesPanel());

        List<String> layoutProfiles = new ArrayList<>();
        layoutProfiles.add("Imported profile");
        DefaultSectionCodeAreaLayoutProfile layoutProfile = new DefaultSectionCodeAreaLayoutProfile();
        layoutProfile.setShowHeader(legacyPreferences.isShowHeader());
        layoutProfile.setShowRowPosition(legacyPreferences.isShowLineNumbers());
        layoutProfile.setSpaceGroupSize(legacyPreferences.getByteGroupSize());
        layoutProfile.setDoubleSpaceGroupSize(legacyPreferences.getSpaceGroupSize());
        layoutOptions.setLayoutProfile(0, layoutProfile);
        layoutOptions.setLayoutProfilesList(layoutProfiles);

        List<String> themeProfiles = new ArrayList<>();
        themeProfiles.add("Imported profile");
        SectionCodeAreaThemeProfile themeProfile = new SectionCodeAreaThemeProfile();
        themeProfile.setBackgroundPaintMode(legacyPreferences.getBackgroundPaintMode());
        themeProfile.setPaintRowPosBackground(legacyPreferences.isPaintRowPosBackground());
        themeProfile.setDecoration(SectionCodeAreaDecorations.HEADER_LINE, legacyPreferences.isDecorationHeaderLine());
        themeProfile.setDecoration(SectionCodeAreaDecorations.ROW_POSITION_LINE, legacyPreferences.isDecorationLineNumLine());
        themeProfile.setDecoration(SectionCodeAreaDecorations.SPLIT_LINE, legacyPreferences.isDecorationPreviewLine());
        themeProfile.setDecoration(SectionCodeAreaDecorations.BOX_LINES, legacyPreferences.isDecorationBox());
        themeOptions.setThemeProfile(0, themeProfile);
        themeOptions.setThemeProfilesList(themeProfiles);

        encodingOptions.setSelectedEncoding(legacyPreferences.getSelectedEncoding());
        encodingOptions.setEncodings(new ArrayList<>(legacyPreferences.getEncodings()));
        Collection<String> legacyEncodings = legacyPreferences.getEncodings();
        List<String> encodings = new ArrayList<>(legacyEncodings);
        if (!encodings.isEmpty() && !encodings.contains(ENCODING_UTF8)) {
            encodings.add(ENCODING_UTF8);
        }
        encodingOptions.setEncodings(encodings);
        fontOptions.setUseDefaultFont(legacyPreferences.isUseDefaultFont());
        fontOptions.setFont(legacyPreferences.getCodeFont(CodeAreaOptions.DEFAULT_FONT));

        storage.flush();
    }

    private void convertPreferences_0_2_0() {
        String codeType = storage.get(CodeAreaOptions.KEY_VIEW_MODE, "DUAL");
        if ("HEXADECIMAL".equals(codeType)) {
            codeAreaOptions.setViewMode(CodeAreaViewMode.CODE_MATRIX);
        } else if ("PREVIEW".equals(codeType)) {
            codeAreaOptions.setViewMode(CodeAreaViewMode.TEXT_PREVIEW);
        }
        storage.flush();
    }
}
