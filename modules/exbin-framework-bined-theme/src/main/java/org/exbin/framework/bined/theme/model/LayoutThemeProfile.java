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
package org.exbin.framework.bined.theme.model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;

/**
 * Layout theme profile.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LayoutThemeProfile implements ThemeProfile {

    private String profileName;
    private boolean visible = true;
    private DefaultSectionCodeAreaLayoutProfile layoutProfile;

    public LayoutThemeProfile(String profileName, DefaultSectionCodeAreaLayoutProfile layoutProfile) {
        this.profileName = profileName;
        this.layoutProfile = layoutProfile;
    }

    @Nonnull
    @Override
    public String getProfileName() {
        return profileName;
    }

    @Nonnull
    public DefaultSectionCodeAreaLayoutProfile getLayoutProfile() {
        return layoutProfile;
    }

    @Override
    public void copyTo(ThemeProfile targetProfile) {
        ((LayoutThemeProfile) targetProfile).profileName = profileName;
        ((LayoutThemeProfile) targetProfile).layoutProfile = layoutProfile;
    }
}
