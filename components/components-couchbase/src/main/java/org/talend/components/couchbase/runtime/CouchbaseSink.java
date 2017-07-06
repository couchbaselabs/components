/*
 * Copyright (c) 2016 Couchbase, Inc.
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

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.couchbase.CouchbaseSourceOrSink;
import org.talend.components.couchbase.output.CouchbaseOutputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

public class CouchbaseSink extends CouchbaseSourceOrSink implements Sink {

    private static final long serialVersionUID = 1313511127549129199L;

    private CouchbaseConnection connection;
    private String idFieldName;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        CouchbaseOutputProperties inputProperties = (CouchbaseOutputProperties) properties;
        this.bootstrapNodes = inputProperties.bootstrapNodes.getStringValue();
        this.bucket = inputProperties.bucket.getStringValue();
        this.password = inputProperties.password.getStringValue();
        this.idFieldName = inputProperties.idFieldName.getStringValue();
        return ValidationResult.OK;
    }

    @Override
    public WriteOperation<?> createWriteOperation() {
        return new CouchbaseWriteOperation(this);
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
    public ValidationResult validate(RuntimeContainer container) {
        try {
            connection = connect();
            return ValidationResult.OK;
        } catch (Exception ex) {
            return createValidationResult(ex);
        }
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public CouchbaseConnection getConnection() {
        if (connection == null) {
            connection = connect();
        }
        return connection;
    }

    private CouchbaseConnection connect() {
        CouchbaseConnection connection = new CouchbaseConnection(bootstrapNodes, bucket, password);
        connection.connect();
        return connection;
    }
}
