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
package org.exbin.framework.bined.search;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.search.gui.BinarySearchPanel;
import org.exbin.framework.bined.search.service.BinarySearchService;

/**
 * TODO: Binary search.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinarySearch {

    private BinarySearchService binarySearchService;
    private final BinarySearchPanel binarySearchPanel = new BinarySearchPanel();

    private XBApplication application;

    public BinarySearch() {
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

}
