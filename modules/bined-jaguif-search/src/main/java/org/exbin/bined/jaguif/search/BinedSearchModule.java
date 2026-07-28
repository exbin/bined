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
package org.exbin.bined.jaguif.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.jspecify.annotations.NullMarked;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.bined.jaguif.document.BinEdFileManager;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.document.BinedDocumentModule;
import org.exbin.jaguif.language.api.LanguageModuleApi;

/**
 * Binary data search module.
 */
@NullMarked
public class BinedSearchModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedSearchModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private final List<FindAgainListener> findAgainListeners = new ArrayList<>();

    public BinedSearchModule() {
    }

    public void registerSearchComponent() {
        BinedDocumentModule binedModule = App.getModule(BinedDocumentModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addBinEdComponentExtension((BinEdComponentPanel component) -> Optional.of(new DefaultBinEdComponentSearch()));
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedSearchModule.class);
        }

        return resourceBundle;
    }

    public List<FindAgainListener> getFindAgainListeners() {
        return findAgainListeners;
    }

    public void addFindAgainListener(FindAgainListener findAgainListener) {
        findAgainListeners.add(findAgainListener);
    }

    public void removeFindAgainListener(FindAgainListener findAgainListener) {
        findAgainListeners.remove(findAgainListener);
    }
    
    public BinarySearchController createBinarySearchController(BinaryDataComponent binaryComponent) {
        return new BinarySearchController(binaryComponent);
    }
}
