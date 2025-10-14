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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.settings.FontSizeOptions;
import org.exbin.framework.utils.ActionUtils;


@ParametersAreNonnullByDefault
public class ViewFontActions {

    public static final String ZOOM_IN_ACTION_ID = "zoomInAction";
    public static final String ZOOM_OUT_ACTION_ID = "zoomOutAction";
    public static final String RESET_FONT_SIZE_ACTION_ID = "resetFontSizeAction";

    private ResourceBundle resourceBundle;
    private FontSizeOptions fontSizeOptions;

    public ViewFontActions() {
    }

    public void setup(ResourceBundle resourceBundle, FontSizeOptions fontSizeOptions) {
        this.resourceBundle = resourceBundle;
        this.fontSizeOptions = fontSizeOptions;
    }

    @Nonnull
    public ZoomInAction createZoomInAction() {
        ZoomInAction zoomInAction = new ZoomInAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(zoomInAction, resourceBundle, ZOOM_IN_ACTION_ID);
        zoomInAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionUtils.getMetaMask()));
        zoomInAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, zoomInAction);
        zoomInAction.setFontSizeOptions(fontSizeOptions);
        return zoomInAction;
    }

    @Nonnull
    public ZoomOutAction createZoomOutAction() {
        ZoomOutAction zoomOutAction = new ZoomOutAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(zoomOutAction, resourceBundle, ZOOM_OUT_ACTION_ID);
        zoomOutAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionUtils.getMetaMask()));
        zoomOutAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, zoomOutAction);
        zoomOutAction.setFontSizeOptions(fontSizeOptions);
        return zoomOutAction;
    }

    @Nonnull
    public ResetFontSizeAction createResetFontSizeAction() {
        ResetFontSizeAction resetFontSizeAction = new ResetFontSizeAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(resetFontSizeAction, resourceBundle, RESET_FONT_SIZE_ACTION_ID);
        resetFontSizeAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_0, ActionUtils.getMetaMask()));
        resetFontSizeAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, resetFontSizeAction);
        resetFontSizeAction.setFontSizeOptions(fontSizeOptions);
        return resetFontSizeAction;
    }

    @ParametersAreNonnullByDefault
    public static class ZoomInAction extends AbstractAction implements ActionContextChange {

        @Nullable
        private CodeAreaCore codeArea;
        @Nullable
        private FontSizeOptions fontSizeOptions;

        public void setFontSizeOptions(FontSizeOptions fontSizeOptions) {
            this.fontSizeOptions = fontSizeOptions;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (codeArea == null) {
                return;
            }

            SectCodeArea sectCodeArea = (SectCodeArea) codeArea;
            Font currentFont = sectCodeArea.getCodeFont();
            int currentSize = currentFont.getSize();
            int newSize = Math.min(currentSize + 1, FontSizeOptions.MAX_FONT_SIZE);

            if (newSize != currentSize) {
                Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), newSize);
                sectCodeArea.setCodeFont(newFont);
                codeArea.repaint();

                if (fontSizeOptions != null) {
                    fontSizeOptions.setFontSize(newSize);
                }
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                setEnabled(codeArea != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class ZoomOutAction extends AbstractAction implements ActionContextChange {

        @Nullable
        private CodeAreaCore codeArea;
        @Nullable
        private FontSizeOptions fontSizeOptions;

        public void setFontSizeOptions(FontSizeOptions fontSizeOptions) {
            this.fontSizeOptions = fontSizeOptions;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (codeArea == null) {
                return;
            }

            SectCodeArea sectCodeArea = (SectCodeArea) codeArea;
            Font currentFont = sectCodeArea.getCodeFont();
            int currentSize = currentFont.getSize();
            int newSize = Math.max(currentSize - 1, FontSizeOptions.MIN_FONT_SIZE);

            if (newSize != currentSize) {
                Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), newSize);
                sectCodeArea.setCodeFont(newFont);
                codeArea.repaint();

                if (fontSizeOptions != null) {
                    fontSizeOptions.setFontSize(newSize);
                }
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                setEnabled(codeArea != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class ResetFontSizeAction extends AbstractAction implements ActionContextChange {

        @Nullable
        private CodeAreaCore codeArea;
        @Nullable
        private FontSizeOptions fontSizeOptions;

        public void setFontSizeOptions(FontSizeOptions fontSizeOptions) {
            this.fontSizeOptions = fontSizeOptions;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (codeArea == null) {
                return;
            }

            SectCodeArea sectCodeArea = (SectCodeArea) codeArea;
            Font currentFont = sectCodeArea.getCodeFont();
            Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), FontSizeOptions.DEFAULT_FONT_SIZE);
            sectCodeArea.setCodeFont(newFont);
            codeArea.repaint();

            if (fontSizeOptions != null) {
                fontSizeOptions.setFontSize(FontSizeOptions.DEFAULT_FONT_SIZE);
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                setEnabled(codeArea != null);
            });
        }
    }
}
