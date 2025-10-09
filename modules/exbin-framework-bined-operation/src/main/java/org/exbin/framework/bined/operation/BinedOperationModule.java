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
import org.exbin.framework.bined.action.ClipboardCodeActions;
import org.exbin.framework.bined.operation.action.InsertDataAction;
import org.exbin.framework.bined.operation.action.ConvertDataAction;
import org.exbin.framework.bined.operation.action.CopyAsAction;
import org.exbin.framework.bined.operation.action.PasteFromAction;
import org.exbin.framework.bined.operation.method.RandomDataMethod;
import org.exbin.framework.bined.operation.method.SimpleFillDataMethod;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.bined.operation.api.CopyAsDataMethod;
import org.exbin.framework.bined.operation.api.InsertDataMethod;
import org.exbin.framework.bined.operation.api.PasteFromDataMethod;
import org.exbin.framework.bined.operation.method.Base64DataMethod;
import org.exbin.framework.bined.operation.method.BitSwappingDataMethod;
import org.exbin.framework.bined.operation.method.CompressionDataMethod;
import org.exbin.framework.bined.operation.method.CopyAsTextDataMethod;
import org.exbin.framework.bined.operation.method.DateTimeConversionMethod;
import org.exbin.framework.bined.operation.method.PasteFromTextDataMethod;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule.NextToMode;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.menu.api.MenuManagement;


@ParametersAreNonnullByDefault
public class BinedOperationModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedOperationModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private final List<InsertDataMethod> insertDataMethods = new ArrayList<>();
    private final List<ConvertDataMethod> convertDataMethods = new ArrayList<>();
    private final List<CopyAsDataMethod> copyAsDataMethods = new ArrayList<>();
    private final List<PasteFromDataMethod> pasteFromDataMethods = new ArrayList<>();

    public BinedOperationModule() {
    }

    public void addBasicMethods() {
        SimpleFillDataMethod simpleFillDataMethod = new SimpleFillDataMethod();
        addInsertDataMethod(simpleFillDataMethod);
        RandomDataMethod randomDataMethod = new RandomDataMethod();
        addInsertDataMethod(randomDataMethod);
        BitSwappingDataMethod bitSwappingDataMethod = new BitSwappingDataMethod();
        addConvertDataMethod(bitSwappingDataMethod);
        Base64DataMethod base64DataMethod = new Base64DataMethod();
        addConvertDataMethod(base64DataMethod);
        DateTimeConversionMethod dateTimeConversionMethod = new DateTimeConversionMethod();
        addConvertDataMethod(dateTimeConversionMethod);
        CompressionDataMethod compressionDataMethod = new CompressionDataMethod();
        addConvertDataMethod(compressionDataMethod);
        CopyAsTextDataMethod copyAsTextDataMethod = new CopyAsTextDataMethod();
        addCopyAsDataMethod(copyAsTextDataMethod);
        PasteFromTextDataMethod fromTextDataMethod = new PasteFromTextDataMethod();
        addPasteFromDataMethod(fromTextDataMethod);
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

    @Nonnull
    private AbstractAction createCopyAsAction() {
        ensureSetup();
        CopyAsAction copyAsAction = new CopyAsAction();
        copyAsAction.setup(resourceBundle);

        copyAsAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
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
        return copyAsAction;
    }

    @Nonnull
    private AbstractAction createPasteFromAction() {
        ensureSetup();
        PasteFromAction pasteFromAction = new PasteFromAction();
        pasteFromAction.setup(resourceBundle);

        pasteFromAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
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
        return pasteFromAction;
    }

    public void registerBlockEditActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createInsertDataAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.EDIT_OPERATION_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createConvertDataAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.EDIT_OPERATION_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createCopyAsAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, ClipboardCodeActions.CopyAsCodeAction.ACTION_ID));
        contribution = mgmt.registerMenuItem(createPasteFromAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, ClipboardCodeActions.PasteFromCodeAction.ACTION_ID));
    }

    public void registerBlockEditPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createInsertDataAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        contribution = mgmt.registerMenuItem(createConvertDataAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        contribution = mgmt.registerMenuItem(createCopyAsAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_EDIT_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, ClipboardCodeActions.CopyAsCodeAction.ACTION_ID));
        contribution = mgmt.registerMenuItem(createPasteFromAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_EDIT_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, ClipboardCodeActions.PasteFromCodeAction.ACTION_ID));
    }

    public void addInsertDataMethod(InsertDataMethod insertDataMethod) {
        insertDataMethods.add(insertDataMethod);
    }

    public void addConvertDataMethod(ConvertDataMethod convertDataMethod) {
        convertDataMethods.add(convertDataMethod);
    }

    public void addCopyAsDataMethod(CopyAsDataMethod copyAsDataMethod) {
        copyAsDataMethods.add(copyAsDataMethod);
    }

    public void addPasteFromDataMethod(PasteFromDataMethod pasteFromDataMethod) {
        pasteFromDataMethods.add(pasteFromDataMethod);
    }

    @Nonnull
    public List<InsertDataMethod> getInsertDataMethods() {
        return insertDataMethods;
    }

    @Nonnull
    public List<ConvertDataMethod> getConvertDataMethods() {
        return convertDataMethods;
    }

    @Nonnull
    public List<CopyAsDataMethod> getCopyAsDataMethods() {
        return copyAsDataMethods;
    }

    @Nonnull
    public List<PasteFromDataMethod> getPasteFromDataMethods() {
        return pasteFromDataMethods;
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
