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
package org.exbin.bined.jaguif.viewer.contribution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.bined.jaguif.viewer.action.ShowRowPositionAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.menu.api.ActionMenuCreation;

/**
 * Show row position contribution.
 */
public class ShowRowPositionContribution implements ActionSequenceContribution {

    public static final String CONTRIBUTION_ID = "showRowPosition";
    
    protected ActionMenuCreation showPositionCreating;

    public ShowRowPositionContribution() {
        showPositionCreating = null;
    }

    public ShowRowPositionContribution(@Nullable ActionMenuCreation showPositionCreating) {
        this.showPositionCreating = showPositionCreating;
    }

    @Nonnull
    @Override
    public Action createAction() {
        ShowRowPositionAction action = new ShowRowPositionAction();
        BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
        action.init(binedViewerModule.getResourceBundle());
        if (showPositionCreating != null) {
            action.putValue(ActionConsts.ACTION_MENU_CREATION, showPositionCreating);
        }
        return action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return CONTRIBUTION_ID;
    }
}
