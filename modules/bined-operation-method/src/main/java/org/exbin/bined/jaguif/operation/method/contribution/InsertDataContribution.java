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
package org.exbin.bined.jaguif.operation.method.contribution;

import javax.annotation.Nonnull;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.operation.method.BinedOperationMethodModule;
import org.exbin.bined.jaguif.operation.method.action.InsertDataAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.menu.api.ActionMenuCreation;

/**
 * Insert data contribution.
 */
public class InsertDataContribution implements ActionSequenceContribution {

    public static final String CONTRIBUTION_ID = "insertData";

    @Nonnull
    @Override
    public Action createAction() {
        InsertDataAction action = new InsertDataAction();
        BinedOperationMethodModule binedOperationModule = App.getModule(BinedOperationMethodModule.class);
        action.init(binedOperationModule.getResourceBundle());

        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedComponentModule binedModule = App.getModule(BinedComponentModule.class);
                BinedComponentModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                return menuVariant != BinedComponentModule.PopupMenuVariant.BASIC && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
            }
        });

        return action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return CONTRIBUTION_ID;
    }
}
