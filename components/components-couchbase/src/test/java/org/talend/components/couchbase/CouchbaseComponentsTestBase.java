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

import javax.inject.Inject;

import org.junit.Test;
import org.talend.components.api.test.AbstractComponentTest2;
import org.talend.components.couchbase.input.CouchbaseInputDefinition;
import org.talend.components.couchbase.output.CouchbaseOutputDefinition;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;

public class CouchbaseComponentsTestBase extends AbstractComponentTest2 {

    @Inject
    protected DefinitionRegistryService definitionRegistryService;

    @Override
    public DefinitionRegistryService getDefinitionRegistry() {
        return definitionRegistryService;
    }

    @Test
    public void componentHasBeenRegistered() {
        assertComponentIsRegistered(Definition.class, CouchbaseInputDefinition.COMPONENT_NAME,
                CouchbaseInputDefinition.class);
        assertComponentIsRegistered(Definition.class, CouchbaseOutputDefinition.COMPONENT_NAME,
                CouchbaseOutputDefinition.class);
    }
}
