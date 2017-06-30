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

import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import org.apache.avro.generic.IndexedRecord;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CouchbaseReader implements Reader<IndexedRecord> {
    private transient static final Logger LOG = LoggerFactory.getLogger(CouchbaseStreamingConnection.class);

    private final RuntimeContainer container;
    private final CouchbaseSource source;
    private final CouchbaseEventGenericRecordConverter converter;
    private CouchbaseStreamingConnection connection;
    private LinkedBlockingQueue<ByteBuf> resultsQueue;
    private IndexedRecord currentRecord;

    public CouchbaseReader(RuntimeContainer container, CouchbaseSource source) {
        super();
        this.container = container;
        this.source = source;
        this.converter = new CouchbaseEventGenericRecordConverter();
        converter.setSchema(source.getSchema());
    }

    @Override
    public boolean start() throws IOException {
        try {
            connection = source.getConnection(container);
            resultsQueue = new LinkedBlockingQueue<ByteBuf>();
            connection.startStreaming(resultsQueue);
        } catch (ClassNotFoundException e) {
            throw new ComponentException(e);
        }
        return advance();
    }

    @Override
    public boolean advance() throws IOException {
        while (true) {
            ByteBuf event;
            try {
                event = resultsQueue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.error("Failed to poll event from the results queue", e);
                return false;
            }
            if (event != null) {
                currentRecord = converter.convertToAvro(event);
                connection.acknowledge(event);
                event.release();
                return true;
            }
            if (!connection.isStreaming() && resultsQueue.isEmpty()) {
                break;
            }
        }
        return false;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        return currentRecord;
    }

    @Override
    public Instant getCurrentTimestamp() throws NoSuchElementException {
        return null;
    }

    @Override
    public void close() throws IOException {
        connection.stopStreaming();
    }

    @Override
    public Source getCurrentSource() {
        return source;
    }

    @Override
    public Map<String, Object> getReturnValues() {
        Result result = new Result();
        return result.toMap();
    }
}
