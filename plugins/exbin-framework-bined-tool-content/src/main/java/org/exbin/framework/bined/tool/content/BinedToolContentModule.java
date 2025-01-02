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
package org.exbin.framework.bined.tool.content;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.PluginModule;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.bined.tool.content.action.ClipboardContentAction;
import org.exbin.framework.bined.tool.content.action.DragDropContentAction;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Binary editor clipboard support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedToolContentModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedToolContentModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedToolContentModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedToolContentModule.class);
        }

        return resourceBundle;
    }

    @Override
    public void register() {
        registerClipboardContentMenu();
        registerDragDropContentMenu();
    }
    
    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public ClipboardContentAction createClipboardContentAction() {
        ensureSetup();
        ClipboardContentAction clipboardContentAction = new ClipboardContentAction();
        clipboardContentAction.setup(resourceBundle);
        return clipboardContentAction;
    }

    @Nonnull
    public DragDropContentAction createDragDropContentAction() {
        ensureSetup();
        DragDropContentAction dragDropContentAction = new DragDropContentAction();
        dragDropContentAction.setup(resourceBundle);
        return dragDropContentAction;
    }

    public void registerClipboardContentMenu() {
        createClipboardContentAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, createClipboardContentAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
    }

    public void registerDragDropContentMenu() {
        createDragDropContentAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, createDragDropContentAction());
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.MIDDLE));
    }
}
