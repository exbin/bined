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

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.utils.ComponentProvider;

/**
 * Binary data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinaryDataComponent extends ContextComponent, ComponentProvider, CodeTypeState, NonprintablesState {

    /**
     * Returns code area component.
     *
     * @return code area component
     */
    @Nonnull
    CodeAreaCore getCodeArea();

    /**
     * Returns undo redo if available.
     *
     * @return undo redo
     */
    @Nonnull
    Optional<BinaryDataUndoRedo> getUndoRedo();

    /**
     * Returns assigned extension.
     *
     * @param <T> extension type
     * @param clazz extension class
     * @return extension instance
     */
    @Nonnull
    <T extends BinEdComponentExtension> T getComponentExtension(Class<T> clazz);
}
