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
package org.exbin.bined.jaguif.compare.gui;

import org.exbin.jaguif.utils.TestApplication;
import org.exbin.jaguif.utils.UtilsModule;
import org.exbin.jaguif.utils.WindowUtils;
import org.junit.Test;

/**
 * Test for BinEdDiffPanel.
 */
public class BinEdDiffPanelTest {

    @Test
    public void testPanel() {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.jaguif.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.jaguif.language.api.TestLanguageModule());
            testApplication.addModule(org.exbin.jaguif.options.api.OptionsModuleApi.MODULE_ID, new org.exbin.jaguif.options.api.TestOptionsModule());
            testApplication.addModule(org.exbin.jaguif.statusbar.api.StatusBarModuleApi.MODULE_ID, new org.exbin.jaguif.statusbar.StatusBarModule());
            testApplication.addModule(org.exbin.jaguif.context.api.ContextModuleApi.MODULE_ID, new org.exbin.jaguif.context.ContextModule());
            testApplication.addModule(org.exbin.jaguif.contribution.api.ContributionModuleApi.MODULE_ID, new org.exbin.jaguif.contribution.ContributionModule());
            testApplication.addModule(org.exbin.jaguif.action.api.ActionModuleApi.MODULE_ID, new org.exbin.jaguif.action.ActionModule());
            testApplication.addModule(org.exbin.bined.jaguif.component.BinedComponentModule.MODULE_ID, new org.exbin.bined.jaguif.component.BinedComponentModule());
            WindowUtils.wrapInWindow(new BinEdDiffPanel());
        });
    }
}
