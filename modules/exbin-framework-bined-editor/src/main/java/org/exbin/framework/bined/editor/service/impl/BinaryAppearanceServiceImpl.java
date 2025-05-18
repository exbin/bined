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
package org.exbin.framework.bined.editor.service.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.bined.editor.BinedEditorModule;
import org.exbin.framework.bined.editor.service.BinaryAppearanceService;
import org.exbin.framework.editor.api.EditorProvider;

/**
 * Appearance service implementation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryAppearanceServiceImpl implements BinaryAppearanceService {

    private final BinedEditorModule binedModule;
    private final EditorProvider editorProvider;

    public BinaryAppearanceServiceImpl(BinedEditorModule binedModule, EditorProvider editorProvider) {
        this.binedModule = binedModule;
        this.editorProvider = editorProvider;
    }

    @Override
    public boolean getWordWrapMode() {
        return false; //((BinEdEditorProvider) editorProvider).isWordWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        //((BinEdEditorProvider) editorProvider).setWordWrapMode(mode);

        binedModule.createRowWrappingAction().putValue(Action.SELECTED_KEY, mode);
    }
}
