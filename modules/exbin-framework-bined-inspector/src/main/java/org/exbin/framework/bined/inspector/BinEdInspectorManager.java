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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * BinEd inspector manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdInspectorManager {

    private final List<BinEdInspectorProvider> inspectorProviders = new ArrayList<>();
    
    public void addInspector(BinEdInspectorProvider inspectorProvider) {
        inspectorProviders.add(inspectorProvider);
    }

    @Nonnull
    public List<BinEdInspectorProvider> getInspectorProviders() {
        return inspectorProviders;
    }
    
    @Nonnull
    public <T extends BinEdInspectorProvider> Optional<T> getInspectorProvider(Class<T> clazz) {
        for (BinEdInspectorProvider inspectorProvider : inspectorProviders) {
            if (clazz.isInstance(inspectorProvider)) {
                return Optional.of(clazz.cast(inspectorProvider));
            }
        }

        return Optional.empty();
    }

    public void removeAllInspectors() {
        inspectorProviders.clear();
    }
}
