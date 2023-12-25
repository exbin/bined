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
package org.exbin.framework.bined.makro;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.makro.action.AddMakroAction;
import org.exbin.framework.bined.makro.action.EditMakroAction;
import org.exbin.framework.bined.makro.action.ManageMakrosAction;
import org.exbin.framework.bined.makro.action.StartMakroRecordingAction;
import org.exbin.framework.bined.makro.action.StopMakroRecordingAction;
import org.exbin.framework.bined.makro.gui.MakrosManagerPanel;
import org.exbin.framework.bined.makro.model.MakroRecord;
import org.exbin.framework.bined.makro.preferences.MakroPreferences;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Makros manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MakrosManager {

    public static final String MAKROS_POPUP_SUBMENU_ID = BinedMakroModule.MODULE_ID + ".makrosPopupSubMenu";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(MakrosManager.class);

    private final List<MakroRecord> makroRecords = new ArrayList<>();
    private MakroPreferences makroPreferences;

    private XBApplication application;
    private EditorProvider editorProvider;

    private final ManageMakrosAction manageMakrosAction = new ManageMakrosAction();
    private final StartMakroRecordingAction startMakroRecordingAction = new StartMakroRecordingAction();
    private final StopMakroRecordingAction stopMakroRecordingAction = new StopMakroRecordingAction();
    private final AddMakroAction addMakroAction = new AddMakroAction();
    private final EditMakroAction editMakroAction = new EditMakroAction();
    private JMenu makrosMenu;

    public MakrosManager() {
        manageMakrosAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setApplication(XBApplication application) {
        this.application = application;

        addMakroAction.setup(application, resourceBundle);
        editMakroAction.setup(application, resourceBundle);
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        manageMakrosAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
        startMakroRecordingAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
        stopMakroRecordingAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
    }

    public void init() {
        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);

        Preferences preferences = application.getAppPreferences();
        makroPreferences = new MakroPreferences(preferences);
        loadMakroRecords();
        MakrosManager.this.updateMakrosMenu();
    }

    private void loadMakroRecords() {
        int makrosCount = makroPreferences.getMakrosCount();
        for (int i = 0; i < makrosCount; i++) {
            MakroRecord makroRecord = makroPreferences.getMakroRecord(i);
            makroRecords.add(makroRecord);
        }
    }

    private void saveMakroRecords() {
        int makrosCount = makroRecords.size();
        makroPreferences.setMakrosCount(makrosCount);
        for (int i = 0; i < makrosCount; i++) {
            makroPreferences.setMakroRecord(i, makroRecords.get(i));
        }
    }

    @Nonnull
    public List<MakroRecord> getMakroRecords() {
        return makroRecords;
    }

    @Nonnull
    public AbstractAction getManageMakrosAction() {
        return manageMakrosAction;
    }

    public void setMakroRecords(List<MakroRecord> records) {
        makroRecords.clear();
        makroRecords.addAll(records);
        saveMakroRecords();
        MakrosManager.this.updateMakrosMenu();
    }

    @Nonnull
    public MakrosManagerPanel createMakrosManagerPanel() {
        final MakrosManagerPanel makrosManagerPanel = new MakrosManagerPanel();
        makrosManagerPanel.setControl(new MakrosManagerPanel.Control() {
            @Override
            public void addRecord() {
                addMakroAction.actionPerformed(null);
                Optional<MakroRecord> makroRecord = addMakroAction.getMakroRecord();
                if (makroRecord.isPresent()) {
                    List<MakroRecord> records = makrosManagerPanel.getMakroRecords();
                    records.add(makroRecord.get());
                    makrosManagerPanel.setMakroRecords(records);
                }
            }

            @Override
            public void editRecord() {
                int selectedRow = makrosManagerPanel.getTable().getSelectedRow();
                MakroRecord selectedRecord = makrosManagerPanel.getMakroRecords().get(selectedRow);
                editMakroAction.setMakroRecord(new MakroRecord(selectedRecord));
                editMakroAction.actionPerformed(null);
                Optional<MakroRecord> makroRecord = editMakroAction.getMakroRecord();
                if (makroRecord.isPresent()) {
                    makrosManagerPanel.updateRecord(makroRecord.get(), selectedRow);
                }
            }

            @Override
            public void removeRecord() {
                int[] selectedRows = makrosManagerPanel.getTable().getSelectedRows();
                Arrays.sort(selectedRows);
                List<MakroRecord> records = makrosManagerPanel.getMakroRecords();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    records.remove(selectedRows[i]);
                }
                makrosManagerPanel.setMakroRecords(records);
            }

            @Override
            public void selectAll() {
                makrosManagerPanel.getTable().selectAll();
            }

            @Override
            public void moveUp() {
                JTable table = makrosManagerPanel.getTable();
                int[] selectedRows = table.getSelectedRows();
                Arrays.sort(selectedRows);
                List<MakroRecord> records = makrosManagerPanel.getMakroRecords();
                ListSelectionModel selectionModel = table.getSelectionModel();
                for (int i = 0; i < selectedRows.length; i++) {
                    int index = selectedRows[i];
                    selectionModel.removeSelectionInterval(index, index);
                    MakroRecord movedRecord = records.remove(index - 1);
                    records.add(index, movedRecord);
                    table.addRowSelectionInterval(index - 1, index - 1);
                }
                makrosManagerPanel.updateMakroRecords(records);
            }

            @Override
            public void moveDown() {
                JTable table = makrosManagerPanel.getTable();
                int[] selectedRows = table.getSelectedRows();
                Arrays.sort(selectedRows);
                List<MakroRecord> records = makrosManagerPanel.getMakroRecords();
                ListSelectionModel selectionModel = table.getSelectionModel();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int index = selectedRows[i];
                    selectionModel.removeSelectionInterval(index, index);
                    MakroRecord movedRecord = records.remove(index);
                    records.add(index + 1, movedRecord);
                    table.addRowSelectionInterval(index + 1, index + 1);
                }
                makrosManagerPanel.updateMakroRecords(records);
            }
        });

        return makrosManagerPanel;
    }

    @Nonnull
    public JMenu getMakrosMenu() {
        if (makrosMenu == null) {
            Action makrosMenuAction = new AbstractAction(resourceBundle.getString("makrosMenu.text")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
            makrosMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("makrosMenu.shortDescription"));
            makrosMenu = new JMenu(makrosMenuAction);
            updateMakrosMenu();
        }
        return makrosMenu;
    }

    public void registerMakrosPopupMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        Action makrosPopupMenuAction = new AbstractAction(resourceBundle.getString("makrosMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        makrosPopupMenuAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                return menuVariant == BinedModule.PopupMenuVariant.EDITOR;
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        JMenu makrosPopupMenu = new JMenu(makrosPopupMenuAction);
        makrosPopupMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                updateMakrosMenu(makrosPopupMenu);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, BinedMakroModule.MODULE_ID, makrosPopupMenu, new MenuPosition(BinedModule.CODE_AREA_POPUP_FIND_GROUP_ID));
    }

    public void registerMakroComponentActions(JComponent component) {
        /*        ActionMap actionMap = component.getActionMap();
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        int metaMask = ActionUtils.getMetaMask();
        for (int i = 0; i < 10; i++) {
            final int bookmarkIndex = i;
            String goToActionKey = "go-to-bookmark-" + i;
            actionMap.put(goToActionKey, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                        ExtCodeArea codeArea = fileHandler.getCodeArea();
                        executeMakro(codeArea, bookmarkIndex);
                    }
                }
            });
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, metaMask), goToActionKey);
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_NUMPAD0 + i, metaMask), goToActionKey);

            String addActionKey = "add-bookmark-" + i;
            actionMap.put(addActionKey, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                        ExtCodeArea codeArea = fileHandler.getCodeArea();
                        startMakroRecording(codeArea, bookmarkIndex);
                    }
                }
            });
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK), addActionKey);
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_NUMPAD0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK), addActionKey);

            String clearActionKey = "clear-bookmark-" + i;
            actionMap.put(clearActionKey, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearMakro(bookmarkIndex);
                }
            });
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), clearActionKey);
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_NUMPAD0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), clearActionKey);
        }
        component.setActionMap(actionMap);
        component.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap); */
    }

    public void executeMakro(ExtCodeArea codeArea, int makroIndex) {
        if (makroRecords.size() > makroIndex) {
            MakroRecord record = makroRecords.get(makroIndex);

//            codeArea.setCaretPosition(record.getStartPosition());
//            codeArea.centerOnCursor();
        }
    }

    public void startMakroRecording(ExtCodeArea codeArea) {
//        MakroRecord record = makroRecords.get(makroIndex);
//        record.setStartPosition(position);
//        record.setLength(1);
//        saveMakroRecords();
//        updateMakrosMenu();
    }

    public void stopMakroRecording() {
//        saveMakroRecords();
//        updateMakrosMenu();
    }

    public void updateMakrosMenu() {
        if (makrosMenu != null) {
            updateMakrosMenu(makrosMenu);
        }
    }

    public void updateMakrosMenu(JMenu menu) {
        menu.removeAll();

        int recordsLimit = Math.min(makroRecords.size(), 10);
        int metaMask = ActionUtils.getMetaMask();
        String makroActionName = resourceBundle.getString("makroAction.text");
        for (int i = 0; i < recordsLimit; i++) {
            MakroRecord makroRecord = makroRecords.get(i);

            Action makroAction = new AbstractAction(makroActionName + " " + (i + 1)) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                        ExtCodeArea codeArea = fileHandler.getCodeArea();
                        // executeMakro(codeArea, makroRecord);
                    }
                }
            };

            menu.add(ActionUtils.actionToMenuItem(makroAction));
        }

        if (!makroRecords.isEmpty()) {
            menu.addSeparator();
        }
        menu.add(ActionUtils.actionToMenuItem(startMakroRecordingAction));
        menu.add(ActionUtils.actionToMenuItem(stopMakroRecordingAction));
        menu.add(ActionUtils.actionToMenuItem(manageMakrosAction));
    }
}
