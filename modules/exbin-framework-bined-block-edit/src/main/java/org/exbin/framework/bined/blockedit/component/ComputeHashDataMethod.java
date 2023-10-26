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
package org.exbin.framework.bined.blockedit.component;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.blockedit.api.ConvertDataMethod;
import org.exbin.framework.bined.blockedit.component.gui.ComputeHashDataPanel;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.bined.blockedit.operation.DataOperationDataProvider;
import org.exbin.framework.bined.blockedit.operation.InsertDataOperation;
import org.exbin.framework.bined.blockedit.operation.ReplaceDataOperation;

/**
 * Compute Hash digest data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ComputeHashDataMethod implements ConvertDataMethod {

    private java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(ComputeHashDataPanel.class);

    private XBApplication application;
    private EditableBinaryData previewBinaryData;
    private long previewLengthLimit = 0;
    private HashType lastHashType = null;

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("component.name");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        ComputeHashDataPanel component = new ComputeHashDataPanel();
        component.setHashTypeChangeListener(() -> {
            HashType hashType = component.getHashType().orElse(null);
            if (lastHashType != hashType) {
                lastHashType = hashType;
                component.setBitSizes(HashType.BIT_SIZES.get(hashType));
            }
        });
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((ComputeHashDataPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createConvertCommand(Component component, CodeAreaCore codeArea, long position, long length) {
        ComputeHashDataPanel panel = (ComputeHashDataPanel) component;
        Optional<HashType> hashType = panel.getHashType();
        int bitSize = panel.getBitSize();

        DataOperationDataProvider dataOperationDataProvider = (EditableBinaryData binaryData) -> {
            convertData(binaryData, hashType.get(), bitSize, position);
        };

        return new ReplaceDataOperation.ReplaceDataCommand(new ReplaceDataOperation(codeArea, position, length, dataOperationDataProvider));
    }

    @Override
    public void performDirectConvert(Component component, CodeAreaCore codeArea, long position, long length, EditableBinaryData targetData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void convertData(EditableBinaryData binaryData, HashType hashType, int bitSize, long position) throws IllegalStateException {
        Digest digest = getDigest(hashType, bitSize);
        digest.reset();
        // TODO digest.update();
        int digestSize = (digest.getDigestSize() + 7) << 3;
        byte[] output = new byte[digestSize];
        digest.doFinal(output, 0);
    }

    @Nonnull
    private Digest getDigest(HashType hashType, int bitSize) {
        switch (hashType) {
            case GOST3411:
                return new GOST3411Digest();
            case KECCAK:
                return new KeccakDigest(bitSize);
            case MD2:
                return new MD2Digest();
            case MD4:
                return new MD4Digest();
            case MD5:
                return new MD5Digest();
            case RIPEMD: {
                switch (bitSize) {
                    case 128:
                        return new RIPEMD128Digest();
                    case 160:
                        return new RIPEMD160Digest();
                    case 256:
                        return new RIPEMD256Digest();
                    case 320:
                        return new RIPEMD320Digest();
                }
            }
            case SHA1:
                return new SHA1Digest();
            case SHA224:
                return new SHA224Digest();
            case SHA256:
                return new SHA256Digest();
            case SHA384:
                return new SHA384Digest();
            case SHA512:
                return new SHA512Digest();
            case SHA3:
                return new SHA3Digest();
            case SHAKE:
                return new SHAKEDigest();
            case SM3:
                return new SM3Digest();
            case TIGER:
                return new TigerDigest();
            case WHIRLPOOL:
                return new WhirlpoolDigest();
            default:
                throw CodeAreaUtils.getInvalidTypeException(hashType);
        }
    }

    @Override
    public void setPreviewDataTarget(Component component, BinaryData sourceBinaryData, EditableBinaryData targetBinaryData, long lengthLimit) {
        this.previewBinaryData = targetBinaryData;
        this.previewLengthLimit = lengthLimit;
        ComputeHashDataPanel panel = (ComputeHashDataPanel) component;
        panel.setModeChangeListener(() -> {
            fillPreviewData(panel);
        });
        fillPreviewData(panel);
    }

    private void fillPreviewData(ComputeHashDataPanel panel) {
        SwingUtilities.invokeLater(() -> {
            Optional<HashType> hashType = panel.getHashType();
            int bitSize = panel.getBitSize();

//            long dataLength = panel.getDataLength();
//            if (dataLength > previewLengthLimit) {
//                dataLength = previewLengthLimit;
//            }
//            EditableBinaryData sampleBinaryData = panel.getSampleBinaryData();
            previewBinaryData.clear();
            if (hashType.isPresent()) { 
//            previewBinaryData.insertUninitialized(0, dataLength);
                convertData(previewBinaryData, hashType.get(), bitSize, 0);
            }
        });
    }

    public enum HashType {
        KECCAK,
        MD2,
        MD4,
        MD5,
        RIPEMD,
        SHA1,
        SHA224,
        SHA256,
        SHA384,
        SHA512,
        SHA3,
        SHAKE,
        SM3,
        TIGER,
        GOST3411,
        WHIRLPOOL;

        public static Map<HashType, List<Integer>> BIT_SIZES = new HashMap<HashType, List<Integer>>() {
            {
                put(KECCAK, Arrays.asList(224, 256, 288, 384, 512));
                put(RIPEMD, Arrays.asList(128, 160, 256, 320));
                put(SHA3, Arrays.asList(224, 256, 384, 512));
                put(SHAKE, Arrays.asList(128, 256));
            }
        };
    }
}
