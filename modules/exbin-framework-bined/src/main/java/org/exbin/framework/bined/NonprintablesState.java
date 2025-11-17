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
package org.exbin.framework.bined;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.StateChangeType;

/**
 * Nonprintables state.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface NonprintablesState {

    /**
     * Returns show nonprintables characters state.
     *
     * @return show nonprintables
     */
    boolean isShowNonprintables();

    /**
     * Sets show nonprintables characters state.
     *
     * @param showNonprintables show nonprintables
     */
    void setShowNonprintables(boolean showNonprintables);

    public enum ChangeType implements StateChangeType {
        NONPRINTABLES
    }
}
