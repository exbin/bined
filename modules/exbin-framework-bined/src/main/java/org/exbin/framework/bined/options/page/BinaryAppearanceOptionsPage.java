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
package org.exbin.framework.bined.options.page;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.options.BinaryAppearanceOptions;
import org.exbin.framework.bined.options.gui.BinaryAppearanceOptionsPanel;
import org.exbin.framework.bined.service.BinaryAppearanceService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Binary appearance options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryAppearanceOptionsPage implements DefaultOptionsPage<BinaryAppearanceOptions> {

    public static final String PAGE_ID = "binaryAppearance";

    private BinaryAppearanceService binaryAppearanceService;

    public void setBinaryAppearanceService(BinaryAppearanceService binaryAppearanceService) {
        this.binaryAppearanceService = binaryAppearanceService;
    }

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<BinaryAppearanceOptions> createComponent() {
        return new BinaryAppearanceOptionsPanel();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(BinaryAppearanceOptionsPanel.class);
    }

    @Nonnull
    @Override
    public BinaryAppearanceOptions createOptions() {
        return new BinaryAppearanceOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, BinaryAppearanceOptions options) {
        new BinaryAppearanceOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, BinaryAppearanceOptions options) {
        options.copyTo(new BinaryAppearanceOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(BinaryAppearanceOptions options) {
        binaryAppearanceService.setWordWrapMode(options.isLineWrapping());
    }
}
