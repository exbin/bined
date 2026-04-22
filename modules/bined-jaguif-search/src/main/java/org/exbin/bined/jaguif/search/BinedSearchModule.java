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
package org.exbin.bined.jaguif.search;

import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.bined.jaguif.document.BinEdFileManager;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.document.BinedDocumentModule;
import org.exbin.bined.jaguif.search.action.FindReplaceActions;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.ActionMenuContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Binary editor search module.
 */
@ParametersAreNonnullByDefault
public class BinedSearchModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedSearchModule.class);

    public static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private FindReplaceActions findReplaceActions;

    public BinedSearchModule() {
    }

    public void registerSearchComponent() {
        BinedDocumentModule binedModule = App.getModule(BinedDocumentModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addBinEdComponentExtension((BinEdComponentPanel component) -> Optional.of(new DefaultBinEdComponentSearch()));
    }

    @Nonnull
    public FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.init(getResourceBundle());
        }

        return findReplaceActions;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        // TODO SearchModule
        String groupId = BinedComponentModule.EDIT_FIND_MENU_GROUP_ID;
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return findReplaceActions.createEditFindAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return "binarySearchFind";
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return findReplaceActions.createEditFindAgainAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return "binarySearchFindAgain";
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return findReplaceActions.createEditReplaceAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return "binarySearchReplace";
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupId));
    }

    public void registerEditFindPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return findReplaceActions.createEditFindAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return "binarySearchFind";
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_FIND_GROUP_ID));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return findReplaceActions.createEditReplaceAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return "binarySearchReplace";
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_FIND_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return findReplaceActions.createEditFindAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return "binarySearchFind";
            }
        };
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedSearchModule.class);
        }

        return resourceBundle;
    }
}
