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

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.operation.undo.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.PluginModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.inspector.pixelmap.gui.PixelMapPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.inspector.BinEdInspector;
import org.exbin.framework.bined.inspector.BinEdInspectorManager;
import org.exbin.framework.bined.inspector.BinEdInspectorProvider;

/**
 * Binary editor data pixel map inspector plugin.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedInspectorPixelMapModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedInspectorPixelMapModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedInspectorPixelMapModule() {
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedInspectorPixelMapModule.class);
        }

        return resourceBundle;
    }

    @Override
    public void register() {
        BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
        BinEdInspectorManager inspectorManager = binedInspectorModule.getBinEdInspectorManager();
        inspectorManager.addInspector(new BinEdInspectorProvider() {
            
            private BinEdInspector inspector;

            @Nonnull
            @Override
            public String getName() {
                return "Pixel Map";
            }

            @Nonnull
            @Override
            public BinEdInspector createInspector() {
                if (inspector == null) {
                    inspector = new BinEdInspector() {

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
                    };
                }
                return inspector;
            }
        });
    }
}
