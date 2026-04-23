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
package org.exbin.bined.jaguif.document;

import org.exbin.bined.jaguif.document.action.ViewFontActions;
import java.io.File;
import java.net.URI;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.auxiliary.binary_data.array.paged.ByteArrayPagedData;
import org.exbin.bined.jaguif.component.BinEdComponentExtension;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.component.status.contribution.BinaryCursorPositionStatusContrib;
import org.exbin.bined.jaguif.component.status.contribution.BinaryEditModeStatusContrib;
import org.exbin.bined.jaguif.component.status.contribution.BinaryEncodingStatusContrib;
import org.exbin.bined.jaguif.document.status.contribution.BinaryDocumentSizeStatusContrib;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.bined.jaguif.document.status.contribution.BinaryProcessingModeStatusContrib;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SubSequenceContribution;
import org.exbin.jaguif.contribution.api.SubSequenceContributionRule;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.docking.api.DocumentDocking;
import org.exbin.jaguif.document.api.Document;
import org.exbin.jaguif.document.api.DocumentSource;
import org.exbin.jaguif.document.api.DocumentManagement;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.document.api.DocumentType;
import org.exbin.jaguif.file.api.FileDocumentSource;
import org.exbin.jaguif.menu.api.ActionMenuContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.statusbar.api.StatusBarDefinitionManagement;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;

/**
 * Binary data component module.
 */
@ParametersAreNonnullByDefault
public class BinedDocumentModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedDocumentModule.class);

    public static final String VIEW_FONT_SUB_MENU_ID = MODULE_ID + ".viewFontSubMenu";
    public static final String VIEW_FONT_ZOOM_MENU_GROUP_ID = MODULE_ID + ".viewZoomMenuGroup";
    public static final String BINARY_DOCUMENT_ID = "binary";

    private java.util.ResourceBundle resourceBundle = null;

    private BinEdFileManager fileManager = null;

    private ViewFontActions viewFontActions;
    private FileProcessingMode initialFileProcessing = FileProcessingMode.MEMORY;

    public BinedDocumentModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedDocumentModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public ViewFontActions getViewFontActions() {
        if (viewFontActions == null) {
            viewFontActions = new ViewFontActions();
            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
            org.exbin.bined.jaguif.document.settings.CodeAreaFontSizeOptions fontSizeOptions
                    = new org.exbin.bined.jaguif.document.settings.CodeAreaFontSizeOptions(optionsModule.getAppOptions());
            viewFontActions.init(getResourceBundle(), fontSizeOptions);
        }

        return viewFontActions;
    }

    public void setInitialFileProcessing(FileProcessingMode initialFileProcessing) {
        this.initialFileProcessing = initialFileProcessing;
    }

    public void registerDocument() {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        documentManager.registerDocumentType(new DocumentType() {
            @Nonnull
            @Override
            public String getTypeId() {
                return BINARY_DOCUMENT_ID;
            }

            @Nonnull
            @Override
            public BinaryFileDocument createDefaultDocument() {
                BinaryFileDocument binaryDocument = createBinaryDocument();
                binaryDocument.loadFrom(documentModule.createMemoryDocumentSource());
                return binaryDocument;
            }

            @Nonnull
            @Override
            public Optional<Document> createDocument(DocumentSource documentSource) {
                if (documentSource instanceof FileDocumentSource) {
                    BinaryFileDocument document = createBinaryDocument();
                    document.loadFrom(documentSource);
                    return Optional.of(document);
                }

                return Optional.empty();
            }

            @Nonnull
            private BinaryFileDocument createBinaryDocument() {
                BinaryFileDocument binaryFileDocument = new BinaryFileDocument();
                getFileManager();
                fileManager.initDataComponent(binaryFileDocument.getDataComponent());
                fileManager.initCommandHandler(binaryFileDocument.getDataComponent());
                OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
                OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
                SettingsOptionsProvider settingsOptionsProvider = settingsManager.getSettingsOptionsProvider();
                binaryFileDocument.applySettings(settingsOptionsProvider);
                binaryFileDocument.setContentData(new ByteArrayPagedData());
                binaryFileDocument.setInitialProcessingMode(initialFileProcessing);
                return binaryFileDocument;
            }
        });
    }

    public void registerViewZoomMenuActions() {
        getViewFontActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SubSequenceContribution subContribution = mgmt.registerMenuItem(VIEW_FONT_SUB_MENU_ID, resourceBundle.getString("viewFontSubMenu.name"));
        mgmt.registerMenuRule(subContribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));

        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_FONT_ZOOM_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return viewFontActions.createZoomInAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return ViewFontActions.ZoomInAction.ACTION_ID;
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_FONT_ZOOM_MENU_GROUP_ID));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return viewFontActions.createZoomOutAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return ViewFontActions.ZoomOutAction.ACTION_ID;
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_FONT_ZOOM_MENU_GROUP_ID));
        contribution = new ActionMenuContribution() {
            @Nonnull
            @Override
            public Action createAction() {
                return viewFontActions.createResetFontSizeAction();
            }

            @Nonnull
            @Override
            public String getContributionId() {
                return ViewFontActions.ResetFontSizeAction.ACTION_ID;
            }
        };
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_FONT_ZOOM_MENU_GROUP_ID));
    }

    public void registerStatusBar() {
        StatusBarModuleApi statusBarModule = App.getModule(StatusBarModuleApi.class);
        statusBarModule.registerStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, MODULE_ID);
        StatusBarDefinitionManagement statusBarManager = statusBarModule.getMainStatusBarDefinition(BinedComponentModule.BINARY_STATUS_BAR_ID, MODULE_ID);
        statusBarManager.registerStatusBarContribution(new BinaryEncodingStatusContrib());
        statusBarManager.registerStatusBarContribution(new BinaryDocumentSizeStatusContrib());
        statusBarManager.registerStatusBarContribution(new BinaryCursorPositionStatusContrib());
        statusBarManager.registerStatusBarContribution(new BinaryProcessingModeStatusContrib());
        statusBarManager.registerStatusBarContribution(new BinaryEditModeStatusContrib());
    }

    public void start() {
        // TODO
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement contextManager = frameModule.getFrameController().getContextManager();
        ContextDocking contextDocking = contextManager.getActiveState(ContextDocking.class);
        if (contextDocking instanceof DocumentDocking) {
            ((DocumentDocking) contextDocking).openNewDocument();
        }

        // TODO Rework to use different approach than extension
        getFileManager().addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
            @Nonnull
            @Override
            public Optional<BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                getViewFontActions();
                viewFontActions.registerComponentActions(component.getCodeArea());
                return Optional.empty();
            }
        });
    }

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.openFile(uri);
    }

    @Nonnull
    public BinEdFileManager getFileManager() {
        if (fileManager == null) {
            fileManager = new BinEdFileManager();
        }
        return fileManager;
    }

    @Nonnull
    public String getNewFileTitlePrefix() {
        return resourceBundle.getString("newFileTitlePrefix");
    }

    public void registerCodeAreaCommandHandlerProvider(CodeAreaCommandHandlerProvider commandHandlerProvider) {
        getFileManager().setCommandHandlerProvider(commandHandlerProvider);
    }
}
