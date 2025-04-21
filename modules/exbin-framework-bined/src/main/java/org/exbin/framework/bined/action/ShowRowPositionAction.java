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
package org.exbin.framework.bined.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.capability.LayoutProfileCapable;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextChangeManager;

/**
 * Show row position action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowRowPositionAction extends AbstractAction {

    public static final String ACTION_ID = "showRowPositionAction";

    private CodeAreaCore codeArea;

    public ShowRowPositionAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                    codeArea = instance;
                    boolean hasInstance = instance != null;
                    if (codeArea != null) {
                        putValue(Action.SELECTED_KEY, ((LayoutProfileCapable) codeArea).getLayoutProfile().isShowRowPosition());
                    }
                    setEnabled(hasInstance);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SectionCodeAreaLayoutProfile layoutProfile = ((LayoutProfileCapable) codeArea).getLayoutProfile();
        layoutProfile.setShowRowPosition(!layoutProfile.isShowRowPosition());
        ((LayoutProfileCapable) codeArea).setLayoutProfile(layoutProfile);
        // TODO App.getModule(ActionModuleApi.class).updateActionsForComponent(CodeAreaCore.class, codeArea);
    }
}
