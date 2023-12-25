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
package org.exbin.framework.bined.makro.preferences;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.bined.makro.model.MakroRecord;
import org.exbin.framework.bined.makro.options.MakroOptions;

/**
 * Makro preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MakroPreferences implements MakroOptions {

    public static final String PREFERENCES_MAKROS_COUNT = "makrosCount";
    public static final String PREFERENCES_MAKRO_VALUE_PREFIX = "makro.";

    public static final String MAKRO_NAME = "name";
    public static final String STEP = "step";

    private final Preferences preferences;

    public MakroPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public int getMakrosCount() {
        return preferences.getInt(PREFERENCES_MAKROS_COUNT, 0);
    }

    @Override
    public MakroRecord getMakroRecord(int index) {
        String prefix = PREFERENCES_MAKRO_VALUE_PREFIX + index + ".";
        String name = preferences.get(prefix + MAKRO_NAME, "");
        MakroRecord makroRecord = new MakroRecord(name);

        List<String> steps = new ArrayList<>();
        int stepIndex = 1;
        while (true) {
            String line = preferences.get(prefix + STEP + "." + stepIndex, "");
            if (!line.trim().isEmpty()) {
                steps.add(line);
                stepIndex++;
            } else {
                break;
            }
        }
        makroRecord.setSteps(steps);

        return makroRecord;
    }

    @Override
    public void setMakrosCount(int count) {
        preferences.putInt(PREFERENCES_MAKROS_COUNT, count);
    }

    @Override
    public void setMakroRecord(int index, MakroRecord record) {
        String prefix = PREFERENCES_MAKRO_VALUE_PREFIX + index + ".";
        preferences.put(prefix + MAKRO_NAME, record.getName());

        List<String> steps = record.getSteps();
        int stepIndex = 1;
        for (String step : steps) {
            preferences.put(prefix + STEP + "." + stepIndex, step);
            stepIndex++;
        }

        String oldLine;
        do {
            oldLine = preferences.get(prefix + STEP + "." + stepIndex, "");
            preferences.remove(prefix + STEP + "." + stepIndex);
        } while (!oldLine.trim().isEmpty());
    }
}
