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

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.couchbase.CouchbaseDefinition;
import org.talend.components.couchbase.runtime.CouchbaseSink;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class CouchbaseOutputDefinition extends CouchbaseDefinition {

    public static final String COMPONENT_NAME = "tCouchbaseOutput"; //$NON-NLS-1$

    public CouchbaseOutputDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return CouchbaseOutputProperties.class;
    }

    @Override
    public Property[] getReturnProperties() {
        return new Property[]{RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_SUCCESS_RECORD_COUNT_PROP, RETURN_REJECT_RECORD_COUNT_PROP,
                RETURN_ERROR_MESSAGE_PROP};
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine executionEngine, ComponentProperties properties, ConnectorTopology connectorTopology) {
        if (connectorTopology == ConnectorTopology.INCOMING) {
            return getRuntimeInfo(CouchbaseSink.class.getCanonicalName());
        } else {
            return null;
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING);
    }
}
