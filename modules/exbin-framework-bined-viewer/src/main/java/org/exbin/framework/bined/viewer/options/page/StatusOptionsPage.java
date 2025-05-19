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
package org.exbin.framework.bined.viewer.options.page;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.viewer.options.gui.StatusOptionsPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Status options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusOptionsPage implements DefaultOptionsPage<StatusOptions> {

    public static final String PAGE_ID = "status";

    private java.util.ResourceBundle resourceBundle;
    private BinEdFileManager fileManager;

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setFileManager(BinEdFileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<StatusOptions> createComponent() {
        StatusOptionsPanel panel = new StatusOptionsPanel();

        List<String> cursorPositionCodeTypes = new ArrayList<>();
        cursorPositionCodeTypes.add(resourceBundle.getString("cursorPositionCodeType.octal"));
        cursorPositionCodeTypes.add(resourceBundle.getString("cursorPositionCodeType.decimal"));
        cursorPositionCodeTypes.add(resourceBundle.getString("cursorPositionCodeType.hexadecimal"));
        panel.setCursorPositionCodeTypes(cursorPositionCodeTypes);

        List<String> documentSizeCodeTypes = new ArrayList<>();
        documentSizeCodeTypes.add(resourceBundle.getString("documentSizeCodeType.octal"));
        documentSizeCodeTypes.add(resourceBundle.getString("documentSizeCodeType.decimal"));
        documentSizeCodeTypes.add(resourceBundle.getString("documentSizeCodeType.hexadecimal"));
        panel.setDocumentSizeCodeTypes(documentSizeCodeTypes);

        return panel;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(StatusOptionsPanel.class);
    }

    @Nonnull
    @Override
    public StatusOptions createOptions() {
        return new StatusOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, StatusOptions options) {
        new StatusOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, StatusOptions options) {
        options.copyTo(new StatusOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(StatusOptions options) {
        fileManager.applyPreferencesChanges(options);
    }
}
