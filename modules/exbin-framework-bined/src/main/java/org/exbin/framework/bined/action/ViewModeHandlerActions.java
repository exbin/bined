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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * View mode actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ViewModeHandlerActions implements FileDependentAction {

    public static final String DUAL_VIEW_MODE_ACTION_ID = "dualViewModeAction";
    public static final String CODE_MATRIX_VIEW_MODE_ACTION_ID = "codeMatrixViewModeAction";
    public static final String TEXT_PREVIEW_VIEW_MODE_ACTION_ID = "textPreviewViewModeAction";

    public static final String VIEW_MODE_RADIO_GROUP_ID = "viewModeRadioGroup";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action dualModeAction;
    private Action codeMatrixModeAction;
    private Action textPreviewModeAction;

    private CodeAreaViewMode viewMode = CodeAreaViewMode.DUAL;

    public ViewModeHandlerActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        CodeAreaViewMode viewMode = activeFile.isPresent() ? ((BinEdFileHandler) activeFile.get()).getCodeArea().getViewMode() : null;
        if (dualModeAction != null) {
            dualModeAction.setEnabled(activeFile.isPresent());
            if (viewMode == CodeAreaViewMode.DUAL) {
                dualModeAction.putValue(Action.SELECTED_KEY, true);
            }
        }
        if (codeMatrixModeAction != null) {
            codeMatrixModeAction.setEnabled(activeFile.isPresent());
            if (viewMode == CodeAreaViewMode.CODE_MATRIX) {
                codeMatrixModeAction.putValue(Action.SELECTED_KEY, true);
            }
        }
        if (textPreviewModeAction != null) {
            textPreviewModeAction.setEnabled(activeFile.isPresent());
            if (viewMode == CodeAreaViewMode.TEXT_PREVIEW) {
                textPreviewModeAction.putValue(Action.SELECTED_KEY, true);
            }
        }
    }

    public void setViewMode(CodeAreaViewMode viewMode) {
        this.viewMode = viewMode;
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
        codeArea.setViewMode(viewMode);
    }

    @Nonnull
    public Action getDualModeAction() {
        if (dualModeAction == null) {
            dualModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setViewMode(CodeAreaViewMode.DUAL);
                    }
                }
            };
            ActionUtils.setupAction(dualModeAction, resourceBundle, DUAL_VIEW_MODE_ACTION_ID);
            dualModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            dualModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            dualModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.DUAL);
        }
        return dualModeAction;
    }

    @Nonnull
    public Action getCodeMatrixModeAction() {
        if (codeMatrixModeAction == null) {
            codeMatrixModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setViewMode(CodeAreaViewMode.CODE_MATRIX);
                    }
                }
            };
            ActionUtils.setupAction(codeMatrixModeAction, resourceBundle, CODE_MATRIX_VIEW_MODE_ACTION_ID);
            codeMatrixModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            codeMatrixModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            codeMatrixModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.CODE_MATRIX);

        }
        return codeMatrixModeAction;
    }

    @Nonnull
    public Action getTextPreviewModeAction() {
        if (textPreviewModeAction == null) {
            textPreviewModeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editorProvider instanceof BinEdEditorProvider) {
                        setViewMode(CodeAreaViewMode.TEXT_PREVIEW);
                    }
                }
            };
            ActionUtils.setupAction(textPreviewModeAction, resourceBundle, TEXT_PREVIEW_VIEW_MODE_ACTION_ID);
            textPreviewModeAction.putValue(ActionUtils.ACTION_RADIO_GROUP, VIEW_MODE_RADIO_GROUP_ID);
            textPreviewModeAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
            textPreviewModeAction.putValue(Action.SELECTED_KEY, viewMode == CodeAreaViewMode.TEXT_PREVIEW);

        }
        return textPreviewModeAction;
    }
}
