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
package org.exbin.framework.bined.preferences;

import org.exbin.framework.preferences.api.Preferences;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.color.BasicCodeAreaDecorationColorType;
import org.exbin.bined.color.CodeAreaBasicColors;
import org.exbin.bined.highlight.swing.color.CodeAreaColorizationColorType;
import org.exbin.bined.highlight.swing.color.CodeAreaMatchColorType;
import org.exbin.bined.highlight.swing.color.CodeAreaNonprintablesColorType;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.bined.options.CodeAreaColorOptions;

/**
 * Color layout preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaColorPreferences implements CodeAreaColorOptions {

    public static final String PREFERENCES_COLOR_PROFILES_COUNT = "colorProfilesCount";
    public static final String PREFERENCES_COLOR_PROFILE_SELECTED = "colorProfileSelected";
    public static final String PREFERENCES_COLOR_PROFILE_NAME_PREFIX = "colorProfileName.";
    public static final String PREFERENCES_COLOR_VALUE_PREFIX = "color.";

    public static final String COLOR_TEXT_COLOR = "textColor";
    public static final String COLOR_TEXT_BACKGROUND = "textBackground";
    public static final String COLOR_SELECTION_COLOR = "selectionColor";
    public static final String COLOR_SELECTION_BACKGROUND = "selectionBackground";
    public static final String COLOR_SELECTION_MIRROR_COLOR = "selectionMirrorColor";
    public static final String COLOR_SELECTION_MIRROR_BACKGROUND = "selectionMirrorBackground";
    public static final String COLOR_ALTERNATE_COLOR = "alternateColor";
    public static final String COLOR_ALTERNATE_BACKGROUND = "alternateBackground";
    public static final String COLOR_CURSOR_COLOR = "cursorColor";
    public static final String COLOR_CURSOR_NEGATIVE_COLOR = "cursorNegativeColor";

    public static final String COLOR_DECORATION_LINE = "decoration.line";
    public static final String COLOR_CONTROL_CODES_COLOR = "controlCodesColor";
    public static final String COLOR_CONTROL_CODES_BACKGROUND = "controlCodesBackground";
    public static final String COLOR_UPPER_CODES_COLOR = "upperCodesColor";
    public static final String COLOR_UPPER_CODES_BACKGROUND = "upperCodesBackground";

    public static final String MATCH_COLOR = "matchColor";
    public static final String MATCH_BACKGROUND = "matchBackground";
    public static final String CURRENT_MATCH_COLOR = "currentMatchColor";
    public static final String CURRENT_MATCH_BACKGROUND = "currentMatchBackground";

    public static final String NONPRINTABLES_COLOR = "nonprintablesColor";
    public static final String NONPRINTABLES_BACKGROUND = "nonprintablesBackground";

    private final Preferences preferences;

    public CodeAreaColorPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public List<String> getColorProfilesList() {
        List<String> profilesList = new ArrayList<>();
        int profilesCount = preferences.getInt(PREFERENCES_COLOR_PROFILES_COUNT, 0);

        for (int i = 0; i < profilesCount; i++) {
            String profileName = preferences.get(PREFERENCES_COLOR_PROFILE_NAME_PREFIX + String.valueOf(i), "");
            profilesList.add(profileName);
        }

        return profilesList;
    }

    public void setColorProfilesList(List<String> colorProfilesNames) {
        int themesCount = colorProfilesNames.size();
        preferences.putInt(PREFERENCES_COLOR_PROFILES_COUNT, themesCount);

        for (int i = 0; i < themesCount; i++) {
            preferences.put(PREFERENCES_COLOR_PROFILE_NAME_PREFIX + String.valueOf(i), colorProfilesNames.get(i));
        }
    }

    @Override
    public int getSelectedProfile() {
        return preferences.getInt(PREFERENCES_COLOR_PROFILE_SELECTED, -1);
    }

    @Override
    public void setSelectedProfile(int profileIndex) {
        preferences.putInt(PREFERENCES_COLOR_PROFILE_SELECTED, profileIndex);
    }

    @Nonnull
    @Override
    public SectionCodeAreaColorProfile getColorsProfile(int profileIndex) {
        SectionCodeAreaColorProfile colorProfile = new SectionCodeAreaColorProfile();
        String colorProfilePrefix = PREFERENCES_COLOR_VALUE_PREFIX + String.valueOf(profileIndex) + ".";

        colorProfile.setColor(CodeAreaBasicColors.TEXT_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_TEXT_COLOR)));
        colorProfile.setColor(CodeAreaBasicColors.TEXT_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + COLOR_TEXT_BACKGROUND)));
        colorProfile.setColor(CodeAreaBasicColors.SELECTION_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_SELECTION_COLOR)));
        colorProfile.setColor(CodeAreaBasicColors.SELECTION_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + COLOR_SELECTION_BACKGROUND)));
        colorProfile.setColor(CodeAreaBasicColors.SELECTION_MIRROR_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_SELECTION_MIRROR_COLOR)));
        colorProfile.setColor(CodeAreaBasicColors.SELECTION_MIRROR_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + COLOR_SELECTION_MIRROR_BACKGROUND)));
        colorProfile.setColor(CodeAreaBasicColors.ALTERNATE_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_ALTERNATE_COLOR)));
        colorProfile.setColor(CodeAreaBasicColors.ALTERNATE_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + COLOR_ALTERNATE_BACKGROUND)));
        colorProfile.setColor(CodeAreaBasicColors.CURSOR_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_CURSOR_COLOR)));
        colorProfile.setColor(CodeAreaBasicColors.CURSOR_NEGATIVE_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_CURSOR_NEGATIVE_COLOR)));

        colorProfile.setColor(BasicCodeAreaDecorationColorType.LINE, textAsColor(preferences.get(colorProfilePrefix + COLOR_DECORATION_LINE)));
        colorProfile.setColor(CodeAreaColorizationColorType.CONTROL_CODES_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_CONTROL_CODES_COLOR)));
        colorProfile.setColor(CodeAreaColorizationColorType.CONTROL_CODES_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + COLOR_CONTROL_CODES_BACKGROUND)));
        colorProfile.setColor(CodeAreaColorizationColorType.UPPER_CODES_COLOR, textAsColor(preferences.get(colorProfilePrefix + COLOR_UPPER_CODES_COLOR)));
        colorProfile.setColor(CodeAreaColorizationColorType.UPPER_CODES_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + COLOR_UPPER_CODES_BACKGROUND)));

        colorProfile.setColor(CodeAreaMatchColorType.MATCH_COLOR, textAsColor(preferences.get(colorProfilePrefix + MATCH_COLOR)));
        colorProfile.setColor(CodeAreaMatchColorType.MATCH_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + MATCH_BACKGROUND)));
        colorProfile.setColor(CodeAreaMatchColorType.CURRENT_MATCH_COLOR, textAsColor(preferences.get(colorProfilePrefix + CURRENT_MATCH_COLOR)));
        colorProfile.setColor(CodeAreaMatchColorType.CURRENT_MATCH_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + CURRENT_MATCH_BACKGROUND)));

        colorProfile.setColor(CodeAreaNonprintablesColorType.NONPRINTABLES_COLOR, textAsColor(preferences.get(colorProfilePrefix + NONPRINTABLES_COLOR)));
        colorProfile.setColor(CodeAreaNonprintablesColorType.NONPRINTABLES_BACKGROUND, textAsColor(preferences.get(colorProfilePrefix + NONPRINTABLES_BACKGROUND)));

        return colorProfile;
    }

    @Override
    public void setColorsProfile(int profileIndex, SectionCodeAreaColorProfile colorProfile) {
        String colorProfilePrefix = PREFERENCES_COLOR_VALUE_PREFIX + String.valueOf(profileIndex) + ".";

        preferences.put(colorProfilePrefix + COLOR_TEXT_COLOR, colorAsText(colorProfile.getColor(CodeAreaBasicColors.TEXT_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_TEXT_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaBasicColors.TEXT_BACKGROUND)));
        preferences.put(colorProfilePrefix + COLOR_SELECTION_COLOR, colorAsText(colorProfile.getColor(CodeAreaBasicColors.SELECTION_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_SELECTION_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaBasicColors.SELECTION_BACKGROUND)));
        preferences.put(colorProfilePrefix + COLOR_SELECTION_MIRROR_COLOR, colorAsText(colorProfile.getColor(CodeAreaBasicColors.SELECTION_MIRROR_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_SELECTION_MIRROR_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaBasicColors.SELECTION_MIRROR_BACKGROUND)));
        preferences.put(colorProfilePrefix + COLOR_ALTERNATE_COLOR, colorAsText(colorProfile.getColor(CodeAreaBasicColors.ALTERNATE_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_ALTERNATE_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaBasicColors.ALTERNATE_BACKGROUND)));
        preferences.put(colorProfilePrefix + COLOR_CURSOR_COLOR, colorAsText(colorProfile.getColor(CodeAreaBasicColors.CURSOR_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_CURSOR_NEGATIVE_COLOR, colorAsText(colorProfile.getColor(CodeAreaBasicColors.CURSOR_NEGATIVE_COLOR)));

        preferences.put(colorProfilePrefix + COLOR_DECORATION_LINE, colorAsText(colorProfile.getColor(BasicCodeAreaDecorationColorType.LINE)));
        preferences.put(colorProfilePrefix + COLOR_CONTROL_CODES_COLOR, colorAsText(colorProfile.getColor(CodeAreaColorizationColorType.CONTROL_CODES_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_CONTROL_CODES_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaColorizationColorType.CONTROL_CODES_BACKGROUND)));
        preferences.put(colorProfilePrefix + COLOR_UPPER_CODES_COLOR, colorAsText(colorProfile.getColor(CodeAreaColorizationColorType.UPPER_CODES_COLOR)));
        preferences.put(colorProfilePrefix + COLOR_UPPER_CODES_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaColorizationColorType.UPPER_CODES_BACKGROUND)));

        preferences.put(colorProfilePrefix + MATCH_COLOR, colorAsText(colorProfile.getColor(CodeAreaMatchColorType.MATCH_COLOR)));
        preferences.put(colorProfilePrefix + MATCH_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaMatchColorType.MATCH_BACKGROUND)));
        preferences.put(colorProfilePrefix + CURRENT_MATCH_COLOR, colorAsText(colorProfile.getColor(CodeAreaMatchColorType.CURRENT_MATCH_COLOR)));
        preferences.put(colorProfilePrefix + CURRENT_MATCH_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaMatchColorType.CURRENT_MATCH_BACKGROUND)));

        preferences.put(colorProfilePrefix + NONPRINTABLES_COLOR, colorAsText(colorProfile.getColor(CodeAreaNonprintablesColorType.NONPRINTABLES_COLOR)));
        preferences.put(colorProfilePrefix + NONPRINTABLES_BACKGROUND, colorAsText(colorProfile.getColor(CodeAreaNonprintablesColorType.NONPRINTABLES_BACKGROUND)));
    }

    @Override
    public void removeColorsProfile(int profileIndex) {
        String colorProfilePrefix = PREFERENCES_COLOR_VALUE_PREFIX + String.valueOf(profileIndex) + ".";

        preferences.remove(colorProfilePrefix + COLOR_TEXT_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_TEXT_BACKGROUND);
        preferences.remove(colorProfilePrefix + COLOR_SELECTION_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_SELECTION_BACKGROUND);
        preferences.remove(colorProfilePrefix + COLOR_SELECTION_MIRROR_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_SELECTION_MIRROR_BACKGROUND);
        preferences.remove(colorProfilePrefix + COLOR_ALTERNATE_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_ALTERNATE_BACKGROUND);
        preferences.remove(colorProfilePrefix + COLOR_CURSOR_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_CURSOR_NEGATIVE_COLOR);

        preferences.remove(colorProfilePrefix + COLOR_DECORATION_LINE);
        preferences.remove(colorProfilePrefix + COLOR_CONTROL_CODES_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_CONTROL_CODES_BACKGROUND);
        preferences.remove(colorProfilePrefix + COLOR_UPPER_CODES_COLOR);
        preferences.remove(colorProfilePrefix + COLOR_UPPER_CODES_BACKGROUND);

        preferences.remove(colorProfilePrefix + MATCH_COLOR);
        preferences.remove(colorProfilePrefix + MATCH_BACKGROUND);
        preferences.remove(colorProfilePrefix + CURRENT_MATCH_COLOR);
        preferences.remove(colorProfilePrefix + CURRENT_MATCH_BACKGROUND);

        preferences.remove(colorProfilePrefix + NONPRINTABLES_COLOR);
        preferences.remove(colorProfilePrefix + NONPRINTABLES_BACKGROUND);
    }

    /**
     * Converts color to text.
     *
     * @param color color
     * @return color string in hex format, e.g. "#FFFFFF"
     */
    @Nullable
    private static String colorAsText(@Nullable Color color) {
        if (color == null) {
            return null;
        }
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return String.format("#%02x%02x%02x", red, green, blue);
    }

    /**
     * Converts text to color.
     *
     * @param colorStr e.g. "#FFFFFF"
     * @return color or null
     */
    @Nullable
    private static Color textAsColor(Optional<String> colorStr) {
        if (!colorStr.isPresent()) {
            return null;
        }
        return Color.decode(colorStr.get());
    }
}
