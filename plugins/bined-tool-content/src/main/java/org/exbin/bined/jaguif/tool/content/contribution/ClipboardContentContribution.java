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
package org.exbin.bined.jaguif.tool.content.contribution;

import javax.annotation.Nonnull;
import javax.swing.Action;
import org.exbin.bined.jaguif.tool.content.BinedToolContentModule;
import org.exbin.bined.jaguif.tool.content.action.ClipboardContentAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;

/**
 * Clipboard content contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class ClipboardContentContribution implements ActionSequenceContribution {

    public static final String CONTRIBUTION_ID = "clipboardContent";

    @Nonnull
    @Override
    public Action createAction() {
        ClipboardContentAction action = new ClipboardContentAction();
        BinedToolContentModule binedToolContentModule = App.getModule(BinedToolContentModule.class);
        action.init(binedToolContentModule.getResourceBundle());
        return action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return CONTRIBUTION_ID;
    }
}
