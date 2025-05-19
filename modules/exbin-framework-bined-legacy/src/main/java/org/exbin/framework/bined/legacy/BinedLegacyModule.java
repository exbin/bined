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
package org.exbin.framework.bined.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.section.layout.SectionCodeAreaDecorations;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.editor.options.EditorOptions;
import org.exbin.framework.bined.theme.options.CodeAreaColorOptions;
import org.exbin.framework.bined.theme.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.options.CodeAreaThemeOptions;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.text.encoding.options.TextEncodingOptions;
import org.exbin.framework.text.font.options.TextFontOptions;

/**
 * Binary data editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedLegacyModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedLegacyModule.class);
    private final static String PREFERENCES_VERSION = "version";
    private final static String PREFERENCES_VERSION_VALUE = "0.2.1";

    private java.util.ResourceBundle resourceBundle = null;
    private OptionsStorage storage;
    private CodeAreaOptions codeAreaOptions;
    private EditorOptions editorOptions;
    private TextEncodingOptions encodingOptions;
    private TextFontOptions fontOptions;
    private CodeAreaLayoutOptions layoutOptions;
    private CodeAreaThemeOptions themeOptions;
    private CodeAreaColorOptions colorOptions;

    // TODO nonprintables -> nonprintables, colors:nonprintable, activeMatch -> currentMatch

    public BinedLegacyModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedLegacyModule.class);
        }

        return resourceBundle;
    }
    
    public void setThemeOptions(CodeAreaLayoutOptions layoutOptions, CodeAreaThemeOptions themeOptions, CodeAreaColorOptions colorOptions) {
        this.layoutOptions = layoutOptions;
        this.themeOptions = themeOptions;
        this.colorOptions = colorOptions;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
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
        if (!encodings.isEmpty() && !encodings.contains(LegacyPreferences.ENCODING_UTF8)) {
            encodings.add(LegacyPreferences.ENCODING_UTF8);
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
