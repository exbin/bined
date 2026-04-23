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
package org.exbin.bined.jaguif.operation.method;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.contribution.CopyAsCodeContribution;
import org.exbin.bined.jaguif.component.contribution.PasteFromCodeContribution;
import org.exbin.bined.jaguif.operation.method.basic.RandomDataMethod;
import org.exbin.bined.jaguif.operation.method.basic.SimpleFillDataMethod;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.bined.jaguif.operation.method.api.ConvertDataMethod;
import org.exbin.bined.jaguif.operation.method.api.CopyAsDataMethod;
import org.exbin.bined.jaguif.operation.method.api.InsertDataMethod;
import org.exbin.bined.jaguif.operation.method.api.PasteFromDataMethod;
import org.exbin.bined.jaguif.operation.method.contribution.ConvertDataContribution;
import org.exbin.bined.jaguif.operation.method.contribution.CopyAsContribution;
import org.exbin.bined.jaguif.operation.method.contribution.InsertDataContribution;
import org.exbin.bined.jaguif.operation.method.contribution.PasteFromContribution;
import org.exbin.bined.jaguif.operation.method.basic.Base64DataMethod;
import org.exbin.bined.jaguif.operation.method.basic.BitSwappingDataMethod;
import org.exbin.bined.jaguif.operation.method.basic.CompressionDataMethod;
import org.exbin.bined.jaguif.operation.method.basic.CopyAsTextDataMethod;
import org.exbin.bined.jaguif.operation.method.basic.DateTimeConversionMethod;
import org.exbin.bined.jaguif.operation.method.basic.PasteFromTextDataMethod;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.RelativeSequenceContributionRule;
import org.exbin.jaguif.contribution.api.RelativeSequenceContributionRule.NextToMode;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;

/**
 * BinEd operation module.
 */
@ParametersAreNonnullByDefault
public class BinedOperationMethodModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedOperationMethodModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private final List<InsertDataMethod> insertDataMethods = new ArrayList<>();
    private final List<ConvertDataMethod> convertDataMethods = new ArrayList<>();
    private final List<CopyAsDataMethod> copyAsDataMethods = new ArrayList<>();
    private final List<PasteFromDataMethod> pasteFromDataMethods = new ArrayList<>();

    public BinedOperationMethodModule() {
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

    public void registerBlockEditActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new InsertDataContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.EDIT_OPERATION_MENU_GROUP_ID));
        contribution = new ConvertDataContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.EDIT_OPERATION_MENU_GROUP_ID));
        contribution = new CopyAsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, CopyAsCodeContribution.CONTRIBUTION_ID));
        contribution = new PasteFromContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, PasteFromCodeContribution.CONTRIBUTION_ID));
    }

    public void registerBlockEditPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = new InsertDataContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        contribution = new ConvertDataContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        contribution = new CopyAsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_EDIT_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, CopyAsCodeContribution.CONTRIBUTION_ID));
        contribution = new PasteFromContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_EDIT_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(NextToMode.AFTER, PasteFromCodeContribution.CONTRIBUTION_ID));
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
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOperationMethodModule.class);
        }

        return resourceBundle;
    }
}
