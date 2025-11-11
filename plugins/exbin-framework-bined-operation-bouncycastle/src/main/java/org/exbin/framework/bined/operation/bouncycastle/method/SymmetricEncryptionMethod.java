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
package org.exbin.framework.bined.operation.bouncycastle.method;

import java.awt.Component;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.PreviewDataHandler;
import org.exbin.framework.bined.operation.ConversionDataProvider;
import org.exbin.framework.bined.operation.command.ConvertDataCommand;
import org.exbin.framework.bined.operation.ConvertDataOperation;
import org.exbin.framework.bined.operation.bouncycastle.method.gui.EncryptionPanel;
import org.exbin.framework.bined.operation.gui.BinaryPreviewPanel;

/**
 * Encyption and decryption data conversion method.
 */
@ParametersAreNonnullByDefault
public class SymmetricEncryptionMethod implements ConvertDataMethod {

    private static final String PROVIDER = "BC";
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final int ITERATION_COUNT = 10000;

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EncryptionPanel.class);

    private long previewLengthLimit = 0;
    private PreviewDataHandler previewDataHandler;
    private BinaryPreviewPanel previewPanel;

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("component.name");
    }

    @Nonnull
    @Override
    public Component createComponent() {
        EncryptionPanel component = new EncryptionPanel();
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((EncryptionPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createConvertCommand(Component component, CodeAreaCore codeArea) {
        EncryptionPanel panel = (EncryptionPanel) component;
        OperationType operationType = panel.getOperationType();
        Algorithm algorithm = panel.getAlgorithm();
        char[] password = panel.getPassword();

        long position;
        long length;
        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        if (selection.isEmpty()) {
            position = 0;
            length = codeArea.getDataSize();
        } else {
            position = selection.getFirst();
            length = selection.getLength();
        }

        ConversionDataProvider conversionDataProvider = (EditableBinaryData binaryData, long sourcePosition, long sourceLength, long targetPosition) -> {
            convertData(binaryData, sourcePosition, sourceLength, operationType, algorithm, password, binaryData, targetPosition);
        };

        return new ConvertDataCommand(codeArea, new ConvertDataOperation(position, length, length, conversionDataProvider));
    }

    @Nonnull
    @Override
    public BinaryData performDirectConvert(Component component, CodeAreaCore codeArea) {
        EncryptionPanel panel = (EncryptionPanel) component;
        OperationType operationType = panel.getOperationType();
        Algorithm algorithm = panel.getAlgorithm();
        char[] password = panel.getPassword();

        long position;
        long length;
        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        if (selection.isEmpty()) {
            position = 0;
            length = codeArea.getDataSize();
        } else {
            position = selection.getFirst();
            length = selection.getLength();
        }

        EditableBinaryData binaryData = new ByteArrayEditableData();
        convertData(codeArea.getContentData(), position, length, operationType, algorithm, password, binaryData, 0);
        return binaryData;
    }

    @Nonnull
    public String convertData(BinaryData sourceBinaryData, long position, long length, OperationType operationType,
                            Algorithm algorithm, char[] password, EditableBinaryData targetBinaryData, long targetPosition) {

        byte[] sourceData = new byte[(int) length];
        sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);

        try {
            byte[] output;
            if (operationType == OperationType.ENCRYPT) {
                output = encrypt(sourceData, algorithm, password);
            } else {
                output = decrypt(sourceData, algorithm, password);
            }
            targetBinaryData.insert(targetPosition, output);

        } catch (Exception ex) {
            String errorMsg = "Crypto error: " + ex.getMessage();
            byte[] output = errorMsg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            targetBinaryData.insert(targetPosition, output);
            return errorMsg;
        }
        
        return "";
    }

    private byte[] encrypt(byte[] data, Algorithm algorithm, char[] password) throws Exception {
        // Generate random salt and IV
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(salt);
        random.nextBytes(iv);

        // Derive key from password
        SecretKey key = deriveKey(password, salt, algorithm.getKeySize());

        // Encrypt data
        Cipher cipher = Cipher.getInstance(algorithm.getTransformation(), PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(data);

        // Combine salt + IV + encrypted data
        ByteBuffer buffer = ByteBuffer.allocate(SALT_LENGTH + IV_LENGTH + encrypted.length);
        buffer.put(salt);
        buffer.put(iv);
        buffer.put(encrypted);

        return buffer.array();
    }

    private byte[] decrypt(byte[] data, Algorithm algorithm, char[] password) throws Exception {
        if (data.length < SALT_LENGTH + IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data");
        }

        // Extract salt, IV, and encrypted data
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        buffer.get(salt);
        buffer.get(iv);

        byte[] encrypted = new byte[data.length - SALT_LENGTH - IV_LENGTH];
        buffer.get(encrypted);

        // Derive key from password
        SecretKey key = deriveKey(password, salt, algorithm.getKeySize());

        // Decrypt data
        Cipher cipher = Cipher.getInstance(algorithm.getTransformation(), PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(encrypted);
    }

    @Nonnull
    private SecretKey deriveKey(char[] password, byte[] salt, int keySize) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256", PROVIDER);
        KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, keySize);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        EncryptionPanel panel = (EncryptionPanel) component;
        panel.setParamChangeListener(() -> {
            fillPreviewData(panel, codeArea);
        });
        fillPreviewData(panel, codeArea);
    }

    private void fillPreviewData(EncryptionPanel panel, CodeAreaCore codeArea) {
        previewPanel = new BinaryPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);
        SwingUtilities.invokeLater(() -> {
            OperationType operationType = panel.getOperationType();
            Algorithm algorithm = panel.getAlgorithm();
            char[] password = panel.getPassword();

            if (password == null || password.length == 0) {
                return;
            }

            EditableBinaryData previewBinaryData = new ByteArrayEditableData();
            previewBinaryData.clear();
            long position;
            long length;
            SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
            if (selection.isEmpty()) {
                position = 0;
                length = codeArea.getDataSize();
            } else {
                position = selection.getFirst();
                length = selection.getLength();
            }

            length = Math.min(length, 1024);

            String resultMessage = convertData(codeArea.getContentData(), position, length, operationType, algorithm, password, previewBinaryData, 0);

            if (resultMessage.isEmpty()) {
                long previewDataSize = previewBinaryData.getDataSize();
                if (previewDataSize > previewLengthLimit) {
                    previewBinaryData.remove(previewLengthLimit, previewDataSize - previewLengthLimit);
                }
                previewPanel.setPreviewData(previewBinaryData);
            } else {
                previewPanel.setErrorMessage(resultMessage);
            }
        });
    }

    public enum OperationType {
        ENCRYPT,
        DECRYPT
    }

    public enum Algorithm {
        AES_128("AES/CBC/PKCS5Padding", 128),
        AES_256("AES/CBC/PKCS5Padding", 256);

        private final String transformation;
        private final int keySize;

        Algorithm(String transformation, int keySize) {
            this.transformation = transformation;
            this.keySize = keySize;
        }

        @Nonnull
        public String getTransformation() {
            return transformation;
        }

        public int getKeySize() {
            return keySize;
        }
    }
}
