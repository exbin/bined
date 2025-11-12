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
package org.exbin.framework.bined.theme.settings.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.utils.WindowUtils;
import org.junit.Test;

/**
 * Test for ThemeProfilesSettingsPanel.
 */
public class ThemeProfilesSettingsPanelTest {

    @Test
    public void testPanel() {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new ThemeProfilesSettingsPanel());
        });

        Thread[] uiThread = new Thread[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                uiThread[0] = Thread.currentThread();
            });
            uiThread[0].join();
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(ThemeProfilesSettingsPanelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
