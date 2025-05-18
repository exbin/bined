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
package org.exbin.framework.bined.theme.options;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Code area layout preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaLayoutOptions implements OptionsData {

    public static final String KEY_LAYOUT_PROFILES_COUNT = "layoutProfilesCount";
    public static final String KEY_LAYOUT_PROFILE_SELECTED = "layoutProfileSelected";
    public static final String KEY_LAYOUT_PROFILE_NAME_PREFIX = "layoutProfileName.";
    public static final String KEY_LAYOUT_VALUE_PREFIX = "layouts.";

    public static final String LAYOUT_SHOW_HEADER = "showHeader";
    public static final String LAYOUT_SHOW_ROW_POSITION = "showRowPosition";

    public static final String LAYOUT_TOP_HEADER_SPACE = "topHeaderSpace";
    public static final String LAYOUT_BOTTOM_HEADER_SPACE = "bottomHeaderSpace";
    public static final String LAYOUT_LEFT_ROW_POSITION_SPACE = "leftRowPositionSpace";
    public static final String LAYOUT_RIGHT_ROW_POSITION_SPACE = "rightRowPositionSpace";

    public static final String LAYOUT_HALF_SPACE_GROUP_SIZE = "halfSpaceGroupSize";
    public static final String LAYOUT_SPACE_GROUP_SIZE = "spaceGroupSize";
    public static final String LAYOUT_DOUBLE_SPACE_GROUP_SIZE = "doubleSpaceGroupSize";

    private final OptionsStorage storage;

    public CodeAreaLayoutOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public List<String> getLayoutProfilesList() {
        List<String> profilesList = new ArrayList<>();
        int profilesCount = storage.getInt(KEY_LAYOUT_PROFILES_COUNT, 0);

        for (int i = 0; i < profilesCount; i++) {
            String profileName = storage.get(KEY_LAYOUT_PROFILE_NAME_PREFIX + String.valueOf(i), "");
            profilesList.add(profileName);
        }

        return profilesList;
    }

    public void setLayoutProfilesList(List<String> layoutNames) {
        int themesCount = layoutNames.size();
        storage.putInt(KEY_LAYOUT_PROFILES_COUNT, themesCount);

        for (int i = 0; i < themesCount; i++) {
            storage.put(KEY_LAYOUT_PROFILE_NAME_PREFIX + String.valueOf(i), layoutNames.get(i));
        }
    }

    public int getSelectedProfile() {
        return storage.getInt(KEY_LAYOUT_PROFILE_SELECTED, -1);
    }

    public void setSelectedProfile(int profileIndex) {
        storage.putInt(KEY_LAYOUT_PROFILE_SELECTED, profileIndex);
    }

    @Nonnull
    public DefaultSectionCodeAreaLayoutProfile getLayoutProfile(int profileIndex) {
        DefaultSectionCodeAreaLayoutProfile layoutProfile = new DefaultSectionCodeAreaLayoutProfile();
        String layoutPrefix = KEY_LAYOUT_VALUE_PREFIX + String.valueOf(profileIndex) + ".";
        layoutProfile.setShowHeader(storage.getBoolean(layoutPrefix + LAYOUT_SHOW_HEADER, layoutProfile.isShowHeader()));
        layoutProfile.setShowRowPosition(storage.getBoolean(layoutPrefix + LAYOUT_SHOW_ROW_POSITION, layoutProfile.isShowRowPosition()));

        layoutProfile.setTopHeaderSpace(storage.getInt(layoutPrefix + LAYOUT_TOP_HEADER_SPACE, layoutProfile.getTopHeaderSpace()));
        layoutProfile.setBottomHeaderSpace(storage.getInt(layoutPrefix + LAYOUT_BOTTOM_HEADER_SPACE, layoutProfile.getBottomHeaderSpace()));
        layoutProfile.setLeftRowPositionSpace(storage.getInt(layoutPrefix + LAYOUT_LEFT_ROW_POSITION_SPACE, layoutProfile.getLeftRowPositionSpace()));
        layoutProfile.setRightRowPositionSpace(storage.getInt(layoutPrefix + LAYOUT_RIGHT_ROW_POSITION_SPACE, layoutProfile.getRightRowPositionSpace()));

        layoutProfile.setHalfSpaceGroupSize(storage.getInt(layoutPrefix + LAYOUT_HALF_SPACE_GROUP_SIZE, layoutProfile.getHalfSpaceGroupSize()));
        layoutProfile.setSpaceGroupSize(storage.getInt(layoutPrefix + LAYOUT_SPACE_GROUP_SIZE, layoutProfile.getSpaceGroupSize()));
        layoutProfile.setDoubleSpaceGroupSize(storage.getInt(layoutPrefix + LAYOUT_DOUBLE_SPACE_GROUP_SIZE, layoutProfile.getDoubleSpaceGroupSize()));

        return layoutProfile;
    }

    public void setLayoutProfile(int profileIndex, DefaultSectionCodeAreaLayoutProfile layoutProfile) {
        String layoutPrefix = KEY_LAYOUT_VALUE_PREFIX + String.valueOf(profileIndex) + ".";
        storage.putBoolean(layoutPrefix + LAYOUT_SHOW_HEADER, layoutProfile.isShowHeader());
        storage.putBoolean(layoutPrefix + LAYOUT_SHOW_ROW_POSITION, layoutProfile.isShowRowPosition());

        storage.putInt(layoutPrefix + LAYOUT_TOP_HEADER_SPACE, layoutProfile.getTopHeaderSpace());
        storage.putInt(layoutPrefix + LAYOUT_BOTTOM_HEADER_SPACE, layoutProfile.getBottomHeaderSpace());
        storage.putInt(layoutPrefix + LAYOUT_LEFT_ROW_POSITION_SPACE, layoutProfile.getLeftRowPositionSpace());
        storage.putInt(layoutPrefix + LAYOUT_RIGHT_ROW_POSITION_SPACE, layoutProfile.getRightRowPositionSpace());

        storage.putInt(layoutPrefix + LAYOUT_HALF_SPACE_GROUP_SIZE, layoutProfile.getHalfSpaceGroupSize());
        storage.putInt(layoutPrefix + LAYOUT_SPACE_GROUP_SIZE, layoutProfile.getSpaceGroupSize());
        storage.putInt(layoutPrefix + LAYOUT_DOUBLE_SPACE_GROUP_SIZE, layoutProfile.getDoubleSpaceGroupSize());
    }

    public void removeLayoutProfile(int profileIndex) {
        String layoutPrefix = KEY_LAYOUT_VALUE_PREFIX + String.valueOf(profileIndex) + ".";
        storage.remove(layoutPrefix + LAYOUT_SHOW_HEADER);
        storage.remove(layoutPrefix + LAYOUT_SHOW_ROW_POSITION);

        storage.remove(layoutPrefix + LAYOUT_TOP_HEADER_SPACE);
        storage.remove(layoutPrefix + LAYOUT_BOTTOM_HEADER_SPACE);
        storage.remove(layoutPrefix + LAYOUT_LEFT_ROW_POSITION_SPACE);
        storage.remove(layoutPrefix + LAYOUT_RIGHT_ROW_POSITION_SPACE);

        storage.remove(layoutPrefix + LAYOUT_HALF_SPACE_GROUP_SIZE);
        storage.remove(layoutPrefix + LAYOUT_SPACE_GROUP_SIZE);
        storage.remove(layoutPrefix + LAYOUT_DOUBLE_SPACE_GROUP_SIZE);
    }

    @Override
    public void copyTo(OptionsData options) {
        CodeAreaLayoutOptions with = (CodeAreaLayoutOptions) options;
        with.setLayoutProfilesList(getLayoutProfilesList());
        with.setSelectedProfile(getSelectedProfile());
    }
}
