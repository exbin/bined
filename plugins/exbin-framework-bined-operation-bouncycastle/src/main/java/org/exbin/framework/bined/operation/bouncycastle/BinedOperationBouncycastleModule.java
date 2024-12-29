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
package org.exbin.framework.bined.operation.bouncycastle;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.PluginModule;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.bouncycastle.component.ComputeHashDataMethod;

/**
 * Binary data editor operations module using bouncy castle library.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOperationBouncycastleModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedOperationBouncycastleModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedOperationBouncycastleModule() {
    }

    @Override
    public void register() {
        BinedOperationModule binedOperationModule = App.getModule(BinedOperationModule.class);

        ComputeHashDataMethod computeHashDataMethod = new ComputeHashDataMethod();
        binedOperationModule.addConvertDataComponent(computeHashDataMethod);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOperationBouncycastleModule.class);
        }

        return resourceBundle;
    }
}
