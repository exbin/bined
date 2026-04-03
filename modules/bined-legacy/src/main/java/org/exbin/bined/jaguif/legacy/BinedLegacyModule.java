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
package org.exbin.bined.jaguif.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.section.layout.SectionCodeAreaDecorations;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.bined.jaguif.component.FileProcessingMode;
import org.exbin.bined.jaguif.editor.settings.BinaryFileProcessingOptions;
import org.exbin.bined.jaguif.legacy.settings.BinedLegacyOptions;
import org.exbin.bined.jaguif.inspector.settings.DataInspectorOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaOptions;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.preferences.FilePreferences;
import org.exbin.jaguif.options.preferences.FilePreferencesFactory;
import org.exbin.jaguif.options.preferences.PreferencesWrapper;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsOptions;
import org.exbin.jaguif.options.settings.api.SettingsOptionsBuilder;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.text.font.settings.TextFontOptions;

/**
 * Binary data editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedLegacyModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedLegacyModule.class);
    private final static String PREFERENCES_VERSION = "version";

    private java.util.ResourceBundle resourceBundle = null;

    // TODO unprintables -> nonprintables, colors:nonprintable, activeMatch -> currentMatch

    public BinedLegacyModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedLegacyModule.class);
        }

        return resourceBundle;
    }
    
    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void importLegacySettings() {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = settingsManager.getSettingsOptionsProvider();
        BinedLegacyOptions legacyOptions = settingsOptionsProvider.getSettingsOptions(BinedLegacyOptions.class);

        String legacyImported = legacyOptions.getLegacyImported();
        if ("".equals(legacyImported)) {
            PreferencesWrapper legacyStorage = new PreferencesWrapper(new FilePreferences(null, "", FilePreferencesFactory.getPreferencesFile("/org/exbin/bined/editor")));
            convertOlderPreferences(legacyStorage, settingsOptionsProvider);
        }
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        settingsManagement.registerSettingsOptions(BinedLegacyOptions.class, (optionsStorage) -> new BinedLegacyOptions(optionsStorage));
    }

    private void convertOlderPreferences(OptionsStorage storage, SettingsOptionsProvider settingsOptionsProvider) {
        BinedLegacyOptions legacyOptions = settingsOptionsProvider.getSettingsOptions(BinedLegacyOptions.class);
        final String legacyDef = "LEGACY";
        String storedVersion = storage.get(PREFERENCES_VERSION, legacyDef);

        if (legacyDef.equals(storedVersion)) {
            try {
                importLegacyPreferences(storage, settingsOptionsProvider);
            } finally {
                legacyOptions.setLegacyImported(storedVersion);
            }
            return;
        }
        
        try {
            // Convert preferences from 0.2.4 or previous
            ConversionStorage conversionStorage = new ConversionStorage(storage);
            OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
            OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
            Collection<Class<? extends SettingsOptions>> optionsClasses = settingsManager.getOptionsClasses();
            for (Class<? extends SettingsOptions> optionsClass : optionsClasses) {
                try {
                    SettingsOptionsBuilder<?> optionsBuilder = settingsManager.getSettingsOptionsBuilder(optionsClass);
                    SettingsOptions sourceOptions = optionsBuilder.createInstance(conversionStorage);
                    SettingsOptions targetOptions = settingsOptionsProvider.getSettingsOptions(optionsClass);
                    sourceOptions.copyTo(targetOptions);
                } catch (Exception ex) {
                    Logger.getLogger(BinedLegacyModule.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if ("0.2.0".equals(storedVersion)) {
                convertPreferences_0_2_0(storage, settingsOptionsProvider);
            }
        } finally {
            legacyOptions.setLegacyImported(storedVersion);
        }
    }

    private void importLegacyPreferences(OptionsStorage storage, SettingsOptionsProvider settingsOptionsProvider) {
        CodeAreaOptions codeAreaOptions = settingsOptionsProvider.getSettingsOptions(CodeAreaOptions.class);
        BinaryFileProcessingOptions fileProcessingOptions = settingsOptionsProvider.getSettingsOptions(BinaryFileProcessingOptions.class);
        TextEncodingOptions encodingOptions = settingsOptionsProvider.getSettingsOptions(TextEncodingOptions.class);
        TextFontOptions fontOptions = settingsOptionsProvider.getSettingsOptions(TextFontOptions.class);
        CodeAreaLayoutOptions layoutOptions = settingsOptionsProvider.getSettingsOptions(CodeAreaLayoutOptions.class);
        CodeAreaThemeOptions themeOptions = settingsOptionsProvider.getSettingsOptions(CodeAreaThemeOptions.class);
        DataInspectorOptions dataInspectorOptions = settingsOptionsProvider.getSettingsOptions(DataInspectorOptions.class);

        LegacyPreferences legacyPreferences = new LegacyPreferences(storage);
        codeAreaOptions.setCodeType(legacyPreferences.getCodeType());
        codeAreaOptions.setRowWrappingMode(legacyPreferences.isLineWrapping() ? RowWrappingMode.WRAPPING : RowWrappingMode.NO_WRAPPING);
        codeAreaOptions.setShowNonprintables(legacyPreferences.isShowNonprintables());
        codeAreaOptions.setCodeCharactersCase(legacyPreferences.getCodeCharactersCase());
        codeAreaOptions.setPositionCodeType(legacyPreferences.getPositionCodeType());
        codeAreaOptions.setViewMode(legacyPreferences.getViewMode());
        codeAreaOptions.setPaintRowPosBackground(legacyPreferences.isPaintRowPosBackground());
        codeAreaOptions.setCodeColorization(legacyPreferences.isCodeColorization());

        fileProcessingOptions.setFileProcessingMode(legacyPreferences.isDeltaMemoryMode() ? FileProcessingMode.DELTA : FileProcessingMode.MEMORY);
        dataInspectorOptions.setShowParsingPanel(legacyPreferences.isShowValuesPanel());

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

    private void convertPreferences_0_2_0(OptionsStorage storage, SettingsOptionsProvider settingsOptionsProvider) {
        CodeAreaOptions codeAreaOptions = settingsOptionsProvider.getSettingsOptions(CodeAreaOptions.class);
        String codeType = storage.get(CodeAreaOptions.KEY_VIEW_MODE, "DUAL");
        if ("HEXADECIMAL".equals(codeType)) {
            codeAreaOptions.setViewMode(CodeAreaViewMode.CODE_MATRIX);
        } else if ("PREVIEW".equals(codeType)) {
            codeAreaOptions.setViewMode(CodeAreaViewMode.TEXT_PREVIEW);
        }
        storage.flush();
    }
}
