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
package org.exbin.bined.jaguif.document.status.gui;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.jaguif.component.status.gui.BinaryDataSizeComponent;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.document.api.ContextDocument;
import static org.exbin.jaguif.statusbar.api.StatusBarComponent.KEY_CONTEXT_CHANGE;

/**
 * BinEd file processing mode status component.
 */
@ParametersAreNonnullByDefault
public class BinaryDocumentSizeComponent extends BinaryDataSizeComponent {

    public BinaryDocumentSizeComponent() {
        final ContextChange parentContextChange = (ContextChange) getValue(KEY_CONTEXT_CHANGE);
        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextDocument.class, (ContextDocument instance) -> {
                    if (instance instanceof BinaryFileDocument) {
                        updateForDocument((BinaryFileDocument) instance);
                    } else {
                        clear();
                    }
                });
                /* registrar.registerStateUpdateListener(ContextDocument.class, (ContextDocument instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinaryFileDocument && (updateType == BinaryFileDocument.UpdateType.ORIGINAL_SIZE)) {
                        updateForDocument((BinaryFileDocument) instance);
                    }
                }); */
                parentContextChange.register(registrar);
            }

            private void updateForDocument(BinaryFileDocument document) {
                originalDataSize = document.getDocumentOriginalSize();
                update();
            }
        });
    }
}
