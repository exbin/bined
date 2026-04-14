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
package org.exbin.bined.jaguif.operation.code;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.PluginModule;
import org.exbin.bined.jaguif.operation.method.BinedOperationMethodModule;
import org.exbin.bined.jaguif.operation.code.method.CopyAsCodeDataMethod;
import org.exbin.bined.jaguif.operation.code.method.PasteFromCodeDataMethod;
import org.exbin.jaguif.language.api.LanguageModuleApi;

/**
 * Module for operations to copy or paste from language programming codes.
 */
@ParametersAreNonnullByDefault
public class BinedOperationCodeModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedOperationCodeModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedOperationCodeModule() {
    }

    @Override
    public void register() {
        BinedOperationMethodModule operationMethodModule = App.getModule(BinedOperationMethodModule.class);
        operationMethodModule.addCopyAsDataMethod(new CopyAsCodeDataMethod());
        operationMethodModule.addPasteFromDataMethod(new PasteFromCodeDataMethod());
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOperationCodeModule.class);
        }

        return resourceBundle;
    }
}
