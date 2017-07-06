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

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;

public class CouchbaseWriter implements Writer<Result> {
    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseWriter.class);

    private final CouchbaseWriteOperation operation;
    private final CouchbaseSink sink;
    private final String idFieldName;
    private volatile boolean opened;
    private Result result;
    private CouchbaseConnection connection;

    public CouchbaseWriter(CouchbaseWriteOperation operation) {
        this.operation = operation;
        this.sink = (CouchbaseSink) operation.getSink();
        this.idFieldName = sink.getIdFieldName();
    }

    @Override
    public void open(String uId) throws IOException {
        if (opened) {
            LOG.debug("Writer is already opened");
            return;
        }
        connection = sink.getConnection();
        connection.increment();
        result = new Result(uId);
        opened = true;
    }

    @Override
    public void write(Object datum) throws IOException {
        if (!opened) {
            throw new IOException("Writer is not opened");
        }

        result.totalCount++;
        if (datum == null) {
            return;
        }

        // Data object is always IndexedRecord
        IndexedRecord record = (IndexedRecord) datum;

        Schema schema = record.getSchema();
        Schema.Field idField = schema.getField(idFieldName);
        if (idField == null) {
            throw new IOException("Schema does not contain ID field: " + idFieldName);
        }

        int idPos = idField.pos();
        Object id = record.get(idPos);
        if (id == null) {
            throw new IOException("ID field is null: " + idField.name());
        }

        connection.upsert(id.toString(), datum.toString());
    }

    @Override
    public Result close() throws IOException {
        connection.decrement();
        return result;
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return operation;
    }
}
