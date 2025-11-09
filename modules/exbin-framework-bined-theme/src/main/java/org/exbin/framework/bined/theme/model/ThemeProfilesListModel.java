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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;

/**
 * Theme profile list model.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ThemeProfilesListModel extends AbstractListModel<ThemeProfile> {

    protected final List<ThemeProfile> profiles = new ArrayList<>();

    public ThemeProfilesListModel() {
    }

    @Override
    public int getSize() {
        if (profiles == null) {
            return 0;
        }
        return profiles.size();
    }

    public boolean isEmpty() {
        return profiles == null || profiles.isEmpty();
    }

    @Nullable
    @Override
    public ThemeProfile getElementAt(int index) {
        return profiles.get(index);
    }

    @Nonnull
    public List<ThemeProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ThemeProfile> profiles) {
        int size = this.profiles.size();
        if (size > 0) {
            this.profiles.clear();
            fireIntervalRemoved(this, 0, size - 1);
        }
        int profilesSize = profiles.size();
        if (profilesSize > 0) {
            this.profiles.addAll(profiles);
            fireIntervalAdded(this, 0, profilesSize - 1);
        }
    }

    public void addAll(List<ThemeProfile> list, int index) {
        if (index >= 0) {
            profiles.addAll(index, list);
            fireIntervalAdded(this, index, list.size() + index);
        } else {
            profiles.addAll(list);
            fireIntervalAdded(this, profiles.size() - list.size(), profiles.size());
        }
    }

    public void removeIndices(int[] indices) {
        if (indices.length == 0) {
            return;
        }
        Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            profiles.remove(indices[i]);
            fireIntervalRemoved(this, indices[i], indices[i]);
        }
    }

    public void remove(int index) {
        profiles.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void add(int index, ThemeProfile item) {
        profiles.add(index, item);
        fireIntervalAdded(this, index, index);
    }

    public void add(ThemeProfile item) {
        profiles.add(item);
        int index = profiles.size() - 1;
        fireIntervalAdded(this, index, index);
    }

    public void notifyProfileModified(int index) {
        fireContentsChanged(this, index, index);
    }
}
