/*
 * Copyright (c) 2017 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talend.components.couchbase;

import java.util.Arrays;
import java.util.List;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.SupportedProduct;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.daikon.runtime.RuntimeInfo;

public abstract class CouchbaseDefinition extends AbstractComponentDefinition {

    public CouchbaseDefinition(String componentName) {
        super(componentName, ExecutionEngine.DI);
    }

    @Override
    public String[] getFamilies() {
        return new String[]{"Databases/Couchbase"};
    }

    public static RuntimeInfo getRuntimeInfo(String runtimeClassName) {
        return new SimpleRuntimeInfo(CouchbaseDefinition.class.getClassLoader(),
                DependenciesReader.computeDependenciesFilePath("org.talend.components", "components-couchbase"),
                runtimeClassName);

    }

    @Override
    public List<String> getSupportedProducts() {
        return Arrays.asList(SupportedProduct.DI);
    }
}
