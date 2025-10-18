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
package org.exbin.framework.bined.viewer.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.viewer.settings.gui.BinaryAppearanceSettingsPanel;
import org.exbin.framework.bined.viewer.service.BinaryAppearanceService;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;

/**
 * Binary appearance settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryAppearanceSettingsComponent implements SettingsComponentProvider<BinaryAppearanceOptions> {

    public static final String COMPONENT_ID = "binaryAppearance";

    private BinaryAppearanceService binaryAppearanceService;

    public void setBinaryAppearanceService(BinaryAppearanceService binaryAppearanceService) {
        this.binaryAppearanceService = binaryAppearanceService;
    }

    @Nonnull
    @Override
    public SettingsComponent<BinaryAppearanceOptions> createComponent() {
        return new BinaryAppearanceSettingsPanel();
    }
}
