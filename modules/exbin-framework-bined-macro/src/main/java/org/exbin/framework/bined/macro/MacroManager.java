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
package org.exbin.framework.bined.macro;

import com.sun.tools.javac.util.Pair;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.exbin.framework.bined.macro.action.AddMacroAction;
import org.exbin.framework.bined.macro.action.EditMacroAction;
import org.exbin.framework.bined.macro.action.ManageMacrosAction;
import org.exbin.framework.bined.macro.action.ExecuteLastMacroAction;
import org.exbin.framework.bined.macro.action.StartMacroRecordingAction;
import org.exbin.framework.bined.macro.action.StopMacroRecordingAction;
import org.exbin.framework.bined.macro.gui.MacrosManagerPanel;
import org.exbin.framework.bined.macro.model.MacroRecord;
import org.exbin.framework.bined.macro.operation.CodeAreaMacroCommandHandler;
import org.exbin.framework.bined.macro.operation.MacroStep;
import org.exbin.framework.bined.macro.preferences.MacroPreferences;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Macros manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MacroManager {

    public static final String MACROS_POPUP_SUBMENU_ID = BinedMacroModule.MODULE_ID + ".macrosPopupSubMenu";

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(MacroManager.class);

    private final List<MacroRecord> macroRecords = new ArrayList<>();
    private MacroPreferences macroPreferences;

    private XBApplication application;
    private EditorProvider editorProvider;

    private final ManageMacrosAction manageMacrosAction = new ManageMacrosAction();
    private final StartMacroRecordingAction startMacroRecordingAction = new StartMacroRecordingAction();
    private final StopMacroRecordingAction stopMacroRecordingAction = new StopMacroRecordingAction();
    private final ExecuteLastMacroAction executeLastMacroAction = new ExecuteLastMacroAction();
    private final AddMacroAction addMacroAction = new AddMacroAction();
    private final EditMacroAction editMacroAction = new EditMacroAction();
    private JMenu macrosMenu;

    public MacroManager() {
        manageMacrosAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        addMacroAction.setup(application, resourceBundle);
        editMacroAction.setup(application, resourceBundle);
        manageMacrosAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
        executeLastMacroAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
        executeLastMacroAction.setMacroManager(this);
        startMacroRecordingAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
        startMacroRecordingAction.setMacroManager(this);
        stopMacroRecordingAction.setup(Objects.requireNonNull(application), editorProvider, resourceBundle);
        stopMacroRecordingAction.setMacroManager(this);
    }

    public void init() {
        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);

        Preferences preferences = application.getAppPreferences();
        macroPreferences = new MacroPreferences(preferences);
        loadMacroRecords();
        MacroManager.this.updateMacrosMenu();
    }

    private void loadMacroRecords() {
        int macrosCount = macroPreferences.getMacrosCount();
        for (int i = 0; i < macrosCount; i++) {
            MacroRecord macroRecord = macroPreferences.getMacroRecord(i);
            macroRecords.add(macroRecord);
        }
    }

    private void saveMacroRecords() {
        int macrosCount = macroRecords.size();
        macroPreferences.setMacrosCount(macrosCount);
        for (int i = 0; i < macrosCount; i++) {
            macroPreferences.setMacroRecord(i, macroRecords.get(i));
        }
    }

    @Nonnull
    public List<MacroRecord> getMacroRecords() {
        return macroRecords;
    }

    @Nonnull
    public AbstractAction getManageMacrosAction() {
        return manageMacrosAction;
    }

    public void setMacroRecords(List<MacroRecord> records) {
        macroRecords.clear();
        macroRecords.addAll(records);
        saveMacroRecords();
        MacroManager.this.updateMacrosMenu();
    }

    @Nonnull
    public MacrosManagerPanel createMacrosManagerPanel() {
        final MacrosManagerPanel macrosManagerPanel = new MacrosManagerPanel();
        macrosManagerPanel.setControl(new MacrosManagerPanel.Control() {
            @Override
            public void addRecord() {
                addMacroAction.actionPerformed(null);
                Optional<MacroRecord> macroRecord = addMacroAction.getMacroRecord();
                if (macroRecord.isPresent()) {
                    List<MacroRecord> records = macrosManagerPanel.getMacroRecords();
                    records.add(macroRecord.get());
                    macrosManagerPanel.setMacroRecords(records);
                }
            }

            @Override
            public void editRecord() {
                int selectedRow = macrosManagerPanel.getTable().getSelectedRow();
                MacroRecord selectedRecord = macrosManagerPanel.getMacroRecords().get(selectedRow);
                editMacroAction.setMacroRecord(new MacroRecord(selectedRecord));
                editMacroAction.actionPerformed(null);
                Optional<MacroRecord> macroRecord = editMacroAction.getMacroRecord();
                if (macroRecord.isPresent()) {
                    macrosManagerPanel.updateRecord(macroRecord.get(), selectedRow);
                }
            }

            @Override
            public void removeRecord() {
                int[] selectedRows = macrosManagerPanel.getTable().getSelectedRows();
                Arrays.sort(selectedRows);
                List<MacroRecord> records = macrosManagerPanel.getMacroRecords();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    records.remove(selectedRows[i]);
                }
                macrosManagerPanel.setMacroRecords(records);
            }

            @Override
            public void selectAll() {
                macrosManagerPanel.getTable().selectAll();
            }

            @Override
            public void moveUp() {
                JTable table = macrosManagerPanel.getTable();
                int[] selectedRows = table.getSelectedRows();
                Arrays.sort(selectedRows);
                List<MacroRecord> records = macrosManagerPanel.getMacroRecords();
                ListSelectionModel selectionModel = table.getSelectionModel();
                for (int i = 0; i < selectedRows.length; i++) {
                    int index = selectedRows[i];
                    selectionModel.removeSelectionInterval(index, index);
                    MacroRecord movedRecord = records.remove(index - 1);
                    records.add(index, movedRecord);
                    table.addRowSelectionInterval(index - 1, index - 1);
                }
                macrosManagerPanel.updateMacroRecords(records);
            }

            @Override
            public void moveDown() {
                JTable table = macrosManagerPanel.getTable();
                int[] selectedRows = table.getSelectedRows();
                Arrays.sort(selectedRows);
                List<MacroRecord> records = macrosManagerPanel.getMacroRecords();
                ListSelectionModel selectionModel = table.getSelectionModel();
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int index = selectedRows[i];
                    selectionModel.removeSelectionInterval(index, index);
                    MacroRecord movedRecord = records.remove(index);
                    records.add(index + 1, movedRecord);
                    table.addRowSelectionInterval(index + 1, index + 1);
                }
                macrosManagerPanel.updateMacroRecords(records);
            }
        });

        return macrosManagerPanel;
    }

    @Nonnull
    public JMenu getMacrosMenu() {
        if (macrosMenu == null) {
            Action macrosMenuAction = new AbstractAction(resourceBundle.getString("macrosMenu.text")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
            macrosMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("macrosMenu.shortDescription"));
            macrosMenu = new JMenu(macrosMenuAction);
            updateMacrosMenu();
        }
        return macrosMenu;
    }

    public void registerMacrosPopupMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        Action macrosPopupMenuAction = new AbstractAction(resourceBundle.getString("macrosMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        macrosPopupMenuAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
        JMenu macrosPopupMenu = new JMenu(macrosPopupMenuAction);
        macrosPopupMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                updateMacrosMenu(macrosPopupMenu);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, BinedMacroModule.MODULE_ID, macrosPopupMenu, new MenuPosition(BinedModule.CODE_AREA_POPUP_FIND_GROUP_ID));
    }

    public void registerMacroComponentActions(JComponent component) {
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
                        executeMacro(codeArea, bookmarkIndex);
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
                        startMacroRecording(codeArea, bookmarkIndex);
                    }
                }
            });
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK), addActionKey);
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_NUMPAD0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK), addActionKey);

            String clearActionKey = "clear-bookmark-" + i;
            actionMap.put(clearActionKey, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearMacro(bookmarkIndex);
                }
            });
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), clearActionKey);
            inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_NUMPAD0 + i, metaMask | KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), clearActionKey);
        }
        component.setActionMap(actionMap);
        component.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap); */
    }

    public void executeMacro(ExtCodeArea codeArea, int macroIndex) {
        if (macroRecords.size() > macroIndex) {
            MacroRecord record = macroRecords.get(macroIndex);
            CodeAreaMacroCommandHandler commandHandler = (CodeAreaMacroCommandHandler) codeArea.getCommandHandler();

            List<String> steps = record.getSteps();
            for (String step : steps) {
                try {
                    Pair<MacroStep, List<Object>> parseStep = CodeAreaMacroCommandHandler.parseStep(step);
                    MacroStep macroStep = parseStep.fst;
                    List<Object> parameters = parseStep.snd;
                    switch (macroStep) {
                        case KEY_PRESSED: {
                            if (!parameters.isEmpty()) {
                                String text = (String) parameters.get(0);
                                for (char character : text.toCharArray()) {
                                    commandHandler.executeMacroStep(macroStep, List.of(character));
                                }
                                continue;
                            }
                            break;
                        }
                        case CARET_MOVE: {
                            if (parameters.size() > 1) {
                                Integer count = (Integer) parameters.get(1);
                                for (int i = 0; i < count; i++) {
                                    commandHandler.executeMacroStep(macroStep, List.of(parameters.get(0)));
                                }
                                continue;
                            }
                            break;
                        }
                    }
                    commandHandler.executeMacroStep(macroStep, parameters);
                } catch (ParseException | NumberFormatException ex) {
                    Logger.getLogger(MacroManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void startMacroRecording(ExtCodeArea codeArea) {
        CodeAreaMacroCommandHandler commandHandler = (CodeAreaMacroCommandHandler) codeArea.getCommandHandler();
        MacroRecord macroRecord = new MacroRecord();
        // TODO add number postfix
        macroRecord.setName("Macro");
        commandHandler.setRecordingMacro(macroRecord);
    }

    public void stopMacroRecording(ExtCodeArea codeArea) {
        CodeAreaMacroCommandHandler commandHandler = (CodeAreaMacroCommandHandler) codeArea.getCommandHandler();
        Optional<MacroRecord> recordingMacro = commandHandler.getRecordingMacro();
        if (recordingMacro.isPresent()) {
            MacroRecord macroRecord = recordingMacro.get();
            if (!macroRecord.isEmpty()) {
                macroRecords.add(macroRecord);
                saveMacroRecords();
                MacroManager.this.updateMacrosMenu();
            }
        }
        commandHandler.setRecordingMacro(null);
    }

    public void updateMacrosMenu() {
        if (macrosMenu != null) {
            updateMacrosMenu(macrosMenu);
        }
    }

    public void updateMacrosMenu(JMenu menu) {
        menu.removeAll();

        int recordsLimit = Math.min(macroRecords.size(), 10);
        int metaMask = ActionUtils.getMetaMask();
        String macroActionName = resourceBundle.getString("macroAction.text");
        for (int i = 0; i < recordsLimit; i++) {
            final int macroIndex = i;
            Action macroAction = new AbstractAction(macroActionName + " " + (i + 1)) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                        ExtCodeArea codeArea = fileHandler.getCodeArea();
                        executeMacro(codeArea, macroIndex);
                    }
                }
            };

            menu.add(ActionUtils.actionToMenuItem(macroAction));
        }

        if (!macroRecords.isEmpty()) {
            menu.addSeparator();
        }
        menu.add(ActionUtils.actionToMenuItem(executeLastMacroAction));
        menu.add(ActionUtils.actionToMenuItem(startMacroRecordingAction));
        menu.add(ActionUtils.actionToMenuItem(stopMacroRecordingAction));
        menu.add(ActionUtils.actionToMenuItem(manageMacrosAction));
    }
}
