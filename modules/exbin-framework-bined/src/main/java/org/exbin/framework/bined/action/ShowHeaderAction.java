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
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.extended.capability.LayoutProfileCapable;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;

/**
 * Show header action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowHeaderAction extends AbstractAction {

    public static final String ACTION_ID = "showHeaderAction";

    private CodeAreaCore codeArea;
    private ResourceBundle resourceBundle;

    public ShowHeaderAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Nonnull
            @Override
            public Set<Class<?>> forClasses() {
                return Collections.singleton(CodeAreaCore.class);
            }

            @Override
            public void componentActive(Set<Object> affectedClasses) {
                boolean hasInstance = !affectedClasses.isEmpty();
                codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
                setEnabled(hasInstance);
                if (codeArea != null) {
                    putValue(Action.SELECTED_KEY, ((LayoutProfileCapable) codeArea).getLayoutProfile().isShowHeader());
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ExtendedCodeAreaLayoutProfile layoutProfile = ((LayoutProfileCapable) codeArea).getLayoutProfile();
        layoutProfile.setShowHeader(!layoutProfile.isShowHeader());
        ((LayoutProfileCapable) codeArea).setLayoutProfile(layoutProfile);
        App.getModule(ActionModuleApi.class).updateActionsForComponent(codeArea);
    }
}
