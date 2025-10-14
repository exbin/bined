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
package org.exbin.framework.bined.viewer.settings;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.PositionCodeType;
import org.exbin.framework.bined.RelativePositionMode;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Go to position options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToPositionOptions implements SettingsOptions {

    public static final String KEY_GO_TO_BINARY_POSITION_MODE = "goToBinaryPositionMode";
    public static final String KEY_GO_TO_BINARY_POSITION_VALUE_TYPE = "goToBinaryPositionValueType";

    private final OptionsStorage storage;

    public GoToPositionOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public RelativePositionMode getPositionMode() {
        RelativePositionMode defaultMode = RelativePositionMode.FROM_START;
        try {
            return RelativePositionMode.valueOf(storage.get(KEY_GO_TO_BINARY_POSITION_MODE, defaultMode.name()));
        } catch (Exception ex) {
            Logger.getLogger(GoToPositionOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultMode;
        }
    }

    public void setPositionMode(RelativePositionMode positionMode) {
        storage.put(KEY_GO_TO_BINARY_POSITION_MODE, positionMode.name());
    }

    @Nonnull
    public PositionCodeType getGoToBinaryPositionValueType() {
        PositionCodeType defaultCodeType = PositionCodeType.DECIMAL;
        try {
            return PositionCodeType.valueOf(storage.get(KEY_GO_TO_BINARY_POSITION_VALUE_TYPE, defaultCodeType.name()));
        } catch (Exception ex) {
            Logger.getLogger(GoToPositionOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setGoToBinaryPositionValueType(PositionCodeType goToBinaryPositionValueType) {
        storage.put(KEY_GO_TO_BINARY_POSITION_VALUE_TYPE, goToBinaryPositionValueType.name());
    }

    @Override
    public void copyTo(SettingsOptions options) {
        GoToPositionOptions with = (GoToPositionOptions) options;
        with.setGoToBinaryPositionValueType(getGoToBinaryPositionValueType());
        with.setPositionMode(getPositionMode());
    }
}
