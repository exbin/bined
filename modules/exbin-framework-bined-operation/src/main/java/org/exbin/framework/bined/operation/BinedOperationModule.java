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
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionMenuCreation;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.operation.action.InsertDataAction;
import org.exbin.framework.bined.operation.action.ConvertDataAction;
import org.exbin.framework.bined.operation.component.RandomDataMethod;
import org.exbin.framework.bined.operation.component.SimpleFillDataMethod;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.bined.operation.api.InsertDataMethod;
import org.exbin.framework.bined.operation.component.Base64DataMethod;
import org.exbin.framework.bined.operation.component.BitSwappingDataMethod;

/**
 * Binary data editor operations module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOperationModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedOperationModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    private final List<InsertDataMethod> insertDataComponents = new ArrayList<>();
    private final List<ConvertDataMethod> convertDataComponents = new ArrayList<>();

    public BinedOperationModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        SimpleFillDataMethod simpleFillDataMethod = new SimpleFillDataMethod();
        addInsertDataComponent(simpleFillDataMethod);
        RandomDataMethod randomDataMethod = new RandomDataMethod();
        addInsertDataComponent(randomDataMethod);
        BitSwappingDataMethod bitSwappingDataMethod = new BitSwappingDataMethod();
        addConvertDataComponent(bitSwappingDataMethod);
        Base64DataMethod base64DataMethod = new Base64DataMethod();
        addConvertDataComponent(base64DataMethod);

        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
    }

    @Nonnull
    private AbstractAction createInsertDataAction() {
        ensureSetup();
        InsertDataAction insertDataAction = new InsertDataAction();
        insertDataAction.setup(resourceBundle);

        insertDataAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                SectCodeArea codeArea = binedModule.getActiveCodeArea();
                return menuVariant != BinedModule.PopupMenuVariant.BASIC && codeArea.isEditable() && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        return insertDataAction;
    }

    @Nonnull
    private AbstractAction createConvertDataAction() {
        ensureSetup();
        ConvertDataAction convertDataAction = new ConvertDataAction();
        convertDataAction.setup(resourceBundle);
        convertDataAction.setEditorProvider(editorProvider);

        convertDataAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                SectCodeArea codeArea = binedModule.getActiveCodeArea();
                return menuVariant != BinedModule.PopupMenuVariant.BASIC && codeArea.isEditable() && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        return convertDataAction;
    }

    public void registerBlockEditActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuContribution contribution = actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, createInsertDataAction());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.EDIT_OPERATION_MENU_GROUP_ID));
        contribution = actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, createConvertDataAction());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.EDIT_OPERATION_MENU_GROUP_ID));
    }

    public void registerBlockEditPopupMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuContribution contribution = actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID, createInsertDataAction());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        contribution = actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID, createConvertDataAction());
        actionModule.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
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

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    private void ensureSetup() {
        if (editorProvider == null) {
            getEditorProvider();
        }

        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
