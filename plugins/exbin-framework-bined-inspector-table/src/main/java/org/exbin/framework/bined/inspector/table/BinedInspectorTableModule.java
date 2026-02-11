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
package org.exbin.framework.bined.inspector.table;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.PluginModule;
import org.exbin.framework.bined.inspector.BinEdInspector;
import org.exbin.framework.bined.inspector.BinEdInspectorManager;
import org.exbin.framework.bined.inspector.BinEdInspectorProvider;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.inspector.table.api.ValueRowType;
import org.exbin.framework.bined.inspector.table.value.ByteValueRowType;
import org.exbin.framework.bined.inspector.table.value.DoubleValueRowType;
import org.exbin.framework.bined.inspector.table.value.FloatValueRowType;
import org.exbin.framework.bined.inspector.table.value.IntegerValueRowType;
import org.exbin.framework.bined.inspector.table.value.LongValueRowType;
import org.exbin.framework.bined.inspector.table.value.WordValueRowType;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Binary editor data table inspector module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedInspectorTableModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedInspectorTableModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private final Map<String, ValueRowType> valueRowTypes = new HashMap<>();

    public BinedInspectorTableModule() {
    }

    @Override
    public void register() {
        BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
        BinEdInspectorManager inspectorManager = binedInspectorModule.getBinEdInspectorManager();
        inspectorManager.addInspector(new TableInspectorProvider(resourceBundle));

        registerRowType(new ByteValueRowType());
        registerRowType(new WordValueRowType());
        registerRowType(new IntegerValueRowType());
        registerRowType(new LongValueRowType());
        registerRowType(new FloatValueRowType());
        registerRowType(new DoubleValueRowType());
    }

    public void registerRowType(ValueRowType valueRowType) {
        valueRowTypes.put(valueRowType.getId(), valueRowType);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedInspectorTableModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public Map<String, ValueRowType> getValueRowTypes() {
        return valueRowTypes;
    }
}
