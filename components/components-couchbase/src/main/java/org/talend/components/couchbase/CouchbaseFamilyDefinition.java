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

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.couchbase.input.CouchbaseInputDefinition;
import org.talend.components.couchbase.output.CouchbaseOutputDefinition;

import aQute.bnd.annotation.component.Component;

@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + CouchbaseFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class CouchbaseFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "Couchbase";

    public CouchbaseFamilyDefinition() {
        super(NAME,
                new CouchbaseInputDefinition(),
                new CouchbaseOutputDefinition()
        );

    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }
}
