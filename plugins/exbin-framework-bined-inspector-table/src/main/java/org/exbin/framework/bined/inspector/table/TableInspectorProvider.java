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
package org.exbin.framework.bined.inspector.table;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.inspector.BinEdInspector;
import org.exbin.framework.bined.inspector.BinEdInspectorProvider;

/**
 * Table inspector provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TableInspectorProvider implements BinEdInspectorProvider {

    public static final String INSPECTOR_ID = "table";

    protected TableInspector inspector;
    protected final ResourceBundle resourceBundle;

    public TableInspectorProvider(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    @Override
    public String getId() {
        return INSPECTOR_ID;
    }

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("tableInspector.name");
    }

    @Nonnull
    @Override
    public BinEdInspector createInspector() {
        if (inspector == null) {
            inspector = new TableInspector();
        }
        return inspector;
    }
}
