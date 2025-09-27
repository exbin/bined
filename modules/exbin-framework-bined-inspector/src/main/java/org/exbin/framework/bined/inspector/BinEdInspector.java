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
package org.exbin.framework.bined.inspector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * BinEd inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinEdInspector {

    /**
     * Returns visual component.
     *
     * @return component
     */
    @Nonnull
    JComponent getComponent();

    /**
     * Sets code area.
     *
     * @param codeArea code area
     * @param undoRedo undo redo
     */
    void setCodeArea(CodeAreaCore codeArea, @Nullable BinaryDataUndoRedo undoRedo);

    /**
     * Activates synchronization.
     */
    void activateSync();

    /**
     * Deactivates synchronization.
     */
    void deactivateSync();

    /**
     * Initializes from provider options.
     *
     * @param options options
     */
    void onInitFromOptions(OptionsStorage options);
}
