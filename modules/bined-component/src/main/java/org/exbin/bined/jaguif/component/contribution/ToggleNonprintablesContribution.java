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
package org.exbin.bined.jaguif.component.contribution;

import javax.annotation.Nonnull;
import javax.swing.Action;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.action.ShowNonprintablesActions;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;

/**
 * Toggle nonprintables contribution.
 */
public class ToggleNonprintablesContribution implements ActionSequenceContribution {

    public static final String CONTRIBUTION_ID = "viewNonprintables";

    @Nonnull
    @Override
    public Action createAction() {
        BinedComponentModule binedComponentModule = App.getModule(BinedComponentModule.class);
        ShowNonprintablesActions showNonprintablesActions = binedComponentModule.getShowNonprintablesActions();
        ShowNonprintablesActions.ViewNonprintablesAction action = showNonprintablesActions.createToggleNonprintablesAction();
        action.init(binedComponentModule.getResourceBundle());
        return action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return CONTRIBUTION_ID;
    }
}
