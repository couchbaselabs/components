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

package org.talend.components.couchbase.output;

import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.couchbase.CouchbaseProperties;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

public class CouchbaseOutputProperties extends CouchbaseProperties {

    public final Property<String> idFieldName = newString("idFieldName");

    public CouchbaseOutputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        idFieldName.setValue("id");
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        getForm(Form.MAIN).addRow(idFieldName);
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(MAIN_CONNECTOR);
        }
    }
}
