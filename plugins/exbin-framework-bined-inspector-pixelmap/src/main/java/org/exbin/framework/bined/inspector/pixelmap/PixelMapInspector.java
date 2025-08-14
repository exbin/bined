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
package org.exbin.framework.bined.inspector.pixelmap;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.operation.undo.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.bined.inspector.pixelmap.gui.PixelMapPanel;
import org.exbin.framework.bined.inspector.BinEdInspector;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Pixel map inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PixelMapInspector implements BinEdInspector {

    private PixelMapPanel component;

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (component == null) {
            component = new PixelMapPanel();
        }
        return component;
    }

    @Override
    public void setCodeArea(CodeAreaCore codeArea, BinaryDataUndoRedo undoRedo) {
        component.setCodeArea(codeArea);
    }

    @Override
    public void activateSync() {
        // TODO
    }

    @Override
    public void deactivateSync() {
        // TODO
    }

    @Override
    public void onInitFromOptions(OptionsStorage options) {
    }
}
