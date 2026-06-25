/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.macro.operation;

import java.util.List;
import org.jspecify.annotations.NullMarked;

/**
 * Macro operation.
 */
@NullMarked
public class MacroOperation {

    private final MacroStep macroStep;
    private final List<Object> parameters;

    public MacroOperation(MacroStep macroStep, List<Object> parameters) {
        this.macroStep = macroStep;
        this.parameters = parameters;
    }

    public MacroStep getMacroStep() {
        return macroStep;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
