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
package org.exbin.framework.bined.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.operation.action.InsertDataAction;
import org.exbin.framework.bined.operation.action.ConvertDataAction;
import org.exbin.framework.bined.operation.component.RandomDataMethod;
import org.exbin.framework.bined.operation.component.SimpleFillDataMethod;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.bined.operation.api.InsertDataMethod;
import org.exbin.framework.bined.operation.component.Base64DataMethod;
import org.exbin.framework.bined.operation.component.BitSwappingDataMethod;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;

/**
 * Binary data editor operations module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOperationModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedOperationModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private final List<InsertDataMethod> insertDataComponents = new ArrayList<>();
    private final List<ConvertDataMethod> convertDataComponents = new ArrayList<>();

    public BinedOperationModule() {
    }

    public void addBasicMethods() {
        SimpleFillDataMethod simpleFillDataMethod = new SimpleFillDataMethod();
        addInsertDataComponent(simpleFillDataMethod);
        RandomDataMethod randomDataMethod = new RandomDataMethod();
        addInsertDataComponent(randomDataMethod);
        BitSwappingDataMethod bitSwappingDataMethod = new BitSwappingDataMethod();
        addConvertDataComponent(bitSwappingDataMethod);
        Base64DataMethod base64DataMethod = new Base64DataMethod();
        addConvertDataComponent(base64DataMethod);
    }

    @Nonnull
    private AbstractAction createInsertDataAction() {
        ensureSetup();
        InsertDataAction insertDataAction = new InsertDataAction();
        insertDataAction.setup(resourceBundle);

        insertDataAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                return menuVariant != BinedModule.PopupMenuVariant.BASIC && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
            }
        });
        return insertDataAction;
    }

    @Nonnull
    private AbstractAction createConvertDataAction() {
        ensureSetup();
        ConvertDataAction convertDataAction = new ConvertDataAction();
        convertDataAction.setup(resourceBundle);

        convertDataAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                return menuVariant != BinedModule.PopupMenuVariant.BASIC && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
            }
        });
        return convertDataAction;
    }

    public void registerBlockEditActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(createInsertDataAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.EDIT_OPERATION_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createConvertDataAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.EDIT_OPERATION_MENU_GROUP_ID));
    }

    public void registerBlockEditPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(createInsertDataAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        contribution = mgmt.registerMenuItem(createConvertDataAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
    }

    public void addInsertDataComponent(InsertDataMethod insertDataComponent) {
        insertDataComponents.add(insertDataComponent);
    }

    public void addConvertDataComponent(ConvertDataMethod convertDataComponent) {
        convertDataComponents.add(convertDataComponent);
    }

    @Nonnull
    public List<InsertDataMethod> getInsertDataComponents() {
        return insertDataComponents;
    }

    @Nonnull
    public List<ConvertDataMethod> getConvertDataComponents() {
        return convertDataComponents;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOperationModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
