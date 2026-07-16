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
package org.exbin.bined.jaguif.component;

import java.util.Optional;
import org.exbin.bined.EditOperation;
import org.jspecify.annotations.NullMarked;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.statusbar.api.StatusBarComponent;
import org.exbin.jaguif.utils.ComponentProvider;

/**
 * Binary data component.
 */
@NullMarked
public interface BinaryDataComponent extends ContextComponent, ComponentProvider, CodeTypeState, NonprintablesState {

    /**
     * Returns code area component.
     *
     * @return code area component
     */
    CodeAreaCore getCodeArea();

    /**
     * Returns undo redo if available.
     *
     * @return undo redo
     */
    Optional<BinaryDataUndoRedo> getUndoRedo();

    /**
     * Returns context management if available.
     *
     * @return context management
     */
    Optional<ActiveContextManagement> getContextManagement();

    /**
     * Returns assigned extension.
     *
     * @param <T> extension type
     * @param clazz extension class
     * @return extension instance
     */
    <T extends BinEdComponentExtension> T getComponentExtension(Class<T> clazz);

    /**
     * Returns status bar component.
     *
     * @param <T> status component type
     * @param clazz status component class
     * @return status bar component
     */
    <T extends StatusBarComponent> Optional<T> getStatusBarComponent(Class<T> clazz);

    /**
     * Sets edit operation.
     *
     * @param editOperation edit operation
     */
    void setEditOperation(EditOperation editOperation);
}
