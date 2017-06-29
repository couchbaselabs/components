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

package org.talend.components.couchbase.runtime;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.couchbase.ComponentConstants;
import org.talend.components.couchbase.input.CouchbaseInputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

import java.io.IOException;
import java.util.List;

public class CouchbaseSource implements Source {
    private static final long serialVersionUID = 3602741914997413619L;
    
    private Schema schema;
    private String bootstrapNodes;
    private String bucket;
    private String password;
    private CouchbaseStreamingConnection connection;

    private static ValidationResultMutable fillValidationResult(ValidationResultMutable vr, Exception ex) {
        if (vr == null) {
            return null;
        }

        if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
            vr.setMessage(ex.toString());
        } else {
            vr.setMessage(ex.getMessage());
        }
        vr.setStatus(ValidationResult.Result.ERROR);
        return vr;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        CouchbaseInputProperties inputProperties = (CouchbaseInputProperties) properties;
        this.bootstrapNodes = inputProperties.bootstrapNodes.getStringValue();
        this.bucket = inputProperties.bucket.getStringValue();
        this.password = inputProperties.password.getStringValue();
        this.schema = inputProperties.schema.schema.getValue();
        return ValidationResult.OK;
    }

    @Override
    public Reader createReader(RuntimeContainer container) {
        return new CouchbaseReader(container, this);
    }

    public Schema getSchema() {
        return schema;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public ValidationResult validate(RuntimeContainer runtime) {
        ValidationResultMutable vr = new ValidationResultMutable();
        try {
            connection = connect(runtime);
            vr.setStatus(ValidationResult.Result.OK);
        } catch (Exception ex) {
            fillValidationResult(vr, ex);
        }
        return vr;
    }

    public CouchbaseStreamingConnection getConnection(RuntimeContainer runtime) throws ClassNotFoundException {
        if (connection == null) {
            connection = connect(runtime);
        }
        return connection;
    }

    private CouchbaseStreamingConnection connect(RuntimeContainer runtime) {
        CouchbaseStreamingConnection connection = new CouchbaseStreamingConnection(bootstrapNodes, bucket, password);
        connection.connect();
        if (runtime != null) {
            runtime.setComponentData(runtime.getCurrentComponentId(), ComponentConstants.CONNECTION_KEY, connection);
        }

        return connection;
    }

}