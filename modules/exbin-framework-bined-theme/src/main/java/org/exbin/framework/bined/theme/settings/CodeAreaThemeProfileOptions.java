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
package org.exbin.framework.bined.theme.settings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Theme layout profile options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaThemeProfileOptions implements SettingsOptions {

    private CodeAreaThemeOptions options;
    private final List<ProfileRecord> profileRecords = new ArrayList<>();
    private int selectedProfile = -1;

    public CodeAreaThemeProfileOptions(OptionsStorage storage) {
        options = new CodeAreaThemeOptions(storage);
    }

    public CodeAreaThemeProfileOptions(CodeAreaThemeOptions options) {
        this.options = options;
    }

    @Nonnull
    public List<String> getProfileNames() {
        List<String> profilesNames = new ArrayList<>();
        profileRecords.forEach((profile) -> {
            profilesNames.add(profile.getName());
        });
        return profilesNames;
    }

    @Nonnull
    public SectionCodeAreaThemeProfile getThemeProfile(int index) {
        ProfileRecord record = profileRecords.get(index);
        if (record.profile == null) {
            // Lazy loading
            record = new ProfileRecord(record.name, options.getThemeProfile(index));
            profileRecords.set(index, record);
        }

        return record.profile;
    }

    public void setThemeProfile(int index, SectionCodeAreaThemeProfile themeProfile) {
        ProfileRecord record = profileRecords.get(index);
        record = new ProfileRecord(record.name, themeProfile);
        profileRecords.set(index, record);
    }

    public void removeThemeProfile(int index) {
        // Load all lazy records after changed index
        for (int i = index + 1; i < profileRecords.size(); i++) {
            ProfileRecord record = profileRecords.get(i);
            if (record.profile == null) {
                record = new ProfileRecord(record.name, options.getThemeProfile(i));
                profileRecords.set(i, record);
            }
        }
        if (selectedProfile == index) {
            selectedProfile = -1;
        } else if (selectedProfile > index) {
            selectedProfile--;
        }
        profileRecords.remove(index);
    }

    public void fullyLoad() {
        for (int i = 0; i < profileRecords.size(); i++) {
            ProfileRecord record = profileRecords.get(i);
            if (record.profile == null) {
                record = new ProfileRecord(record.name, options.getThemeProfile(i));
                profileRecords.set(i, record);
            }
        }
    }

    public int getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(int profileIndex) {
        selectedProfile = profileIndex;
    }

    public void clearProfiles() {
        profileRecords.clear();
    }

    public void addProfile(String profileName, SectionCodeAreaThemeProfile themeProfile) {
        profileRecords.add(new ProfileRecord(profileName, themeProfile));
    }

    public void loadFromPreferences(CodeAreaThemeOptions options) {
        this.options = options;
        profileRecords.clear();
        List<String> themeProfilesList = options.getThemeProfilesList();
        themeProfilesList.forEach((name) -> {
            profileRecords.add(new ProfileRecord(name, null));
        });
        selectedProfile = options.getSelectedProfile();
    }

    public void saveToPreferences(CodeAreaThemeOptions options) {
        options.setSelectedProfile(selectedProfile);
        options.setThemeProfilesList(getProfileNames());
        for (int i = 0; i < profileRecords.size(); i++) {
            ProfileRecord record = profileRecords.get(i);
            SectionCodeAreaThemeProfile profile = record.profile;
            if (profile != null) {
                options.setThemeProfile(i, record.profile);
            }
        }
    }

    @Override
    public void copyTo(SettingsOptions options) {
        CodeAreaThemeProfileOptions with = (CodeAreaThemeProfileOptions) options;
        with.clearProfiles();
        for (int i = 0; i < profileRecords.size(); i++) {
            ProfileRecord record = profileRecords.get(i);
            with.addProfile(record.name, record.profile);
        }
    }

    @Immutable
    @ParametersAreNonnullByDefault
    public static class ProfileRecord {

        private final String name;
        private final SectionCodeAreaThemeProfile profile;

        public ProfileRecord(String name, SectionCodeAreaThemeProfile profile) {
            this.name = name;
            this.profile = profile;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public SectionCodeAreaThemeProfile getProfile() {
            return profile;
        }
    }
}
