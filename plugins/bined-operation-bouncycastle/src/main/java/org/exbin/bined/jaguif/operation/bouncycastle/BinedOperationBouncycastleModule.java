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
package org.exbin.bined.jaguif.operation.bouncycastle;

import java.security.Security;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.PluginModule;
import org.exbin.bined.jaguif.operation.method.BinedOperationMethodModule;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.bined.jaguif.operation.bouncycastle.method.ComputeHashDataMethod;
import org.exbin.bined.jaguif.operation.bouncycastle.method.SymmetricEncryptionMethod;
import org.exbin.jaguif.ui.api.UiModuleApi;

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
        // Register Bouncy Castle provider
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        UiModuleApi uiModule = App.getModule(UiModuleApi.class);
        uiModule.addPostInitAction(() -> {
            BinedOperationMethodModule binedOperationModule = App.getModule(BinedOperationMethodModule.class);

            ComputeHashDataMethod computeHashDataMethod = new ComputeHashDataMethod();
            binedOperationModule.addConvertDataMethod(computeHashDataMethod);

            SymmetricEncryptionMethod encryptionMethod = new SymmetricEncryptionMethod();
            binedOperationModule.addConvertDataMethod(encryptionMethod);
        });
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOperationBouncycastleModule.class);
        }

        return resourceBundle;
    }
}
