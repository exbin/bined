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
package org.exbin.framework.bined.theme.settings.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.settings.CodeAreaColorProfileOptions;
import org.exbin.framework.bined.theme.settings.gui.PreviewPanel.PreviewType;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.utils.WindowUtils;

/**
 * Manage list of color profiles panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ColorProfilesPanel extends javax.swing.JPanel implements ProfileListPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ColorProfilesPanel.class);

    private boolean modified = false;

    private AddProfileOperation addProfileOperation = null;
    private EditProfileOperation editProfileOperation = null;
    private CopyProfileOperation copyProfileOperation = null;
    private TemplateProfileOperation templateProfileOperation = null;

    public ColorProfilesPanel() {
        initComponents();
        init();
    }

    private void init() {
        profilesList.setModel(new ProfilesListModel());
        profilesList.setCellRenderer(new ProfileCellRenderer());
        profilesList.addListSelectionListener((ListSelectionEvent e) -> updateStates());
    }

    public void setAddProfileOperation(AddProfileOperation addProfileOperation) {
        this.addProfileOperation = addProfileOperation;
    }

    public void setEditProfileOperation(EditProfileOperation editProfileOperation) {
        this.editProfileOperation = editProfileOperation;
    }

    public void setCopyProfileOperation(CopyProfileOperation copyProfileOperation) {
        this.copyProfileOperation = copyProfileOperation;
    }

    public void setTemplateProfileOperation(TemplateProfileOperation templateProfileOperation) {
        this.templateProfileOperation = templateProfileOperation;
    }

    private void updateStates() {
        int[] selectedIndices = profilesList.getSelectedIndices();
        boolean hasSelection = selectedIndices.length > 0;
        boolean singleSelection = selectedIndices.length == 1;
        boolean hasAnyItems = !getProfilesListModel().isEmpty();
        editButton.setEnabled(singleSelection);
        copyButton.setEnabled(singleSelection);
        selectAllButton.setEnabled(hasAnyItems);
        removeButton.setEnabled(hasSelection);

        if (hasSelection) {
            upButton.setEnabled(profilesList.getMaxSelectionIndex() >= selectedIndices.length);
            downButton.setEnabled(profilesList.getMinSelectionIndex() + selectedIndices.length < getProfilesListModel().getSize());
            if (selectedIndices.length == 1) {
                previewPanel.getCodeArea().setColorsProfile(getProfilesListModel().getElementAt(selectedIndices[0]).getColorProfile());
            }
        } else {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
    }

    @Nonnull
    private ProfilesListModel getProfilesListModel() {
        return ((ProfilesListModel) profilesList.getModel());
    }

    public SectionCodeAreaColorProfile getProfile(int profileIndex) {
        return getProfilesListModel().getElementAt(profileIndex).colorProfile;
    }

    @Nonnull
    @Override
    public List<String> getProfileNames() {
        List<String> profileNames = new ArrayList<>();
        getProfilesListModel().profiles.forEach((profile) -> profileNames.add(profile.profileName));
        return profileNames;
    }

    @Override
    public void addProfileListPanelListener(ListDataListener listener) {
        getProfilesListModel().addListDataListener(listener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        profilesListScrollPane = new javax.swing.JScrollPane();
        profilesList = new javax.swing.JList<>();
        profilesControlPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        fromTemplateButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        hideButton = new javax.swing.JButton();
        previewPanel = new PreviewPanel(PreviewType.WITH_SEARCH);

        profilesListScrollPane.setViewportView(profilesList);

        profilesControlPanel.setPreferredSize(new java.awt.Dimension(152, 459));

        addButton.setText(resourceBundle.getString("addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        fromTemplateButton.setText(resourceBundle.getString("fromTemplateButton.text")); // NOI18N
        fromTemplateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromTemplateButtonActionPerformed(evt);
            }
        });

        editButton.setText(resourceBundle.getString("editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        copyButton.setText(resourceBundle.getString("copyButton.text")); // NOI18N
        copyButton.setEnabled(false);
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        upButton.setText(resourceBundle.getString("upButton.text")); // NOI18N
        upButton.setEnabled(false);
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText(resourceBundle.getString("downButton.text")); // NOI18N
        downButton.setEnabled(false);
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        selectAllButton.setText(resourceBundle.getString("selectAllButton.text")); // NOI18N
        selectAllButton.setEnabled(false);
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        removeButton.setText(resourceBundle.getString("removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        hideButton.setText(resourceBundle.getString("hideButton.text")); // NOI18N
        hideButton.setEnabled(false);
        hideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout profilesControlPanelLayout = new javax.swing.GroupLayout(profilesControlPanel);
        profilesControlPanel.setLayout(profilesControlPanelLayout);
        profilesControlPanelLayout.setHorizontalGroup(
            profilesControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profilesControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(profilesControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hideButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectAllButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(copyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fromTemplateButton, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addContainerGap())
        );
        profilesControlPanelLayout.setVerticalGroup(
            profilesControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profilesControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromTemplateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectAllButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hideButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(profilesListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profilesControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(profilesControlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(profilesListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        int[] indices = profilesList.getSelectedIndices();
        int last = 0;
        for (int i = 0; i < indices.length; i++) {
            int next = indices[i];
            if (last != next) {
                ColorProfile item = model.getElementAt(next);
                model.add(next - 1, item);
                profilesList.getSelectionModel().addSelectionInterval(next - 1, next - 1);
                model.remove(next + 1);
            } else {
                last++;
            }
        }
        wasModified();
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        int[] indices = profilesList.getSelectedIndices();
        int last = model.getSize() - 1;
        for (int i = indices.length; i > 0; i--) {
            int next = indices[i - 1];
            if (last != next) {
                ColorProfile item = model.getElementAt(next);
                model.add(next + 2, item);
                profilesList.getSelectionModel().addSelectionInterval(next + 2, next + 2);
                model.remove(next);
            } else {
                last--;
            }
        }
        wasModified();
    }//GEN-LAST:event_downButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        int selectedIndex = profilesList.getSelectedIndex();

        if (addProfileOperation != null) {
            ColorProfile newProfileRecord = addProfileOperation.run(this, getNewProfileName());
            if (newProfileRecord != null) {
                if (selectedIndex >= 0) {
                    profilesList.clearSelection();
                    model.add(selectedIndex, newProfileRecord);
                } else {
                    model.add(newProfileRecord);
                }
                wasModified();
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        model.removeIndices(profilesList.getSelectedIndices());
        profilesList.clearSelection();
        wasModified();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        int selectedIndex = profilesList.getSelectedIndex();
        ColorProfile oldProfileRecord = model.getElementAt(selectedIndex);

        if (editProfileOperation != null) {
            ColorProfile newProfileRecord = editProfileOperation.run(this, oldProfileRecord);
            if (newProfileRecord != null) {
                ColorProfile profileRecord = model.getElementAt(selectedIndex);
                profileRecord.profileName = newProfileRecord.getProfileName();
                profileRecord.colorProfile = newProfileRecord.colorProfile;
                model.notifyProfileModified(selectedIndex);
                wasModified();
            }
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void hideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hideButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        if (profilesList.getSelectedIndices().length < profilesList.getModel().getSize()) {
            profilesList.setSelectionInterval(0, profilesList.getModel().getSize() - 1);
        } else {
            profilesList.clearSelection();
        }
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        int selectedIndex = profilesList.getSelectedIndex();
        ColorProfile sourceProfile = model.getElementAt(selectedIndex);
        ColorProfile profileRecord = new ColorProfile(sourceProfile.profileName, sourceProfile.colorProfile);

        if (copyProfileOperation != null) {
            ColorProfile newProfileRecord = copyProfileOperation.run(this, profileRecord);
            if (newProfileRecord != null) {
                if (selectedIndex >= 0) {
                    model.add(selectedIndex + 1, newProfileRecord);
                    profilesList.setSelectedIndex(selectedIndex + 1);
                } else {
                    profilesList.clearSelection();
                    model.add(newProfileRecord);
                }
                wasModified();
            }
        }
    }//GEN-LAST:event_copyButtonActionPerformed

    private void fromTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromTemplateButtonActionPerformed
        ProfilesListModel model = getProfilesListModel();
        int selectedIndex = profilesList.getSelectedIndex();

        if (templateProfileOperation != null) {
            ColorProfile newProfileRecord = templateProfileOperation.run(this);
            if (newProfileRecord != null) {
                if (selectedIndex >= 0) {
                    profilesList.clearSelection();
                    model.add(selectedIndex, newProfileRecord);
                } else {
                    model.add(newProfileRecord);
                }
                wasModified();
            }
        }
    }//GEN-LAST:event_fromTemplateButtonActionPerformed

    public boolean isModified() {
        return modified;
    }

    private void wasModified() {
        modified = true;
        updateStates();
    }

    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        CodeAreaColorProfileOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaColorProfileOptions.class);
        List<ColorProfile> profiles = new ArrayList<>();
        List<String> profileNames = options.getProfileNames();
        for (int index = 0; index < profileNames.size(); index++) {
            ColorProfile profile = new ColorProfile(
                    profileNames.get(index),
                    options.getColorsProfile(index)
            );
            profiles.add(profile);
        }

        ProfilesListModel model = getProfilesListModel();
        model.setProfiles(profiles);
    }

    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        CodeAreaColorProfileOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaColorProfileOptions.class);
        options.clearProfiles();
        ProfilesListModel model = getProfilesListModel();
        List<ColorProfile> profiles = model.getProfiles();
        for (int index = 0; index < profiles.size(); index++) {
            ColorProfile profile = profiles.get(index);
            options.addProfile(profile.profileName, profile.colorProfile);
        }
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new ColorProfilesPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton fromTemplateButton;
    private javax.swing.JButton hideButton;
    private org.exbin.framework.bined.theme.settings.gui.PreviewPanel previewPanel;
    private javax.swing.JPanel profilesControlPanel;
    private javax.swing.JList<ColorProfile> profilesList;
    private javax.swing.JScrollPane profilesListScrollPane;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public final static class ColorProfile {

        private String profileName;
        private boolean visible = true;
        private SectionCodeAreaColorProfile colorProfile;

        public ColorProfile(String profileName, SectionCodeAreaColorProfile colorProfile) {
            this.profileName = profileName;
            this.colorProfile = colorProfile;
        }

        @Nonnull
        public String getProfileName() {
            return profileName;
        }

        @Nonnull
        public SectionCodeAreaColorProfile getColorProfile() {
            return colorProfile;
        }
    }

    @ParametersAreNonnullByDefault
    private static final class ProfilesListModel extends AbstractListModel<ColorProfile> {

        private final List<ColorProfile> profiles = new ArrayList<>();

        public ProfilesListModel() {
        }

        @Override
        public int getSize() {
            if (profiles == null) {
                return 0;
            }
            return profiles.size();
        }

        public boolean isEmpty() {
            return profiles == null || profiles.isEmpty();
        }

        @Nullable
        @Override
        public ColorProfile getElementAt(int index) {
            return profiles.get(index);
        }

        @Nonnull
        public List<ColorProfile> getProfiles() {
            return profiles;
        }

        public void setProfiles(List<ColorProfile> profiles) {
            int size = this.profiles.size();
            if (size > 0) {
                this.profiles.clear();
                fireIntervalRemoved(this, 0, size - 1);
            }
            int profilesSize = profiles.size();
            if (profilesSize > 0) {
                this.profiles.addAll(profiles);
                fireIntervalAdded(this, 0, profilesSize - 1);
            }
        }

        public void addAll(List<ColorProfile> list, int index) {
            if (index >= 0) {
                profiles.addAll(index, list);
                fireIntervalAdded(this, index, list.size() + index);
            } else {
                profiles.addAll(list);
                fireIntervalAdded(this, profiles.size() - list.size(), profiles.size());
            }
        }

        public void removeIndices(int[] indices) {
            if (indices.length == 0) {
                return;
            }
            Arrays.sort(indices);
            for (int i = indices.length - 1; i >= 0; i--) {
                profiles.remove(indices[i]);
                fireIntervalRemoved(this, indices[i], indices[i]);
            }
        }

        public void remove(int index) {
            profiles.remove(index);
            fireIntervalRemoved(this, index, index);
        }

        public void add(int index, ColorProfile item) {
            profiles.add(index, item);
            fireIntervalAdded(this, index, index);
        }

        public void add(ColorProfile item) {
            profiles.add(item);
            int index = profiles.size() - 1;
            fireIntervalAdded(this, index, index);
        }

        public void notifyProfileModified(int index) {
            fireContentsChanged(this, index, index);
        }
    }

    @ParametersAreNonnullByDefault
    private static final class ProfileCellRenderer implements ListCellRenderer<ColorProfile> {

        private final DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends ColorProfile> list, ColorProfile value, int index, boolean isSelected, boolean cellHasFocus) {
            return defaultListCellRenderer.getListCellRendererComponent(list, value.profileName, index, isSelected, cellHasFocus);
        }
    }

    @Nonnull
    private String getNewProfileName() {
        String profileName = "Profile ";
        int profileIndex = 1;
        while (hasProfileWithName(profileName + profileIndex)) {
            profileIndex++;
        }

        return profileName + profileIndex;
    }

    private boolean hasProfileWithName(String profileName) {
        return getProfilesListModel().getProfiles().stream().anyMatch((profile) -> (profileName.equals(profile.profileName)));
    }

    @ParametersAreNonnullByDefault
    public static interface AddProfileOperation {

        @Nullable
        ColorProfile run(JComponent parentComponent, String profileName);
    }

    @ParametersAreNonnullByDefault
    public static interface EditProfileOperation {

        @Nullable
        ColorProfile run(JComponent parentComponent, ColorProfile profileRecord);
    }

    @ParametersAreNonnullByDefault
    public static interface CopyProfileOperation {

        @Nullable
        ColorProfile run(JComponent parentComponent, ColorProfile profileRecord);
    }

    @ParametersAreNonnullByDefault
    public static interface TemplateProfileOperation {

        @Nullable
        ColorProfile run(JComponent parentComponent);
    }
}
