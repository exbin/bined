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
package org.exbin.bined.jaguif.legacy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.options.api.OptionsStorage;

/**
 * Legacy preferences for version 0.1.
 */
@ParametersAreNonnullByDefault
public class ConversionStorage implements OptionsStorage {

    protected OptionsStorage storage;
    protected Map<String, String> conversionPrefixes = new HashMap<>();

    public ConversionStorage(OptionsStorage storage) {
        this.storage = storage;
//        conversionPrefixes.put(key, value);
    }

    @Override
    public void flush() {
        storage.flush();
    }

    @Override
    public boolean exists(String key) {
        return storage.exists(getKey(key));
    }

    @Nonnull
    @Override
    public Optional<String> get(String key) {
        return storage.get(getKey(key));
    }

    @Nonnull
    @Override
    public String get(String key, String def) {
        return storage.get(getKey(key), def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return storage.getBoolean(getKey(key), def);
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return storage.getByteArray(getKey(key), def);
    }

    @Override
    public double getDouble(String key, double def) {
        return storage.getDouble(getKey(key), def);
    }

    @Override
    public float getFloat(String key, float def) {
        return storage.getFloat(getKey(key), def);
    }

    @Override
    public int getInt(String key, int def) {
        return storage.getInt(getKey(key), def);
    }

    @Override
    public long getLong(String key, long def) {
        return storage.getLong(getKey(key), def);
    }

    @Override
    public void put(String key, @Nullable String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putBoolean(String key, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putDouble(String key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putFloat(String key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putInt(String key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putLong(String key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sync() {
        storage.sync();
    }

    @Nonnull
    protected String getKey(String key) {
        // Recent file records conversion
        if (key.startsWith("recentFile.")) {
            if (key.startsWith("recentFile.path.")) {
                try {
                    int index = Integer.parseInt(key.substring(16));
                    return "recentFile.path." + String.valueOf(index + 1);
                } catch (NumberFormatException ex) {
                    // ignore
                }
            } else if (key.startsWith("recentFile.module.")) {
                try {
                    int index = Integer.parseInt(key.substring(18));
                    return "recentFile.module." + String.valueOf(index + 1);
                } catch (NumberFormatException ex) {
                    // ignore
                }
            }
            if (key.startsWith("recentFile.mode.")) {
                try {
                    int index = Integer.parseInt(key.substring(16));
                    return "recentFile.mode." + String.valueOf(index + 1);
                } catch (NumberFormatException ex) {
                    // ignore
                }
            }
        }

        for (Map.Entry<String, String> entry : conversionPrefixes.entrySet()) {
            String sourceKey = entry.getKey();

            if (key.startsWith(sourceKey)) {
                String targetKey = entry.getValue();
                return targetKey + sourceKey.substring(sourceKey.length());
            }
        }

        return key;
    }
}
