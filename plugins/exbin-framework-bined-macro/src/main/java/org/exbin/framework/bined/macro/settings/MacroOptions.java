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
package org.exbin.framework.bined.macro.settings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.macro.model.MacroRecord;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Macro options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MacroOptions implements SettingsOptions {

    public static final String KEY_MACROS_COUNT = "macrosCount";
    public static final String KEY_MACRO_VALUE_PREFIX = "macro.";

    public static final String MACRO_NAME = "name";
    public static final String STEP = "step";

    private final OptionsStorage storage;

    public MacroOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public int getMacrosCount() {
        return storage.getInt(KEY_MACROS_COUNT, 0);
    }

    @Nonnull
    public MacroRecord getMacroRecord(int index) {
        String prefix = KEY_MACRO_VALUE_PREFIX + index + ".";
        String name = storage.get(prefix + MACRO_NAME, "");
        MacroRecord macroRecord = new MacroRecord(name);

        List<String> steps = new ArrayList<>();
        int stepIndex = 1;
        while (true) {
            String line = storage.get(prefix + STEP + "." + stepIndex, "");
            if (!line.trim().isEmpty()) {
                steps.add(line);
                stepIndex++;
            } else {
                break;
            }
        }
        macroRecord.setSteps(steps);

        return macroRecord;
    }

    public void setMacrosCount(int count) {
        storage.putInt(KEY_MACROS_COUNT, count);
    }

    public void setMacroRecord(int index, MacroRecord record) {
        String prefix = KEY_MACRO_VALUE_PREFIX + index + ".";
        storage.put(prefix + MACRO_NAME, record.getName());

        List<String> steps = record.getSteps();
        int stepIndex = 1;
        for (String step : steps) {
            storage.put(prefix + STEP + "." + stepIndex, step);
            stepIndex++;
        }

        String oldLine;
        do {
            oldLine = storage.get(prefix + STEP + "." + stepIndex, "");
            storage.remove(prefix + STEP + "." + stepIndex);
        } while (!oldLine.trim().isEmpty());
    }

    @Override
    public void copyTo(SettingsOptions options) {
        MacroOptions with = (MacroOptions) options;
        int macrosCount = getMacrosCount();
        with.setMacrosCount(macrosCount);
        for (int i = 0; i < macrosCount; i++) {
            with.setMacroRecord(i, getMacroRecord(i));
        }
    }
}
