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
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Color layout profile options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaColorProfileOptions implements OptionsData {

    private CodeAreaColorOptions options;
    private final List<ProfileRecord> profileRecords = new ArrayList<>();
    private int selectedProfile = -1;

    public CodeAreaColorProfileOptions(OptionsStorage storage) {
        options = new CodeAreaColorOptions(storage);
    }

    public CodeAreaColorProfileOptions(CodeAreaColorOptions options) {
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
    public SectionCodeAreaColorProfile getColorsProfile(int index) {
        ProfileRecord record = profileRecords.get(index);
        if (record.profile == null) {
            // Lazy loading
            record = new ProfileRecord(record.name, options.getColorsProfile(index));
            profileRecords.set(index, record);
        }

        return record.profile;
    }

    public void setColorsProfile(int index, SectionCodeAreaColorProfile colorProfile) {
        ProfileRecord record = profileRecords.get(index);
        record = new ProfileRecord(record.name, colorProfile);
        profileRecords.set(index, record);
    }

    public void removeColorsProfile(int index) {
        // Load all lazy records after changed index
        for (int i = index + 1; i < profileRecords.size(); i++) {
            ProfileRecord record = profileRecords.get(i);
            if (record.profile == null) {
                record = new ProfileRecord(record.name, options.getColorsProfile(i));
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
                record = new ProfileRecord(record.name, options.getColorsProfile(i));
                profileRecords.set(i, record);
            }
        }
    }

    public void clearProfiles() {
        profileRecords.clear();
    }

    public void addProfile(String profileName, SectionCodeAreaColorProfile colorProfile) {
        profileRecords.add(new ProfileRecord(profileName, colorProfile));
    }

    public void loadFromPreferences(CodeAreaColorOptions preferences) {
        this.options = preferences;
        profileRecords.clear();
        List<String> colorProfilesList = preferences.getColorProfilesList();
        colorProfilesList.forEach((name) -> {
            profileRecords.add(new ProfileRecord(name, null));
        });
        selectedProfile = preferences.getSelectedProfile();
    }

    public void saveToPreferences(CodeAreaColorOptions preferences) {
        preferences.setSelectedProfile(selectedProfile);
        preferences.setColorProfilesList(getProfileNames());
        for (int i = 0; i < profileRecords.size(); i++) {
            ProfileRecord record = profileRecords.get(i);
            SectionCodeAreaColorProfile profile = record.profile;
            if (profile != null) {
                preferences.setColorsProfile(i, record.profile);
            }
        }
    }

    @Override
    public void copyTo(OptionsData options) {
        CodeAreaColorProfileOptions with = (CodeAreaColorProfileOptions) options;
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
        private final SectionCodeAreaColorProfile profile;

        public ProfileRecord(String name, SectionCodeAreaColorProfile profile) {
            this.name = name;
            this.profile = profile;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public SectionCodeAreaColorProfile getProfile() {
            return profile;
        }
    }
}
